package er.highcharts.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonRawValue;

import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSKeyValueCodingAdditions;


/**
 * KVCAExtensionGraph extends the KVCA pattern to allow arbitrary key paths to be mapped. 
 * 
 * You may nominate any key path you wish to set an object on (including another KVCAExtensionGraph object if you want) 
 * 
 * @author matt
 * 
 */
public class KVCAExtensionGraph implements NSKeyValueCoding, NSKeyValueCodingAdditions {

	/**
	 * Standard Logger
	 */
	private static Logger log = Logger.getLogger(KVCAExtensionGraph.class);
	
	/**
	 * The nodes. We tell Jackson to ignore this property,
	 * as the @JsonUnwrapped annotation doesn't play with Map objects.
	 * Instead, we use the @JsonAnyGetter annotation in a separate object
	 * in this class: {@code #erHighchartsAnyGetterPolicy}
	 * 
	 */
	@JsonIgnore
	public Map<String, Object> nodes = new HashMap<String, Object>();

	/**
	 * The solution to the @JsonUnwrapped issues with Maps.
	 * 
	 * This method is called <code>erHighchartsAnyGetterPolicy</code> as it's unlikely
	 * a user or process will be setting objects on this object with that key.
	 * 
	 * @return
	 */
	@JsonAnyGetter
	@JsonRawValue
	public Map<String, Object> erHighchartsAnyGetterPolicy(){
		return nodes;
	}
	
	/**
	 * Constructor
	 */
	public KVCAExtensionGraph(){
		super();
	}
	
	@Override
	public void takeValueForKeyPath(Object value, String keyPath) {
		log.debug("KVCAExtension.takeValueForKeyPath: keyPath ["+keyPath+"]");
		if (keyPath == null) {
			return;
		}

		if(keyPath.contains(NSKeyValueCodingAdditions.KeyPathSeparator)){
			log.debug("KVCAExtension.takeValueForKeyPath: keyPath has a dotta!");
			String key = keyPath.substring(0, keyPath.indexOf(NSKeyValueCodingAdditions.KeyPathSeparator));
			
			log.debug("Key ["+key+"]");
			
			String nextPath = keyPath.substring( key.length() + NSKeyValueCodingAdditions.KeyPathSeparator.length() );
			log.debug("Next Path ["+nextPath+"]");
			
			Object localObject = nodes.get(key);
			
			if(localObject==null){
				log.debug("KVCAExtension.takeValueForKeyPath: no local object for key ["+key+"], creating an extension");

				localObject = new KVCAExtensionGraph();
				
			}
			
			if(localObject instanceof KVCAExtensionGraph//needed?
					|| NSKeyValueCodingAdditions.class.isAssignableFrom(localObject.getClass())
					){

				log.debug("KVCAExtension.takeValueForKeyPath: local object is of type ["+localObject.getClass().getName()+"]");
				log.debug("KVCAExtension.takeValueForKeyPath: calling next path on local object ["+nextPath+"]");

				((NSKeyValueCodingAdditions)localObject).takeValueForKeyPath(value, nextPath);
				
			}
			
			nodes.put(key, localObject);
			//nodes.setObjectForKey(localObject, key);
			
			
		}else{
			log.debug("KVCAExtension.takeValueForKeyPath: no dotta! in key path, calling tvfk instead ["+keyPath+"]");
			this.takeValueForKey(value, keyPath);//there is no next path, so try to store locally.
		}
	
	}

	@Override
	public Object valueForKeyPath(String keyPath) {
		log.debug("KVCAExtension.valueForKeyPath: keyPath = " + keyPath);
		if (keyPath == null) {
			return null;
		}
		if(keyPath.contains(NSKeyValueCodingAdditions.KeyPathSeparator)){
			String key = keyPath.substring(0, keyPath.indexOf(NSKeyValueCodingAdditions.KeyPathSeparator));
			
			log.debug("Key ["+key+"]");
			
			String nextPath = keyPath.substring(  key.length() + NSKeyValueCodingAdditions.KeyPathSeparator.length() );
			log.debug("Next Path ["+nextPath+"]");
			
			Object localObject = nodes.get(key);
			
			
			Object returnValue = null;
			
			if(localObject==null){
				
				log.debug("tried to find object for key ["+key+"] but came up trumps");
				
			}else{

				if(localObject instanceof KVCAExtensionGraph){//the object is an extension stack
					
					returnValue = ((KVCAExtensionGraph)localObject).valueForKeyPath(nextPath);
					
				}else if (localObject instanceof List){
					
					
				}else if( NSKeyValueCoding.class.isAssignableFrom(Object.class) ){//the object implements NSKVCA
					returnValue = ((KVCAExtensionGraph)localObject).valueForKeyPath(nextPath);
					
				}else{
					log.debug("the object is of type ["+localObject.getClass().getName()+"] but we've still got a next key path of ["+nextPath+"] returning null");	
					returnValue = null;
				}
				
			}
			log.debug("returning ret value of ["+returnValue+"]");
			
			return returnValue;
		}else{
			return valueForKey(keyPath);//there is no next path, so try to get what in the local storage.
		}
	

	}

	@Override
	public void takeValueForKey(Object value, String key) {
		nodes.put(key, value);
	}

	@Override
	public Object valueForKey(String key) {
		return nodes.get(key);
	}


	/**
	 * Apply all values of the argument to this.
	 * 
	 * @param supp - the supplementary Graph to apply
	 */
	public void apply(KVCAExtensionGraph supp) {
		if(supp!=null){
			//this.nodes.putAll(supp.nodes);//for now we just do a put all, but it really should be smarter than that.
			
			Set<String> keys = supp.nodes.keySet();
			for (String key : keys){
				this.takeValueForKeyPath(supp.valueForKeyPath(key), key);
			}
			
		}
	}

}
