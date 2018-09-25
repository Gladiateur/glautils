/*
 * BeanFileManager.java 17/12/23
 */

package gla.meta;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.sat4j.core.WriteFileManager;
import static gla.meta.Utils.out;
import static gla.meta.Utils.closeOut;

/**
 * 
 * @author Gladiateur
 * @since GlaUtils-1.2
 */
public class Config extends WriteFileManager{
	
	public static void initConfig(){
		initJavaBeanConfig();
		initConnectionConfig();
		closeOut();
	}
	
	/**
	 * 初始化javabean配置文件
	 * */
	public static void initJavaBeanConfig() {
		final String fileName = "javabean.properties";
		FileOutputStream fop = null;
		File file;
		StringBuffer sb = new StringBuffer();
		sb.append("#由GlaUtils自动创建\n\n#path:实体类所在的包名,这里的书写格式比如：'com\\\\mycompany\\\\javabean',这个键的值可以为空\n");
		sb.append("path=\n\n#指定表：\n#1.数据库中所有表全部需生成实体类时，使用通配符 * 。\n");
		sb.append("#2.选择指定表，多个表用逗号隔开\n#3.反选表，比如!table,反选多个表时必须添加括号且用逗号隔开，比如：!(tab1,tab2)\ntables=\n\n");
		sb.append("#构造器样式:0-不生成构造器，1-生成无参构造器，2-生成无参和全参构造器\nconstructor-style=0\n\n");
		sb.append("#这个键用于声明哪一张表需要验证身份，该键键值格式为：表名，列名1，列名2\nCheckTable=");
	
		String path;
		try {
			file = new File("");
			path = file.getCanonicalPath() + "\\src\\" + fileName;
			file = new File(path);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			if (file.exists()) {
				// 判断内容是否为空，如若空则写入，不为空则不写
				if (file.length() == 0) {
					fop = new FileOutputStream(path);
					byte[] contentInBytes = sb.toString().getBytes();
					fop.write(contentInBytes);
					fop.flush();
					fop.close();
					out("javabean.properties ok!");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
