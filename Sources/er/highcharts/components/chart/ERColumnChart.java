package er.highcharts.components.chart;

import java.util.List;

import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOCustomObject;

import er.highcharts.components.ERHighchartComponent;
import er.highcharts.model.KVCAExtensionGraph;

/**
 * Convenience component to generate column charts.
 * See the Documentation for examples on use.
 * 
 * @binding series (List<NSKeyValueCodingAdditions>) a collection of data you wish to be expressed in the Highcharts <pre>series</pre> JSON output.
 *          This can be any collection of objects which adhere to the {@code NSKeyValueCodingAdditions} interface, such as an NSArray from an EO 
 *          to-many relationship, a List of EOCustomObject objects, or a collection of {@code KVCAExtensionGraph} objects, for example.
 * @binding mappings (NSDictionary<String, String>) any key mappings to ensure the "name" and "data" maps correctly in the graph.
 * @categories (List<EOCustomObject>) a collection of values to display on the x-axis of the chart.
 * @binding graph (KVCAExtensionGraph) An extension graph object with any extra option values to apply to the chart.
 * 
 * @author matt
 *
 */
public class ERColumnChart extends ERHighchartComponent {

	/**
	 * Default serial version id
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Standard constructor
	 * @param context
	 */
	public ERColumnChart(WOContext context) {
        super(context);
    }
	
	/**
	 * The graph. The "chart.type" is explicitly set to "column".
	 * Attempts to build the "column" section of the graph by applying the bound
	 * "series" objects and any "mappings" if present. Categories are built if
	 * bound. Finally, it applies the bound "graph" object to it, to ensure the calling 
	 * component can override any Highcharts options.
	 * 
	 * @return a {@code KVCAExtensionGraph} object representing the Column section of a Highcharts graph.
	 */
	@SuppressWarnings("unchecked")
	public KVCAExtensionGraph graph() {
		KVCAExtensionGraph graph = new KVCAExtensionGraph();

		// set the defaults, and allow the binding to override
		graph.takeValueForKeyPath("column", "chart.type");

		graph = buildSeries(graph);

		//any categories?
		List<EOCustomObject> categories = (List<EOCustomObject>)valueForBinding(CATEGORIES_BINDING);
		if(categories != null){
			graph.takeValueForKeyPath(categories, "xAxis.categories");
		}

		// apply the bound graph last, so it overrides any previously set
		// content.
		graph.apply(boundGraph());

		return graph;
	}
}