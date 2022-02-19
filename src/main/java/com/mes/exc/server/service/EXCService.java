package com.mes.exc.server.service;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.mes.exc.server.service.po.exc.EXCCallTaskBPM;
import com.mes.exc.server.service.po.exc.EXCLevelType;
import com.mes.exc.server.service.po.exc.EXCOptionItem;
import com.mes.exc.server.service.po.exc.EXCRunConfig;
import com.mes.exc.server.service.po.exc.action.EXCCallAction;
import com.mes.exc.server.service.po.exc.action.EXCCallApply;
import com.mes.exc.server.service.po.exc.base.EXCAlarmRule;
import com.mes.exc.server.service.po.exc.base.EXCExceptionRule;
import com.mes.exc.server.service.po.exc.base.EXCExceptionTemplate;
import com.mes.exc.server.service.po.exc.base.EXCExceptionType;
import com.mes.exc.server.service.po.exc.base.EXCStation;
import com.mes.exc.server.service.po.exc.base.EXCStationType;
import com.mes.exc.server.service.po.exc.define.EXCActionTypes;
import com.mes.exc.server.service.po.exc.define.EXCAndonTypes;
import com.mes.exc.server.service.po.exc.define.EXCCallStatus;
import com.mes.exc.server.service.po.exc.define.EXCResourceTypes;
import com.mes.exc.server.service.po.exc.define.TagTypes;
import com.mes.exc.server.service.po.exc.define.TaskRelevancyTypes;
import com.mes.exc.server.service.po.exc.tree.EXCAndon;
import com.mes.exc.server.service.po.exc.tree.EXCCallDispatch;
import com.mes.exc.server.service.po.exc.tree.EXCCallTask;
import com.mes.exc.server.service.po.exc.tree.EXCMessage;
import com.mes.exc.server.service.mesenum.BPMEventModule;
import com.mes.exc.server.service.po.ServiceResult;
import com.mes.exc.server.service.po.bfc.QRTypes;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.service.po.bpm.BPMTaskBase;

public interface EXCService {

	ServiceResult<List<EXCAlarmRule>> EXC_GetAlarmRuleList(BMSEmployee wLoginUser, long wStationType, int wRespondLevel,
			int wActive);

	ServiceResult<EXCAlarmRule> EXC_GetAlarmRule(BMSEmployee wLoginUser, long wID, long wAlarmID, String wAlarmCode);

	ServiceResult<Long> EXC_UpdateAlarmRule(BMSEmployee wLoginUser, EXCAlarmRule wEXCAlarmRule);

	ServiceResult<Integer> Active(BMSEmployee wLoginUser, List<Long> wIDList, int wActive);

	ServiceResult<List<EXCMessage>> EXC_GetMessageList(BMSEmployee wLoginUser, int wResponsorID, int wModuleID,
			int wActive, int wShiftID, Calendar wStartTime, Calendar wEndTime);

	ServiceResult<Integer> EXC_UpdateMessageList(BMSEmployee wLoginUser, List<EXCMessage> wEXCMessageList);

	ServiceResult<Integer> EXC_SendMessageList(BMSEmployee wLoginUser, List<EXCMessage> wEXCMessageList);

	ServiceResult<Map<Integer, Integer>> EXC_GetMessagCount(BMSEmployee wLoginUser, int wResponsorID, int wShfitID);

	ServiceResult<List<EXCAndon>> EXC_GetAndonList(BMSEmployee wLoginUser, int wWorkshopID, int wLineID, int wPlaceID,
			String wStationCode, long wSourceID, EXCAndonTypes wSourceType, int wShiftID, Calendar wStartTime,
			Calendar wEndTime, List<Integer> wStatus);

	ServiceResult<Integer> EXC_UpdateAndon(BMSEmployee wLoginUser, EXCAndon wEXCAndon);

	ServiceResult<Integer> EXC_UpdateAndonStatus(BMSEmployee wLoginUser);

	ServiceResult<List<EXCCallAction>> EXC_GetCallActionList(BMSEmployee wLoginUser, long wTaskID,
			EXCActionTypes wActionType, long wOperatorID, long wForwarder, long wDispatchID);

	ServiceResult<EXCCallAction> EXC_GetCallActionByID(BMSEmployee wLoginUser, long wID);

	ServiceResult<Long> EXC_UpdateCallAction(BMSEmployee wLoginUser, EXCCallAction wEXCCallAction, int wShiftID,
			boolean wIsOverShift);

	ServiceResult<List<EXCOptionItem>> EXC_GetActionTypeList(BMSEmployee wLoginUser);

	ServiceResult<List<EXCCallApply>> EXC_GetCallApplyList(BMSEmployee wLoginUser, String wStationNo, long wStationType,
			long wStationID, int wRespondLevel, int wDisplayBoard, int wOnSite, long wApplicantID, long wApproverID,
			long wConfirmID, String wPartNo, Calendar wStartTime, Calendar wEndTime, int wStatus);

