package mepaul.beansql;


public class PageSql {
	public static  String pagedQuerySql(String sql,int start,int limit,String keys){
		String pagedSql = null;
		if("db2".equals(AppShare.$().persistMeta.getDefaultDbType())){
			pagedSql = pagedQuerySql4DB2(sql, start, limit,keys);
		}
		return pagedSql;
	}
	private static String pagedQuerySql4DB2(String sql,int start,int limit,String keys) {
		
		StringBuilder pagePiece = new StringBuilder();
		pagePiece.append(",ROW_NUMBER() OVER(");
		if(keys.trim().length()>0){
			pagePiece.append("ORDER BY ").append(keys.toString());
		}
		pagePiece.append(") AS RN"); 
		sql = sql.replaceFirst(",0", pagePiece.toString());
		StringBuilder pageSql = new StringBuilder();
		start +=1;
		int pageEnd = start+limit;
		pageSql.append("SELECT * FROM (").append(sql).append(")t ")
				.append("WHERE RN >=").append(start)
				.append(" AND RN <").append(pageEnd);
		return pageSql.toString();
}

}
