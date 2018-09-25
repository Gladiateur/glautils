/*
 * AutobeansEntityConfig.java 18/6/27
 */
package gla.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明将生成的实体类的相关信息。
 * 该注解用于代替javabean.properties配置文件。
 * 
 * @author Gladiateur
 * @since 1.4
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutobeansEntityConfig{
	String path();
	String tables();
	String constructorStyle() default "0";
}
