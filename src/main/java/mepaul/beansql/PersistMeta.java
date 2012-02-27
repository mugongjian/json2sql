package mepaul.beansql;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PersistMeta {
	public TableMeta getTable(String tableName)
	{
		return metaMap.get(tableName);
	}
	
	public String getDefaultDbType()
	{
		return defaultDbType;
	}
	public void setDefaultDbType(String defaultDbType)
	{
		this.defaultDbType = defaultDbType;
	}
	public long getVersion()
	{
		return version;
	}
	public void setVersion(long version)
	{
		this.version = version;
	}
	public String toString(){
		return "tables:\n"+metaMap.keySet().toString();
	}
	public PersistMeta(Map<String,TableMeta> metaMap)
	{
		this.metaMap = 	Collections.unmodifiableMap(metaMap);
	}
	public PersistMeta(){
			metaMap = new HashMap<String,TableMeta>();
	}
	private  final Map<String,TableMeta> metaMap ;
	private  String defaultDbType;
	private  long version;
	
}
