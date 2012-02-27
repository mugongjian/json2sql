package mepaul.beansql;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import mepaul.beansql.TableMeta.FieldMeta;

public interface ReadableTableMeta {

	Entry<String, String> getTableName();

	Collection<String> getObjFields();

	FieldMeta getFieldMeta(String objField);

	/**
	 * 
	 * @return db primaryKey
	 */
	List<String> getPrimaryKey();

	String getCondition(String condKey);

	boolean isMixed();

	Iterator<ReadableTableMeta> metaIterator();

	/**
	 * if(fieldType == 'string'){fieldvalue -> 'fieldvalue'}
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	String getQuotedFieldValue(String field, String value);
}
