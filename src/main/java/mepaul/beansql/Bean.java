package mepaul.beansql;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import mepaul.beansql.TableMeta.FieldMeta;

import com.google.gson.Gson;

public class Bean implements Jsonable<Bean> {
	private BeanState state;
	private SetBeanCondition condition;

	private Map<String, String> data ;
	
	public static final String DB_WHERE_LABEL = "__cond__";
	public static final String DB_FIELDS_LABEL = "__fields__";
	public static final String PAGE_PLACE_HOLDER = ",0";
	public static final String TABLE_NAME_LABEL ="__table__";
	public static final String ITEM_STATE_FIELD_LABEL ="__state__";
	public static final String DATA_LABEL  = "__data__";
	private String  tableName;
	private TableMeta meta;
	public Bean(BeanState state, String  tableName) {
		this.state = state;
		this.tableName = tableName;
		condition = new SetBeanCondition();
		PersistMeta pm = AppShare.$().persistMeta;
		meta = pm.getTable(tableName);
		data = new HashMap<String,String>();		
	}
	/* use for simple bean*/
	public Bean() {
		data = new HashMap<String,String>();
	}

	public String tableName() {
		return tableName;
	}
	
	private String addQuoteIf(Entry<String,String> entry){
		String type = meta.fieldMetas.get(entry.getKey()).type;
		String value = entry.getValue();
		if("string".equals(type)){
			value = "'"+value+"'";
		}
		return value;
	}
	public String toJson() {
		
		StringBuilder json = new StringBuilder();
		json.append("{");
		json.append(getJson(data));
		json.append("}");
		return json.toString();
	}
	public static String getJson(Map<String,String> data){
		StringBuilder json = new StringBuilder();

		if (data.size() > 0) {
			for (Entry<String, String> entry : data.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				json.append('"').append(key).append('"').append(":\"").append(value).append("\",");
			}
			json.deleteCharAt(json.length() - 1);
		}
		return 	json.toString();
	}
	public String get(String field) {
		return data.get(field);
	}

	public String set(String field, String value) {
		return data.put(field, value);
	}
	public String remove(String field){
		return data.remove(field);
	}
	public int size() {
		return data.size();
	}

	public static enum BeanState {
		Query, Update, Add, Remove, PagedQuery,Count			

	}
	/*
	public RowMapper getRowMapper(){
		return new RowMapper() {
			
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				String fieldString = data.get(DB_FIELDS_LABEL);
				String[] queryFields;
				if(fieldString == null){
					Collection<String> fields = meta.getObjFields();
					queryFields = new String[fields.size()];
					fields.toArray(queryFields);
				}else{
					queryFields = fieldString.split(",",-1);
				}
				Bean b = new Bean();
				for(String f : queryFields) {
					String dbField = meta.getFieldMeta(f).db;
					String value = rs.getString(dbField);
					b.set(f,value);
				}
				return b;
			}
		};
	}
*/
	public String getSql(){
		switch(state){
		case Update:
			return updateSql();
		case Add:
			return insertSql();
		case Remove:
			return deleteSql();
		case Query:
			return querySql();
		case PagedQuery:{
			StringBuilder keys = new StringBuilder();
			List<String> primaryKeys = meta.getPrimaryKey();
			if(primaryKeys.size()>0){
				for (String k : primaryKeys) {
					FieldMeta fm = meta.getFieldMeta(k);
					String dbFiled = fm.db;
					keys.append(dbFiled).append(",");
				}
				keys.deleteCharAt(keys.length() - 1);
			}
			int start = Integer.parseInt(data.get("start"));
			int limit = Integer.parseInt(data.get("limit"));
			return PageSql.pagedQuerySql(querySql(),start,limit,keys.toString());
		}
			
		case Count:
			return countSql();
		}
		return null;
	}

		/**
		 * 
		 * @param data
		 *            {field:value,...}
		 * @return "insert into {T} (dbField,dbField) values(value1,value2)"
		 */
		
		private String insertSql() {
			StringBuilder insertSql = new StringBuilder();
			insertSql.append("INSERT INTO ").append(
					meta.getTableName().getValue());
			StringBuilder fields_str = new StringBuilder();
			StringBuilder values_str = new StringBuilder();
			for(Entry<String,String> e: data.entrySet()){
				FieldMeta fm = meta.getFieldMeta(e.getKey());
				if(fm == null){
					throw new RuntimeException("unkown field :-( "+meta.tableName+"."+e.getKey());
				}
				String dbField = fm.db;
				fields_str.append(dbField).append(',');
				values_str.append(addQuoteIf(e)).append(',');
			}
			if(data.size() > 0){
				fields_str.deleteCharAt(fields_str.length() - 1);
				values_str.deleteCharAt(values_str.length() - 1);
			}

			insertSql.append(" (").append(fields_str).append(')');
			insertSql.append(" VALUES (").append(values_str).append(')');
			return insertSql.toString();
		}

