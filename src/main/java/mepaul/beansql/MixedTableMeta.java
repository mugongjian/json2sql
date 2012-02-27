package mepaul.beansql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mepaul.beansql.TableMeta.FieldMeta;
import mepaul.beansql.meta.struct.SimpleEntry;
/**
 * List of TableMeta
 * @author jpg
 *
 */
public class MixedTableMeta implements RdFullTableMeta{
	private List<MixedElement> elements = new ArrayList<MixedElement>();
	
	public String toJson()
	{
		return null;
	}
	/**
	 * 
	 * @param meta
	 * @param viewField  
	 * @param otherFieldIsDefault  if ViewField not defined,what to do other field
	 * @param howtoLink
	 * @return
	 */
	public MixedTableMeta ref(
			RdFullTableMeta meta,
			Map<String,String> viewField,
			boolean otherFieldIsDefault,
			List<String> howtoLink)
	{
		elements.add(
				new MixedElement(
				meta, viewField, 
				otherFieldIsDefault, howtoLink)
		);
		
		return this;
	}
	public MixedTableMeta unref(RdFullTableMeta meta)
	{
		for(MixedElement element : elements){
			if(element.meta.equals(meta)){
				elements.remove(element);
			}
		}
		return this;
	}
	/**
	 * wrapper for TableMeta for which in MixedTableMeta
	 * @author jpg
	 *
	 */
	private class MixedElement implements RdFullTableMeta
	{
		private RdFullTableMeta meta;
		/**
		 * defined view field,value references meta.key
		 */
		private Map<String,String> viewFields;
		/**
		 * codition on how to link(join) other table
		 */
		private List<String> howtoLink;
		/**
		 *  if true other field used
		 */
		private boolean otherUsed;
		public MixedElement(
				RdFullTableMeta meta,
				Map<String,String> viewFields,
				boolean otherUsed,
				List<String> howtoLink)
		{
			this.meta = meta;
			this.viewFields = (viewFields==null)?
					new HashMap<String,String>():viewFields;
			this.otherUsed = otherUsed;
			this.howtoLink = (howtoLink == null)?
					new ArrayList<String>():howtoLink;
		}
		public Entry<String, String> getTableName()
		{
			return meta.getTableName();
		}

		public List<String> getObjFields()
		{
			ArrayList<String> objFields = new ArrayList<String>();
			
			objFields.addAll(viewFields.keySet());
			
			if(otherUsed){
				Collection<String> notInList = viewFields.values();
				Collection<String> fields = meta.getObjFields();
				for(String field : fields){
					if(notInList.contains(field) ){
						continue;
					}
					objFields.add(field);
				}
			}
			return objFields;
			
		}
		
		public String getCondition(String condKey)
		{
			return meta.getCondition(condKey);
		}
		public FieldMeta getFieldMeta(String objField)
		{
			return getFieldMeta(objField, true);
		}
		public FieldMeta getFieldMeta(String objField, boolean useFullName)
		{
			String metaKey = viewFields.get(objField);
			if(metaKey == null ){
				if(otherUsed){
					return meta.getFieldMeta(objField,true);
				}else{
					return null;
				}
			}
			return meta.getFieldMeta(metaKey,true);
		}
		public List<String> getPrimaryKey()
		{
			return meta.getPrimaryKey();
		}
		public boolean isMixed()
		{
			return meta.isMixed();
		}
		public Iterator metaIterator()
		{
			return null;
		}
		public String getQuotedFieldValue(String field, String value) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	public Entry<String,String> getTableName()
	{
		StringBuilder value = new StringBuilder();
		StringBuilder key = new StringBuilder();
		for(MixedElement el: elements){
			Entry<String,String> tableNameMeta = el.meta.getTableName();
			key.append(tableNameMeta.getKey()).append(",");
			value.append(tableNameMeta.getValue()).append(",");
		}
		value.deleteCharAt(value.length()-1);
		key.deleteCharAt(key.length()-1);
		
		return ( new SimpleEntry<String,String>(
					key.toString(),
					value.toString())
				);
	}

	public List<String> getObjFields()
	{
		List<String> mixedField = new ArrayList<String>();
		for(MixedElement e : elements){
			mixedField.addAll(e.getObjFields());
		}
		return mixedField;
	}
	public List<String> getPrimaryKey()
	{
		List<String> pks = new ArrayList<String>();
		for(MixedElement e: elements){
			pks.addAll(e.getPrimaryKey());
		}
		return pks;
	}
	public String getCondition(String condKey)
	{
		String cond = null;
		for(MixedElement e: elements){
			cond = e.getCondition(condKey);
			if(cond != null){break;}
		}
		return cond;
	}
	/**
	 * !!! objField must be uniqued
	 */
	public FieldMeta getFieldMeta(String objField)
	{
		return getFieldMeta(objField, true);
	}
	public FieldMeta getFieldMeta(String objField, boolean useFullName)
	{
		FieldMeta field = null;
		for(MixedElement e: elements){
			field = e.getFieldMeta(objField,useFullName);
			if(field != null){break;}
		}
		return field;
	}
	public boolean isMixed()
	{
		return true;
	}
	
	public Iterator metaIterator()
	{
		return elements.iterator();
	}
	public boolean equals(Object other)
	{
		if(!(other instanceof MixedTableMeta)){
			return false;
		}
		MixedTableMeta otherMixed = (MixedTableMeta) other;
		if(this.elements.size() != otherMixed.elements.size()){
			return false;
		}
		for(int i=0; i<this.elements.size(); i++){
			boolean ele_equal = this.elements.get(i).equals(
					otherMixed.elements.get(i));
			if(!ele_equal){
				return false;
			}
		}
		return true;
	}
	public int hashCode()
	{
		StringBuilder tableNames = new StringBuilder();
		for(MixedElement e : elements){
			String tableName = e.meta.getTableName().getValue();
			tableNames.append(tableName);
		}
		return tableNames.toString().hashCode();
	}
	public String getQuotedFieldValue(String field, String value) {
		// TODO Auto-generated method stub
		return null;
	}
}
