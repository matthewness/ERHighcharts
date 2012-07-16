package er.highcharts.components.chart;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.module.SimpleModule;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;

import er.ajax.AjaxUtils;
import er.extensions.foundation.ERXStringUtilities;
import er.highcharts.components.ERHighchartComponent;
import er.highcharts.control.ERHighchartsRequestHandler;
import er.highcharts.control.serializers.ERJodaDateTimeSerializer;
import er.highcharts.control.serializers.ERNSTimestampSerializer;
import er.highcharts.model.KVCAExtensionGraph;

/**
 * ERHighchart is the base component responsible for generating the Highchart JSON/JavaScript
 * output that Highcharts renders, and the HTML container in which it is rendered.
 * 
 * ERHighchart also takes care of the exporting, animation, and credit option values if they are not already set.
 * 
 * @binding graph (KVCAExtensionGraph) the graph containing all option values to be rendered.
 * @author matt
 *
 */
public class ERHighchart extends ERHighchartComponent {

	/**
	 * Default serial version id
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * This class Logger
	 */
	private static Logger log = Logger.getLogger(ERHighchart.class);
	
	/**
	 * The required JavaScript file references
	 */
	private static final NSArray<String> ADDITIONAL_JAVASCRIPT_FILES
		= new NSArray<String>(new String[]{
				"prototype-adapter.js",
				"highcharts.js",
				"exporting.js"
		});
	
	/**
	 * The cached Request Handler URL for transcoding
	 */
	private static String _localTranscodeURL = null;

	/**
	 * The HTML identifier for the chart container
	 */
	private String _chartId = null;


	/**
	 * Standard constructor
	 * @param context
	 */
	public ERHighchart(WOContext context) {
        super(context);
    }
	
	/**
	 * The HTML identifier for the chart container
	 */
	public String chartId(){
		if(_chartId==null){
			_chartId = "erhighcharts_"+this.hashCode();
		}
		return _chartId;
	}
	
	/**
	 * The bound CSS style values for the chart container.
	 * @return
	 */
	public String style(){
		return (String)valueForBinding(STYLE_BINDING);
	}
	
	/**
	 * The CSS style values for the chart container.
	 * @return
	 */
	public String chartStyle(){
		if(ERXStringUtilities.stringIsNullOrEmpty(style())){
			return "height: 400px;";
		}else{
			return style();
		}
	}
	
	/**
	 * We define the Highcharts javascript resources here
	 */
	@Override
	public NSArray<String> additionalJavascriptFiles(){
		return ADDITIONAL_JAVASCRIPT_FILES;
	}
	
	/**
	 * The String value, built by merging the {@code KVCAExtensionGraph} graph
	 * object with the defaults and bindings, and mapping the object out to a JSON 
	 * representation using Jackson, which Highcharts uses to render the chart.
	 * 
	 * @return
	 */
	public String chartString(){
		
		KVCAExtensionGraph graph = boundGraph();
		
		String ret = "ERROR";

		if(graph==null){
			return ret;
		}
		
		//set the HTML element target
		graph.takeValueForKeyPath(chartId(), "chart.renderTo");
		graph.takeValueForKeyPath(true, "chart.animation");
		graph.takeValueForKeyPath(true, "plotOptions.series.animation");
		
		//set the credits if they are not set by the parent.
		Boolean creditsEnabled = (Boolean) graph.valueForKeyPath("credits.enabled");
		if(creditsEnabled==null){
			graph.takeValueForKeyPath(false, "credits.enabled");
		}
		
		//set the transcode url
		String transcodeURL = (String)graph.valueForKeyPath("exporting.url");
		if(transcodeURL==null){
			graph.takeValueForKeyPath(localTranscodeURL(), "exporting.url");		
		}
				
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
			
			SimpleModule erHighchartsModule = new SimpleModule("ERHighchartsModule", new Version(1, 0, 0, null));

			//Custom serializer for DateTime objects
			erHighchartsModule.addSerializer(new ERJodaDateTimeSerializer());
			//Custom serializer for NSTimestamp objects
			erHighchartsModule.addSerializer(new ERNSTimestampSerializer());
			
			mapper.registerModule(erHighchartsModule);
			
			ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
			ret = writer.writeValueAsString(graph);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return ret;

	}
	
	/**
	 * The Request Handler URL used by Highcharts to call the transcoding functions,
	 * @return
	 */
	private String localTranscodeURL() {
		if(_localTranscodeURL==null){
			_localTranscodeURL = ERHighchartsRequestHandler.transcodeUrl(context(), null);
		}
		return _localTranscodeURL;
	}

	/**
	 * Ensure the JavaScript resources are present in the HTML document.
	 */
	public void appendToResponse(WOResponse response, WOContext context){
		AjaxUtils.addScriptResourceInHead(context, response, "Ajax", "prototype.js");
		AjaxUtils.addScriptResourceInHead(context, response, "Ajax", "effects.js");
		AjaxUtils.addScriptResourceInHead(context, response, "Ajax", "wonder.js");
		super.appendToResponse(response, context);
	}
	
}