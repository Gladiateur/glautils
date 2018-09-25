package gla.meta;

import gla.annotation.AutobeansEntityConfig;
import gla.annotation.AutobeansMySQLConfig;

import java.sql.SQLException;

//since GlaUtils1.4
@AutobeansMySQLConfig(dbName = "platform", password = "123456", username = "root")
@AutobeansEntityConfig(path = "it/heima/domain", tables = "!(admin,advice,secondcategory)")
public class AccessTest {
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
//		EntityWriter.setKeyTablesExclusive(true);
//		EntityWriter.setKeyTablesFlag(true);
//		EntityWriter.setTables("*");
		EntityWriter.autobeans();
//		String[] a = {"user","star","log"};
//		String[] b = {"admin","user","student","star","log"};
//		String[] distinct = EntityWriter.distinct(a, b);
//		for (String string : distinct) {
//			System.out.println(string);
//		}
//		System.out.println(1);
		//method "exclusiveTables" should be public		18.6.30
//		String exclusiveTables = EntityWriter.exclusiveTables("!(user,admin)");
//		System.out.println(exclusiveTables);
		
		/*
		 * non-static method,so if you want to use method "autobean()",
		 * you must create the object,but there is no 
		 * default construcor for you,so if you need to create the object,
		 * you must use the constructor :
		 * public EntityWriter(String tableName) 
		 * if you want to use "autobean()",you must get 
		 * a object of EntityWriter,you will use the constructor:
		 * new EntityWriter("tableName");
		 * before above,you must get a connection,or GlaUtils will
		 * throw a exception named "NullConnectionException".
		 * 
		 * may be you will use it like this:
		 * <p>
		 * Connection conn = new FrameRoot().getConnection();
		 * System.out.println(conn);
		 * EntityWriter entityWriter = new EntityWriter("jsondb.city");
		 * entityWriter.autobean();
		 * </p>
		 * Anyway,this method will not run normally.
		 * so,this method's modify should be "private".	18.6.30
		 */
		
		//------------------------------------------------
		//autobean(String table, int cs)
		//cannot be "private" cause class Utils will call it.
		/* Connection conn = new FrameRoot().getConnection();
		 System.out.println(conn);
		EntityWriter.autobean("user", 0);*/
		
		//--------------------
		
	}
}
