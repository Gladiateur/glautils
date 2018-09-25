/*
 * HandlerFactory.java 18/07/01
 */

package gla.meta;

import gla.annotation.AutobeansEntityConfig;
import gla.annotation.AutobeansMySQLConfig;
import gla.debug.Debug;
import gla.debug.DebugImpl;

import java.util.Map;

public class HandlerFactory{
	private Debug debugLogger = new DebugImpl();
	private String className;
	
	public HandlerFactory(String className){
		this.className = className;
	}
	
	public ConfigHandler getHandler(){
		AutobeansMySQLConfig mysqlConfigAnno = null;
		AutobeansEntityConfig entityAnno = null;
		try {
			mysqlConfigAnno = Class.forName(className)
					.getAnnotation(AutobeansMySQLConfig.class);
			entityAnno = Class.forName(className)
					.getAnnotation(AutobeansEntityConfig.class);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if((mysqlConfigAnno != null) && (entityAnno != null)){
			debugLogger.debug("Debug-getHandler [ConfigHandler:AnnotationHandler]");
			return new AnnotationHandler(mysqlConfigAnno, entityAnno);
		}else{
			debugLogger.debug("Debug-getHandler [ConfigHandler:PropertiesHandler]");
			Map<String, String> connMap = ConstsManager.getConnMap();
			Map<String, String> beanMap = ConstsManager.getBeanMap();
			return new PropertiesHandler(connMap, beanMap);
		}
		
	}
}