	ServiceResult<EXCCallApply> EXC_GetCallApplyByID(BMSEmployee wLoginUser, long wID);

	ServiceResult<Long> EXC_UpdateCallApply(BMSEmployee wLoginUser, EXCCallApply wEXCCallApply, int wShiftID);

	ServiceResult<EXCStation> EXC_GetStation(BMSEmployee wLoginUser, long wQRCodeID, String wQRCode, QRTypes wQRType);

	ServiceResult<List<EXCExceptionType>> EXC_GetExceptionTypeList(BMSEmployee wLoginUser, String wName,
			long wStationType, TaskRelevancyTypes wRelevancyTaskType, int wActive);

	ServiceResult<List<EXCOptionItem>> EXC_GetRespondLevelList(BMSEmployee wLoginUser);

	ServiceResult<List<EXCOptionItem>> EXC_UpdateRespondLevelList(BMSEmployee wLoginUser,
			List<EXCOptionItem> wRespondLevelList);

	ServiceResult<Boolean> EXC_IsAllowApply(BMSEmployee wLoginUser, EXCCallApply wEXCCallApply, int wShiftID);

	ServiceResult<List<EXCCallTask>> EXC_GetCallTaskListByStatus(BMSEmployee wLoginUser, long wApplyID,
			String wStationNo, long wStationType, long wStationID, int wRespondLevel, int wDisplayBoard, int wOnSite,
			long wApplicantID, long wOperatorID, long wConfirmID, int wShiftID, String wPartNo, Calendar wStartTime,
			Calendar wEndTime, EXCCallStatus wStatus, int wExceptionType);

	ServiceResult<List<EXCCallTask>> EXC_GetCallTaskListByOperatorID(BMSEmployee wLoginUser, long wOperatorID,
			int wShiftID);

	ServiceResult<List<EXCCallTask>> EXC_GetCallTaskListByDispatcher(BMSEmployee wLoginUser, long wDispatcherID,
			int wShiftID, Calendar wStartTime, Calendar wEndTime, List<Integer> wStatus);

	ServiceResult<List<EXCCallTask>> EXC_GetCallTaskList(BMSEmployee wLoginUser, List<Long> wID, long wApplyID,
			String wStationNo, long wStationType, long wStationID, int wRespondLevel, int wDisplayBoard, int wOnSite,
			long wApplicantID, long wOperatorID, long wConfirmID, int wShiftID, String wPartNo, Calendar wStartTime,
			Calendar wEndTime, List<Integer> wStatus);

	ServiceResult<EXCCallTask> EXC_GetCallTaskByID(BMSEmployee wLoginUser, long wID);

	ServiceResult<List<EXCCallDispatch>> EXC_GetCallDispatchList(BMSEmployee wLoginUser, long wTaskID, long wOperatorID,
			long wCreatorID, int wShiftID, Calendar wStartTime, Calendar wEndTime, EXCCallStatus wStatus);

	ServiceResult<List<EXCOptionItem>> EXC_GetRequestActions(BMSEmployee wLoginUser, long wDispatchID,
			boolean wIsOnSite, TagTypes wTagType, EXCCallStatus wEXCCallStatus,
			List<EXCCallDispatch> wEXCCallDispatchList, long wApplicantID, long wConfirmID);

	ServiceResult<List<EXCExceptionRule>> EXC_GetExceptionRuleList(BMSEmployee wLoginUser, String wName,
			long wExceptionType, int wRespondLevel, EXCResourceTypes wRequestType, EXCResourceTypes wResponseType,
			EXCResourceTypes wConfirmType, int wActive);

	ServiceResult<List<EXCExceptionTemplate>> EXC_GetExceptionTemplateList(BMSEmployee wLoginUser);

	ServiceResult<EXCExceptionRule> EXC_GetExceptionRuleByID(BMSEmployee wLoginUser, long wID);

	ServiceResult<Long> EXC_UpdateExceptionRule(BMSEmployee wLoginUser, EXCExceptionRule wEXCExceptionRule);

	ServiceResult<Integer> EXC_ActiveExceptionRule(BMSEmployee wLoginUser, List<Long> wIDList, int wActive);

	ServiceResult<EXCExceptionType> EXC_GetExceptionTypeByID(BMSEmployee wLoginUser, long wID);

	ServiceResult<Long> EXC_UpdateExceptionType(BMSEmployee wLoginUser, EXCExceptionType wEXCExceptionType);

	ServiceResult<Integer> EXC_ActiveExceptionType(BMSEmployee wLoginUser, List<Long> wIDList, int wActive);

	ServiceResult<List<EXCStation>> EXC_GetStationList(BMSEmployee wLoginUser, String wStationName, long wStationType,
			QRTypes wRelevancyType, long wRelevancyID, int wActive);

	ServiceResult<EXCStation> EXC_GetStationByID(BMSEmployee wLoginUser, long wID, String wStationNo);

