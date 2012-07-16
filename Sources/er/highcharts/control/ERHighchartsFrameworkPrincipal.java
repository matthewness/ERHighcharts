package er.highcharts.control;

import com.webobjects.appserver.WOApplication;

import er.extensions.ERXFrameworkPrincipal;
import er.extensions.foundation.ERXProperties;

/**
 * The Framework Principal class responsible for initialising the ERHighcharts request handler for the application in use.
 * 
 * @author matt
 *
 */
public class ERHighchartsFrameworkPrincipal extends ERXFrameworkPrincipal {

	/**
	 * Default behaviour of ERXFrameworkPrincipal
	 */
	static {
		setUpFrameworkPrincipalClass(ERHighchartsFrameworkPrincipal.class);
	}

	/**
	 * The Property key for application specific request handler keys
	 */
	private static final String ERHIGHCHARTS_REQUEST_HANDLER_KEY_PROPERTY = "er.highcharts.transcode.requestHandlerKey";

	/**
	 * The default request handler key, when 
	 * ERHIGHCHARTS_REQUEST_HANDLER_KEY_PROPERTY is not present in the Application
	 * Properties
	 */
	private static final String ERHIGHCHARTS_REQUEST_HANDLER_KEY_DEFAULT = "transcode";
	
//	public final static Class REQUIRES[] = new Class[] {ERXExtensions.class, Ajax.class};
	 

	/**
	 * Set up the Request Handler.
	 */
	@Override
	public void finishInitialization() {
		WOApplication.application().registerRequestHandler(new ERHighchartsRequestHandler(), requestHandlerKey());
	}

	/**
	 * The request handler key. Attempt to find it in the Application's Properties,
	 * and fall back on the default if it doesn't exist.
	 * @return
	 */
	public static String requestHandlerKey(){
		return ERXProperties.stringForKeyWithDefault(ERHIGHCHARTS_REQUEST_HANDLER_KEY_PROPERTY, ERHIGHCHARTS_REQUEST_HANDLER_KEY_DEFAULT);
	}
	

}