package com.mes.exc.server.service.po.exc.base;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 异常地点类型
 * @author ShrisJava
 *
 */
public class EXCStationType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public long ID;
	/**
	 * 类型名称
	 */
	public String Name;
	/**
	 * 关联类型 可不使用
	 */
	public int RelevancyType;

	/**
	 * 创建人
	 */
	public long CreatorID;
	/**
	 * 创建时间
	 */
	public Calendar CreateTime;

	/**
	 * 录入人
	 */
	public long EditorID;
	/**
	 * 录入时间
	 */
	public Calendar EditTime;

	/**
	 * 状态
	 */
	public int Active;

	public EXCStationType() {
		CreateTime = Calendar.getInstance();
		EditTime = Calendar.getInstance();
		Name = "";
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public int getRelevancyType() {
		return RelevancyType;
	}

	public void setRelevancyType(int relevancyType) {
		RelevancyType = relevancyType;
	}

	public long getCreatorID() {
		return CreatorID;
	}

	public void setCreatorID(long creatorID) {
		CreatorID = creatorID;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public long getEditorID() {
		return EditorID;
	}

	public void setEditorID(long editorID) {
		EditorID = editorID;
	}

	public Calendar getEditTime() {
		return EditTime;
	}

	public void setEditTime(Calendar editTime) {
		EditTime = editTime;
	}

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}
}
