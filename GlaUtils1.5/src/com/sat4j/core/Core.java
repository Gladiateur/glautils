/*
 * @(#)Core.java	2.0 18/05/12 
 *
 * Copyright 2018 Gladiateur, Inc. All rights reserved.
 */

package com.sat4j.core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.sql.DataTruncation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.StringTokenizer;

import com.sat4j.core.Logger;
import com.sat4j.anno.Requiredment;
import com.sat4j.exception.NotStartMySQLServiceException;
import com.sat4j.exception.NullConnectionException;
import com.sat4j.exception.TargetNotExistException;
import com.sat4j.user.FrameRoot;

/**
 * <p>
 * 
 * </p>
 * 
 * @author Gladiateur
 * @version v2.0 18/5/12
 * @since sat4j-v2.0.jar
 * @since jdk 1.8
 */
@Requiredment
public final class Core extends AbsFunction {
	private Core() {
	}

	/** 初始化日志对象 **/
	private static Logger log = new Logger();

	/**
	 * 初始化connMap对象 配置文件default-connection.properties被封装成了Map对象。
	 * */
	public static Map<String, String> connMap = null;

	/**
	 * vis:是否打印反馈信息，true打印，false不打印 这个变量完成扩展功能，用户可以根据选择来设置它的值。
	 * 
	 * */
	/*
	 * vis从配置文件获取
	 */
	@Requiredment
	/* 修改后删除本注解 */
	public static boolean vis = false;

	/**
	 * conn:连接对象，初始化为空。
	 * */
	public static Connection conn = null;

	/**
	 * 初始化Statement对象。
	 * */
	private static Statement stat = null;

	/**
	 * 初始化ResultSet对象
	 * 
	 * */
	private static ResultSet rs = null;

	/** since 2.0 Constants **/
	private static final String TABLE_PREFIX = "Tables_in_";
	private static final String SQL_FIELD = "Field";
	private static final String SQL_TYPE = "Type";
	private static final String FILE_SUFFIX = ".java";
	private static final String METHOD_PREFIX = "get";	//to config
	private static final String METHOD_SUFFIX = "Class";//to config
	
	/** **************************** **/

	/**
	 * 定义被保护的数据库的名称。
	 * */
	/* 此处需要一个 必须修改的注解，比如@Requirement */
	/*
	 * 此处常量提出到文件，变量名，变量类型不变，只改变获取方式。 18.5.12
	 */
	@Requiredment
	/* 修改后删除本注解 */
	private final static String[] protectedDatabase = { "bin_db",
			"informaion_schema", "mysql", "performance_schma", "sys", "world" };

	/**
	 * 初始化配置文件信息。 用户可以创建一个类并且继承FrameRoot，然后再这个类中运行空的主函数即可自动创建并初始化用户需要的配置文件。
	 * 
	 * @see FrameRoot
	 * */
	public final static void initConfigs() {
		WriteFileManager.initConnectionConfig();
		WriteFileManager.initBeansConfig();
		System.out.println("配置文件已初始化，请刷新工程！");
	}

	/**
	 * 该方法功能是将加载的连接数据库配置文件的数据封装在Map集合里并返回
	 * 为建立数据库的链接做准备。该方法调用常量管理器的方法getConnMap。
	 * 通过它获取配置文件default-connection.properties的键值对数据。
	 * 
	 * 这个方法必须在getConnection之前执行。
	 * */
	public final static Map<String, String> init0() {
		return connMap = ConstsManager.getConnMap();
	}