		/**
		 * 
		 * @param data
		 *            for updated
		 * @param condition
		 * @return
		 */
		private String updateSql() {

			StringBuilder updateSql = new StringBuilder();
			updateSql.append("UPDATE ").append(meta.getTableName().getValue());
			StringBuilder assign = new StringBuilder();
			for (Entry<String, String> entry : data.entrySet()) {
				String field = entry.getKey();
				String dbField = meta.getFieldMeta(field).db;
				String value = addQuoteIf(entry);
				assign.append(dbField).append("=").append(value).append(",");
			}
			assign.deleteCharAt(assign.length() - 1);

			updateSql.append(" SET ").append(assign);

			String where = where();
			updateSql.append(where);
			return updateSql.toString();
		}
	
	private String deleteSql() {
			StringBuilder sql = new StringBuilder();
			sql.append("DELETE FROM ").append(meta.getTableName().getValue());

			String whereInfo = where();
			sql.append(whereInfo);
			return sql.toString();
		}
		private String countSql(){
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT COUNT(*) AS RESULT FROM ").append(meta.getTableName().getValue());
			String whereInfo = where();
			sql.append(whereInfo);
			return sql.toString();
		}

		/**
		 * 
		 * @param metainfo
		 *            query what
		 * @param cond
		 *            condition
		 * @return
		 */
		private String querySql() {
			StringBuilder sql = new StringBuilder();

			String needFieldStr = data.get(DB_FIELDS_LABEL);

			String[] needFields = null;
			if (needFieldStr != null) {
				needFields = needFieldStr.split(",", -1);
			}
			String dbFields = field(needFields, true);
			sql.append("SELECT ").append(dbFields).append(PAGE_PLACE_HOLDER)
					.append(" FROM ").append(meta.getTableName().getValue());

			String where = where();

			sql.append(where);

			String sortFieldStr = data.get("sort");
			if (sortFieldStr == null) {
				return sql.toString();
			}
			String[] sortFields = null;
			sortFields = sortFieldStr.split(",", -1);
			String sortDirection = data.get("dir");

			String order = order(sortFields);
			sql.append(order);

			String dir = (sortDirection == null) ? "ASC" : sortDirection;
			sql.append(" ").append(dir);

			return sql.toString();
		}

		private String order(String[] sortFields) {
			StringBuilder orderSql = new StringBuilder();
			orderSql.append(" ORDER BY ");
			for (String field : sortFields) {
				String dbField = meta.getFieldMeta(field).db;
				orderSql.append(dbField).append(",");
			}
			orderSql.deleteCharAt(orderSql.length() - 1);
			return orderSql.toString();
		}

		/**
		 * get select sql field
		 * 
		 * @param needFields
		 * @param usePreferField
		 *            true will use prefered field (like :"count(*) as result")
		 * @return
		 */
		private String field(String[] needFields, boolean usePreferField) {

			if (needFields == null) {
				Collection<String> fields = meta.getObjFields();
				StringBuilder fieldStr = new StringBuilder();
				for (String field : fields) {
					fieldStr.append(meta.getFieldMeta(field).db).append(",");
				}
				fieldStr.deleteCharAt(fieldStr.length() - 1);
				return fieldStr.toString();
			}

			StringBuilder fields = new StringBuilder();
			for (String objField : needFields) {
				FieldMeta field = meta.getFieldMeta(objField);
				// for defined specific db field ,
				if (field == null && !usePreferField) {
					continue;
				}
				String sqlField = (field == null) ? objField : field.db;
				fields.append(sqlField).append(",");
			}
			fields.deleteCharAt(fields.length() - 1);
			return fields.toString();
		}

		private String where() {
			if (condition == null) {
				return "";
			}

			StringBuilder where = new StringBuilder();
			String wheresql = condition.toSql(meta);
			String whereNoBrace = wheresql.substring(1, wheresql.length()-1);
			if( whereNoBrace.length()>0){
				where.append(" WHERE ").append(whereNoBrace);
				
			}
			
			return where.toString();
		}
		private void addPrimaryKey(){
			List<String> primarykeys = meta.getPrimaryKey();
			for(String key : primarykeys){
				if(! data.containsKey(key)){
					set(key,fakeUUID());					
				}
			}
		}
		private String fakeUUID() {
			String str = UUID.randomUUID().hashCode() + "";
			if (str.startsWith("-")) {
				str = str.substring(1);
			}
			return str;
		}
		/**
		 * if conditions is null,user primarykey to instead of;
		 * for update
		 * @param beans
		 * @return
		 */
	protected void primaryKeyCondition() {

		List<String> primarykeys = meta.getPrimaryKey();
		for (String key : primarykeys) {
			String value = remove(key);
			if(value != null){
				condition.add('&',key,"=",value);
			}

		}

	}
	public void primaryCondition(){
		switch(state){
		case Add:
			addPrimaryKey();
			break;
		case Update:	
			primaryKeyCondition();
			break;
		}	
	}
	public  void makeCondition(String condJson) {
		
		if (condJson == null)
			return ;
		Gson g = new Gson();
		String[] a  = g.fromJson(condJson, String[].class);
		makeCondition(a);
	}
	/*
	 * 
	 * @param StrArray [&|()]
	 */
	public void makeCondition(String[] StrArray){
		SetBeanCondition cond = (SetBeanCondition) condition;
		cond.makeCondition(Arrays.asList(StrArray));
	}
	public void makeCondition(List<String> condList){
		SetBeanCondition cond = (SetBeanCondition) condition;
		cond.makeCondition(condList);
	}
	@Override
	public String toString() {
		return (data != null)?data.toString():condition.toString();
	}
	
}
