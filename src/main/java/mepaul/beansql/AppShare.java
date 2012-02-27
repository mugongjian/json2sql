package mepaul.beansql;

import mepaul.beansql.JsMethod;

/**
 * bind some obj to app,easy to find ,easy to maintain.
 * @author jpg
 *
 */

public class AppShare {
	static class AppShareHolder {
		static AppShare share = new AppShare();
	}
	public static AppShare $(){
		return AppShareHolder.share;
	}
	public  volatile PersistMeta persistMeta;
	public  volatile JsMethod jsMethod;
	
}