	/**
	 * 
	 * 这个方法建立数据库的连接并返回连接对象。
	 * 该方法通过jdbc驱动连接本地的mysql数据库。通过常量管理器ConstsManager
	 * 获取到配置文件中配置数据库的连接信息。 该方法处理了可能发生的异常。
	 * 
	 * @throws SQLException
	 *             抛出数据库异常
	 * @throws ClassNotFoundException
	 *             抛出找不到类的异常
	 * @since 2.0
	 * */
	/*
	 * 优化本方法
	 */
	@Requiredment
	/* 优化后删除本注解 */
	public final static Connection getConnection() throws SQLException,
			ClassNotFoundException {
		if (conn == null) {
			try {
				if(connMap != null){
					Class.forName(connMap.get("DRIVER"));
					conn = DriverManager.getConnection(connMap.get("URL"),
							connMap.get("USERNAME"), connMap.get("PASSWORD"));
					if(conn == null){
						throw new NullConnectionException("创建连接失败！"
								+ "请检查相关数据库驱动包是否已导入。数据库名称、用户名、密码是否正确。");
					}
				}else{
					throw new NullPointerException("connMap未初始化,请在创建连接前执行init0()");
				}
			} catch (SQLSyntaxErrorException e) {
				log.print(e, "创建连接时出现的异常 ：" + e.getMessage());
				throw new RuntimeException("创建连接时出现的异常 ：" + e.getMessage());
			} catch (SQLException e) {
				if (e.getErrorCode() == 1045) {
					log.print(e, "配置文件中root值或password值错误！\n不存在数据库用户：["
							+ connMap.get("USERNAME") + "],或密码错误");
					throw new RuntimeException(
							"配置文件中root值或password值错误！\n不存在数据库用户：["
									+ connMap.get("USERNAME") + "],或密码错误");
				}
			} catch (NullPointerException e) {
				log.print(e, "创建连接时出现空指针异常：" + e.getMessage());
				//throw new RuntimeException("创建连接时出现空指针异常：" + e.getMessage());
			} catch (RuntimeException e) {
				System.out
						.println("没有找到配置文件default-connection.propreties");
			}
			return conn;
		}
		return conn;
	}
	//-----------------
	public final static Connection getConnection(String dbName,String username,String password) throws SQLException,
	ClassNotFoundException {
	if (conn == null) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			final String URL = "jdbc:mysql://localhost:3306/" + dbName+ "?useUnicode=true&characterEncoding=UTF-8";
			conn = DriverManager.getConnection(URL,
					username, password);
		} catch (SQLSyntaxErrorException e) {
			log.print(e, "创建连接时出现的异常 ：" + e.getMessage());
			throw new RuntimeException("创建连接时出现的异常 ：" + e.getMessage());
		} catch (SQLException e) {
			if (e.getErrorCode() == 1045) {
				log.print(e, "配置文件中root值或password值错误！\n不存在数据库用户：["
						+ connMap.get("USERNAME") + "],或密码错误");
				throw new RuntimeException(
						"配置文件中root值或password值错误！\n不存在数据库用户：["
								+ connMap.get("USERNAME") + "],或密码错误");
			}
		} catch (NullPointerException e) {
			log.print(e, "创建连接时出现空指针异常：" + e.getMessage());
			//throw new RuntimeException("创建连接时出现空指针异常：" + e.getMessage());
		} catch (RuntimeException e) {
			System.out
					.println("没有找到配置文件default-connection.propreties");
		}
		return conn;
	}
	return conn;
	}
	//----------------------------
	
	
	/**
	 * 该方法用于创建一个数据库，这个方法处理了部分异常。
	 * 用户将会先与数据库创建连接，之后便可以创建数据库，而这个方法是底层的，只是提供给上层，用户调用上层的方法即可。
	 * 使用此方法时，需要传入准创建数据库的名称，该名称格式务必合法，否则将会报错。 如果创建两次相同的数据库同样会报错。
	 * 当创建成功时，会提示用户数据库XXX创建成功。失败时会提示错误代码。
	 * 
	 * @param dbName
	 *            数据库名称
	 * */
	public final static void createDatabase(String dbName)
			throws ClassNotFoundException {
		// Connection conn = Core.getConnection()
		String sql = "create database " + dbName;
		// ResultSet rs = null;
		try {
			stat = conn.createStatement();
			stat.execute(sql);
			System.out.println("数据库：[" + dbName + "] 创建成功！");
		} catch (SQLSyntaxErrorException e) {
			System.out.println("创建数据库时异常:" + e.getMessage());
		} catch (SQLException e) {
			if (("Can't create database '" + dbName + "'; database exists")
					.equals(e.getMessage())) {
				System.out.println("错误号：" + e.getErrorCode() + ",创建数据库失败，数据库["
						+ dbName + "] 已存在！");
			} else {
				e.printStackTrace();
				System.out
						.println("com.sat4j.core.Core.createDatabase(Stirng dbName):出现未知的错误！");
			}
		} catch (NullPointerException e) {
			System.out
					.println("com.sat4j.core.Core.createDatabase(Stirng dbName)：创建数据库时出现空指针异常："
							+ e.getMessage());
		}
	}

	/**
	 * 这个方法用于删除一个数据库，当然，不允许删除mysql自带的数据库 这个方法提供给上层，不需要让用户直接调用此方法。这个方法给上层抛出异常。
	 * 当传入一个已存在的数据库名称时，这个方法会首先判断，传入的数据库是否是mysql自带的， 如果是则不允许用户删除，并给用户提示。
	 * 如果不是则删除，并提示删除成功。 如果用户传入了一个不存在的数据库则删除失败，并提示错误信息。
	 * 
	 * @param dbName
	 *            数据库名称
	 * @throws ClassNotFoundException
	 * */
	public final static void deleteDatabase(String dbName)
			throws ClassNotFoundException {
		for (String string : protectedDatabase) {
			if (string.equals(dbName) == true) {
				System.out.println("受保护的数据库[" + dbName + "]不允许被删除！");
				return;
			}
		}
		String sql = "drop database " + dbName;
		Statement stat = null;
		try {
			stat = conn.createStatement();
			stat.execute(sql);
			System.out.println("数据库：[" + dbName + "] 删除成功！");
		} catch (SQLSyntaxErrorException e) {
			System.out.println("你有一个语法错误：" + e.getMessage());
		} catch (SQLException e) {
			if (("Can't drop database '" + dbName + "'; database doesn't exist")
					.equals(e.getMessage())) {
				System.out.println("错误号：" + e.getErrorCode() + ",删除数据库失败，数据库["
						+ dbName + "] 不存在！");
			} else {
				e.printStackTrace();
			}
		} catch (NullPointerException e) {
			System.out.println("删除数据库时出现空指针异常：" + e.getMessage());
		}

	}

	/**
	 * 这个方法用于输出一个数据库中的表名集合
	 * <p>
	 * 在2.0版本后当数据库不存在时会抛出TargetNotExistException异常。
	 * </p>
	 * 
	 * @param dbName
	 * @throws ClassNotFoundException 
	 * */
	public final static ArrayList<String> getTables(String dbName) throws ClassNotFoundException {
		ArrayList<String> list = new ArrayList<String>();
		StringJoiner sj = new StringJoiner("");
		String tabName = null;
		String sql = null;
		try {
//			Connection conn = new FrameRoot().getConnection();
			stat = conn.createStatement();
			sql = "use " + dbName;
			sj.add("SQL:\n\t");
			sj.add("USE ");
			sj.add(dbName.concat(";"));
			stat.execute(sql);
			sql = "SHOW TABLES";
			sj.add("\n\t".concat(sql));
			sj.add(";");
			rs = stat.executeQuery(sql);
			if (rs != null) {
				while (rs.next()) {
					tabName = rs.getString(TABLE_PREFIX + dbName);
					list.add(tabName);
				}
			}
		} catch (SQLSyntaxErrorException e) {
			if (e.getErrorCode() == 1064) {
				throw new RuntimeException("存在语法错误，请检查数据库名称" + e.getMessage());
			}
			if (("Unknown database '" + dbName + "'").equals(e.getMessage())) {
				log.print(e, "你输入了一个不存在的数据库！\t" + e.getMessage());
				throw new TargetNotExistException("数据库[" + dbName + "]不存在");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			log.print(e, "获取表名集合时发生异常！可能是由于mysql服务未启动造成");
			throw new NotStartMySQLServiceException(
					"获取表名集合时发生异常！可能是由于mysql服务未启动造成");
		}
		return list;
	}

	/**
	 * 该方法功能是根据表名集合循环输出表名数组
	 * <p>
	 * 方法getTables通过数据库名称获取该数据库的所有表名的List集合。 将该集合当做参数传入本方法将得到数据库名的字符串数组。
	 * </p>
	 * 
	 * @see Core#getTables(String)
	 * */
	public final static String[] getTablesName(ArrayList<String> list) {
		String[] tabs = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			tabs[i] = (String) list.get(i);
		}
		return tabs;
	}

	/**
	 * 该方法或去带有数据库名的表的全名。 该方法改进已过时的getTablesName(String dbName,ArrayList&lt;String&gt;
	 * list)方法。
	 * 
	 * @since 2.0
	 * @param dbName
	 * @return
	 * @throws ClassNotFoundException 
	 */
	public final static String[] getTablesFullName(String dbName) throws ClassNotFoundException {
		ArrayList<String> list = getTables(dbName);
		String[] tabs = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			tabs[i] = dbName + "." + (String) list.get(i);
		}
		return tabs;
	}

	/**
	 * 该方法已过时。
	 * <p>
	 * 这个方法的参数list为不必要参数。 2.0版本推荐使用getTablesFullName(String dbName)方法。
	 * </p>
	 * 
	 * @see Core#getTablesFullName(String)
	 * */
	@Deprecated
	public final static String[] getTablesName(String dbName,
			ArrayList<String> list) {
		String[] tabs = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			tabs[i] = dbName + "." + (String) list.get(i);
		}
		return tabs;
	}

	/**
	 * 该方法不允许关键字作为表名。
	 * <p>
	 * 当您使用关键字作为表名，例如order则会出错。在Sat4j 2.0版本中，该方法代替了原先的 getDesign(String dbName,
	 * String tabName)方法。
	 * </p>
	 * <p>
	 * 该方法功能是通过表名获取该表的结构集合。
	 * </p>
	 * <p>
	 * 可用以下代码段测试该方法。 init0(); getConnection(); ResultSet rs =
	 * getDesign("t_user"); System.out.println(rs); while(rs.next()){
	 * System.out.println(rs.getString("Field")); }
	 * <p>
	 * 
	 * @param tabName
	 * @return
	 */
	/* 由该方法可扩展出获取表结构对象 该对象包括表的字符集，字段，类型，约束等信息 */
	public final static ResultSet getDesign(String tabName) {
		String sql = null;
		try {
			stat = conn.createStatement();
			sql = "desc " + tabName;
			rs = stat.executeQuery(sql);
		} catch (SQLException e) {
			System.out.println("检测语句：" + sql);
			System.out.println("获取表结构异常：" + e.getMessage());
			log.print(e, "获取表结构异常：" + e.getMessage());
		} catch (NullPointerException e) {
			throw new NullConnectionException("与数据库的连接是空的！！！您还未取得连接！！！");
		}
		return rs;
	}

	// desc [dbName.tabName];输出设计视图的集合
	/**
	 * 该方法返回一个表的结构，封装在ResultSet集合中。
	 * <p>
	 * 该方法允许表名为关键字order的表生成实体类。sat4j 2.0版本过时。<br>
	 * 该方法dbName参数当不是order时，获取的是配置文件配置的数据库名称。即参数被架空。
	 * </p>
	 * <p>
	 * <b>该方法已由getDesign(String tabName)代替。</b>
	 * </p>
	 * 
	 * @return ResultSet 表结果集
	 * @see Core#getDesign(String)
	 * */
	@Deprecated
	public final static ResultSet getDesign(String dbName, String tabName) {
		System.out.println("正在获取该表的结构...");
		Statement stat = null;
		ResultSet rs = null;
		String sql = null;
		try {
			stat = conn.createStatement();
			if ("order".equals(tabName) == true) {
				tabName = dbName + "." + tabName;
			}
			sql = "desc " + tabName;
			rs = stat.executeQuery(sql);
		} catch (SQLException e) {
			System.out.println("检测语句：" + sql);
			System.out.println("获取表结构异常：" + e.getMessage());
			log.print(e, "获取表结构异常：" + e.getMessage()
					+ "\t位置：com.sat4j.core.Core.getDesign");
		} catch (NullPointerException e) {
			throw new NullConnectionException("与数据库的连接是空的！！！您还未取得连接！！！");
		}
		return rs;
	}

	/**
	 * 根据getDesign方法返回的结果把&lt;Field,Type&gt;封装到一个Map集合。
	 * <p>
	 * 该方法通过getDesign(String tabName)方法得到的表结构返回一个Map集合。
	 * 该集合键存字段名称，值存的的字段类型，这里的类型是sql中的类型，并且携带长度。
	 * </p>
	 * <p>
	 * 该方法调用案例如下。<br>
	 * init0(); getConnection(); ResultSet design = getDesign("t_user");
	 * Map&lt;String, String&gt; map = getFieldAndTypeMap(design); Set&lt;Entry&lt;String,
	 * String&gt;&gt; entrySet = map.entrySet(); for (Entry&lt;String, String&gt; cell :
	 * entrySet) { System.out.println(cell.getKey()+","+cell.getValue()); }
	 * </p>
	 * 
	 * since 2.0
	 * */
	public final static Map<String, String> getFieldAndTypeMap(ResultSet rs) {
		Map<String, String> map = new HashMap<String, String>();
		if (rs != null) {
			try {
				while (rs.next()) {
					map.put(rs.getString(SQL_FIELD), rs.getString(SQL_TYPE));
				}
			} catch (SQLException e) {
				System.out.println("将ResultSet集合中的Field,Type的值封装到Map集合视出现异常："
						+ e.getMessage());
				log.print(e);
			}
		}
		return map;
	}

	/**
	 * 从表结构中获取该表所有属性的List集合。
	 * <p>
	 * 通过表结获取该表所有属性的List集合。<br>
	 * 这个方法返回的集合是有序序列。
	 * </p>
	 * 
	 * */
	public final static ArrayList<String> getFieldList(ResultSet rs) {
		ArrayList<String> list = new ArrayList<String>();
		if (rs != null) {
			try {
				while (rs.next()) {
					list.add(rs.getString(SQL_FIELD));
				}
			} catch (SQLException e) {
				System.out.println("获取表中列名集合时出现异常：" + e.getMessage());
				log.print(e);
			}
		}
		return list;
	}

	/**
	 * 从表结构中获取该表所有属性类型的List集合。
	 * <p>
	 * 通过表结获取该表所有属性类型的List集合。<br>
	 * 这个方法返回的集合是有序序列。
	 * </p>
	 * 
	 * */
	public final static ArrayList<String> getTypeList(ResultSet rs) {
		ArrayList<String> list = new ArrayList<String>();
		if (rs != null) {
			try {
				while (rs.next()) {
					list.add(rs.getString(SQL_TYPE));
				}
			} catch (SQLException e) {
				System.out.println("获取表中类型集合时出现异常：" + e.getMessage());
			}
		}
		return list;
	}

	/**
	 * 根据list，遍历出所有的Field(列名)的值，返回列名数组。
	 * <p>
	 * 事例：<br>
	 * init0(); getConnection(); ResultSet design = getDesign("t_user");
	 * ArrayList&lt;String&gt; fieldList = getFieldList(design); String[] colsName =
	 * getColsName(fieldList); for (String string : colsName) {
	 * System.out.println(string); }
	 * </p>
	 * 
	 * */
	public final static String[] getColsName(ArrayList<String> list) {
		String[] cols = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			cols[i] = (String) list.get(i);
		}
		return cols;
	}

	/**
	 * 该方法代替已过时的getColsName方法。
	 * <p>
	 * 通过配置文件中的数据库与传入的表名参数获取表中各字段组成的数组。
	 * </p>
	 * 
	 * @param tabName
	 * @return
	 */
	public final static String[] getColsName(String tabName) {
		ResultSet rs = getDesign(tabName);
		ArrayList<String> list = getFieldList(rs);
		String[] cols = getColsName(list);
		return cols;
	}

	/**
	 * 封装底层的方法，根据数据库名和表名返回表中列名数组。
	 * <p>
	 * <b>该方法已过时。参数dbName只要不是order就会被架空</b> 另请参见Core2#getColsName(String)方法。
	 * </p>
	 * 
	 * ·@param dbName 数据库名称
	 * 
	 * @param tabName
	 *            表名
	 * @see Core#getColsName(String)
	 * */
	@Deprecated
	public final static String[] getColsName(String dbName, String tabName) {
		ResultSet rs = getDesign(dbName, tabName);
		ArrayList<String> list = getFieldList(rs);
		String[] cols = getColsName(list);
		return cols;
	}

	/**
	 * 根据list,遍历所有的Type的值
	 * <p>
	 * 事例：<br>
	 * init0(); getConnection(); ResultSet design = getDesign("t_user");
	 * ArrayList&lt;String&gt; typeList = getTypeList(design); String[] types =
	 * getTypes(typeList); for (String string : types) {
	 * System.out.println(string); }
	 * </p>
	 * 
	 * @return String[] 类型数组
	 * */
	public final static String[] getTypes(ArrayList<String> list) {
		String[] types = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			types[i] = (String) list.get(i);
		}
		return types;
	}

	/**
	 * 该方法代替getTypes(String dbName, String tabName)方法。
	 * <p>
	 * 通过输入表名获取该表全部属性类型的数组。
	 * </p>
	 * <p>
	 * 事例：<br>
	 * init0(); getConnection(); String[] types = getTypes("t_user"); for
	 * (String string : types) { System.out.println(string); }
	 * </p>
	 * 
	 * @param tabName 表名
	 * @return String[] 表名数组
	 */
	public final static String[] getTypes(String tabName) {
		ResultSet rs = getDesign(tabName);
		ArrayList<String> list = getTypeList(rs);
		String[] types = getColsName(list);
		return types;
	}

	/**
	 * <b>该方法已过时。参数dbName无意义。</b>
	 * <p>
	 * 现由getTypes(String tabName)代替。
	 * </p>
	 * 
	 * @param dbName
	 *            数据库名称
	 * @param tabName
	 *            表名
	 * @return String[] String[]
	 * @see Core#getTypes(String)
	 * */
	@Deprecated
	public final static String[] getTypes(String dbName, String tabName) {
		ResultSet rs = getDesign(dbName, tabName);
		ArrayList<String> list = getTypeList(rs);
		String[] types = getColsName(list);
		return types;
	}

	/**
	 * 首字母大写
	 * 
	 * 因为数据库不区分大小写，而java严格区分大小写，所以不能直接根据遍历的表名数组来创建java文件
	 * 而是把遍历表名数组得到的表名经过首字母大写处理后再去创建实体类文件，这样符合java的命名规范。
	 * 
	 * @param name 字符串
	 * @return String 字符串
	 * */
	public final static String captureName(String name) {
		if (isLowwer(name) == true) {
			char[] cs = name.toCharArray();
			cs[0] -= 32;
			return String.valueOf(cs);
		} else {
			return name;
		}

	}

	private static void out(String str, boolean flag) {
		if (flag) {
			System.out.println(str);
		}
	}

	/**
	 * 创建实体类文件的方法
	 * 
	 * <p>
	 * 该方法用于自动创建用户指定的数据库里各表的实体类 创建成功后请刷新工程。<br>
	 * 事例：<br>
	 * init0(); getConnection(); vis=true; createFile("T_user", "/src");
	 * </p>
	 * 
	 * @param fileName 文件名
	 * @param filepath 路径
	 * */
	public final static void createFile(String fileName, String filepath) {
		String javaFileName = Core.captureName(fileName);
		File directory = new File("");
		String path = null;
		try {
			path = directory.getCanonicalPath() + filepath;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
		javaFileName += FILE_SUFFIX;
		out("Creating javabean:" + javaFileName, vis);
		File file = new File(f, javaFileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
				out(javaFileName + " was Created", vis);
			} catch (IOException e) {
				log.print(e,
						"创建实体类文件时出现空指针异常，位置:com.sat4j.core.Core2.createFile");
				e.printStackTrace();
			}
		}
	}

	/**
	 * <b>该方法已过时。</b><br>
	 * <p>
	 * default-relevance.properties配置文件被封装成了Map&lt;K,V&gt;集合。
	 * 其中K就是键名,V就是键值,比如K=String;V=char,varchar,text。
	 * 这个方法就是把键值V根据列表分隔符转化成一个字符串型的数组。 这个方法参考百度。将一串由列表分隔符连接而成的字符串转换成字符串数组。
	 * 比如：String text="char,varchar,text"; 将text作为参数调用该方法的结果为:String[]
	 * sqlTypeArray={"char","varchar","text"};
	 * </p>
	 * <p>
	 * 该功能在meta中使用ResultSetMetaData的实例实现。通过它的方法getColumnName，getColumnClassName
	 * 可分别获取到字段名和字段类型。
	 * </p>
	 * 
	 * @param mapValue 配置文件default-relevance.properties的每个键的键值
	 * @return String[] sqlTypeArray sql类型数组
	 * */
	@Deprecated
	public final static String[] substringToArray(String mapValue) {
		StringTokenizer st = new StringTokenizer(mapValue, ",");
		String[] sqlTypeArray = new String[st.countTokens()];
		int i = 0;
		while (st.hasMoreTokens()) {
			sqlTypeArray[i++] = st.nextToken();
		}
		return sqlTypeArray;
	}

	/**
	 * 判断首字母是否为小写
	 * <p>
	 * 关于首字母，若已经是大写的，比如"P"，那么就不需要执行char[i]-32; 否则，结果首字母会会变为"0"，这样做不是预期效果。
	 * </p>
	 * <p>
	 * captureName方法应该先对首字母进行判断，判断是否是小写。 该方法功能是对一个字符串的首字母进行判断，判断其是否为小写，
	 * 若是则返回true,若不是则返回false。<br>
	 * 在ASCII码中97-122号为26个小写英文字母，据此来判断单个字符是否为小写。
	 * 这里说明为什么需要这个方法：因为在以前的SAT4j版本中，没有这个方法时，出现了一个小错误,
	 * 当时数据库中的某张表的表名的首字母是"P",就是说它已经是大写的了,在执行了方法captureName后出现了不符合预期的结果。
	 * 因为不知道用户的数据库中的表名的首字母是否是大写，而实体类的类名是按照表名生成的并且java中的命名规范要求类名
	 * 首字母必须大写,所以要先判断表名的首字母是否是小写,若是,则执行首字母大写的方法captureName,否则不执行。
	 * </p>
	 * <p>
	 * 事例：<br>
	 * System.out.println(isLowwer("t_user"));
	 * System.out.println(isLowwer("T_user"));
	 * </p>
	 * 
	 * @param table 表名
	 * @return boolean 首字母是否小写
	 * */
	public final static boolean isLowwer(String table) {
		byte[] b = table.getBytes();
		return (b[0] >= 97 && b[0] <= 122) == true ? true : false;
	}

	/**
	 * 该方法已过时。请参见meta-sql-1.0.jar<br>
	 * <p>
	 * 自动生成实体类的方法现为meta中的EntityWriter.autobeans()。
	 * </p>
	 * <p>
	 * 该方法的目的是实现自动生成实体类。 自动生成实体类的核心部分，把核心代码单独封装在这个类中。
	 * 首先根据实体类文件名和文件路径创建空的实体类文件，之后按照规则写入内容。
	 * 这段核心代码原本是写在类FrameRoot里的方法autoBeans()里的。这里做了核心代码的提取工作。
	 * 将核心代码单独封装在核心类中并且私有化,通过方法getOutputBeans将该功能暴露到外部,再在上层的类 中调用暴露出去的方法即可。
	 * </p>
	 * 
	 * @param String
	 *            [] tabNames 数据库表名数组
	 * @param String
	 *            path 实体类文件的相对路径
	 * @param Stirng
	 *            dbName 数据库名称
	 * @param String
	 *            packName 实体类文件的目标包名
	 * */
	@Deprecated
	private final static void outputBeans(String[] tabNames, String path,
			String dbName, String packName) {
		for (String fileName : tabNames) {
			createFile(fileName, path);
			WriteFileManager.writer(dbName, path, fileName, packName);
		}
	}

	/**
	 * 该方法已过时。请参见meta-sql-1.0.jar<br>
	 * <p>
	 * 自动生成实体类的方法现为meta中的EntityWriter.autobeans()。
	 * </p>
	 * @throws ClassNotFoundException 
	 * */
	@Deprecated
	private final static void autoBeans0() throws ClassNotFoundException {
		String dbName, path;
		String[] tabNames;
		FrameRoot fr = new FrameRoot();
		Map<String, String> beanMap = ConstsManager.getBeanMap();
		path = beanMap.get("path");
		Map<String, String> connMap = ConstsManager.getConnMap();
		dbName = connMap.get("dbName");
		String packName = formatToPackage(path);
		tabNames = fr.getTablesName(dbName);
		outputBeans(tabNames, path, dbName, packName);
		System.out.println("所有文件创建成功请刷新您的工程！");
		log.print("你已成功创建了数据库" + dbName + "中的实体类");
	}

	/**
	 * 路径格式转报名格式
	 * windows文件系统的路径格式为:com\a\b\c,在java中的路径格式为:com.a.b.c;所以需要将配置文件javabean
	 * .properties 中的文件系统的路径格式转换为java中的路径格式。
	 * */
	private final static String formatToPackage(String path) {
		String packName;
		path = path.substring(5); // /path的完全格式是\src\xxx\xxx...
		packName = path.replace('\\', '.');
		return packName;
	}

	/**
	 * 该方法已过时。请参见meta-sql-1.0.jar<br>
	 * <p>
	 * 自动生成实体类的方法现为meta中的EntityWriter.autobeans()。
	 * </p>
	 * @throws ClassNotFoundException 
	 * */
	@Deprecated
	public final static void autoBeans() throws ClassNotFoundException {
		autoBeans0();
	}

	/**
	 * 从实体类对象获取表名
	 * */
	private final static String getTabName(Object bean) {
		String string = bean.getClass().getName();
		int i = string.lastIndexOf('.');
		return string.substring(i + 1).toLowerCase();
	}
	private static String fullName(Object obj){
		return obj.getClass().getName();
	}
	
	/**
	 * 获取当前系统时间,该方法返回一个java.sql.Date的一个实例。
	 * 
	 * @return java.sql.Date 日期
	 * @since 2.0
	 */
	public static java.sql.Date currentDate(){
		return new java.sql.Date(System.currentTimeMillis());
	}
	
	// insert 自动实现注册的方法 17/5/21-22
	/**
	 * 自动向数据库中插入一条记录。
	 * <p>
	 * 每个实体类是每张表的映射。该方法传入一个实体类对象便可通过该对象获取表名也
	 * 可以通过这个对象反射出各个 属性。执行自动拼接的sql语句。
	 * </p>
	 * <p>
	 * 该方法的参数是一个实体类对象，诸如"User,Student..."之类的对象,在定义这个方法时还不确定参数具体是谁的实例，
	 * 利用多态，这些对象统一都是<tt>Object</tt>类的实例。所以定义参数的类型为Object。<br>
	 * 执行过程：首先根据传入的对象获取该对象的全类名，再根据全类名得到它的Class对象，然后通过反射得到实体类对象中的所有getter方法。
	 * 将这些方法存储在Map集合中。之后通过循环拼接出sql语句。最后执行这个sql语句。
	 * </p>
	 * <p>
	 * 该方法的过程比较复杂，核心的思想是高级for循环和反射的混合使用。这个方法目前还处于初级阶段，需要处理各种异常。
	 * 比较复杂的是拼接sql语句，其中有这么一个算式：<br>
	 * String
	 * sql="('"+getField1()+"','"+getField2()+"','"+...+"','"+getFieldn();+"')";
	 * <br>
	 * 对以上式子的分析：从getField1()到getFieldn()都是实体类中除了getClass()的其他所有get方法。
	 * 注意它们的返回类型不一定相同。 但是无论它们的返回类型是什么都不影响它们可以直接拼接sql语句。
	 * </p>
	 * 
	 * 分界线
	 * <hr>
	 * 2.0版本后的注释
	 * 这里的参数bean，通常由struts的属性或模型驱动来获取实体对象。通过struts将表单处理为对象。
	 * 以面向对象的思想，将对象当做参数传入，即可将该实体的数据存储到数据库所对应的表的记录当中。
	 * 
	 * 
	 * @param bean
	 *            实体类对象
	 * @param conn
	 *            连接对象
	 * @return int 受影响行数      
	 * @since 2.0
	 * */
	@Requiredment /* 该方法需提出sql，并考虑id自增与id非自增的情况, 批量插入 18.5.13  */
	/* 遍历每个字段是否自增，若是则不加入sql语句  rs.getMetaData().isAutoIncrement(column)*/
	public static int insert(Object bean, Connection conn) {
		if (conn == null) {
			throw new NullConnectionException("数据库未连接！");
		}
		if (bean == null) {
			throw new NullPointerException(
					"你正试图向表中插入一条记录，但这条记录是空的！SAT4j终止了你的操作，请检查对象：[" + bean
							+ "]是否为空。");
		}
		int n = 0;	//受影响的行数
		// 1.获取类的全名
		String beanName = fullName(bean);
		// 2.通过反射，得到所有的属性名
		Class<?> c = null;
		try {
			c = Class.forName(beanName);
		} catch (ClassNotFoundException e1) {
		}
		Method[] method = c.getMethods();
		// 3.通过反射获取属性值
		List<Method> list = new ArrayList<Method>();
		for (Method method2 : method) {
			String methodName = method2.getName();
			
			@Requiredment /* 以下两句可提出字符串作为参数  	18.5.13 */
			boolean f1 = methodName.startsWith(METHOD_PREFIX);
			boolean f2 = methodName.endsWith(METHOD_SUFFIX);
			if(f1 && !f2){	//只获取get开头但结尾不为Class的方法
				list.add(method2);
			}
		}
		int count0 = list.size();
		// 4.循环生成sql语句的成分
		String sql = "";
		// INSERT INTO tbl_name (col1,col2) VALUES(15,col1*2);
		try {
			StringJoiner sj = new StringJoiner(",");
			for (int i = 0; i < count0; i++) {
				Object invoke = list.get(i).invoke(bean);
				sj.add("'"+invoke.toString()+"'");
			}
			sql = " ( "+sj.toString()+" ) ";
		} catch (IllegalAccessException e) {
			log.print(e);
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			log.print(e);
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			log.print(e);
			e.printStackTrace();
		}
		String colList = "";
		StringJoiner sj2 = new StringJoiner(",");
//		Field[] declaredFields = c.getDeclaredFields();
//		for (Field field : declaredFields) {
//			System.out.println("test : field = "+field.getName());
//			sj2.add(field.getName());
//		}
		for (int j = 0; j < list.size(); j++) {
			String fieldName = list.get(j).getName();
			String field = fieldName.substring(METHOD_PREFIX.length());
			sj2.add(field);
		}
		colList = "(" + sj2.toString() + ") ";
		sql = "insert into " + getTabName(bean) + colList + " values" + sql
				+ ";";
		out("SQL:\n\t"+sql, vis);
		try {
			stat = conn.createStatement();
			n = stat.executeUpdate(sql);
			if(n>=1){
				System.out.println("受影响的行数：" + n);
				log.print("成功插入一条数据，受影响的行数：" + n);
			}else{
				System.out.println("插入记录失败");
				log.print("插入记录失败");
			}
		} catch (DataTruncation e) {
			log.print(e, "插入记录时字段超长！" + e.getMessage());
			throw new RuntimeException("插入记录时字段超长！" + e.getMessage());
		} catch (SQLIntegrityConstraintViolationException e) {
			if (e.getErrorCode() == 1062) {
				System.out.println("主键重复！" + e.getMessage());
			}
		} catch (SQLException e) {
			log.print(e, "自动插入数据时出现了未知的异常！");
			e.printStackTrace();
		}
		return n;
	}

	/**
	 * 根据用户的配置自动完成身份验证。
	 * <p>
	 * 该方法实现了抽象类<tt>AbsFunction</tt>中的方法。
	 * </p>
	 * 
	 * @param paramId
	 *            需要验证的参数1，比如用户名
	 * @param paramPwd
	 *            需要验证的参数2，比如密码
	 * @param conn
	 *            连接对象
	 * */
	/* 
	 * 支持多种角色的自动登录，1.3版本的Sat4j只能支持一种角色的身份验证配置 ，
	 * 比如当存在用户和管理员这两种角色时
	 * 1.3版本的sat4j只能选择其一进行身份验证的配置  
	 * 
	 * 还应考虑到角色状态
	 * */
	@Requiredment 
	@Override
	public boolean checkStatus(Object paramId, Object paramPwd, Connection conn) {
		String tabName = "", col1 = "", col2 = "";
		// 1.获取配置文件的映射
		Map<String, String> beanMap = ConstsManager.getBeanMap();
		// 2.获取CheckTable键值
		String checkTableString = beanMap.get("checkTableString");
		// 3.字符串转为数组
		String[] checkTable = checkTableString.split(",");
		// 4.获取子字符串
		try {
			tabName = checkTable[0];
			col1 = checkTable[1];
			col2 = checkTable[2];
		} catch (ArrayIndexOutOfBoundsException e) {
			log.print(e);
			throw new RuntimeException(
					"配置文件参数有错误！在配置文件javabean.properties中的CheckTable的键值应该是：表名，列名1，列名2");
		}

		// 5.以下是拼接sql语句的核心代码
		// String
		// sql="select * from "+tabName+"("+col1+","+col2+")"+" where "+col1+"='"+paramId+"' and "+col2+"='"+paramPwd+"'";
		String sql = "select " + col1 + " from " + tabName + " where " + col1
				+ "='" + paramId + "' and " + col2 + "='" + paramPwd + "';";
		System.out.println(sql);
		Object colObj = null;
		try {
			stat = conn.createStatement();
			rs = stat.executeQuery(sql);
		} catch (SQLSyntaxErrorException e) {
			if (e.getErrorCode() == 1146) {
				log.print(e, "CheckTable的键值配置有误：表[" + tabName + "]不存在！");
				throw new RuntimeException("CheckTable的键值配置有误：表[" + tabName
						+ "]不存在！");
			} else if (e.getErrorCode() == 1054) {
				log.print(e, "CheckTable的键值配置有误:列[" + col1 + "]或[" + col2
						+ "]不存在！");
				throw new RuntimeException("CheckTable的键值配置有误:列[" + col1
						+ "]或[" + col2 + "]不存在！");
			} else {
				log.print(e,
						"自动验证身份是发生的未知异常！该异常信息已被日志记录！异常号：" + e.getErrorCode()
								+ " 异常信息：" + e.getMessage());
				throw new RuntimeException("自动验证身份是发生的未知异常！该异常信息已被日志记录！"
						+ e.getMessage());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (rs != null) {
			try {
				while (rs.next()) {
					colObj = rs.getObject(col1);
				}
				// System.out.println(colObj);
				if (colObj != null) {
					return true;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			return false;
		}
		return false;
	}

	/**
	 * 该方法已过时。请使用String.split(","):String[]
	 * */
	@Override
	@Deprecated
	public String[] transformToArray(String string) {
		return substringToArray(string);
	}

	/**
	 * 根据用户的配置自动完成身份验证
	 * 
	 * @param paramId
	 *            需要验证的参数1，比如用户名
	 * @param paramPwd
	 *            需要验证的参数2，比如密码
	 * @return boolean 验证结果
	 * */
	public final static boolean check(Object paramId, Object paramPwd) {
		try {
			conn = new FrameRoot().getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return new Core().checkStatus(paramId, paramPwd, conn);
	}

	/**
	 * 通过表名将数据存入ResultSet中。
	 * 
	 * @param tabName
	 *            表名
	 * @return ResultSet 表结果集
	 * */
	public final static ResultSet selectResultSet(String tabName) {
		if(conn == null){
			throw new NullConnectionException("connection:null");
		}
//		try {
//			//conn = new FrameRoot().getConnection();
//		} catch (SQLException e1) {
//			e1.printStackTrace();
//		} catch (ClassNotFoundException e1) {
//			e1.printStackTrace();
//		}
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "select * from " + tabName;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs != null) {
				return rs;
			} else {
				throw new RuntimeException("表的结果为空！" + tabName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * 通过表名和列名字符串将数据存入ResultSet中。
	 * 
	 * @param tabName
	 *            表名
	 * @param cols
	 *            列名列表字符串，例如：name,sex,...
	 * @return ResultSet 表结果集
	 * */
	public final static ResultSet selectResultSet(String tabName, String cols) {
		try {
			conn = new FrameRoot().getConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "select " + cols + " from " + tabName;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs != null) {
				return rs;
			} else {
				throw new RuntimeException("表的结果为空！" + tabName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * @param tabName 表名
	 * @param conditionStatement 查询条件
	 * @return ResultSet 表结果集
	 * */
	public final static ResultSet selectResultSetByCondition(String tabName,
			String conditionStatement) {
		// Connection conn = null; 测试通过
		try {
			conn = new FrameRoot().getConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "select * from " + tabName + " where "
				+ conditionStatement;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs != null) {
				return rs;
			} else {
				throw new RuntimeException("表的结果为空！" + tabName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * 该方法将ResultSet结果集转换为List集合。
	 * <p>
	 * 参考：http://jingyan.baidu.com/article/8065f87f80dd5c233124980f.html
	 * </p>
	 * 
	 * @param rs 表结果集
	 * @return List&lt;Map&lt;String, Object&gt;&gt; 结果集合
	 * @throws SQLException SQL异常
	 * */
	public final static List<Map<String, Object>> convertList(ResultSet rs)
			throws SQLException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		ResultSetMetaData md = rs.getMetaData();
		int columnCount = md.getColumnCount();
		while (rs.next()) {
			Map<String, Object> rowData = new HashMap<String, Object>();
			for (int i = 1; i <= columnCount; i++) {
				rowData.put(md.getColumnName(i), rs.getObject(i));
			}
			list.add(rowData);
		}
		return list;
	}

	/**
	 * 通过表名和条件语句来删除表中的一条记录，返回0则删除失败，返回1则删除成功。
	 * 
	 * @param tabName
	 *            表名
	 * @param statement
	 *            条件语句
	 * @return int 受影响的行数
	 * */
	public final static int delete(String tabName, String statement) {
		Statement stmt = null;
		int count = 0; // 0 means fail
		String sql = "delete from " + tabName + " where " + statement;
		try {
			conn = new FrameRoot().getConnection();
			stmt = conn.createStatement();
			count = stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 通过表名，修改语句和条件语句来更新表中的一条记录，返回0则更新失败，返回1则更新成功。
	 * 
	 * @param tabName
	 *            表名
	 * @param updateStatement
	 *            更新语句
	 * @param conditionStatement
	 *            条件语句
	 * @return int 受影响行数
	 * */
	public final static int update(String tabName, String updateStatement,
			String conditionStatement) {
		int count = 0; // 0 means fail
		String sql = "update " + tabName + " set " + updateStatement
				+ " where " + conditionStatement;
		try {
			conn = new FrameRoot().getConnection();
			stat = conn.createStatement();
			count = stat.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 获取当前时间，格式设置为yyyy-MM-dd,并转换成mysql中的类型。
	 * 参考：http://www.cnblogs.com/zhaotiancheng/p/6413067.html
	 * <p>
	 * 获取当前系统时间，并转换成mysql数据库所支持的格式。
	 * </p>
	 * 
	 * @param dateFormat
	 *            设置日期格式
	 * @return java.sql.Date 日期
	 * */
	public final static java.sql.Date transformTime(String dateFormat) {
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		String time = format.format(new Date());
		java.sql.Date timePara = null;
		try {
			timePara = new java.sql.Date(format.parse(time).getTime());
			System.out.println(timePara);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timePara;
	}
}
