package mepaul.beansql;

import java.util.ArrayList;
import java.util.List;

public class SetBeanCondition implements Condition{
	private List<Condition> conditions ;
	private List<Character> conditionRelation;
	public SetBeanCondition() {
		conditions  = new ArrayList<Condition>();
		conditionRelation = new ArrayList<Character>();
	}
	public SetBeanCondition(List<String> conds) {
		this();
		makeCondition(conds);
	}
	public String toSql(TableMeta meta) {
		StringBuilder where = new StringBuilder();
		for (int i = 0; i < conditions.size(); i++) {
			Condition cond = conditions.get(i);
			char rel = conditionRelation.get(i);
			String relStr = null;
			switch(rel){
				case	'&':
					relStr=" AND ";
					break;
				case	'|':
					relStr = " OR ";
					break;
			}
			where.append(relStr).append(cond.toSql(meta));
		}
		String wherestr = "";
		if(conditions.size() > 0){
			wherestr = where.toString().substring(4); 
		}
		return '('+wherestr+')';
	}

	public void makeCondition(List<String> strArray) {
		for (int i = 0; i < strArray.size(); i += 4) {
			
			String c = strArray.get(i);
			if("&".equals(c) || "|".equals(c) ){
				conditionRelation.add(c.charAt(0));
			} else {
				conditionRelation.add('&');
				i--;
			} 
			String maybeSub = strArray.get(i+1);
			if(maybeSub.equals("(")){
				int j = 0;
				for( j =strArray.size()-1; j > i+1; j--){
					if(")".equals(strArray.get(j))){
						conditions.add(
								new SetBeanCondition(strArray.subList(i+2, j)));
						break;
					}
				}
				i = j+1 -4;;
				continue;
			}
			String maybeNative = strArray.get(i+1);
			if(maybeNative.startsWith("${") && maybeNative.endsWith("}")){
				String realTarget = maybeNative.substring(2,maybeNative.length()-1);
				conditions.add(new BasicBeanCondition(realTarget, "real", ""));
				i -= 2;
				continue;
			}
			conditions.add(new BasicBeanCondition(strArray.get(i+1), strArray.get(i + 2),
					strArray.get(i + 3)));
		}
		
	}
	
	public void add(char c, String key, String string, String value) {
		conditionRelation.add('&');
		conditions.add(new BasicBeanCondition(key, "=", value));
	}
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for(int i=0; i<conditions.size(); i++){
			s.append(conditionRelation.get(i)).append("\t").append(conditions.get(i)).append("\n");
		}
		return s.toString();
	}
	
}
