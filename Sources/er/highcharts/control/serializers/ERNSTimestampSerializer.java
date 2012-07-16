package er.highcharts.control.serializers;

import java.io.IOException;
import java.util.Calendar;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

import com.webobjects.foundation.NSTimestamp;

/**
 * The global Serializer for NSTimestamp objects
 * 
 * Translates the timestamp values into a JavaScript <code>Date.UTC</code> call.
 * 
 * @author matt
 *
 */
public class ERNSTimestampSerializer extends SerializerBase<NSTimestamp> {

	public ERNSTimestampSerializer(){
		super(NSTimestamp.class, true);
	}
	@Override
	public void serialize(NSTimestamp timestamp, JsonGenerator gen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		if(timestamp==null){
			gen.writeNull();
		}else{
			Calendar cal = Calendar.getInstance();
			cal.setTime(timestamp);
			String utcDescription = 
					"Date.UTC("+cal.get(Calendar.YEAR)+","+(cal.get(Calendar.MONTH))
							+","+cal.get(Calendar.DAY_OF_MONTH)+","+cal.get(Calendar.HOUR_OF_DAY)+","+cal.get(Calendar.MINUTE)+","+
							cal.get(Calendar.SECOND)+")";
			gen.writeRawValue(utcDescription);			
		}

	}
}
