/*
 * AutobeansMySQLConfig.java 18/6/27
 */
package gla.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明相关连接数据库信息。
 * 该注解用于代替default-connection.properties配置文件。
 * 
 * @author Gladiateur
 * @since 1.4
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutobeansMySQLConfig{
	String dbName();
	String username();
	String password();
}
