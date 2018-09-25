package gla.meta;

//import gla.annotation.AutobeansEntityConfig;
//import gla.annotation.AutobeansMySQLConfig;
import gla.meta.Utils;

import java.sql.SQLException;
import java.util.StringJoiner;

//@AutobeansMySQLConfig(dbName = "jsondb", password = "123456", username = "root")
//@AutobeansEntityConfig(path = "it/bean/domains", tables = "*")
public class GlaUtils1_4Test {		
	public static void main(String[] args) throws SQLException {
//		EntityWriter.autobeans();
	}
	
	
	// srr1 - > exclusiveTables(s1)split(",");
		// srr2 - > frameRoot.getTablesName(DATABASE_NAME)
		public static String[] test_distinct(String[] srr1, String[] srr2) {
			for (int i = 0; i < srr1.length; i++) {  
			    for (int j = 0; j < srr2.length; j++) {  
			        if (srr2[j].equals(srr1[i])) {  
			        	srr2[j] = "";  
			        }  
			    }
			}
			StringJoiner sj = new StringJoiner(",");
			for (int j = 0; j < srr2.length; j++) {
				if (!"".equals(srr2[j])) {
					sj.add(srr2[j]);
				}
			}
			return sj.toString().split(",");
		}

		//返回一个字符串，该字符串为指定不生成实体类的表
		//解析取非时的表达式
		/*
		 * 该方法用于解析配置文件javabean.properties的tables键和
		 * 注解@AutobeansEntityConfig的tables值取非表达式
		 */
		public static String test_exclusiveTables(String tables) {
			String result = "";
			String start = tables.trim();
			String substring = start.substring(1).trim();
			String substring2 = Utils.cut(substring, 1, substring.length() - 2);
			char left = substring.charAt(0);
			char right = substring.charAt(substring.length() - 1);
			if (substring2.contains(",") && left == '(' && right == ')') {
				// 包含逗号时必须有左右括号，且内部不能有括号
				result = Utils.cut(substring, 1, substring.length() - 1);
			} else if (!substring2.contains(",")) {
				// 不包含逗号时，可以有左右括号也可以没有
				if (left == '(' && right == ')') {
					result = Utils.cut(substring, 1, substring.length() - 1);
				} else {
					result = substring.trim();
				}
			} else {
				throw new RuntimeException("指定表取非配置错误");
			}
			return result.replaceAll(" ", "");
		}
}
