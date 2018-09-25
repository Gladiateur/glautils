/*
 * EntityWriter.java 17/12/18
 * 
 * Copyright 2018 GlaUtils1.5
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package gla.meta;

import gla.debug.Debug;
import gla.debug.DebugImpl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringJoiner;

import javax.swing.JOptionPane;

import com.sat4j.core.Core;
import com.sat4j.exception.NullConnectionException;

/**
 * 将数据库中的表生成对应的实体类。
 * <p>
 * 	@AutobeansMySQLConfig(dbName = "test", password = "123", username = "root")
 *	@AutobeansEntityConfig(path = "com/test/domain", tables = "*")
 *	public class Test {
 *		public static void main(String[] args) throws SQLException {
 *			EntityWriter.autobeans();
 *		}
 *	}
 * </p>
 * <p>
 * 注解@AutobeansMySQLConfig 有三个必填参数。
 * dbName：数据库名称<br>
 * password:数据库密码<br>
 * username:数据库用户<br>
 * </p>
 * <p>
 * 注解@AutobeansEntityConfig 有三个参数
 * 其中path,tables为必填参数，constructorStyle为可选参数。<br>
 * path:指定生成实体类的包路径，可以写作：com\\test\\domain
 * 也可以写作com/test/domain。<br>
 * tables:指定数据库中哪些表需要生成实体类。
 * 		可写作"*"，则指定该数据库中全部的表
 * 		也可写作具体的表名，多个表用逗号隔开，比如"admin,user"
 * 		也可进行反选，比如"!user",意为该数据库中除了user表以外都需生成实体类。
 * 		当反选多个表时必须添加括号，比如"!(admin,user)"。
 * </p>
 * 
 * 
 * 
 * @author Gladiateur
 * @since jdk1.8
 */
public class EntityWriter {
	private String tableName;
	private static String[] tableList;
	private Map<String, String> fieldsMap = new HashMap<String, String>();
	private static TextFormatExpress tfe = new TextFormatExpress();
	
	private static ConditionBean conditionBean = new ConditionBean();
	private static HandlerFactory handlerFactory;
	
	private static final String NODE = "/** Created by ? **/";
	private static final Debug debugLogger = new DebugImpl();
	private BufferedOutputStream bos;
	
	private static void configExecutor(){
		/* ************************************
		 * configExecutor:
		 * getStackTrace()[0] --> Thread
		 * getStackTrace()[1] --> EntityWriter
		 * getStackTrace()[2] --> EntityWriter
		 * getStackTrace()[3] --> class of caller
		 *
		 * static block:
		 * getStackTrace()[0] --> Thread
		 * getStackTrace()[1] --> EntityWriter
		 * getStackTrace()[2] --> class of caller
		 * * ************************************
		 */
		String clazz = Thread.currentThread().getStackTrace()[3].getClassName();
		debugLogger.debug("Debug-configExecutor [Current Thread:"+clazz+"]");
		handlerFactory = new HandlerFactory(clazz);
		ConfigHandler handler = handlerFactory.getHandler();
		handler.doConfig();
	}
	
	private static void tableExpression(){
		ConfigHandler ch = new ExpressionHandler(conditionBean.getTables());
		ch.doConfig();
	}
	
