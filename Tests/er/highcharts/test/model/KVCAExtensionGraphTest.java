package er.highcharts.test.model;

import org.junit.Test;

import com.webobjects.foundation.NSArray;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import er.highcharts.model.KVCAExtensionGraph;

public class KVCAExtensionGraphTest {

	@Test
	public void testTakeValueForKey(){
		KVCAExtensionGraph graph = new KVCAExtensionGraph();
		String value = "Norfolk";
		String key = "testkey";
		graph.takeValueForKey(value, key);
		String returnValue = (String) graph.valueForKey(key);
		assertEquals("value for key problems", value, returnValue);
	}
	
	@Test
	public void testTakeValueForKeyPath(){
		KVCAExtensionGraph graph = new KVCAExtensionGraph();
		String value = "Suffolk";
		String key = "testkey.path.to.object";
		graph.takeValueForKeyPath(value, key);
		String returnValue = (String) graph.valueForKeyPath(key);
		assertEquals("value for key path problems", value, returnValue);
	}
	
	@Test
	public void testGraphAdditions(){
		KVCAExtensionGraph graph1 = new KVCAExtensionGraph();
		String value1 = "Gloucestershire";
		String key1 = "testkey.path.to.object";
		graph1.takeValueForKeyPath(value1, key1);

		KVCAExtensionGraph graph2 = new KVCAExtensionGraph();
		String value2 = "Staffordshire";
		String key2 = "testkey.path.to.object";
		graph2.takeValueForKeyPath(value2, key2);

		graph2.apply(graph1);
		
		String returnValue = (String) graph2.valueForKeyPath(key2);
		assertEquals("graph application problems", value1, returnValue);
		
	}
	
	@Test
	public void testGraphNodeRemoval(){
		KVCAExtensionGraph graph = new KVCAExtensionGraph();

		graph.takeValueForKeyPath("Dorset", "a.b.c.d.e");
		graph.takeValueForKeyPath("Devon", "a.b.c.d");

		String returnValue = (String) graph.valueForKeyPath("a.b.c.d.e");
		assertNull("null override problems", returnValue);
		
		String returnValue2 = (String) graph.valueForKeyPath("a.b.c.d");
		assertEquals("replacement value problems", returnValue2, "Devon");
	}
	
	
	@Test
	public void testDefaultGraphNodeType(){
		KVCAExtensionGraph graph = new KVCAExtensionGraph();

		graph.takeValueForKeyPath("Oxfordshire", "a.b.c.d.e");

		Object returnValue = graph.valueForKeyPath("a.b.c");

		assertEquals("class type problems", returnValue.getClass().getName(), KVCAExtensionGraph.class.getName());
	}
	
	@Test
	public void testNonDefaultGraphNodeType(){
		KVCAExtensionGraph graph = new KVCAExtensionGraph();

		graph.takeValueForKeyPath(new NSArray<String>(new String[]{"Surrey", "Kent"}), "a.b.c.d.e");

		Object returnValue = graph.valueForKeyPath("a.b.c.d.e");

		assertEquals("class type problems", returnValue.getClass().getName(), NSArray.class.getName());
	}
	
	
}
