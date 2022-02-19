package com.mes.exc.server.service.po.exc.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.mes.exc.server.service.po.exc.EXCOptionItem;
import com.mes.exc.server.service.po.exc.action.EXCCallAction;
import com.mes.exc.server.service.po.exc.define.EXCActionTypes;

/**
 * 异常任务主体
 * 
 * @author ShrisJava
 *
 */
public class EXCCallTree implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */

	public long TaskID;

	/**
	 * 任务
	 */
	public EXCCallTask CallTask;

	/**
	 * 呼叫操作员撤销记录
	 */
	public EXCCallAction CallCancel;

	/**
	 * 允许的操作
	 */
	public List<EXCOptionItem> CallActions;

	/**
	 * 呼叫处理 - 各路径的处理列表
	 */
	public List<EXCCallDispatch> CallDispatchList;

	public EXCCallTree() {
		CallTask = new EXCCallTask();
		CallCancel = new EXCCallAction();
		CallActions = new ArrayList<EXCOptionItem>();
		CallDispatchList = new ArrayList<EXCCallDispatch>();
	}

	public EXCCallTree(EXCCallTask wEXCCallTask, List<EXCCallDispatch> wEXCCallDispatchList,
			List<EXCCallAction> wEXCCallActionList) {
		TaskID = wEXCCallTask.ID;

		if (wEXCCallTask == null || wEXCCallTask.getID() <= 0) {
			wEXCCallTask = new EXCCallTask();
		}

		CallTask = wEXCCallTask;
		CallActions = new ArrayList<EXCOptionItem>();
		if (wEXCCallDispatchList == null)
			wEXCCallDispatchList = new ArrayList<EXCCallDispatch>();
		CallDispatchList = wEXCCallDispatchList;

		if (wEXCCallActionList == null || wEXCCallActionList.size() < 1)
			return;
		List<EXCCallAction> wEXCCallActionListTemp = wEXCCallActionList.stream()
				.filter((EXCCallAction p) -> p.ActionType == EXCActionTypes.Cancel.getValue())
				.collect(Collectors.toList());
		;
		if (wEXCCallActionListTemp != null && wEXCCallActionListTemp.size() > 0) {
			CallCancel = wEXCCallActionListTemp.get(0);
		}
		wEXCCallActionList = wEXCCallActionList.stream().filter((EXCCallAction p) -> p.DispatchID > 0)
				.collect(Collectors.toList());
		if (wEXCCallActionList.size() < 0)
			return;
		Map<Long, List<EXCCallAction>> wEXCCallActionListDic = wEXCCallActionList.stream()
				.collect(Collectors.groupingBy((EXCCallAction p) -> p.DispatchID, Collectors.toList()));

		for (EXCCallDispatch wEXCCallDispatch : wEXCCallDispatchList) {
			if (wEXCCallActionListDic.containsKey(wEXCCallDispatch.ID))
				wEXCCallDispatch.ActionList = wEXCCallActionListDic.get(wEXCCallDispatch.ID);
		}
	}

	public long getTaskID() {
		return TaskID;
	}

	public void setTaskID(long taskID) {
		TaskID = taskID;
	}

	public EXCCallTask getCallTask() {
		return CallTask;
	}

	public void setCallTask(EXCCallTask callTask) {
		CallTask = callTask;
	}

	public EXCCallAction getCallCancel() {
		return CallCancel;
	}

	public void setCallCancel(EXCCallAction callCancel) {
		CallCancel = callCancel;
	}

	public List<EXCOptionItem> getCallActions() {
		return CallActions;
	}

	public void setCallActions(List<EXCOptionItem> callActions) {
		CallActions = callActions;
	}

	public List<EXCCallDispatch> getCallDispatchList() {
		return CallDispatchList;
	}

	public void setCallDispatchList(List<EXCCallDispatch> callDispatchList) {
		CallDispatchList = callDispatchList;
	}
}
