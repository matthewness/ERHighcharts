package er.highcharts.components.chart;

import java.util.ArrayList;
import java.util.List;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCodingAdditions;

import er.extensions.foundation.ERXStringUtilities;
import er.highcharts.components.ERHighchartComponent;
import er.highcharts.model.KVCAExtensionGraph;

/**
 * Convenience component to generate pie charts.
 * See the Documentation for examples on use.
 * 
 * @binding series (List<NSKeyValueCodingAdditions>) a collection of data you wish to be expressed in the Highcharts <pre>series</pre> JSON output.
 *          This can be any collection of objects which adhere to the {@code NSKeyValueCodingAdditions} interface, such as an NSArray from an EO 
 *          to-many relationship, a List of EOCustomObject objects, or a collection of {@code KVCAExtensionGraph} objects, for example.
 * @binding mappings (NSDictionary<String, String>) any key mappings to ensure the "name", "y", and "data" maps correctly in the graph.
 * @binding wedgeNameKeyPath (String) Alternatively, specify the keyPath <i>from</i> the series objects to call each wedge.
 * @binding wedgeValueKeyPath (String) Alternatively, specify the keyPath <i>from</i> the series objects for each wedge's value.
 * @binding graph (KVCAExtensionGraph) An extension graph object with any extra option values to apply to the chart.
 * 
 * @author matt
 *
 */
public class ERPieChart extends ERHighchartComponent {

	/**
	 * Default serial version id
	 */
	private static final long serialVersionUID = 1L;
	
	
	

	/**
	 * Standard constructor
	 * @param context
	 */
	public ERPieChart(WOContext context) {
        super(context);
    }

	/**
	 * The graph. The "chart.type" is explicitly set to "pie".
	 * Attempts to build the "pie" section of the graph by applying the bound
	 * "series" objects and any "mappings" if present.
	 * Finally, it applies the bound "graph" object to it, to ensure the calling 
	 * component can override any Highcharts options.
	 * 
	 * @return a {@code KVCAExtensionGraph} object representing the Pie section of a highcharts graph.
	 */
	@SuppressWarnings("unchecked")
	public KVCAExtensionGraph graph() {
		KVCAExtensionGraph graph = new KVCAExtensionGraph();
		
		//set the defaults, and allow the binding to override
		graph.takeValueForKeyPath("pie", "chart.type");
		graph.takeValueForKeyPath(true, "plotOptions.pie.allowPointSelect");

		//series.
		List<NSKeyValueCodingAdditions> objectArray = (List<NSKeyValueCodingAdditions>) valueForBinding(SERIES_BINDING);
		if(objectArray!=null){
			
			NSDictionary<String, String> mappings = (NSDictionary<String, String>)valueForBinding(MAPPINGS_BINDING);
			String wedgeNameKeyPath = (String)valueForBinding(SERIES_NAME_KEYPATH_BINDING);
			String wedgeValueKeyPath = (String)valueForBinding(SERIES_VALUE_KEYPATH_BINDING);
			
			//build the series.
			List<Object> seriesList = new ArrayList<Object>();
			
			KVCAExtensionGraph series = new KVCAExtensionGraph();
			series.takeValueForKeyPath("pie", "type");
			
			List<Object> dataList = new ArrayList<Object>();
			for (NSKeyValueCodingAdditions obj : objectArray){
				KVCAExtensionGraph data = new KVCAExtensionGraph();
				
				String namePath = "name";
				if(mappings!=null && mappings.get("name")!=null){
					namePath = mappings.get("name");
				}else if(!ERXStringUtilities.stringIsNullOrEmpty(wedgeNameKeyPath)){
					namePath = wedgeNameKeyPath;
				}
				data.takeValueForKeyPath(obj.valueForKeyPath(namePath), "name");
				
				String yPath = "y";
				if(mappings!=null && mappings.get("y")!=null){
					yPath = mappings.get("y");
				}else if(!ERXStringUtilities.stringIsNullOrEmpty(wedgeValueKeyPath)){
					yPath = wedgeValueKeyPath;
				}
				data.takeValueForKeyPath(obj.valueForKeyPath(yPath), "y");
				
				dataList.add(data);
				
			}
			series.takeValueForKeyPath(dataList, "data");
			seriesList.add(series);
			
			
			graph.takeValueForKeyPath(seriesList, "series");
		}

		//apply the bound graph last, so it overrides any previously set content.
		graph.apply(boundGraph());
		
		return graph;
	}
}