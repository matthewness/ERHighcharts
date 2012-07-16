package er.highcharts.control.serializers;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.joda.time.DateTime;

/**
 * The global Serializer for DateTime objects.
 * 
 * Translates the date values into a JavaScript <code>Date.UTC</code> call.
 * 
 * @author matt
 *
 */
public class ERJodaDateTimeSerializer extends SerializerBase<DateTime> {

	public ERJodaDateTimeSerializer(){
		super(DateTime.class, true);
	}
	@Override
	public void serialize(DateTime dateTime, JsonGenerator gen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		if(dateTime==null){
			gen.writeNull();
		}else{
			String utcDescription = 
					"Date.UTC("+dateTime.getYear()+","+(dateTime.getMonthOfYear()-1)
							+","+dateTime.getDayOfMonth()+","+dateTime.getHourOfDay()+","+dateTime.getMinuteOfHour()+","+
							dateTime.getSecondOfMinute()+")";
			gen.writeRawValue(utcDescription);
			
		}

	}
}
