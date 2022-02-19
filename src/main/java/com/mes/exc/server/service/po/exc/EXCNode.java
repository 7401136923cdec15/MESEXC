package com.mes.exc.server.service.po.exc;

import java.io.Serializable;
import java.util.Calendar;

/**
 * EXC节点
 * 
 * @author ShrisJava
 *
 */
public class EXCNode implements Serializable {
	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 状态文本
	 */
	public String StatusText = "";
	/**
	 * 人员名称
	 */
	public String UserName = "";
	/**
	 * 部门名称
	 */
	public String DepartmentName = "";
	/**
	 * 编辑时刻
	 */
	public Calendar EditTime = Calendar.getInstance();
	/**
	 * 编辑时刻文本
	 */
	public String EditTimeText = "";
	/**
	 * 是否完成
	 */
	public boolean IsFinish;
	/**
	 * 节点类型：1：开始；2：中间；3：结束
	 */
	public int NodeType;
	/**
	 * 备注
	 */
	public String Remark = "";

	public EXCNode() {
		EditTime.set(2000, 1, 1);
	}

	public String getStatusText() {
		return StatusText;
	}

	public void setStatusText(String statusText) {
		StatusText = statusText;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getDepartmentName() {
		return DepartmentName;
	}

	public void setDepartmentName(String departmentName) {
		DepartmentName = departmentName;
	}

	public Calendar getEditTime() {
		return EditTime;
	}

	public void setEditTime(Calendar editTime) {
		EditTime = editTime;
	}

	public String getEditTimeText() {
		return EditTimeText;
	}

	public void setEditTimeText(String editTimeText) {
		EditTimeText = editTimeText;
	}

	public boolean isIsFinish() {
		return IsFinish;
	}

	public void setIsFinish(boolean isFinish) {
		IsFinish = isFinish;
	}

	public int getNodeType() {
		return NodeType;
	}

	public void setNodeType(int nodeType) {
		NodeType = nodeType;
	}

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}
}