	ServiceResult<EXCStation> EXC_GetStationByRelevancyID(BMSEmployee wLoginUser, QRTypes wRelevancyType,
			long wRelevancyID);

	ServiceResult<Long> EXC_UpdateStation(BMSEmployee wLoginUser, EXCStation wEXCStation);

	ServiceResult<Integer> EXC_ActiveStation(BMSEmployee wLoginUser, List<Long> wIDList, int wActive);

	ServiceResult<List<EXCStationType>> EXC_GetStationTypeList(BMSEmployee wLoginUser, String wName,
			QRTypes wRelevancyType, int wActive);

	ServiceResult<EXCStationType> EXC_GetStationTypeByID(BMSEmployee wLoginUser, long wID);

	ServiceResult<Long> EXC_UpdateStationType(BMSEmployee wLoginUser, EXCStationType wEXCStationType);

	ServiceResult<Integer> EXC_ActiveStationType(BMSEmployee wLoginUser, List<Long> wIDList, int wActive);

	ServiceResult<EXCRunConfig> EXC_GetEXCRunConfig(BMSEmployee wLoginUser);

	ServiceResult<Integer> EXC_SaveEXCRunConfig(BMSEmployee wLoginUser, EXCRunConfig wEXCRunConfig);

	ServiceResult<Integer> EXC_ReportAndOverShiftForward(BMSEmployee wLoginUser);

	/**
	 * 自动超时上报、超班转发
	 */
	ServiceResult<Integer> EXC_AutoOverTimeReportAndOverShiftForward(BMSEmployee wBMSEmployee);

	/**
	 * 根据异常等级和类型和日期获取异常列表
	 * 
	 * @param wLoginUser      登录信息
	 * @param wShiftDate      查询日期
	 * @param wAPSShiftPeriod 查询类型
	 * @param wLevel          异常等级
	 * @return 异常列表
	 */
	ServiceResult<List<EXCCallTaskBPM>> EXC_QueryExceptionAll(BMSEmployee wLoginUser, Calendar wShiftDate,
			int wAPSShiftPeriod, int wLevel);

	ServiceResult<List<EXCLevelType>> EXC_QueryLevelTypeLists(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime);

	/**
	 * 查询默认状态的单据
	 */
	ServiceResult<EXCCallTaskBPM> EXC_QueryDefaultCallTaskBPM(BMSEmployee wLoginUser, int wEventID);

	/**
	 * 创建单据
	 */
	ServiceResult<EXCCallTaskBPM> EXC_CreateCallTaskBPM(BMSEmployee wLoginUser, BPMEventModule wEventID);

	/**
	 * 提交单据
	 */
	ServiceResult<EXCCallTaskBPM> EXC_SubmitCallTaskBPM(BMSEmployee wLoginUser, EXCCallTaskBPM wData);

	/**
	 * 查询单条单据
	 */
	ServiceResult<EXCCallTaskBPM> EXC_GetCallTaskBPM(BMSEmployee wLoginUser, int wID);

	/**
	 * 用人员拿任务
	 */
	ServiceResult<List<BPMTaskBase>> EXC_QueryCallTaskBPMEmployeeAll(BMSEmployee wLoginUser, int wTagTypes,
			Calendar wStartTime, Calendar wEndTime);

	/**
	 * 查询单据历史
	 */
	ServiceResult<List<EXCCallTaskBPM>> EXC_QueryCallTaskBPMHistory(BMSEmployee wLoginUser, int wID, String wCode,
			int wUpFlowID, int wShiftID, String wPartNo, int wPlaceID, Calendar wStartTime, Calendar wEndTime,
			int wResponseLevel, int wExceptionType);

	ServiceResult<Integer> EXC_OverTimeReportBPM(BMSEmployee adminUser);

	/**
	 * 转发任务
	 */
	ServiceResult<Integer> EXC_DelegateTask(BMSEmployee wLoginUser, String wTaskID, String wUserID, int wMainTaskID,
			String wRemark);

	/**
	 * 查询未完成的异常列表
	 */
	ServiceResult<List<EXCCallTaskBPM>> EXC_GetAndonList(BMSEmployee wBMSEmployee);

	/**
	 * 获取上级通知人员
	 */
	ServiceResult<List<BMSEmployee>> EXC_QueryNoticeList(BMSEmployee wLoginUser);

	/**
	 * 数据源 异常 待办、已办、发起
	 */
	ServiceResult<List<EXCCallTaskBPM>> EXC_QueryCallTaskBPMEmployeeAllList(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime, int wPartID, int wLevel, int wExceptionType, int wStatus);

	/**
	 * 数据源 异常 待办、已办、发起
	 */
	ServiceResult<List<EXCCallTaskBPM>> EXC_QueryCallTaskBPMList(BMSEmployee wLoginUser, int wExceptionType, int wLevel,String wPartNo,
			 int wStationID,	int wPlaceID, Calendar wStartTime, Calendar wEndTime, int wStatus);
}
