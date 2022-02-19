package com.mes.exc.server.service.po.exc.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EXCAlarmRule implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public long ID;

	/**
	 * 报警代号
	 */
	public String AlarmCode;

	/**
	 * 报警ID
	 */
	public long AlarmID;

	/**
	 * 位置点类型
	 */
	public long StationType;

	/**
	 * 位置点ID
	 */
	public long StationID;

	/**
	 * 位置点编号
	 */
	public String StationNo;

	/**
	 * 触发异常类型
	 */
	public List<Long> ExceptionTypeList;

	/**
	 * 响应等级
	 */
	public int RespondLevel;

	/**
	 * 内容显示文本
	 */
	public String AlarmText;

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
	///
	/// 录入时间
	///
	public Calendar EditTime;

	/**
	 * 状态
	 */
	public int Active;

	public EXCAlarmRule() {
		CreateTime = Calendar.getInstance();
		EditTime = Calendar.getInstance();
		AlarmCode = "";
		AlarmText = "";
		ExceptionTypeList = new ArrayList<Long>();
		StationNo = "";
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public String getAlarmCode() {
		return AlarmCode;
	}

	public void setAlarmCode(String alarmCode) {
		AlarmCode = alarmCode;
	}

	public long getAlarmID() {
		return AlarmID;
	}

	public void setAlarmID(long alarmID) {
		AlarmID = alarmID;
	}

	public long getStationType() {
		return StationType;
	}

	public void setStationType(long stationType) {
		StationType = stationType;
	}

	public long getStationID() {
		return StationID;
	}

	public void setStationID(long stationID) {
		StationID = stationID;
	}

	public String getStationNo() {
		return StationNo;
	}

	public void setStationNo(String stationNo) {
		StationNo = stationNo;
	}

	public List<Long> getExceptionTypeList() {
		return ExceptionTypeList;
	}

	public void setExceptionTypeList(List<Long> exceptionTypeList) {
		ExceptionTypeList = exceptionTypeList;
	}

	public int getRespondLevel() {
		return RespondLevel;
	}

	public void setRespondLevel(int respondLevel) {
		RespondLevel = respondLevel;
	}

	public String getAlarmText() {
		return AlarmText;
	}

	public void setAlarmText(String alarmText) {
		AlarmText = alarmText;
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
