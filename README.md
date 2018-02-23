# SNMPNameProvider
A simple Java api for getting the corresponding names for a list of SNMP oids, and mib file urls.  Uses mibble to load and explore mib files.

How to install:

download SNMPNameProvider.jar and add it to your java class path
or 'git clone' it and include the source in your class path

Usage:
```java
SNMPNameProvider nameProvider = new SNMPNameProvider();

// list of oids that we would like unique names for
List<String> oids = Arrays.asList(new String[]{
  "1.3.6.1.2.1",
  "1.3.6.1.2.1.0",
  "1.3.6.1.2.1.1"
});

// list of mib file urls
List<String> mibs = Arrays.asList(new String[]{
  "http://mib-provider-host.com/mibfile.mib",
  "file://C:/mib-file-folder/mibfile.mib"
});

// create a name provider config containing the necessary information to request the names
SNMPNameProvider.Config nameProviderConfig = new SNMPNameProvider.Config(oids, mibs);

// results will be returned in a Map where the oids are keys, and the names are the cooresponding values
Map<String, String> oidToNameMap = nameProvider.getNames(nameProviderConfig);

// example of printing the names for each oid
for(Map.Entry<String, String> entry : oidToNameMap.entrySet())
  System.out.println(entry.getKey() +": '"+ entry.getValue()+"'");

```
naming generally has the following pattern:

given the oid "1.3.6.1.2.1" the name becomes "1.3.6.internet.mgmt.mib-2'
