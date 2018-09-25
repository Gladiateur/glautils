/*
 * ConditionBean.java 18/07/01
 */

package gla.meta;

public class ConditionBean {
	/* db config */
	private String[] allTables; //数据库中全部表的表名数组

	/* create condition */
	private String path;//实体类生成路径
	private boolean keyTablesFlag;//是否为"*"
	private boolean keyTablesExclusive;//是否为反选

	/* user config */
	private int constructorStyle;//构造器样式
	private String tables;//配置的表的表达式

	/* code */
	private String textPackage ;//生成的声明包的代码段
	private String textNotes;//生成的注释部分
	public String[] getAllTables() {
		return allTables;
	}
	public void setAllTables(String[] allTables) {
		this.allTables = allTables;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public boolean isKeyTablesFlag() {
		return keyTablesFlag;
	}
	public void setKeyTablesFlag(boolean keyTablesFlag) {
		this.keyTablesFlag = keyTablesFlag;
	}
	public boolean isKeyTablesExclusive() {
		return keyTablesExclusive;
	}
	public void setKeyTablesExclusive(boolean keyTablesExclusive) {
		this.keyTablesExclusive = keyTablesExclusive;
	}
	public int getConstructorStyle() {
		return constructorStyle;
	}
	public void setConstructorStyle(int constructorStyle) {
		this.constructorStyle = constructorStyle;
	}
	public String getTables() {
		return tables;
	}
	public void setTables(String tables) {
		this.tables = tables;
	}
	public String getTextPackage() {
		return textPackage;
	}
	public void setTextPackage(String textPackage) {
		this.textPackage = textPackage;
	}
	public String getTextNotes() {
		return textNotes;
	}
	public void setTextNotes(String textNotes) {
		this.textNotes = textNotes;
	}
}
