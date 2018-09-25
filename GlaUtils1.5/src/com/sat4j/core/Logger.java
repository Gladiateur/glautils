/*
 * @(#)Logger.java	17/5/20
 *
 * Copyright 2017 Gladiateur.All rights reserved.
 * 
 * This is a lightweight tool for managing logs.
 * 
 */

package com.sat4j.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 有这个类<code>Logger</code>创建的对象就是一个日志的对象，你可以创建这么一个对象然后调用方法print去保存你想要输出的日志内容。
 * 在这个类当中还包含了一个静态内部类<code>ConstsConfig</code>,由它来加载配置文件logger-cofig.properties。
 * 
 * @author Gladiateur
 * @version v1.0 17/5/21
 * */
public final class Logger {
	
	/**		logFileName  */
	private static String logFileName;
	
	/**		logFilePath	*/
	private static String logFilePath;
	
	/** 	Get current time of system */
	private final static String currentTime=getTime("yyyy-MM-dd");
	
	/**
	 * 这是一个静态内部类<code>ConstsConfig</code>,由这个类来加载配置文件logger-cofig.properties。
	 * */
	public  static class ConstsConfig{
		
		/**	私有化这个类中公共的集合变量logMap */
		private static Map<String,String> logMap=new HashMap<String,String>();
		
		/**	私有化这个类中公共的字符串变量 */
		private static String logFilePath;
		
		/**
		 * 该方法将配置文件ogger-cofig.properties封装成了一个Map集合。
		 * <p>
		 * 你可以这样测试:<br>
		 *	Map<String,String> map=ConstsConfig.loadLoggerConfig();
		 *	System.out.println(map.get("LogFilePath"));
		 * </p>
		 * */
		private final static Map<String,String> loadLoggerConfig() {
			InputStream in = ConstsConfig.class
			.getResourceAsStream("/logger-config.properties");// 获取文件输入流
			Properties prop = new Properties();
			try {
				prop.load(in);
				logFilePath=prop.getProperty("LogFilePath");
			} catch (IOException e) {
				throw new RuntimeException("读取配置文件异常:" + e.getMessage());
			}catch (NullPointerException e) {
				throw new RuntimeException("没有找到配置文件logger-config.propreties");
			}
			logMap.put("LogFilePath", logFilePath);
			return logMap;
		}
	}
	
	/**
	 *	该方法创建日志文件的存放路径。
	 *	<p>
	 *	你可以这样测试：<br>
	 *	builtLog();
	 *	之后你会发现硬盘上多了一个路径logFilePath
	 *	</p>
	 * */
	private final static void builtLog(){
		Map<String,String> logMap=ConstsConfig.loadLoggerConfig();
		logFilePath=logMap.get("LogFilePath");
		File f = new File(logFilePath);
		if (!f.exists()) {
			f.mkdirs();
		}
	}
	
	/**
	 * 该方法获取系统的当前时间，格式为：yyyy-MM-dd。
	 * <p>
	 * 你可以这样测试：<br>
	 * 	Date now = new Date(); 
	 *	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	 *	System.out.println(dateFormat.format(now));
	 * </p>
	 * */
	private final static String getTime(String timeFormat){
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(timeFormat);//可以方便地修改日期格式
		return dateFormat.format(now);
	}
	
	/**
	 * 该方法向日志文件写入内容。
	 * 
	 * @param logFilePath 日志文件的路径
	 * @param content 输出的日志内容
	 * */
	private final static void writer(String logFilePath,String content) {
		Map<String,String> logMap=ConstsConfig.loadLoggerConfig();
		logFilePath=logMap.get("LogFilePath");
		logFileName = "framedriver_log_"+currentTime+".txt";
		FileOutputStream fop = null;
		File file;
		try {
			//System.out.println("logFileName="+logFileName);
			file = new File(logFilePath+"\\"+logFileName);
			//System.out.println("file="+logFilePath+"\\"+logFileName);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
				fop = new FileOutputStream(file);
				byte[] contentInBytes = ("### This is a lightweight tool for managing logs. ###\r\n\r\n"+content+"\r\n").getBytes();
				fop.write(contentInBytes);
				fop.flush();
				fop.close();
				System.out.println("一条日志信息已被记录！");
			}
			else if(file.exists()&& file.length()!=0){
				// get the content in bytes
				FileWriter fw=new FileWriter(file,true);
				fw.write(content+"\r\n");
				fw.flush();
				fw.close();
				System.out.println("一条日志信息已被记录！");
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
	
	/**
	 * 该方法向日志文件写入内容。
	 * <p>
	 * 这个方法可以这样用：<br>
	 * 	Logger log=new Logger();
	 * 	log.print("test log information");
	 * </p>
	 * 
	 * @param content 输出的日志内容
	 * */
	public final void print(String content){
		String time=getTime("yyyy-MM-dd HH:mm:ss");
		String str=time+" "+content;
		builtLog();
		writer(logFilePath,str);
	}
	
	/**
	 * 该方法向日志文件写入内容。
	 * <p>
	 * 这个方法可以这样用：<br>
	 * 	Logger log=new Logger();
	 * 	log.print(Exception e);
	 * </p> 
	 * 
	 * @param e 异常对象
	 * */
	public final void print(Exception e){
		String time=getTime("yyyy-MM-dd HH:mm:ss");
		String content=time+" "+"时出现的异常："+e.toString();
		builtLog();
		writer(logFilePath,content);
	}
	
	/**
	 * 该方法向日志文件写入内容。
	 * <p>
	 * 这个方法可以这样用：<br>
	 *  try {
	 * 		//可能会出现异常的代码
	 * 	} catch (未知或已知的异常 e) {
	 *	 	Logger log=new Logger();
	 * 		log.print(e,"这个异常由于........");
	 *	}
	 * </p>
	 * 
	 * @param e 异常对象
	 * @param content 日志详细内容
	 * */
	public final void print(Exception e,String content){
		String time=getTime("yyyy-MM-dd HH:mm:ss");
		String str=time+" "+"时出现的异常："+e.toString();
		builtLog();
		writer(logFilePath,str+"\t"+content);
	}
	
}