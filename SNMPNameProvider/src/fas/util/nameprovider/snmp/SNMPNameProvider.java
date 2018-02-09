package fas.util.nameprovider.snmp;

import java.io.IOException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;
import net.percederberg.mibble.MibLoaderException;
import net.percederberg.mibble.MibSymbol;

public class SNMPNameProvider {
	/** given a configuration object of form: {objectIds:List<String>[, mibs:List<String>]}
	 * this method will return a mapping of objectIds to their human readable names**/
	public Map<String, String> getNames(Object configOb) {
		Config cfg = config(configOb);
		List<String> objectIds = cfg.objectIds;
		List<String> mibs = cfg.mibs;
		
		MibLoader loader = new MibLoader();
		for(String mib : mibs) {
			try {
				loader.load(new URL(mib));
			} catch (IOException | MibLoaderException ex) {
				throw new RuntimeException(ex);
			}
		}
		Map<String, String> oidToNameMap = new LinkedHashMap<String, String>();
		for(Mib mib : loader.getMibs(true).values()) {
			for(String oid : objectIds) {
				oidToNameMap.put(oid, join(oidNamePath(mib, oid)));
			}
		}
		return oidToNameMap;
	}
	
	private List<String> oidNamePath(Mib mib, String oid){
		String[] parts = oid.split("\\.");
		String partialOid = "";
		List<String> path = new ArrayList<String>(parts.length);
		boolean first = true;
		for(String part : parts) {
			if(first)
				partialOid += part;
			else
				partialOid += "."+part;
			MibSymbol symbol = mib.getSymbolByValue(partialOid);
			String name = (symbol == null? null : symbol.getName());
			path.add(name == null? part : name);
			first = false;
		}
		return path;
	}
	private String join(List<String> parts) {
		StringBuilder sb = new StringBuilder();
		Iterator<String> it = parts.iterator();
		if(it.hasNext()) {
			sb.append(it.next());
			while(it.hasNext())
				sb.append(".").append(it.next());
		}
		return sb.toString();
	}
	
	
	@SuppressWarnings("unchecked")
	public static final Config config(Object config) {
		if(config instanceof Config)
			return (Config)config;
		if(config instanceof Map)
			return config((Map<String, Object>)config);
		if(config instanceof List)
			return config((List<Object>)config);
		throw new InvalidParameterException("configuration must be of the form {objectIds:['x.x.x.x'...], mibs:['<url>'...]}");
	}
	/** create a config from a map of form {objectIds:['x.x.x.x'...], mibs:['<url>'...]}**/
	public static final Config config(Map<String, Object> cfg) {
		if(cfg == null)
			cfg = Collections.emptyMap();
		return config(correctList(cfg.get("objectIds"), String.class), correctList(cfg.get("mibs"), String.class));
	}
	/** create a config from a list of parameters of form [objectIds:['x.x.x.x'...], mibs:['<url>'...]]**/
	public static final Config config(List<Object> cfg) {
		if(cfg == null)
			cfg = Collections.emptyList();
		List<String> objectIds = null;
		List<String> mibs = null;
		for(Object item : cfg) {
			if(objectIds == null && item instanceof List)
				objectIds = correctList(item, String.class);
			else if(mibs == null && item instanceof List)
				mibs = correctList(item, String.class);
		}
		if(objectIds == null)
			objectIds = Collections.emptyList();
		if(mibs == null)
			mibs = Collections.emptyList();
		return config(objectIds, mibs);
	}
	/** create a config from the specified list of object ids and list of mib urls**/
	public static final Config config(List<String> objectIds, List<String> mibs) {
		return new Config(correctList(objectIds, String.class), correctList(mibs, String.class));
	}
	
	/** a configuration object for this NameProvider.  the getName method is expecting either
	 * Map{objectId:String[, mibs:List<String>]} 
	 * or
	 * List{objectid:String[, mibs:List<String>]}**/
	public static final class Config{
		public final List<String> objectIds;
		public final List<String> mibs;
		private Config(List<String> objectIds, List<String> mibs) {
			if(objectIds == null)
				throw new InvalidParameterException("objectId must be a list of Strings, each of form 'x.x...x', where x is an integer");
			if(mibs == null)
				throw new InvalidParameterException("mibs must be a List of Strings");
			this.objectIds = Collections.unmodifiableList(objectIds);
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
