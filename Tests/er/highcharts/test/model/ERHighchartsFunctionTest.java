package er.highcharts.test.model;

import org.junit.Test;

import er.highcharts.model.ERHighchartsFunction;
import static org.junit.Assert.assertEquals;


public class ERHighchartsFunctionTest {

	private final String FUNCTION_BODY = "function";

	
	@Test
	public void testFunctionBodyCreation(){
		ERHighchartsFunction newFunction = new ERHighchartsFunction(FUNCTION_BODY);
		assertEquals(FUNCTION_BODY, newFunction.body);
	}
	
	
}
