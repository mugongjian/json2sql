package mepaul.beansql;



import mepaul.beansql.Bean.BeanState;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
	
	@Test public void  testRun(){
		BeanSqlIniter beansql = new BeanSqlIniter();
		beansql.init("/home/jpg/Templates/walk-code/beansql/src/main/java/mepaul/__meta__.js");
		Bean b = new Bean(BeanState.Query,"paul");
		Assert.assertTrue(b.getSql(), false);
		
		
	}
	
   @Before
   public void setup(){
	   
   }
   @After 
   public void teardown(){
	   
   }
   
}
