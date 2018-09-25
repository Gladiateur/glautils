/*
 * AnnotationHandler.java 18/06/28
 */

package gla.meta;

import com.sat4j.user.FrameRoot;

import gla.annotation.AutobeansEntityConfig;
import gla.annotation.AutobeansMySQLConfig;
import gla.debug.Debug;
import gla.debug.DebugImpl;

/**
 * AnnotationHandler
 * 
 * @author Gladiateur
 * @since 1.4
 */
public class AnnotationHandler implements ConfigHandler {
	private AutobeansMySQLConfig mysqlConfig;
	private AutobeansEntityConfig entityConfig;
	private static ConditionBean conditionBean = EntityWriter.getConditionBean();
	private Debug debugLogger = new DebugImpl();
	
	public AnnotationHandler(AutobeansMySQLConfig mysqlConfig,
			AutobeansEntityConfig entityConfig) {
		this.mysqlConfig = mysqlConfig;
		this.entityConfig = entityConfig;
	}

	@Override
	public void doConfig() {
		String dbName = mysqlConfig.dbName();
		String username = mysqlConfig.username();
		String password = mysqlConfig.password();
		conditionBean.setConstructorStyle(Integer.parseInt(entityConfig
				.constructorStyle()));
		conditionBean.setTextPackage(EntityWriter.declarePackage(
				EntityWriter.formatToPackage("\\src\\" + entityConfig.path())).toString());
		conditionBean.setTextNotes(EntityWriter.notes().toString());
		conditionBean.setPath("src\\" + entityConfig.path());
		debugLogger.debug("Debug-doConfig PATH = "+"src\\" + entityConfig.path());
	
		conditionBean.setTables(entityConfig.tables());
		String[] allTables = Utils.getAllTables(new FrameRoot(), dbName,
				username, password);
		conditionBean.setAllTables(allTables);
		EntityWriter.setConditionBean(conditionBean);
	}
}
