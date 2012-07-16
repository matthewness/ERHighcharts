package er.highcharts.components.chart;

import com.webobjects.appserver.WOContext;

import er.highcharts.components.ERHighchartComponent;
import er.highcharts.model.KVCAExtensionGraph;

/**
 * Convenience component to generate scatter plot charts.
 * See the Documentation for examples on use.
 * 
 * @binding series (List<NSKeyValueCodingAdditions>) a collection of data you wish to be expressed in the Highcharts <pre>series</pre> JSON output.
 *          This can be any collection of objects which adhere to the {@code NSKeyValueCodingAdditions} interface, such as an NSArray from an EO 
 *          to-many relationship, a List of EOCustomObject objects, or a collection of {@code KVCAExtensionGraph} objects, for example.
 * @binding mappings (NSDictionary<String, String>) any key mappings to ensure the "name", "y", and "data" maps correctly in the graph.
 * @binding graph (KVCAExtensionGraph) An extension graph object with any extra option values to apply to the chart.
 * 
 * @author matt
 *
 */
public class ERScatterChart extends ERHighchartComponent {

	/**
	 * Default serial version id
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Standard constructor
	 * @param context
	 */
	public ERScatterChart(WOContext context) {
		super(context);
	}

	/**
	 * The graph. The "chart.type" is explicitly set to "scatter".
	 * Attempts to build the "line" section of the graph by applying the bound
	 * "series" objects and any "mappings" if present.
	 * Finally, it applies the bound "graph" object to it, to ensure the calling 
	 * component can override any Highcharts options.
	 * 
	 * @return a {@code KVCAExtensionGraph} object representing the Scatter Plot section of a highcharts graph.
	 */
	public KVCAExtensionGraph graph() {
		KVCAExtensionGraph graph = new KVCAExtensionGraph();

		// set the defaults, and allow the binding to override
		graph.takeValueForKeyPath("scatter", "chart.type");
		graph.takeValueForKeyPath("xy", "chart.zoomType");

		graph = buildSeries(graph);

		// apply the bound graph last, so it overrides any previously set
		// content.
		graph.apply(boundGraph());

		return graph;
	}
}