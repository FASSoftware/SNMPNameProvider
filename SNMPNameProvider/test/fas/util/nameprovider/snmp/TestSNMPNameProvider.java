package fas.util.nameprovider.snmp;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Test;


public class TestSNMPNameProvider {

	@Test
	public void testGetNames() {
		try {
			SNMPNameProvider provider = new SNMPNameProvider();
			Map<String, String> oidToNameMap = provider.getNames(SNMPNameProvider.config(
				list("1.3.6.1.2.1", "1.3.6.1.2.1.2.2.1.4.1", "1.3.6.1.2.1.33.1.1.5", "1.3.6.1.2.1.33.1.4.4.1.3.1"),
				list("http://vpdemo.jaxcontrols.com/snmp/mib/stdupsv1.mib")));
			System.out.println(prettyPrint(oidToNameMap));
		}catch(RuntimeException ex) {
			ex.printStackTrace();
			fail(ex.toString());
		}
	}
	private static String prettyPrint(Object ob){
		return prettyPrint(new StringBuilder(), ob, 0).toString();
	}
	private static StringBuilder prettyPrint(StringBuilder sb, Object ob, int indent) {
		int nextIndent = indent+1;
		if(ob instanceof Map) {
			sb.append("{").append("\n");
			Map<?,?> map = (Map<?,?>)ob;
			for(Map.Entry<?, ?> entry : map.entrySet()) {
				indent(sb, nextIndent);
				prettyPrint(sb, entry.getKey(), nextIndent).append(" => ");
				prettyPrint(sb, entry.getValue(), nextIndent).append("\n");
			}
			indent(sb, indent).append("}");
		}else if(ob instanceof Collection) {
			sb.append("[").append("\n");
			Collection<?> collection = (Collection<?>)ob;
			for(Object item : collection)
				prettyPrint(indent(sb, nextIndent), item, nextIndent).append("\n");
			indent(sb, indent).append("]");
		}else {
			sb.append(ob);
		}
		return sb;
	}
	private static StringBuilder indent(StringBuilder sb, int indent) {
		for(int i = 0; i < indent; i++)
			sb.append("  ");
		return sb;
	}
	
	@SafeVarargs
	private static final <T> List<T> list(T... items){
		return new ArrayList<T>(Arrays.asList(items));
	}
}
