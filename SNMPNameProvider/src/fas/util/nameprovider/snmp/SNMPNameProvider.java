package fas.util.nameprovider.snmp;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SNMPNameProvider {
	public String getName(Object configOb) {
		Config cfg = config(configOb);
		String objectId = cfg.objectId;
		List<String> mibs = cfg.mibs;
		
		// TODO use mibble to parse provided mib files
		// then use result to identify the name for the provided object
		
		throw new RuntimeException("Not implemented");
	}
	@SuppressWarnings("unchecked")
	private static final Config config(Object config) {
		if(config instanceof Config)
			return (Config)config;
		if(config instanceof Map)
			return config((Map<String, Object>)config);
		if(config instanceof List)
			return config((List<Object>)config);
		if(config instanceof String)
			return config((String)config, null);
		throw new InvalidParameterException();
	}
	private static final Config config(Map<String, Object> cfg) {
		if(cfg == null)
			throw new InvalidParameterException("no configuration was specified.  expecting Map{objectId:String[, mibs:List<String>]}");
		Object objectId = cfg.get("objectId");
		if(!(objectId instanceof String))
			throw new InvalidParameterException("objectId must be a String of form 'x.x...x', where x is an integer");
		List<String> mibs = correctList(cfg.get("mibs"), String.class);
		return new Config((String)objectId, mibs);
	}
	private static final Config config(List<Object> cfg) {
		if(cfg == null)
			throw new InvalidParameterException("no configuration was specified.  expecting List{objectId:String[, mibs:List<String>]}");
		if(cfg.size() < 1)
			throw new InvalidParameterException("Incorrect number of parameters.  expected List{objectId:String[, mibs:List<String>]} requires at least one parameter");
		String objectId = null;
		List<String> mibs = null;
		for(Object item : cfg) {
			if(objectId == null && item instanceof String)
				objectId = (String)item;
			else if(mibs == null && item instanceof List)
				mibs = correctList(item, String.class);
		}
		if(objectId == null)
			throw new InvalidParameterException("objectId must be a String of form 'x.x...x', where x is an integer");
		if(mibs == null)
			mibs = Collections.emptyList();
		return new Config(objectId, mibs);
	}
	private static final Config config(String objectId, List<String> mibs) {
		return new Config(objectId, correctList(mibs, String.class));
	}
	
	/** a configuration object for this NameProvider.  the getName method is expecting either
	 * Map{objectId:String[, mibs:List<String>]} 
	 * or
	 * List{objectid:String[, mibs:List<String>]}**/
	private static final class Config{
		public final String objectId;
		public final List<String> mibs;
		private Config(String objectId, List<String> mibs) {
			if(objectId == null)
				throw new InvalidParameterException("objectId must be a String of form 'x.x...x', where x is an integer");
			if(mibs == null)
				throw new InvalidParameterException("mibs must be a List of Strings");
			this.objectId = objectId;
			this.mibs = Collections.unmodifiableList(mibs);
		}
	}
	/** returns a list that is guaranteed to be non-null, and contains only items of the specified class,
	 * essentially allows an unchecked list to be treated as type-checked hence-forward.  the returned list
	 * is a copy of the original, not the same instance.  any items of incorrect type will have been removed.
	 * if a non-list (including null) is passed in, an empty list will be returned.**/
	private static final <T> List<T> correctList(Object l, Class<T> c) {
		if(!(l instanceof List))
			return Collections.emptyList();
		List<?> uncorrected = (List<?>)l;
		List<T> corrected = new ArrayList<T>();
		for(Object item : uncorrected) {
			if(item == null && c == null || item != null && c.isAssignableFrom(item.getClass())) {
				@SuppressWarnings("unchecked")
				T safeToAdd = (T)item;
				corrected.add(safeToAdd);
			}
		}
		return corrected;
	}
}
