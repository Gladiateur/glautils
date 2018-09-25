/*
 * Utils.java 18/06/03
 */

package gla.meta;

//import gla.debug.Debug;
//import gla.debug.DebugImpl;
import gla.meta.ConditionBean;
import gla.meta.EntityWriter;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import com.sat4j.core.Core;
import com.sat4j.user.FrameRoot;

/**
 * 
 * @since 1.4
 */
public class Utils {
//	private static Debug debugLogger = new DebugImpl();
	private static ConditionBean conditionBean = EntityWriter.getConditionBean();
	/**
	 * 
	 * @param str
	 * @param begin
	 * @param offset
	 * @return
	 */
	public static String cut(String str , int begin,int offset){
		return str.substring(begin, offset).trim();
	}
	
	/**
	 * 
	 * @param tables
	 * @throws SQLException
	 */
	public static String autobeans0(String[] tables) throws SQLException{
//		debugLogger.debug("Debug-autobeans0 Package = "+conditionBean.getTextPackage());
//		debugLogger.debug("Debug-entityContent TextNotes = "+conditionBean.getTextNotes());
		long t1 = System.currentTimeMillis();
		for (String table : tables) {
			EntityWriter ew = new EntityWriter(table.trim());
			conditionBean.setConstructorStyle(EntityWriter.getConditionBean().getConstructorStyle());
			ew.autobean();
//			EntityWriter.autobean(table.trim(), EntityWriter.getConditionBean().getConstructorStyle());
		}
		release();
		long t2 = System.currentTimeMillis()-t1;
		double time = Double.parseDouble(t2+"");
//		out("Time consuming:"+time/1000D+"s");
		closeOut();
		return time/1000D+"s";
	}
	
	/**
	 * @since 1.2t6252033
	 * @param msg
	 */
	private static PrintWriter pw = new PrintWriter(System.out);
	public static void out(String msg){
		pw.println(msg);
		pw.flush();
	}
	public static void closeOut(){
		pw.close();
	}
	
	private static Connection conn;
	
	public static String[] getAllTables(FrameRoot fr,final String dbName){
		String[] allTables = null;
		try {
			conn = fr.getConnection();
			allTables = fr.getTablesName(dbName);
		} catch (Exception e) {
		}
		return allTables;
	}
	public static String[] getAllTables(FrameRoot fr,final String dbName,String username,String pwd){
		String[] allTables = null;
		try {
			conn = Core.getConnection(dbName, username, pwd);
			allTables = fr.getTablesName(dbName);
		} catch (Exception e) {
		}
		return allTables;
	}
	
	private static void release(){
		if(conn != null){
			try {
				conn.close();
			} catch (SQLException e) {
				out("连接关闭异常");
			}
		}
	}
}
