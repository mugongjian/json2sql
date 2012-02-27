package mepaul.beansql;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeJavaClass;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

/**
 * 
 *  Object info = new JsMethod().exec("js file path","js function name",[args]);
 *  
 * @author jpg
 *
 */

public class JsMethod {

	public  Object exec(String file, String function,Object[] args) 
		throws IOException, ClassNotFoundException
	{	
		Context context = Context.enter(); 
		
		Object info = null;
		try {
			 Scriptable scope = context.initStandardObjects();
			 context.evaluateString(
					 scope, 
					 getjsString(file), 
					 file, 
					 0, 
					 null);
			 Object fobj = scope.get(function, scope);
			 // no null args
			 args = args ==null? new Object[]{}:args;
			 info =  ((Function)fobj).call(context, scope, scope, args);
			 NativeJavaClass class_return = (NativeJavaClass) scope.get(
					 function+FUNCTION_RETURN_CLASS_KEY,
					 scope);
			 
			 if( !(info instanceof Undefined) && class_return != null) {
				 info = Context.jsToJava(info, class_return.getClassObject());
			 }
			 
		} finally {
			Context.exit();
		}
		return info ;
	}
	public boolean fileChanged(String fileName)
	{
		File jsFile = new File(fileName);
		Long  jsCachedDate=jsFileDate.get(fileName);
		long jsLastModifiedDate = jsFile.lastModified(); 
		return (jsCachedDate < jsLastModifiedDate);
	}
	/**
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException 
	 */
	private  String getjsString(String fileName) 
		throws IOException
	{
		File jsFile = new File(fileName);
		Long  jsCachedDate=jsFileDate.get(fileName);
		
		long jsLastModifiedDate = jsFile.lastModified(); 
		
		if(  jsCachedDate != null &&
			 jsLastModifiedDate == jsCachedDate) {
			 return jsFileCache.get(fileName);
		}
		BufferedReader buffreader = null;
		StringBuilder jsContent  = null;
		try {
			Reader reader = new FileReader(fileName);
			buffreader = new BufferedReader(reader);
			jsContent = new StringBuilder();
			String line = null;
			while( (line = buffreader.readLine()) != null){
				jsContent.append(line);
			}
		}finally {
			if( buffreader != null) {
				buffreader.close();
			}
		}
		jsFileCache.put(fileName, jsContent.toString());
		jsFileDate.put(fileName,jsFile.lastModified());
		return jsContent.toString();
	}

	/**
	 * js 
	 */
	private Map<String,String> jsFileCache = new HashMap<String,String>();
	/**
	 * 
	 */
	private Map<String,Long> jsFileDate = new HashMap<String,Long>();
	
	private static final String FUNCTION_RETURN_CLASS_KEY="_return_class";
	
}
