package er.highcharts.control;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WORequestHandler;
import com.webobjects.appserver.WOResponse;
import com.webobjects.appserver.WOSession;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSLog;


/**
 * ERHighchartsRequestHandler handles the requests from a Highcharts component
 * attempting to transcode a particular HighCharts SVG document for download.
 * 
 * The Highcharts scripts send four arguments to this request handler :
 * 
 * <ul>
 * 	<li>svg - the string representation of the SVG document to transcode</li>
 *  <li>filename - the filename of the destination file to create</li>
 *  <li>type - the Content-Type of the destination file to create. This will be one of:
 *  	<ul>
 *  		<li>image/png - a PNG image file</li>
 *  		<li>image/jpeg - a JPEG Group image file</li>
 *  		<li>image/svg+xml - the svg document itself</li>
 *  		<li>application/pdf - a PDF file</li>
 *  	</ul>
 *  </li>
 *  <li>width - the width of the destination file, if possible</li>
 * <ul>
 * 
 * These values can be overridden in the KVCAExtensionGraph graph object.
 * 
 * @author matt
 *
 */
public class ERHighchartsRequestHandler extends WORequestHandler {

	/**
	 * Standard Logging
	 */
	public static final Logger log = Logger.getLogger(ERHighchartsRequestHandler.class);

	/**
	 * Request keys
	 */
	private static final String FORM_KEY_SVG = "svg";
	private static final String FORM_KEY_WIDTH = "width";
	private static final String FORM_KEY_TYPE = "type";
	private static final String FORM_KEY_FILENAME = "filename";
	private static final String FORM_KEY_WOSID = "wosid";

	/**
	 * response content types
	 */
	private static final String TYPE_PNG = "image/png";
	private static final String TYPE_JPEG = "image/jpeg";
	private static final String TYPE_SVG = "image/svg+xml";
	private static final String TYPE_PDF = "application/pdf";
	

	/**
	 * The delegate definition for this request handler.
	 */
	public static interface Delegate {
		/**
		 * Called prior to displaying a trancoded image
		 * 
		 * @param request
		 *            the current request
		 * @param context
		 *            the current context
		 * @return true if the current user is allowed to view the transcoded response
		 */
		public boolean reportVisible(WORequest request, WOContext context);
	}

	/**
	 * The delegate for this handler
	 */
	private ERHighchartsRequestHandler.Delegate _delegate;

	/**
	 * Sets the delegate for this request handler.
	 * 
	 * @param delegate
	 *            the delegate for this request handler
	 */
	public void setDelegate(ERHighchartsRequestHandler.Delegate delegate) {
		_delegate = delegate;
	}

	/**
	 * The request handler method.
	 */
	@Override
	public WOResponse handleRequest(WORequest request) {
		WOApplication application = WOApplication.application();
		application.awake();
		
		try {
			WOContext context = application.createContextForRequest(request);
			WOResponse response = application.createResponseInContext(context);

			String wosid = (String) request.formValueForKey(FORM_KEY_WOSID);
			if (wosid == null) {
				wosid = request.cookieValueForKey(FORM_KEY_WOSID);
			}
			context._setRequestSessionID(wosid);
			
			WOSession session = null;
			
			if (context._requestSessionID() != null) {
				session = WOApplication.application().restoreSessionWithID(wosid, context);
			}
			
			try {

				try {

					String svgString = request.stringFormValueForKey(FORM_KEY_SVG);
					Float width = new Float(request.stringFormValueForKey(FORM_KEY_WIDTH));
					String filename = request.stringFormValueForKey(FORM_KEY_FILENAME);
					String type = request.stringFormValueForKey(FORM_KEY_TYPE);
					
					if (_delegate != null && !_delegate.reportVisible(request, context)) {
						throw new SecurityException("You are not allowed to view the requested report.");
					}
					
					if(TYPE_PNG.equals(type)){
						NSData im  = ERHighchartsTranscoding.pngTranscode(svgString, width);
						response.setContent(im);
					}else if(TYPE_JPEG.equals(type)){
						NSData im = ERHighchartsTranscoding.jpegTranscode(svgString, width);
						response.setContent(im);
					}else if (TYPE_SVG.equals(type)){
						response.setContent(svgString);
					}else if (TYPE_PDF.equals(type)){
						NSData im = ERHighchartsTranscoding.pdfTranscode(svgString, width);
						response.setContent(im);
					}
					
					response.setHeader(type, "Content-Type");
					response.setHeader("attachment;filename=\"" + filename + "\"", "content-disposition");
					response.disableClientCaching();
					response.setHeader("no-cache", "cache-control");
					response.setHeader("no-cache", "pragma");
					response.setStatus(200);
					return response;
				} catch (SecurityException e) {
					NSLog.out.appendln(e);
					response.setContent(e.getMessage());
					response.setStatus(403);
				} catch (Exception e) {
					NSLog.out.appendln(e);
					response.setContent(e.getMessage());
					response.setStatus(500);
				}

				return response;
			} finally {
				if (context._requestSessionID() != null) {
					WOApplication.application().saveSessionForContext(context);
				}
			}
		} finally {
			application.sleep();
		}
	}
	
	/**
	 * Registers a handler with the WOApplication.
	 * 
	 * @param requestHandler
	 *            the rest request handler to register
	 */
	public static void register(ERHighchartsRequestHandler requestHandler, String key) {
		WOApplication.application().registerRequestHandler(requestHandler, key);
	}

	/**
	 * Returns the URL pointing to this request handler.
	 * 
	 * @param context the current WOContext
	 * @param queryString the query string to append
	 * @return an {@code ERHighchartsRequestHandler} request handler URL
	 */
	public static String transcodeUrl(WOContext context, String queryString) {
		String transcodeUrl = context.urlWithRequestHandlerKey(ERHighchartsFrameworkPrincipal.requestHandlerKey(), null, queryString);
		return transcodeUrl;
	}

}