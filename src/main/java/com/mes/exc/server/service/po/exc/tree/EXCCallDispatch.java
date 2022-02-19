package com.mes.exc.server.service.po.exc.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.mes.exc.server.service.po.exc.EXCOptionItem;
import com.mes.exc.server.service.po.exc.action.EXCCallAction;
import com.mes.exc.server.service.po.exc.define.EXCCallStatus;

/**
 * 异常责任人任务
 * 
 * @author ShrisJava
 *
 */
public class EXCCallDispatch implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public long ID = 0;

	public long TaskID = 0;

	/** 
	 * 
	 */
	public int CompanyID = 0;

	/**
	 * 创建人
	 */
	public long CreatorID = 0;

	/**
	 * 接收人ID
	 */
	public long OperatorID = 0;

	/**
	 * 录入时间
	 */
	public Calendar CreateTime;

	/**
	 * 修改时间
	 */
	public Calendar EditTime;

	/**
	 * 任务状态 待处理 待确认 已确认 已驳回 已转发 已上报 已撤销
	 */
	public int Status;

	/**
	 * 操作记录
	 */
	public List<EXCCallAction> ActionList;

	/**
	 * 班次ID
	 */
	public int ShiftID;

	/**
	 * 允许的操作
	 */
	public List<EXCOptionItem> CallActions;

	public EXCCallDispatch() {
		ActionList = new ArrayList<EXCCallAction>();
		CallActions = new ArrayList<EXCOptionItem>();
		CreateTime = Calendar.getInstance();
		EditTime = Calendar.getInstance();
	}

	public EXCCallDispatch(EXCCallTask wEXCCallTask, long wOperatorID) {
		TaskID = wEXCCallTask.ID;
		CompanyID = wEXCCallTask.CompanyID;
		CreatorID = wEXCCallTask.ApplicantID;
		OperatorID = wOperatorID;
		CreateTime = Calendar.getInstance();
		EditTime = Calendar.getInstance();
		ShiftID = wEXCCallTask.ShiftID;
		CallActions = new ArrayList<EXCOptionItem>();
		Status = EXCCallStatus.WaitRespond.getValue();
		ActionList = new ArrayList<EXCCallAction>();

	}

	public static List<EXCCallDispatch> ForwarderDispatch(EXCCallAction wEXCCallAction, int wShiftID) {
		List<EXCCallDispatch> wResult = new ArrayList<EXCCallDispatch>();

		if (wEXCCallAction == null || wEXCCallAction.Forwarder == null || wEXCCallAction.Forwarder.size() <= 0)
			return wResult;

		for (Long wForwarder : wEXCCallAction.Forwarder) {
			EXCCallDispatch wEXCCallDispatch = new EXCCallDispatch();

			wEXCCallDispatch.TaskID = wEXCCallAction.TaskID;
			wEXCCallDispatch.CompanyID = wEXCCallAction.CompanyID;
			wEXCCallDispatch.CreatorID = wEXCCallAction.OperatorID;
			wEXCCallDispatch.OperatorID = wForwarder;
			wEXCCallDispatch.CreateTime = Calendar.getInstance();
			wEXCCallDispatch.EditTime = Calendar.getInstance();
			wEXCCallDispatch.ShiftID = wShiftID;
			wEXCCallDispatch.CallActions = new ArrayList<EXCOptionItem>();
			wEXCCallDispatch.Status = EXCCallStatus.WaitRespond.getValue();
			wEXCCallDispatch.ActionList = new ArrayList<EXCCallAction>();
			wResult.add(wEXCCallDispatch);
		}

		return wResult;
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public long getTaskID() {
		return TaskID;
	}

	public void setTaskID(long taskID) {
		TaskID = taskID;
	}

	public int getCompanyID() {
		return CompanyID;
	}

	public void setCompanyID(int companyID) {
		CompanyID = companyID;
	}

	public long getCreatorID() {
		return CreatorID;
	}

	public void setCreatorID(long creatorID) {
		CreatorID = creatorID;
	}

	public long getOperatorID() {
		return OperatorID;
	}

	public void setOperatorID(long operatorID) {
		OperatorID = operatorID;
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

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public List<EXCCallAction> getActionList() {
		return ActionList;
	}

	public void setActionList(List<EXCCallAction> actionList) {
		ActionList = actionList;
	}

	public int getShiftID() {
		return ShiftID;
	}

	public void setShiftID(int shiftID) {
		ShiftID = shiftID;
	}

	public List<EXCOptionItem> getCallActions() {
		return CallActions;
	}

	public void setCallActions(List<EXCOptionItem> callActions) {
		CallActions = callActions;
	}
}
