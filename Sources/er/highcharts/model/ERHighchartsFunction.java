package er.highcharts.model;

import org.codehaus.jackson.annotate.JsonRawValue;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import er.highcharts.control.serializers.ERHighchartsFunctionSerializer;

/**
 * Representation of a JavaScript function body, serialised 'raw'
 * in JSON to enable them as functions.
 * 
 * @author matt
 *
 */
@JsonSerialize(using = ERHighchartsFunctionSerializer.class)
public class ERHighchartsFunction extends Object {

	/**
	 * The function body variable.
	 */
	@JsonRawValue
	public String body;
	
	/**
	 * Standard constructor
	 * 
	 * @param v the value of the function to be set in body
	 */
	public ERHighchartsFunction(String v){
		body = v;
	}
}
