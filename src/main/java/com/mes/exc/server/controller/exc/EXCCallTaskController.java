package com.mes.exc.server.controller.exc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mes.exc.server.controller.BaseController;
import com.mes.exc.server.service.CoreService;
import com.mes.exc.server.service.EXCService;
import com.mes.exc.server.service.po.APIResult;
import com.mes.exc.server.service.po.ServiceResult;
import com.mes.exc.server.service.po.bms.BMSDepartment;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.service.po.exc.EXCNode;
import com.mes.exc.server.service.po.exc.EXCOptionItem;
import com.mes.exc.server.service.po.exc.action.EXCCallAction;
import com.mes.exc.server.service.po.exc.define.EXCActionTypes;
import com.mes.exc.server.service.po.exc.define.EXCCallStatus;
import com.mes.exc.server.service.po.exc.define.TagTypes;
import com.mes.exc.server.service.po.exc.tree.EXCCallDispatch;
import com.mes.exc.server.service.po.exc.tree.EXCCallTask;
import com.mes.exc.server.service.po.exc.tree.EXCCallTree;
import com.mes.exc.server.service.utils.StringUtils;
import com.mes.exc.server.utils.RetCode;

@RestController
@RequestMapping("/api/EXCCallTask")
public class EXCCallTaskController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(EXCCallTaskController.class);

	@Autowired
	EXCService wEXCService;

	@Autowired
	CoreService wCoreService;

	@GetMapping("/All")
	public Object All(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (this.CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wBMSEmployee = this.GetSession(request);

			long wApplyID = StringUtils.parseLong(request.getParameter("ApplyID"));
			int wStationType = StringUtils.parseInt(request.getParameter("StationType"));
			String wStationNo = StringUtils.parseString(request.getParameter("StationNo"));
			long wStationID = StringUtils.parseLong(request.getParameter("StationID"));
			int wRespondLevel = StringUtils.parseInt(request.getParameter("RespondLevel"));
			int wDisplayBoard = StringUtils.parseInt(request.getParameter("DisplayBoard"));
			int wOnSite = StringUtils.parseInt(request.getParameter("OnSite"));
			long wApplicantID = StringUtils.parseLong(request.getParameter("ApplicantID"));
			long wOperatorID = StringUtils.parseLong(request.getParameter("OperatorID"));
			long wConfirmID = StringUtils.parseLong(request.getParameter("ConfirmID"));
			int wShiftID = StringUtils.parseInt(request.getParameter("ShiftID"));
			int wStatus = StringUtils.parseInt(request.getParameter("Status"));
			int wExceptionType = StringUtils.parseInt(request.getParameter("ExceptionType"));
			String wPartNo = StringUtils.parseString(request.getParameter("PartNo"));
			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			ServiceResult<List<EXCCallTask>> wServerRst = wEXCService.EXC_GetCallTaskListByStatus(wBMSEmployee,
					wApplyID, wStationNo, wStationType, wStationID, wRespondLevel, wDisplayBoard, wOnSite, wApplicantID,
					wOperatorID, wConfirmID, wShiftID, wPartNo, wStartTime, wEndTime,
					EXCCallStatus.getEnumType(wStatus), wExceptionType);

			if (StringUtils.isEmpty(wServerRst.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "", wServerRst.GetResult(), null);
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wServerRst.getFaultCode(), wServerRst.GetResult(),
						null);
			}

		} catch (Exception e) {
			logger.error(e.toString());
			wResult = this.GetResult(RetCode.SERVER_CODE_ERR, e.toString(), null, null);
		}
		return wResult;
	}

	@GetMapping("/EmployeeAll")
	public Object EmployeeAll(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (this.CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wBMSEmployee = this.GetSession(request);

			int wPersonJudge = StringUtils.parseInt(request.getParameter("person_judge"));

			int wTagValue = StringUtils.parseInt(request.getParameter("TagValue"));

			@SuppressWarnings("unused")
			int wEventID = StringUtils.parseInt(request.getParameter("EventID"));

			int wShiftID = GetShiftID(wBMSEmployee, 0);

			List<Long> wUserList = new ArrayList<Long>();
			wUserList.add((long) wBMSEmployee.getID());
			if (wPersonJudge > 0) {
				/*
				 * List<SCHWorker> wSCHWorkerList = wSCHService.
				 * SCH_QuerySubWorkerListByLoginID(wBMSEmployee.getCompanyID(),
				 * wBMSEmployee.getID(), wEventID);
				 * 
				 * if (wSCHWorkerList != null && wSCHWorkerList.size() > 0) {
				 * wSCHWorkerList.ForEach(p -> wUserList.add(p.WorkerID)); }
				 */
			}
			List<EXCCallTask> wServerRst = new ArrayList<EXCCallTask>();
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 1, 1);
			String wFaultCode = "";
			ServiceResult<List<EXCCallTask>> wServiceResult;
			for (long wUserTemp : wUserList) {
				switch (TagTypes.getEnumType(wTagValue)) {
				case Dispatcher:
					wServiceResult = wEXCService.EXC_GetCallTaskListByDispatcher(wBMSEmployee, wUserTemp, wShiftID,
							wBaseTime, wBaseTime, null);
					break;
				case Applicant:
					wServiceResult = wEXCService.EXC_GetCallTaskList(wBMSEmployee, null, -1, "", -1, -1, -1, -1, -1,
							wUserTemp, -1, -1, wShiftID, "", wBaseTime, wBaseTime, null);
					break;
				case Confirmer:
					wServiceResult = wEXCService.EXC_GetCallTaskList(wBMSEmployee, null, 0, "", 0, 0, 0, -1, -1, -1, -1,
							wUserTemp, wShiftID, "", wBaseTime, wBaseTime, null);
					break;
				default:
					wServiceResult = wEXCService.EXC_GetCallTaskListByOperatorID(wBMSEmployee, wUserTemp, wShiftID);
					break;
				}
				wFaultCode += wServiceResult.getFaultCode();
				wServerRst.addAll(wServiceResult.GetResult());
			}

			Map<Long, List<EXCCallTask>> wEXCCallTaskMap = wServerRst.stream()
					.collect(Collectors.groupingBy(p -> p.ID));
			wServerRst = wEXCCallTaskMap.values().stream().map(p -> p.get(0)).sorted(
					Comparator.comparing((EXCCallTask p) -> p.CreateTime).reversed().thenComparing(q -> q.RespondLevel))
					.collect(Collectors.toList());

			if (StringUtils.isEmpty(wFaultCode)) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "", wServerRst, null);
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wFaultCode, wServerRst, null);
			}

		} catch (Exception e) {
			logger.error(e.toString());
			wResult = this.GetResult(RetCode.SERVER_CODE_ERR, e.toString(), null, null);
		}
		return wResult;
	}

	@GetMapping("/Info")
	public Object Info(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (this.CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wBMSEmployee = this.GetSession(request);

			long wID = StringUtils.parseLong(request.getParameter("ID"));

			ServiceResult<EXCCallTask> wServerRst = wEXCService.EXC_GetCallTaskByID(wBMSEmployee, wID);

			if (StringUtils.isEmpty(wServerRst.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "", null, wServerRst.GetResult());
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wServerRst.getFaultCode(), null,
						wServerRst.GetResult());
			}

		} catch (Exception e) {
			logger.error(e.toString());
			wResult = this.GetResult(RetCode.SERVER_CODE_ERR, e.toString(), null, null);
		}
		return wResult;
	}

	@GetMapping("/Tree")
	public Object Tree(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (this.CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wBMSEmployee = this.GetSession(request);

			long wID = StringUtils.parseLong(request.getParameter("TaskID"));

			TagTypes wTagValue = TagTypes.getEnumType(StringUtils.parseInt(request.getParameter("TagValue")));

			long wDispatchID = StringUtils.parseLong(request.getParameter("DispatchID"));

			if (wTagValue == TagTypes.Dispatcher && wDispatchID <= 0) {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 1, 1);
			ServiceResult<EXCCallTask> wEXCCallTaskServiceResult = wEXCService.EXC_GetCallTaskByID(wBMSEmployee, wID);

			EXCCallTask wEXCCallTask = wEXCCallTaskServiceResult.GetResult();

			ServiceResult<List<EXCCallDispatch>> wEXCCallDispatchList = wEXCService.EXC_GetCallDispatchList(
					wBMSEmployee, wID, -1, -1, -1, wBaseTime, wBaseTime, EXCCallStatus.Default);

			ServiceResult<List<EXCCallAction>> wEXCCallActionList = wEXCService.EXC_GetCallActionList(wBMSEmployee, wID,
					EXCActionTypes.Default, -1, -1, 0);

			EXCCallTree wServerRst = new EXCCallTree(wEXCCallTask, wEXCCallDispatchList.GetResult(),
					wEXCCallActionList.GetResult());

			if (wServerRst.CallTask != null && !StringUtils.isEmpty(wServerRst.CallTask.StationNo)) {
//				String wQRTypeName = QRTypes.getEnumType(
//						wCoreService.BFC_GetQRType(wBMSEmployee, wServerRst.CallTask.StationNo).Info(Integer.class))
//						.getLable();

				String wQRTypeName = wServerRst.CallTask.StationTypeName;

				if (wQRTypeName.equalsIgnoreCase(wServerRst.CallTask.StationTypeName))
					wServerRst.CallTask.StationTypeName = StringUtils.Format("【{0}】",
							wServerRst.CallTask.StationTypeName);
				else
					wServerRst.CallTask.StationTypeName = StringUtils.Format("【{0}】 {1}", wQRTypeName,
							wServerRst.CallTask.StationTypeName);
			}

			// 允许的操作
			ServiceResult<List<EXCOptionItem>> wServiceResult = wEXCService.EXC_GetRequestActions(wBMSEmployee,
					wDispatchID, wEXCCallTask.OnSite, wTagValue, EXCCallStatus.getEnumType(wEXCCallTask.Status),
					wEXCCallDispatchList.GetResult(), wEXCCallTask.ApplicantID, wEXCCallTask.ConfirmID);

			wServerRst.CallActions = wServiceResult.GetResult();

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "", null, wServerRst);
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode(), null, wServerRst);
			}

		} catch (Exception e) {
			logger.error(e.toString());
			wResult = this.GetResult(RetCode.SERVER_CODE_ERR, e.toString(), null, null);
		}
		return wResult;
	}

	@GetMapping("/OverTimeReport")
	public Object OverTimeReport(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			BMSEmployee wBMSEmployee = this.GetSession(request);

			wEXCService.EXC_AutoOverTimeReportAndOverShiftForward(wBMSEmployee);

			wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "手动超时上报成功!", null, null);
		} catch (Exception e) {
			logger.error(e.toString());
			wResult = this.GetResult(RetCode.SERVER_CODE_ERR, e.toString(), null, null);
		}
		return wResult;
	}

	@GetMapping("/NodeList")
	public Object NodeList(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (this.CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wBMSEmployee = this.GetSession(request);

			long wTaskID = StringUtils.parseLong(request.getParameter("TaskID"));
			boolean wIsReverse = StringUtils.parseBoolean(request.getParameter("IsReverse"));

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 1, 1);
			ServiceResult<EXCCallTask> wEXCCallTaskServiceResult = wEXCService.EXC_GetCallTaskByID(wBMSEmployee,
					wTaskID);

			EXCCallTask wEXCCallTask = wEXCCallTaskServiceResult.GetResult();

			ServiceResult<List<EXCCallDispatch>> wEXCCallDispatchList = wEXCService.EXC_GetCallDispatchList(
					wBMSEmployee, wTaskID, -1, -1, -1, wBaseTime, wBaseTime, EXCCallStatus.Default);

			ServiceResult<List<EXCCallAction>> wEXCCallActionList = wEXCService.EXC_GetCallActionList(wBMSEmployee,
					wTaskID, EXCActionTypes.Default, -1, -1, 0);

			EXCCallTree wTree = new EXCCallTree(wEXCCallTask, wEXCCallDispatchList.GetResult(),
					wEXCCallActionList.GetResult());
			wTree.TaskID = wTaskID;

			if (wTree == null || wTree.TaskID <= 0)
				return this.GetResult(RetCode.SERVER_CODE_ERR, "参数错误!");

			// 拿任务
			ServiceResult<EXCCallTask> wServiceResult = wEXCService.EXC_GetCallTaskByID(wBMSEmployee, wTree.TaskID);
			if (wServiceResult.Result == null || wServiceResult.Result.ID <= 0)
				return this.GetResult(RetCode.SERVER_CODE_ERR, "参数错误!");
			EXCCallTask wTask = wServiceResult.Result;
			// 人员列表
			APIResult wEmApiResult = wCoreService.BMS_GetEmployeeAll(wBMSEmployee, -1, -1, 1);
			List<BMSEmployee> wEmployeeList = wEmApiResult.List(BMSEmployee.class);
			if (wEmployeeList == null || wEmployeeList.size() <= 0)
				return this.GetResult(RetCode.SERVER_CODE_ERR, "获取人员列表失败!");
			// 部门列表
			APIResult wDeApiResult = wCoreService.BMS_QueryDepartmentList(wBMSEmployee);
			List<BMSDepartment> wDepartmentList = wDeApiResult.List(BMSDepartment.class);
			if (wDepartmentList == null || wDepartmentList.size() <= 0)
				return this.GetResult(RetCode.SERVER_CODE_ERR, "获取部门列表失败!");

			// 获取节点信息
			List<EXCNode> wList = new ArrayList<EXCNode>();
			wList = GetNodeList(wEmployeeList, wDepartmentList, wTree, wTask);

			// 逆序
			if (wIsReverse) {
				Collections.reverse(wList);
			}
			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "", wList, null);
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}

		} catch (Exception e) {
			logger.error(e.toString());
			wResult = this.GetResult(RetCode.SERVER_CODE_ERR, e.toString(), null, null);
		}
		return wResult;
	}

	private List<EXCNode> GetNodeList(List<BMSEmployee> wEmployeeList, List<BMSDepartment> wDepartmentList,
			EXCCallTree wTree, EXCCallTask wTask) {
		List<EXCNode> wResult = new ArrayList<EXCNode>();
		try {
			SimpleDateFormat wSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			// 发起人的数据
			EXCNode wStart = new EXCNode();
			wStart.DepartmentName = wEmployeeList.stream().filter(y -> y.ID == wTask.ApplicantID).findFirst()
					.get().Department;
			wStart.UserName = wEmployeeList.stream().filter(p -> p.ID == wTask.ApplicantID).findFirst().get().Name;
			wStart.StatusText = "已发起";
			wStart.EditTimeText = wSDF.format(wTask.ApplicantTime.getTime());
			wStart.NodeType = 1;
			wStart.IsFinish = true;
			wStart.Remark = wTask.Comment;
			wResult.add(wStart);

			if (wTree.CallCancel == null) {
				// 操作流程的数据
				if (wTree.CallDispatchList.size() > 0) {
					for (int i = 0; i < wTree.CallDispatchList.size(); i++) {
						int wIndex = i;
						// 无子节点
						if (wTree.CallDispatchList.get(i).ActionList.size() == 0) {
							if (wTree.CallDispatchList.get(i).Status != EXCCallStatus.Forwarded.getValue()
									&& wTree.CallDispatchList.get(i).Status != EXCCallStatus.WaitConfirm.getValue()) {
								EXCNode wDispatchNode = new EXCNode();
								wDispatchNode.DepartmentName = (wEmployeeList.stream()
										.filter(p -> p.ID == wTree.CallDispatchList.get(wIndex).OperatorID)).findFirst()
												.get().Department;
								wDispatchNode.StatusText = EXCCallStatus
										.getEnumType(wTree.CallDispatchList.get(wIndex).Status).getLable();
								wDispatchNode.EditTimeText = wSDF
										.format(wTree.CallDispatchList.get(wIndex).EditTime.getTime());
								wDispatchNode.IsFinish = true;
								wDispatchNode.NodeType = 2;
								wDispatchNode.UserName = wEmployeeList.stream()
										.filter(p -> p.ID == wTree.CallDispatchList.get(wIndex).OperatorID).findFirst()
										.get().Name;
								if (i == wTree.CallDispatchList.size() - 1)
									wDispatchNode.IsFinish = false;
								wResult.add(wDispatchNode);
							}
						}
						// 有子节点
						else {
							for (int j = 0; j < wTree.CallDispatchList.get(i).ActionList.size(); j++) {
								int wIndexj = j;

								EXCNode wCenter = new EXCNode();
								wCenter.DepartmentName = (wEmployeeList.stream()
										.filter(p -> p.ID == wTree.CallDispatchList.get(wIndex).ActionList
												.get(wIndexj).OperatorID)).findFirst().get().Department;
								wCenter.UserName = (wEmployeeList.stream()
										.filter(p -> p.ID == wTree.CallDispatchList.get(wIndex).ActionList
												.get(wIndexj).OperatorID)).findFirst().get().Name;
								wCenter.EditTimeText = wSDF
										.format(wTree.CallDispatchList.get(i).ActionList.get(j).CreateTime.getTime());
								wCenter.StatusText = "已" + EXCActionTypes
										.getEnumType(wTree.CallDispatchList.get(i).ActionList.get(j).ActionType)
										.getLable();
								wCenter.Remark = wTree.CallDispatchList.get(i).ActionList.get(j).Comment;
								wCenter.NodeType = 2;
								wCenter.IsFinish = true;
								if (wTree.CallDispatchList.get(i).ActionList.get(j).ActionType == EXCActionTypes.Confirm
										.getValue()) {
									wCenter.NodeType = 3;
								}

								wResult.add(wCenter);
							}
						}
						// 收到待处理
						if (wTree.CallDispatchList.get(i).ActionList.size() == 1
								&& i == wTree.CallDispatchList.size() - 1
								&& wTree.CallDispatchList.get(i).Status != EXCCallStatus.Confirmed.getValue()) {
							// 待处理数据
							EXCNode wNotEnd = new EXCNode();
							wNotEnd.DepartmentName = (wEmployeeList.stream()
									.filter(p -> p.ID == wTask.OperatorID.get(0))).findFirst().get().Department;
							wNotEnd.UserName = (wEmployeeList.stream().filter(p -> p.ID == wTask.OperatorID.get(0)))
									.findFirst().get().Name;
							wNotEnd.StatusText = "待处理";
							wNotEnd.IsFinish = false;
							wNotEnd.NodeType = 2;
							wResult.add(wNotEnd);
						}
						// 驳回待处理
						if (wTree.CallDispatchList.get(i).ActionList.size() > 1) {
							if (wTree.CallDispatchList.get(i).ActionList
									.get(wTree.CallDispatchList.get(i).ActionList.size()
											- 1).ActionType == EXCActionTypes.Reject.getValue()
									&& i == wTree.CallDispatchList.size() - 1) {
								// 待处理数据
								EXCNode wNotEnd = new EXCNode();
								wNotEnd.DepartmentName = (wEmployeeList.stream()
										.filter(p -> p.ID == wTask.OperatorID.get(0))).findFirst().get().Department;
								wNotEnd.UserName = (wEmployeeList.stream().filter(p -> p.ID == wTask.OperatorID.get(0)))
										.findFirst().get().Name;
								wNotEnd.StatusText = "待处理";
								wNotEnd.IsFinish = false;
								wNotEnd.NodeType = 2;
								wResult.add(wNotEnd);
							}
						}
					}
				}
				// 未确认数据
				if (wTask.Status != EXCCallStatus.Confirmed.getValue()) {
					// 未确认数据
					EXCNode wNotEnd = new EXCNode();
					wNotEnd.DepartmentName = (wEmployeeList.stream().filter(p -> p.ID == wTask.ConfirmID)).findFirst()
							.get().Department;
					wNotEnd.UserName = (wEmployeeList.stream().filter(p -> p.ID == wTask.ConfirmID)).findFirst()
							.get().Name;
					wNotEnd.StatusText = "待确认";
					wNotEnd.IsFinish = false;
					wNotEnd.NodeType = 3;
					wResult.add(wNotEnd);
				}
			} else {
				EXCNode wRepeal = new EXCNode();
				wRepeal.IsFinish = true;
				wRepeal.NodeType = 3;
				wRepeal.DepartmentName = (wEmployeeList.stream().filter(p -> p.ID == wTree.CallCancel.OperatorID))
						.findFirst().get().Department;
				wRepeal.UserName = (wEmployeeList.stream().filter(y -> y.ID == wTree.CallCancel.OperatorID)).findFirst()
						.get().Name;
				wRepeal.EditTimeText = wSDF.format(wTree.CallCancel.CreateTime.getTime());
				wRepeal.StatusText = "已撤销";
				wResult.add(wRepeal);
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}
}