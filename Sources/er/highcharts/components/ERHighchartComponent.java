package er.highcharts.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCodingAdditions;

import er.extensions.components.ERXComponent;
import er.extensions.foundation.ERXProperties;
import er.extensions.foundation.ERXStringUtilities;
import er.highcharts.model.KVCAExtensionGraph;

/**
 * This class acts as the parent class to the main {@code ERHighchart} chart component
 * and all convenience charting components.
 * 
 * @property er.highcharts.exporting.defaultDatePattern
 *           (String) The date format used when Charts call the {@code dateStamp} method
 *           
 * @author matt
 *
 */
public abstract class ERHighchartComponent extends ERXComponent {

	/**
	 * Default serial version id
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Standard Logger
	 */
	private static Logger log = Logger.getLogger(ERHighchartComponent.class);

	/**
	 * Binding for the {@code KVCAExtensionGraph} graph object
	 */
	private static final String GRAPH_BINDING = "graph";

	/**
	 * Binding for the series
	 */
	protected static final String SERIES_BINDING = "series";
	
	/**
	 * Binding for the categories
	 */
	protected static final String CATEGORIES_BINDING = "categories";

	/**
	 * Binding for the mappings
	 */
	protected static final String MAPPINGS_BINDING = "mappings";
	
	/**
	 * Binding for the chart container style
	 */
	protected static final String STYLE_BINDING = "style";

	/**
	 * Binding for the key path mapping of the series name
	 */
	protected static final String SERIES_NAME_KEYPATH_BINDING = "seriesNameKeyPath";

	/**
	 * Binding for the key path mapping of the series value
	 */
	protected static final String SERIES_VALUE_KEYPATH_BINDING = "seriesValueKeyPath";

	/**
	 * Binding for the key path mapping of the series data
	 */
	protected static final String SERIES_DATA_KEYPATH_BINDING = "seriesDataKeyPath";


	/**
	 * Property for the date format used in {@code dateStamp}.
	 */
	private static final String DEFAULT_EXPORT_DATE_FORMAT_PROPERTY = "er.highcharts.exporting.defaultDatePattern";
	
	/**
	 * Date format used when (@code DEFAULT_EXPORT_DATE_FORMAT_PROPERTY} is not set. 
	 */
	private static final String DEFAULT_EXPORT_DATE_FORMAT_DEFAULT = "yyyy-MM-dd-hh-mm";

	
	/**
	 * Standard constructor
	 * @param context
	 */
	public ERHighchartComponent(WOContext context){
		super(context);
	}
	
	/**
	 */
	public boolean synchronizesVariablesWithBindings(){
		return false;
	}
	
	
	public KVCAExtensionGraph graph() {
		KVCAExtensionGraph graph = new KVCAExtensionGraph();

		graph = buildSeries(graph);

		graph.apply(boundGraph());
		
		graph = fromRawBindings(graph);
		
		return graph;
	}
	/**
	 * Convenience binding getter for the {@code KVCAExtensionGraph} bound object
	 * @return
	 */
	public KVCAExtensionGraph boundGraph(){
		return (KVCAExtensionGraph)valueForBinding(GRAPH_BINDING);
	}

	/**
	 * A simple String representing the current date time.
	 * To modify its format, alter the Property
	 * @return 
	 */
	protected String dateStamp(){
		return new DateTime().toString( dateStampFormatter() );
	}

	/**
	 * The formatter used for the {@code dateStamp} value.
	 * @return
	 */
	private DateTimeFormatter dateStampFormatter(){
		return DateTimeFormat.forPattern( dateStampPattern() );
	}

	/**
	 * The pattern used for formatting the {@code dateStamp} value.
	 * @return
	 */
	private String dateStampPattern(){
		return ERXProperties.stringForKeyWithDefault(DEFAULT_EXPORT_DATE_FORMAT_PROPERTY, DEFAULT_EXPORT_DATE_FORMAT_DEFAULT);
	}

