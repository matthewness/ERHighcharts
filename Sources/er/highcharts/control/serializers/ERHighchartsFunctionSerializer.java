package er.highcharts.control.serializers;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import er.highcharts.model.ERHighchartsFunction;

/**
 * This serializer uses the writeRaw method in JsonGenerator
 * so we can output 'dirty' JSON - that is, non quoted string values
 * which can be recognised as a JavaScript function rather than a primitive variable value.
 * 
 * @author matt
 *
 */
public class ERHighchartsFunctionSerializer extends JsonSerializer<ERHighchartsFunction>{

	@Override
	public void serialize(ERHighchartsFunction hcFunction, JsonGenerator gen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		gen.writeRaw(" : "+hcFunction.body);
	}


}
