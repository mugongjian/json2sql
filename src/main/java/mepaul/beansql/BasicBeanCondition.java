package mepaul.beansql;


/**
 * id = 'aaa', name like %aa%, age in (12,33,55)
 * @author JianPuGang
 *
 */
public class BasicBeanCondition implements Condition {
	private String target;
	private String verb;
	private String state;
	
	public BasicBeanCondition(String target,String verb,String state){
		this.target = target.trim();
		this.verb = verb.trim();
		this.state = state.trim();
		
	}
	
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getVerb() {
		return verb;
	}
	public void setVerb(String verb) {
		this.verb = verb;
	}
	public String getState(TableMeta tm) {
		
		StringBuilder sqlFrag = new StringBuilder();
		char quote = tm.isStringField(target)?'\'':' ';
		
		if("=".equals(verb)){
			sqlFrag.append(quote)
			.append(state)
			.append(quote);
		}else if("like".equals(verb)){
			sqlFrag.append(quote).append('%')
			.append(state)
			.append('%').append(quote);
		}else if("in".equals(verb) || "not in".equals(verb)){
				sqlFrag.append('(');
				String[] states = state.split(",");
				for(String s: states){
					sqlFrag.append(quote)
					.append(s)
					.append(quote).append(',');
				}
				if(states.length>0){
					sqlFrag.deleteCharAt(sqlFrag.length()-1);
				}
				sqlFrag.append(')');
		}else if("is".equals(verb) || "is not".equals(verb)){
			sqlFrag.append("NULL");
		}else{
			sqlFrag.append(quote)
			.append(state)
			.append(quote);
		}
		return sqlFrag.toString();
	}
	public String toSql(TableMeta meta){
		// is native sql
		if("real".equals(verb)){
			return meta.getCondition(target);
		}
		String value = getState(meta);
		String dbField = meta.getFieldMeta(target).db;
		StringBuilder sql = new StringBuilder();
		sql.append(dbField).append(' ').append(verb.toUpperCase())
			.append(' ').append(value);
		return sql.toString();
	}

	public void setState(String state) {
		this.state = state;
	}
	public String getOrigionState(){
		
		return state;
	}
	public String toString(){
		return target+ " " +verb+" "+ state;
	}
	
	
}
