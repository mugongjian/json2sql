package mepaul.beansql;

import mepaul.beansql.TableMeta.FieldMeta;

public interface RdFullTableMeta extends ReadableTableMeta
{
	
	FieldMeta getFieldMeta(String objField,boolean useFullName);
	
}
