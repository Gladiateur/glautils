/*
 * PropertiesHandler.java 18/06/28
 */
package gla.meta;

import gla.debug.Debug;
import gla.debug.DebugImpl;

import java.util.Map;

import com.sat4j.user.FrameRoot;

/**
 * PropertiesHandler
 * 
 * @author Gladiateur
 * @since 1.4
 */
public class PropertiesHandler implements ConfigHandler {

	private Map<String, String> connMap;
	private Map<String, String> beanMap;
	private static ConditionBean conditionBean = EntityWriter.getConditionBean();
	private Debug debugLogger = new DebugImpl();
	
	public PropertiesHandler(Map<String, String> connMap,
			Map<String, String> beanMap) {
		this.connMap = connMap;
		this.beanMap = beanMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gla.meta.ConfigHandler#doConfig()
	 */
	@Override
	public void doConfig() {
		String databaseName = connMap.get("dbName");
		conditionBean.setTextPackage(EntityWriter.declarePackage(
				EntityWriter.formatToPackage(beanMap.get("path"))).toString());
		conditionBean.setTextNotes(EntityWriter.notes().toString());
		//-------------
		conditionBean.setPath(beanMap.get("path").replaceFirst("\\\\", ""));
		debugLogger.debug("Debug-doConfig PATH = "+beanMap.get("path").replaceFirst("\\\\", ""));
		
		conditionBean.setTables(beanMap.get("tables"));
		conditionBean.setConstructorStyle(Integer.parseInt(beanMap
				.get("constructor-style")));
		String[] allTables = Utils.getAllTables(new FrameRoot(), databaseName);
		conditionBean.setAllTables(allTables);
		EntityWriter.setConditionBean(conditionBean);
	}

}
