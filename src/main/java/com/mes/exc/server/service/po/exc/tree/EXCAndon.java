package com.mes.exc.server.service.po.exc.tree;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

/**
 * 异常看板
 * 
 * @author ShrisJava
 *
 */
public class EXCAndon implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public long ID;

	/**
	 * 工厂ID
	 */
	public long CompanyID;
	/**
	 * 车间ID
	 */
	public long WorkshopID;
	/**
	 * 产线ID
	 */
	public long LineID;
	/**
	 * 异常点编码
	 */
	public String StationCode;
	/**
	 * 异常类型文本
	 */
	public String EXType;
	/**
	 * 备注
	 */
	public String Comment;
	/**
	 * 班次ID
	 */
	public int ShiftID;

	/**
	 * 允许延长存在班次数 0 不转发 -1 永久转发
	 */
	public int ShiftTimes;
	/**
	 * 创建时间
	 */
	public Calendar CreateTime;
	/**
	 * 编辑时间
	 */
	public Calendar EditTime;

	/**
	 * 设定异常结束时刻 <=2010-1-1 即无
	 */
	public Calendar EndTime = Calendar.getInstance();
	/**
	 * 责任人
	 */
	public List<Long> OperatorID;
	/**
	 * 发起人
	 */
	public long CreatorID;

	/**
	 * 状态
	 */
	public int Status;
	/**
	 * 异常来源ID
	 */
	public long SourceID;
	/**
	 * 异常来源类型
	 */
	public int SourceType;

	public int PlaceID;

	public String PlaceNo;

	public int ResponseLevel;

	/**
	 * 车辆
	 */
	public String CarName = "";

	public EXCAndon() {
		StationCode = "";
		EXType = "";
		Comment = "";
		PlaceNo = "";
		CreateTime = Calendar.getInstance();
		EditTime = Calendar.getInstance();
		EndTime.set(2000, 1, 1);
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public long getCompanyID() {
		return CompanyID;
	}

	public void setCompanyID(long companyID) {
		CompanyID = companyID;
	}

	public long getWorkshopID() {
		return WorkshopID;
	}

	public void setWorkshopID(long workshopID) {
		WorkshopID = workshopID;
	}

	public long getLineID() {
		return LineID;
	}

	public void setLineID(long lineID) {
		LineID = lineID;
	}

	public String getStationCode() {
		return StationCode;
	}

	public void setStationCode(String stationCode) {
		StationCode = stationCode;
	}

	public String getEXType() {
		return EXType;
	}

	public void setEXType(String eXType) {
		EXType = eXType;
	}

	public String getComment() {
		return Comment;
	}

	public void setComment(String comment) {
		Comment = comment;
	}

	public int getShiftID() {
		return ShiftID;
	}

	public void setShiftID(int shiftID) {
		ShiftID = shiftID;
	}

	public int getShiftTimes() {
		return ShiftTimes;
	}

	public void setShiftTimes(int shiftTimes) {
		ShiftTimes = shiftTimes;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public Calendar getEditTime() {
		return EditTime;
	}

	public void setEditTime(Calendar editTime) {
		EditTime = editTime;
	}

	public Calendar getEndTime() {
		return EndTime;
	}

	public void setEndTime(Calendar endTime) {
		EndTime = endTime;
	}

	public List<Long> getOperatorID() {
		return OperatorID;
	}

	public void setOperatorID(List<Long> operatorID) {
		OperatorID = operatorID;
	}

	public long getCreatorID() {
		return CreatorID;
	}

	public void setCreatorID(long creatorID) {
		CreatorID = creatorID;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public long getSourceID() {
		return SourceID;
	}

	public void setSourceID(long sourceID) {
		SourceID = sourceID;
	}

	public int getSourceType() {
		return SourceType;
	}

	public void setSourceType(int sourceType) {
		SourceType = sourceType;
	}

	public int getPlaceID() {
		return PlaceID;
	}

	public void setPlaceID(int placeID) {
		PlaceID = placeID;
	}

	public String getPlaceNo() {
		return PlaceNo;
	}

	public void setPlaceNo(String placeNo) {
		PlaceNo = placeNo;
	}

	public int getResponseLevel() {
		return ResponseLevel;
	}

	public void setResponseLevel(int responseLevel) {
		ResponseLevel = responseLevel;
	}

	public String getCarName() {
		return CarName;
	}

	public void setCarName(String carName) {
		CarName = carName;
	}
}
