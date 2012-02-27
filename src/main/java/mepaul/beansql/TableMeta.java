package mepaul.beansql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mepaul.beansql.meta.struct.SimpleEntry;

public class TableMeta implements RdFullTableMeta
{

	public Map<String,FieldMeta> fieldMetas;
	
	public List<String> primaryKey;
	public final Entry<String, String> tableName;
	/**
	 * complex condition ,is native sql pieces.
	 */
	public Map<String,String> condition;
	public TableMeta(String tableName,String dbName)
	{
		condition = new HashMap<String,String>();
		primaryKey = new ArrayList<String>();
		fieldMetas = new HashMap<String,FieldMeta>();
		this.tableName = new SimpleEntry<String,String>(
				tableName,dbName);
	}
	public TableMeta addFieldMeta(String obj,String db,String type)
	{
		FieldMeta field = new FieldMeta(obj,db,type);
		fieldMetas.put(obj, field);
		return this;
	}
	public String toJson()
	{
		StringBuilder json = new StringBuilder();
		json.append("{")
		
		.append("field:{");
		for(Entry<String, FieldMeta> fieldEntry : fieldMetas.entrySet()){
			FieldMeta field = fieldEntry.getValue();
			json.append(field.obj).append(":['").append(field.db)
				.append("','").append(field.type).append("'],");
		}
		json.deleteCharAt(json.length()-1);
		json.append("},");

		json.append("tableName:{")
			.append(tableName.getKey()).append(":'")
			.append(tableName.getValue())
			.append("'},");
		
		json.append("primaryKey:[");
		for(String key :primaryKey){
			json.append("'").append(key).append("',");
		}
		if(primaryKey.size() >0){
			json.deleteCharAt(json.length()-1);
		}
		json.append("]}");
		return json.toString();
	}
	public boolean equals(Object other)
	{
		if(! (other instanceof TableMeta)){
			return false;
		}
		TableMeta otherMeta = (TableMeta) other;
		return  (this.tableName.getValue()
		.equals(
				otherMeta.tableName.getValue())
				);
		
	}
	public String getQuotedFieldValue(String field,String value){
		String quotedValue  =value;
		if("string".equals(fieldMetas.get(field).type)){
			quotedValue = "'"+value+"'";
		}
		return quotedValue;
	}
	/**
	 *  if string field
	 * @param field
	 * @return
	 */
	public boolean isStringField(String field){
		return ("string".equals(fieldMetas.get(field).type));
	}
	public int hashCode()
	{
		return tableName.getValue().hashCode();
	}
	public Entry<String, String> getTableName()
	{
		return tableName;
	}
		
	
	public String getCondition(String condKey)
	{
		return condition.get(condKey);
	}

	public Collection<String> getObjFields()
	{
		return fieldMetas.keySet();
	}
	public  FieldMeta getFieldMeta(String objField)
	{
		return getFieldMeta(objField,false);
	}
	
	/**
	 * 
	 * @author jpg
	 *
	 */
	public static class FieldMeta
	{
		public String obj;
		public String db;
		public String type;
		public FieldMeta(String obj,String dB,String type)
		{
			this.obj = obj;
			this.db = dB;
			this.type = type;
		}
	}

	public FieldMeta getFieldMeta(String objField, boolean useFullName)
	{
		FieldMeta meta = fieldMetas.get(objField);
		if(useFullName){
			meta.db = tableName.getValue()+"."+meta.db;
		}
		return meta;
	}
	public List<String> getPrimaryKey()
	{
		return primaryKey;
	}
	public boolean isMixed()
	{
		return false;
	}
	public Iterator<ReadableTableMeta> metaIterator()
	{
		return null;
	}
}