	/**
	 * Common series building code.
	 * @param graph
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public KVCAExtensionGraph buildSeries(KVCAExtensionGraph graph){
		// build the series.
		List<NSKeyValueCodingAdditions> objectArray = (List<NSKeyValueCodingAdditions>) valueForBinding(SERIES_BINDING);
		String seriesNameKeyPath = (String)valueForBinding(SERIES_NAME_KEYPATH_BINDING);
		String seriesDataKeyPath = (String)valueForBinding(SERIES_DATA_KEYPATH_BINDING);

		if (objectArray != null) {
			NSDictionary<String, String> mappings = (NSDictionary<String, String>) valueForBinding(MAPPINGS_BINDING);
			// build the series.
			List<Object> seriesList = new ArrayList<Object>();

			for (NSKeyValueCodingAdditions obj : objectArray) {

				KVCAExtensionGraph series = new KVCAExtensionGraph();

				String namePath = "name";
				if (mappings != null && mappings.get("name") != null) {
					namePath = mappings.get("name");
				}else if (!ERXStringUtilities.stringIsNullOrEmpty(seriesNameKeyPath)){
					namePath = seriesNameKeyPath;
				}
				series.takeValueForKeyPath(obj.valueForKeyPath(namePath), "name");

				String dataPath = "data";
				if (mappings != null && mappings.get("data") != null) {
					dataPath = mappings.get("data");
				}else if (!ERXStringUtilities.stringIsNullOrEmpty(seriesDataKeyPath)){
					dataPath = seriesDataKeyPath;
				}
				series.takeValueForKeyPath(obj.valueForKeyPath(dataPath), "data");
				seriesList.add(series);
				

				Object ps = keyPathForBoundName("pointStart", mappings, obj);
				if(ps !=null){
					series.takeValueForKeyPath(ps, "pointStart");					
				}

				Object pi = keyPathForBoundName("pointInterval", mappings, obj);
				if(pi !=null ){
					series.takeValueForKeyPath(pi, "pointInterval");					
				}
	
				Object type = keyPathForBoundName("type", mappings, obj);
				if(type != null){
					series.takeValueForKeyPath(type, "type");					
				}

			}
			graph.takeValueForKeyPath(seriesList, "series");
		}
		return graph;
	}
	
	/**
	 * Safely tries to add values to the graph from either mappings or standard vfkp.
	 * @param name
	 * @param mappings
	 * @param obj
	 * @return
	 */
	private Object keyPathForBoundName(String name, NSDictionary<String, String> mappings, NSKeyValueCodingAdditions obj){
		if(ERXStringUtilities.stringIsNullOrEmpty(name)){
			return null;
		}
		try{
			String namePath = name;
			if (mappings != null && mappings.get(name) != null) {
				namePath = mappings.get(name);
			}
			Object value = obj.valueForKeyPath(namePath);
			return value;
		}catch(Exception e){
			if(log.isDebugEnabled()){
				e.printStackTrace();				
			}
		}
		return null;
	}
	
	/**
	 * Binding for the key path mapping of the series name
	 */
	public String seriesNameKeyPath(){
		return (String)valueForBinding(SERIES_NAME_KEYPATH_BINDING);
	}
	
	/**
	 * Binding for the key path mapping of the series data
	 */
	public String seriesDataKeyPath() {
		return (String)valueForBinding(SERIES_DATA_KEYPATH_BINDING);
	}
	

	/**
	 * Looks for raw Highcharts JS bindings and applies them.
	 * @param graph
	 * @return
	 */
	public KVCAExtensionGraph fromRawBindings(KVCAExtensionGraph graph){
		NSArray<String> allKeys = this.bindingKeys();
		for (int i=0; i<allKeys.count(); i++){
			String key = allKeys.objectAtIndex(i);
			if(!ERXStringUtilities.stringIsNullOrEmpty(key) && key.indexOf(".")>0){
				Object value = this.valueForBinding(key);
				System.out.println("key ["+i+"] ["+key+"] ["+this.valueForBinding(key)+"]");
				graph.takeValueForKeyPath(value, key);
			}
		}
		return graph;
	}

		
}
