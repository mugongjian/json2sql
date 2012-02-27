package mepaul.beansql;


import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public class BeanSqlIniter   {
	public void init(String jsFileName){
		AppShare.$().jsMethod = new JsMethod();
		initjs(jsFileName);
		scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleWithFixedDelay (
				new NewJsExecutor(jsFileName)
				, 0
				, 1
				, TimeUnit.MINUTES);
	}
	class NewJsExecutor implements Runnable{
		public NewJsExecutor(String fileName){
			this.fileName = fileName;
		}
		public void run(){
			initjs(fileName);
		}
		private String fileName;
	} 
	private void  initjs(String file) {
		JsMethod js = AppShare.$().jsMethod;
		boolean executedJs = false;
		PersistMeta meta  = null;
		try {
			meta = (PersistMeta)js.exec(file, "main", null);
			executedJs = true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally{
			if(executedJs){
				AppShare.$().persistMeta = meta;
			}
		}
	}
	private ScheduledExecutorService scheduler;

}