	/*
	 * If you want to call this method, you must instantiate 
	 * the object of EntityWriter.But it doesn't provide you 
	 * with the default constructor.So when you need to strength 
	 * this class, you must call the following constructor:
	 * public EntityWriter(String tableName).
	 * But before calling, you have to get a connection,otherwise 
	 * GlaUtils will throw an exception named "NullConnectionException".
	 * May be you will use it like this:
	 * <p>
	 * Connection conn = new FrameRoot().getConnection();
	 * System.out.println(conn);
	 * EntityWriter entityWriter = new EntityWriter("jsondb.city");
	 * entityWriter.autobean();
	 * </p>
	 * But you will not run this method normally,cause this method
	 * will be used by Utils.
	 */
	public void autobean() throws SQLException {
		String path0 = conditionBean.getPath();
		File file = new File(path0);
		if(!file.exists()){
			file.mkdirs();
		}
		try {
			bos = new BufferedOutputStream(new FileOutputStream(
					new File(path0+"/"+Core.captureName(tableName)+".java")));
			bos.write(this.entityContent(tableName).toString().getBytes());
			bos.close();
			debugLogger.debug("Debug-autobean ["+Core.captureName(tableName)+".java was written]");
		} catch (FileNotFoundException e) {
			debugLogger.debug("Debug_autobean [FileNotFoundException]");
		} catch (IOException e) {
			debugLogger.debug("Debug_autobean [IOException]");
		}finally{
			if(bos != null){
				try {
					bos.close();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "关闭BufferedOutputStream发生错误", 
							"GlaUtils: 关闭BufferedOutputStream发生错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
	/**
	 * 根据配置文件生成与数据库中表所对应的实体类。
	 * 
	 * @throws SQLException
	 */
	public static void autobeans() throws SQLException {
		configExecutor();
		tableExpression();
		doAutobeans();
	}
	
	public static String doAutobeans() throws SQLException{
		tableExpression();
		if (Objects.isNull(conditionBean.getAllTables())) {	//allTables
			throw new NullConnectionException("创建连接失败！"
					+ "请检查相关数据库驱动包是否已导入。数据库名称、用户名、密码是否正确。");
		}
		tableList = conditionBean.isKeyTablesFlag() == true ? conditionBean.getAllTables()
		: (conditionBean.isKeyTablesExclusive() == true ? exclusiveTables0(conditionBean.getTables(),
				conditionBean.getAllTables()) : conditionBean.getTables().split(","));
		debugLogger.debug("Debug-autobeans Final table list :"+Arrays.asList(tableList));
		return Utils.autobeans0(tableList);
	}
	

	//
	private static String[] exclusiveTables0(String tables, String[] allTables) {
		String exclusiveTables = exclusiveTables(tables);
		String[] srr1 = exclusiveTables.split(",");
		return distinct(srr1, allTables);
	}

	// srr1 - > exclusiveTables(s1)split(",");
	// srr2 - > frameRoot.getTablesName(DATABASE_NAME)
	public static String[] distinct(String[] srr1, String[] srr2) {
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
	public static String exclusiveTables(String tables) {
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

	/**
	 * field -> K,type -> Value
	 * 
	 * @param table
	 * @throws SQLException
	 */
	private void fieldsMap(String table) throws SQLException {
		ResultSet rs = Core.selectResultSet(table);
		if(rs == null){
			throw new NullPointerException("ResultSet rs : null");
		}
		debugLogger.debug("Debug-fieldsMap [Obtained the ResultSet : "+table+" ]");
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount(); // 返回表的总列数
		for (int i = 1; i <= columnCount; i++) {
			fieldsMap.put(rsmd.getColumnName(i), rsmd.getColumnClassName(i));
		}
	}

	/**
	 * 该构造方法构造一个EntityWriter的实例，同时初始化fieldsMap
	 * 
	 * @param tableName
	 * @throws SQLException
	 */
	public EntityWriter(String tableName) throws SQLException {
		this.tableName = tableName;
		this.fieldsMap(tableName);
	}

	/**
	 * 从fieldsMap获取所有属性
	 * 
	 * @return
	 */
	public Set<String> getAttributes() {
		return fieldsMap.keySet();
	}

	/**
	 * 从fieldsMap获取所有类型
	 * 
	 * @return
	 */
	public List<String> getTypes() {
		List<String> list = new ArrayList<String>();
		for (Entry<String, String> string : fieldsMap.entrySet()) {
			list.add(string.getValue());
		}
		return list;
	}

	/**
	 * 从fieldsMap中获取指定属性的类型
	 * 
	 * @param attribute
	 * @return
	 */
	public String getType(String attribute) {
		return (String) fieldsMap.get(attribute);
	}

	/**
	 * 路径写法转为包的写法
	 * */
	public final static String formatToPackage(String path) {
		String packName = null;
		if (path.trim().length() != 0) {
			path = path.substring(5); // length of "path="
			
			//Note:
			//maybe the path like this "com\\gla\\glautils"
			//maybe the path like this "com/gla/glautils"
			//The Pattern "\\\\|/" means change "\\" or "/" to ".".
			packName = path.replaceAll("\\\\|/", ".");
		}
		debugLogger.debug("Debug-formatToPackage [Package name is : "+packName+"]");
		return packName;
	}

	public static StringBuffer declarePackage(String packageName) {
		if (packageName.trim().length() != 0) {
			return tfe.appendFormat("package ?;", packageName);
		}
		return tfe.appendFormat("//default package");
	}

	/**
	 * 注释语句部分
	 * 
	 * @return
	 */
	public static StringBuffer notes() {
		ResourceBundle bundle = ResourceBundle.getBundle("default-config");
		return tfe.appendFormat(NODE, bundle.getString("VERSION"));
	}

	/**
	 * 
	 * @return
	 */
	private StringBuffer declareAttributes() {
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String> string : fieldsMap.entrySet()) {
			tfe.appendFormat("\tprivate ? ?;\n", string.getValue(),
					string.getKey());
			sb.append(tfe);
		}
		return sb;
	}

	/**
	 * 
	 * @param table
	 * @return
	 */
	public final static boolean isLowwer(String table) {
		byte[] b = table.getBytes();
		if (b[0] >= 97 && b[0] <= 122) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public final static String captureName(String name) {
		if (isLowwer(name) == true) {
			char[] cs = name.toCharArray();
			cs[0] -= 32;
			return String.valueOf(cs);
		} else {
			return name;
		}

	}

	// getter方法
	private StringBuffer declareGetterMethods() {
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String> string : fieldsMap.entrySet()) {
			String result = string.getKey();
			String methodName = "get".concat(captureName(result));
			tfe.appendFormat("\tpublic ? ?(){\n\t\treturn ?;\n\t}\n",
					string.getValue(), methodName, result);
			sb.append(tfe);
		}
		return sb;
	}

	// setter方法
	private StringBuffer declareSetterMethods() {
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String> string : fieldsMap.entrySet()) {
			String result = string.getKey();
			String type = string.getValue() + " ";
			String methodName = "set".concat(captureName(result));
			tfe.appendFormat("\tpublic void ?(? ?){\n\t\tthis.?=?;\n\t}\n",
					methodName, type, result, result, result);
			sb.append(tfe);
		}
		return sb;
	}

	// 生成无参构造器
	private StringBuffer declareDefaultConstructor(String table) {
		StringBuffer sb = new StringBuffer();
		tfe.appendFormat("\tpublic ?(){\n\t\tsuper();\n\t}\n",
				captureName(table));
		sb.append(tfe);
		return sb;
	}

	// 生成全参数构造器
	private StringBuffer decalreFullAttributesConstructor(String table) {
		StringBuffer paramList = new StringBuffer(); // 参数列表
		for (Entry<String, String> string : fieldsMap.entrySet()) {
			tfe.appendFormat("? ?,", string.getValue(), string.getKey());
			paramList.append(tfe);
		}
		// 去掉最后的逗号和空格-->去掉最后两个字符
		int length = paramList.length();
		paramList.replace(0, length, paramList.substring(0, length - 2)); // 减2是因为参数列表：“a,b,c,
																			// ”这里最后有个空格，因此要减2
		StringBuffer methodBody = new StringBuffer();
		tfe.appendFormat("\t\tsuper();\n");
		methodBody.append(tfe);
		Set<String> set = getAttributes();
		StringBuffer attributes = new StringBuffer();
		for (String attribute : set) {
			tfe.appendFormat("\t\tthis.?=?;\n", attribute, attribute);
			attributes.append(tfe);
		}
		methodBody.append(attributes);
		StringBuffer sb = new StringBuffer();
		tfe.appendFormat("\tpublic ?(?){\n?\t}\n", captureName(table),
				paramList.toString(), methodBody.toString());
		sb.append(tfe);
		return sb;
	}

	
	// 拼接类文件中的全部内容
	// 是否生成构造器，生成哪种：0-不生成构造器，1-生成无参默认构造器，2-生成无参和全参两个构造器
	private StringBuffer entityContent(String table) {
		StringBuffer sb = new StringBuffer();
		String textAttributes = declareAttributes().toString();
		String textGetterMethods = declareGetterMethods().toString();
		String textSetterMethods = declareSetterMethods().toString();
		tfe.appendFormat("?\n\n?\n\n", conditionBean.getTextPackage(),  conditionBean.getTextNotes());
		sb.append(tfe);
		tfe.appendFormat("public class ?", captureName(table));
		sb.append(tfe);
		tfe.appendFormat("implements java.io.Serializable{\n\n?\n?\n?",
				textAttributes, textGetterMethods, textSetterMethods);
		sb.append(tfe);
		switch (conditionBean.getConstructorStyle()) {
		case 0:
			break;
		case 1:
			tfe.appendFormat("\n?", declareDefaultConstructor(table).toString());
			sb.append(tfe);
			break;
		case 2:
			tfe.appendFormat("\n?\n?", declareDefaultConstructor(table)
					.toString(), decalreFullAttributesConstructor(table)
					.toString());
			sb.append(tfe);
			break;
		}
		sb.append("}");
		return sb;
	}

	public static ConditionBean getConditionBean() {
		return conditionBean;
	}
	public static void setConditionBean(ConditionBean conditionBean) {
		EntityWriter.conditionBean = conditionBean;
	}
}