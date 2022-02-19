package com.mes.exc.server.controller.bpm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mes.exc.server.controller.BaseController;
import com.mes.exc.server.service.BPMService;
import com.mes.exc.server.service.CoreService;
import com.mes.exc.server.service.EXCService;
import com.mes.exc.server.service.LFSService;
import com.mes.exc.server.service.mesenum.BPMEventModule;
import com.mes.exc.server.service.mesenum.BPMHistoryTaskStatus;
import com.mes.exc.server.service.mesenum.EXCCallTaskBPMStatus;
import com.mes.exc.server.service.po.APIResult;
import com.mes.exc.server.service.po.ServiceResult;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.service.po.bpm.BPMActivitiHisTask;
import com.mes.exc.server.service.po.bpm.BPMActivitiProcessInstance;
import com.mes.exc.server.service.po.bpm.BPMActivitiTask;
import com.mes.exc.server.service.po.bpm.BPMTaskBase;
import com.mes.exc.server.service.po.exc.EXCCallTaskBPM;
import com.mes.exc.server.service.utils.CloneTool;
import com.mes.exc.server.service.utils.StringUtils;
import com.mes.exc.server.utils.RetCode;

@RestController
@RequestMapping("/api/Runtime")
public class BPMRuntimeController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(BPMRuntimeController.class);

	@Autowired
	LFSService wLFSService;
	@Autowired
	BPMService wBPMService;
	@Autowired
	CoreService wCoreService;
	@Autowired
	EXCService wEXCService;

	/**
	 * 创建流程任务
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@PostMapping("/startProcessByProcessDefinitionKey")
	public Object startProcessByProcessDefinitionKey(HttpServletRequest request,
			@RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);

			if (!wParam.containsKey("processDefinitionKey")) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			String wModuleIDString = StringUtils.parseString(wParam.get("processDefinitionKey"));
			if (wModuleIDString.startsWith("_")) {
				wModuleIDString = wModuleIDString.substring(1);
			}
			int wModuleID = StringUtils.parseInt(wModuleIDString);

			BPMEventModule wEventID = BPMEventModule.getEnumType(wModuleID);

			String wMsg = "";

			BPMTaskBase wData = null;
			@SuppressWarnings("rawtypes")
			ServiceResult wServiceResult = null;
			List<BPMActivitiTask> wBPMActivitiTask = new ArrayList<BPMActivitiTask>();
			switch (wEventID) {
			case SCCall:
				// EXCDayPlanAudit 单据状态为0且创建人是自己的任务
				// 创建审批单(先查询默认状态单据)
				wServiceResult = wEXCService.EXC_QueryDefaultCallTaskBPM(wLoginUser, wEventID.getValue());
				if (wServiceResult == null || wServiceResult.GetResult() == null
						|| ((BPMTaskBase) wServiceResult.GetResult()).ID <= 0
						|| ((BPMTaskBase) wServiceResult.GetResult()).FlowID <= 0)
					wServiceResult = wEXCService.EXC_CreateCallTaskBPM(wLoginUser, wEventID);
				if (StringUtils.isNotEmpty(wServiceResult.FaultCode)) {
					wMsg += wServiceResult.getFaultCode();
				}
				wData = (EXCCallTaskBPM) wServiceResult.GetResult();

				if (wParam.containsKey("data")) {
					wData = StringUtils.CombineData(wData, wParam.get("data"));
				}
				if (wData.ID > 0) {
					wData.CreateTime = Calendar.getInstance();
					if (wData.FlowID <= 0) {
						wData.FlowID = wBPMService.BPM_CreateProcess(wLoginUser, wEventID, wData.getID(), wData)
								.Info(Integer.class);
					}
					if (wData.FlowID <= 0) {
						wMsg += "创建流程失败！";
					} else {
						wServiceResult = wEXCService.EXC_SubmitCallTaskBPM(wLoginUser, (EXCCallTaskBPM) wData);
						if (wServiceResult.ErrorCode != 0) {
							wMsg += wServiceResult.getFaultCode();
						}

						wBPMActivitiTask = wBPMService.BPM_GetTaskListByInstance(wLoginUser, wData.FlowID)
								.List(BPMActivitiTask.class);
					}
				}
				break;
			default:
				break;
			}
			if (wData == null) {
				wMsg += "该流程暂不支持";
			}

			if (StringUtils.isEmpty(wMsg)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wBPMActivitiTask, wData.FlowID);
				SetResult(wResult, "data", wData);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wMsg);
			}
		} catch (Exception ex) {
			logger.error("BPMRuntimeController startProcessByProcessDefinitionKey error:", ex);
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 提交待办任务
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@PostMapping("/CompleteMyPersonalTask")
	public Object CompleteMyPersonalTask(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);

			if (!wParam.containsKey("TaskID") || !wParam.containsKey("data")) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			int wTaskID = CloneTool.Clone(wParam.get("TaskID"), Integer.class);
			BPMTaskBase wBPMTaskBase = CloneTool.Clone(wParam.get("data"), BPMTaskBase.class);
			int wLocalScope = wParam.containsKey("localScope") ? StringUtils.parseInt(wParam.get("localScope")) : 0;
			if (wTaskID <= 0 || wBPMTaskBase == null || wBPMTaskBase.ID <= 0) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			// 判断任务是否已完成(防止任务重复提交)
			BPMActivitiHisTask wHisTask = wBPMService.BPM_GetTask(wLoginUser, wTaskID).Info(BPMActivitiHisTask.class);
			if (wHisTask == null || StringUtils.isEmpty(wHisTask.ID)) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, "提示：该任务不存在!");
				return wResult;
			} else if (wHisTask.Status == BPMHistoryTaskStatus.NomalFinished.getValue()
					|| wHisTask.Status == BPMHistoryTaskStatus.Canceled.getValue()) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, "提示：该任务已完成或已取消!");
				return wResult;
			}

			int wModuleID = wBPMTaskBase.getFlowType();
			BPMEventModule wEventID = BPMEventModule.getEnumType(wModuleID);
			ServiceResult wServiceResult = null;
			BPMActivitiProcessInstance wBPMActivitiProcessInstance = null;

			ServiceResult<Boolean> wServiceResultBool = new ServiceResult<Boolean>(false);
			switch (wEventID) {
			case SCCall: {
				// 提交任务单
				EXCCallTaskBPM wTask = CloneTool.Clone(wParam.get("data"), EXCCallTaskBPM.class);

				wServiceResultBool = this.wBPMService.BPM_MsgUpdate(wLoginUser, wTaskID, wLocalScope, wTask,
						wParam.get("data"));
				if (wServiceResultBool.getResult() || !StringUtils.isEmpty(wServiceResultBool.getFaultCode())) {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, "提交失败:" + wServiceResultBool.getFaultCode());
					return wResult;
				}
				wServiceResult = wEXCService.EXC_SubmitCallTaskBPM(wLoginUser, wTask);

				wBPMActivitiProcessInstance = wBPMService.BPM_GetInstanceByID(wLoginUser, wTask.FlowID)
						.Info(BPMActivitiProcessInstance.class);

				/**
				 * 判断流程关闭
				 */
				if (wBPMActivitiProcessInstance.DurationInMillis > 0
						&& StringUtils.isEmpty(wBPMActivitiProcessInstance.DeleteReason)
						&& (wTask.Status != EXCCallTaskBPMStatus.NomalClose.getValue()
								|| wTask.Status != EXCCallTaskBPMStatus.ExceptionClose.getValue())) {
					// 若流程关闭且状态非正常关闭与异常关闭则改为正常关闭
					wTask.Status = EXCCallTaskBPMStatus.NomalClose.getValue();
					wServiceResult = wEXCService.EXC_SubmitCallTaskBPM(wLoginUser, wTask);
				}
			}
				break;
			default:
				break;
			}
			if (wServiceResult == null) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, "流程未配置!");
				return wResult;
			}
			List<BPMActivitiTask> wBPMActivitiTask = new ArrayList<BPMActivitiTask>();
			if (wServiceResult.Result != null && ((BPMTaskBase) wServiceResult.Result).FlowID > 0) {
				wBPMActivitiTask = wBPMService
						.BPM_GetTaskListByInstance(wLoginUser, ((BPMTaskBase) wServiceResult.Result).FlowID)
						.List(BPMActivitiTask.class);
				if (wBPMActivitiTask != null) {
					wBPMActivitiTask.removeIf(
							p -> !StringUtils.parseIntList(p.Assignee.split(",")).contains(wLoginUser.getID()));
				}
			}
			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wBPMActivitiTask, wServiceResult.Result);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}

		} catch (Exception ex) {
			logger.error("BPMRuntimeController CompleteMyPersonalTask error:", ex);
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 撤销待办任务
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@GetMapping("/deleteProcessInstance")
	public Object DelectInstance(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);

			int wFlowID = StringUtils.parseInt(request.getParameter("processInstanceId"));
			int wID = StringUtils.parseInt(request.getParameter("ID"));
			int wFlowType = StringUtils.parseInt(request.getParameter("FlowType"));
			String wReason = StringUtils.parseString(request.getParameter("deleteReason"));

			if (StringUtils.isEmpty(wReason))
				wReason = "撤销";

			if (wFlowID <= 0) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			APIResult wAPIResult = wBPMService.BPM_DeleteInstanceByID(wLoginUser, wFlowID, wReason);
			if (wAPIResult.getResultCode() != RetCode.SERVER_CODE_SUC)
				return wAPIResult;

			if (wFlowType > 0 && wID > 0) {
				BPMEventModule wEventID = BPMEventModule.getEnumType(wFlowType);
				switch (wEventID) {
				case SCCall: {
					// 提交任务单
					ServiceResult<EXCCallTaskBPM> wTaskResult = wEXCService.EXC_GetCallTaskBPM(wLoginUser, wID);
					if (wTaskResult.Result != null && wTaskResult.Result.ID > 0) {
						wTaskResult.Result.Status = EXCCallTaskBPMStatus.Canceled.getValue();
						wTaskResult.Result.ExpireTime.set(2000, 0, 1, 0, 0, 0);
						wTaskResult.Result.StatusText = EXCCallTaskBPMStatus.Canceled.getLable();
						wTaskResult.Result.FollowerID = null;

						wEXCService.EXC_SubmitCallTaskBPM(wLoginUser, wTaskResult.Result);
					} else {
						wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
						return wResult;
					}
				}
					break;
				default:
					break;
				}
			}
			wResult = GetResult(RetCode.SERVER_CODE_SUC, "撤销成功！", null, null);
		} catch (Exception ex) {
			logger.error("BPMRuntimeController deleteProcessInstance error:", ex);
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 转发任务
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@PostMapping("/DelegateTask")
	public Object DelegateTask(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			String wTaskID = StringUtils.parseString(wParam.get("taskId"));
			String wUserID = StringUtils.parseString(wParam.get("userId"));
			int wMainTaskID = StringUtils.parseInt(wParam.get("MainTaskID"));
			int wFlowType = StringUtils.parseInt(wParam.get("FlowType"));
			String wRemark = StringUtils.parseString(wParam.get("remark"));

			ServiceResult<Integer> wServiceResult = null;
			if (wFlowType > 0) {
				BPMEventModule wEventID = BPMEventModule.getEnumType(wFlowType);
				switch (wEventID) {
				case SCCall: {
					wServiceResult = wEXCService.EXC_DelegateTask(wLoginUser, wTaskID, wUserID, wMainTaskID, wRemark);
				}
					break;
				default:
					break;
				}
			}

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "转发成功", null, null);
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode(), null,
						wServiceResult.GetResult());
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}
}
