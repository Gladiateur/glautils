package gla.meta;

import java.sql.SQLException;

import org.junit.Test;

//May be I can do it like this:
/*
 * @Tables("*");
 * EntityWriter.autobeans();
 * 
 * Or like this:
 * @Tables({"user"});
 * EntityWriter.autobeans();
 * 
 * Or :
 * @Tables("user,admin,student");
 * EntityWriter.autobeans();
 * 
 * Or :
 * @Tables("!user");
 * EntityWriter.autobeans();
 * 
 * Or :
 * @Tables("!(user,admin)");
 * EntityWriter.autobeans();
 * 
 */
public class EntityWriterTest {
	@Test
	public void test1(){
		Config.initConfig();
	}
	
	@Test
	public void test2() throws SQLException {
		EntityWriter.autobeans();
	}
}
