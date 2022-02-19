package com.mes.exc.server.service.po.exc;

import java.io.Serializable;

/**
 * EXC类型等级频次
 * 
 * @author ShrisJava
 *
 */
public class EXCLevelType implements Serializable {
	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 异常级别
	 */
	public String ResponseLevel = "";
	/**
	 * 异常类型
	 */
	public String Type = "";
	/**
	 * 异常频次
	 */
	public int Times = 0;

	public EXCLevelType() {
	}

	public String getResponseLevel() {
		return ResponseLevel;
	}

	public void setResponseLevel(String responseLevel) {
		ResponseLevel = responseLevel;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public int getTimes() {
		return Times;
	}

	public void setTimes(int times) {
		Times = times;
	}
}
