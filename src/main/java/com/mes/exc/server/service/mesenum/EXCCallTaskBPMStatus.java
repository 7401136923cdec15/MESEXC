package com.mes.exc.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.exc.server.service.po.cfg.CFGItem;

public enum EXCCallTaskBPMStatus {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 待处理呼叫
	 */
	ToHandle(1, "待处理呼叫"),
	/**
	 * 已确认
	 */
	NomalClose(20, "已确认"),
	/**
	 * 异常关闭
	 */
	ExceptionClose(21, "异常关闭"),
	/**
	 * 已撤销
	 */
	Canceled(23, "已撤销");

	private int value;
	private String lable;

	private EXCCallTaskBPMStatus(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static EXCCallTaskBPMStatus getEnumType(int val) {
		for (EXCCallTaskBPMStatus type : EXCCallTaskBPMStatus.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return null;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (EXCCallTaskBPMStatus type : EXCCallTaskBPMStatus.values()) {
			CFGItem wItem = new CFGItem();
			wItem.ID = type.getValue();
			wItem.ItemName = type.getLable();
			wItem.ItemText = type.getLable();
			wItemList.add(wItem);
		}
		return wItemList;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
