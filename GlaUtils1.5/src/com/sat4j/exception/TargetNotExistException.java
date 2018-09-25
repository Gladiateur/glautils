/*
 * @(#)TargetNotExistException.java	1.0 18/05/12
 *
 * Copyright 2018 Gladiateur, Inc. All rights reserved.
 */

package com.sat4j.exception;

/**
 * <p>
 * 该异常为目标不存在异常。可能是数据库，表等。
 * </p>
 * 
 * 
 * @author Gladiateur
 * @since 2.0
 * @since jdk 1.8
 */
public class TargetNotExistException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	/**
	 * 该类<code>TargetNotExistException</code>没有详细信息的构造函数。 
	 * */
	public TargetNotExistException() {
		super();
	}
	
	/**
	 * 该类<code>TargetNotExistException</code>包含异常详细信息的构造函数。 
	 * 
	 * @param msg 异常详细信息
	 * */
	public TargetNotExistException(String message) {
		super(message);
	}
	
}
