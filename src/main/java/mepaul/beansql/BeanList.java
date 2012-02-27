package mepaul.beansql;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class BeanList extends ArrayList<Bean>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String toJson()
	{
		StringBuilder json = new StringBuilder();
		json.append("[");
		if(size()>0){
			for(Bean bean : this){
				json.append(bean.toJson()).append(",");
			}
			json.deleteCharAt(json.length()-1);
		}
		json.append("]");
		return json.toString();
	}

	/**
	 * bean name that maps db table
	 */
	public String name;
}
