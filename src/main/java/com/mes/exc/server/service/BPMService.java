package com.mes.exc.server.service;

import com.mes.exc.server.service.mesenum.BPMEventModule;
import com.mes.exc.server.service.po.APIResult;
import com.mes.exc.server.service.po.ServiceResult;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.service.po.bpm.BPMTaskBase;
import com.mes.exc.server.utils.Configuration;

public interface BPMService {
	static String ServerUrl = Configuration.readConfigString("bpm.server.url", "config/config");

	static String ServerName = Configuration.readConfigString("bpm.server.project.name", "config/config");

	/**
	 * 创建流程实例
	 * 
	 * @param wLoginUser
	 * @param wModule      模块
	 * @param wBusinessKey 业务单ID
	 * @param wData        提交数据
	 * @return
	 */
	APIResult BPM_CreateProcess(BMSEmployee wLoginUser, BPMEventModule wModule, int wBusinessKey, Object wData);

	/**
	 * 提交流程实例 返回数据有
	 * 
	 * @param wLoginUser
	 * @param wModule
	 * @param wData
	 * @return
	 */
	APIResult BPM_CompleteTask(BMSEmployee wLoginUser, int wTaskID, int wLocalScope, Object wData);

	/**
	 * 根据实例ID获取当前操作步骤
	 * 
	 * @param wLoginUser
	 * @param wProcessInstanceID
	 * @return
	 */
	APIResult BPM_CurrentTask(BMSEmployee wLoginUser, int wProcessInstanceID);

	/**
	 * 根据任务ID获取任务信息
	 * 
	 * @param wLoginUser
	 * @param wTaskID
	 * @return
	 */
	APIResult BPM_GetTask(BMSEmployee wLoginUser, int wTaskID);

	/**
	 * 根据任务ID获取可操作步骤
	 * 
	 * @param wLoginUser
	 * @param wTaskID
	 * @return
	 */
	APIResult BPM_GetOperationByTaskID(BMSEmployee wLoginUser, int wTaskID);

	/**
	 * 根据实例ID获取待办任务列表
	 * 
	 * @param wLoginUser
	 * @param wInstanceID
	 * @return
	 */
	APIResult BPM_GetTaskListByInstance(BMSEmployee wLoginUser, int wInstanceID);

	ServiceResult<Boolean> BPM_MsgUpdate(BMSEmployee wLoginUser, int wTaskID, int wLocalScope,
			BPMTaskBase paramBPMTaskBase, Object wData);

	APIResult BPM_GetInstanceByID(BMSEmployee wLoginUser, int wFlowID);

	APIResult BPM_DeleteInstanceByID(BMSEmployee wLoginUser, int wFlowID, String wReason);

	/**
	 * 转发任务
	 */
	APIResult BPM_DelegateTask(BMSEmployee wLoginUser, String taskId, String userId, String wRemark);
}
