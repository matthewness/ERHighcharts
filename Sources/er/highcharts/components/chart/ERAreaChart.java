package er.highcharts.components.chart;

import com.webobjects.appserver.WOContext;

import er.highcharts.components.ERHighchartComponent;
import er.highcharts.model.KVCAExtensionGraph;

/**
 * Convenience component to generate area charts.
 * See the Documentation for examples on use.
 * 
 * @binding series (List<NSKeyValueCodingAdditions>) a collection of data you wish to be expressed in the Highcharts <pre>series</pre> JSON output.
 *          This can be any collection of objects which adhere to the {@code NSKeyValueCodingAdditions} interface, such as an NSArray from an EO 
 *          to-many relationship, a List of EOCustomObject objects, or a collection of {@code KVCAExtensionGraph} objects, for example.
 * @binding mappings (NSDictionary<String, String>) any key mappings to ensure the "name" and "data" maps correctly in the graph.
 * @binding graph (KVCAExtensionGraph) An extension graph object with any extra option values to apply to the chart.
 * 
 * @author matt
 *
 */
public class ERAreaChart extends ERHighchartComponent {

	/**
	 * Default serial version id
	 */
	private static final long serialVersionUID = 1L;
		
	/**
	 * Standard constructor
	 * @param context
	 */
	public ERAreaChart(WOContext context) {
        super(context);
    }

	/**
	 * The graph. The "chart.type" is explicitly set to "area".
	 * Attempts to build the "area" section of the graph by applying the bound
	 * "series" objects and any "mappings" if present. Finally, it applies
	 * the bound "graph" object to it, to ensure the calling component can
	 * override any Highcharts options.
	 * 
	 * @return a {@code KVCAExtensionGraph} object representing the Area section of a highcharts graph.
	 */
	public KVCAExtensionGraph graph() {
		KVCAExtensionGraph graph = new KVCAExtensionGraph();

		// set the defaults, and allow the binding to override
		graph.takeValueForKeyPath("area", "chart.type");

		graph = buildSeries(graph);

		// apply the bound graph last, so it overrides any previously set content.
		graph.apply(boundGraph());

		return graph;
	}

}