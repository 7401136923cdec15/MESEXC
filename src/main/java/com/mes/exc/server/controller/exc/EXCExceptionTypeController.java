package com.mes.exc.server.controller.exc;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mes.exc.server.service.po.exc.EXCOptionItem;
import com.mes.exc.server.service.po.exc.PositionEmployee;
import com.mes.exc.server.service.po.exc.base.EXCExceptionType;
import com.mes.exc.server.service.po.exc.define.TaskRelevancyTypes;
import com.mes.exc.server.controller.BaseController;
import com.mes.exc.server.service.CoreService;
import com.mes.exc.server.service.EXCService;
import com.mes.exc.server.service.po.APIResult;
import com.mes.exc.server.service.po.ServiceResult;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.service.po.sch.SCHWorker;
import com.mes.exc.server.service.utils.CloneTool;
import com.mes.exc.server.service.utils.StringUtils;
import com.mes.exc.server.utils.RetCode;

@RestController
@RequestMapping("/api/EXCExceptionType")
public class EXCExceptionTypeController extends BaseController {
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

			int wRelevancyTaskType = StringUtils.parseInt(request.getParameter("RelevancyTaskType"));
			String wName = StringUtils.parseString(request.getParameter("Name"));
			int wActive = StringUtils.parseInt(request.getParameter("Active"));
			long wStationType = StringUtils.parseLong(request.getParameter("StationType"));

			ServiceResult<List<EXCExceptionType>> wServerRst = wEXCService.EXC_GetExceptionTypeList(wBMSEmployee, wName,
					wStationType, TaskRelevancyTypes.getEnumType(wRelevancyTaskType), wActive);

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

			ServiceResult<EXCExceptionType> wServerRst = wEXCService.EXC_GetExceptionTypeByID(wBMSEmployee, wID);

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

	@PostMapping("/Update")
	public Object Update(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (this.CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wBMSEmployee = this.GetSession(request);

			if (!wParam.containsKey("data")) {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			EXCExceptionType wEXCExceptionType = CloneTool.Clone(wParam.get("data"), EXCExceptionType.class);

			if (wEXCExceptionType.ID > 0) {
				wEXCExceptionType.EditorID = wBMSEmployee.getID();
				wEXCExceptionType.EditTime = Calendar.getInstance();
			} else {
				wEXCExceptionType.CreatorID = wBMSEmployee.getID();
				wEXCExceptionType.CreateTime = Calendar.getInstance();
			}
			ServiceResult<Long> wServerRst = wEXCService.EXC_UpdateExceptionType(wBMSEmployee, wEXCExceptionType);
			wEXCExceptionType.setID(wServerRst.GetResult());
			if (StringUtils.isEmpty(wServerRst.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "", null, wEXCExceptionType);
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wServerRst.getFaultCode(), null, wEXCExceptionType);
			}

		} catch (Exception e) {
			logger.error(e.toString());
			wResult = this.GetResult(RetCode.SERVER_CODE_ERR, e.toString(), null, null);
		}
		return wResult;
	}

	@PostMapping("/Active")
	public Object Active(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (this.CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wBMSEmployee = this.GetSession(request);

			if (!wParam.containsKey("data")) {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			List<EXCExceptionType> wEXCExceptionTypeList = CloneTool.CloneArray(wParam.get("data"),
					EXCExceptionType.class);
			int wActive = wParam.containsKey("Active") ? (int) wParam.get("Active") : 0;

			ServiceResult<Integer> wServerRst = wEXCService.EXC_ActiveExceptionType(wBMSEmployee,
					wEXCExceptionTypeList.stream().map(p -> p.ID).collect(Collectors.toList()), wActive);

			if (StringUtils.isEmpty(wServerRst.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "");
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wServerRst.getFaultCode());
			}

		} catch (Exception e) {
			logger.error(e.toString());
			wResult = this.GetResult(RetCode.SERVER_CODE_ERR, e.toString(), null, null);
		}
		return wResult;
	}

	@GetMapping("/InfoEmployee")
	public Object InfoEmployee(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (this.CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wBMSEmployee = this.GetSession(request); 
			long wExceptionType = StringUtils.parseLong(request.getParameter("ExceptionType"));

			ServiceResult<EXCExceptionType> wServiceResult = wEXCService.EXC_GetExceptionTypeByID(wBMSEmployee,
					wExceptionType);
			EXCExceptionType wEXCExceptionType = wServiceResult.GetResult();
			String wFaultCode = "";
			PositionEmployee wServerRst = new PositionEmployee();

			APIResult wSCHWorkerServiceResult = null;

			if (wEXCExceptionType != null && wEXCExceptionType.ID > 0) {

				wSCHWorkerServiceResult = wCoreService.SCH_QueryWorkerByPositionID(wBMSEmployee,
						wEXCExceptionType.ApproverPositionID, GetShiftID(wBMSEmployee, 0));

				List<SCHWorker> wSCHWorkerList = wSCHWorkerServiceResult.List(SCHWorker.class);

				if (wSCHWorkerList != null && wSCHWorkerList.size() >= 0) {
					wServerRst.Approver = new EXCOptionItem();
					wServerRst.Approver.ID = wSCHWorkerList.get(0).WorkerID;
					wServerRst.Approver.Name = wSCHWorkerList.get(0).WorkerName;
				}
				wSCHWorkerServiceResult = wCoreService.SCH_QueryWorkerByPositionID(wBMSEmployee,
						wEXCExceptionType.ConfirmPositionID, GetShiftID(wBMSEmployee, 0));
				wSCHWorkerList = wSCHWorkerServiceResult.List(SCHWorker.class);

				if (wSCHWorkerList != null && wSCHWorkerList.size() >= 0) {
					wServerRst.Confirmer = new EXCOptionItem();

					wServerRst.Confirmer.ID = wSCHWorkerList.get(0).WorkerID;
					wServerRst.Confirmer.Name = wSCHWorkerList.get(0).WorkerName;

				}

				if (wEXCExceptionType.DutyPositionID != null && wEXCExceptionType.DutyPositionID.size() > 0) {

					for (int item : wEXCExceptionType.DutyPositionID) {
						wSCHWorkerServiceResult = wCoreService.SCH_QueryWorkerByPositionID(wBMSEmployee, item,
								GetShiftID(wBMSEmployee, 0));
						wSCHWorkerList = wSCHWorkerServiceResult.List(SCHWorker.class);

						if (wSCHWorkerList != null && wSCHWorkerList.size() >= 0) {
							for (SCHWorker wSCHWorker : wSCHWorkerList) {
								EXCOptionItem wEXCOptionItem = new EXCOptionItem();

								wEXCOptionItem.ID = wSCHWorker.WorkerID;
								wEXCOptionItem.Name = wSCHWorker.WorkerName;

								wServerRst.ResponserList.add(wEXCOptionItem);
							}

						}

					}

				}

			}

			if (StringUtils.isEmpty(wFaultCode)) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "", null, wServerRst);
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wFaultCode, null, wServerRst);
			}

		} catch (Exception e) {
			logger.error(e.toString());
			wResult = this.GetResult(RetCode.SERVER_CODE_ERR, e.toString(), null, null);
		}
		return wResult;
	}

	@GetMapping("/LevelAll")
	public Object LevelAll(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (this.CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wBMSEmployee = this.GetSession(request);

			ServiceResult<List<EXCOptionItem>> wServerRst = wEXCService.EXC_GetRespondLevelList(wBMSEmployee);

			if (StringUtils.isEmpty(wServerRst.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "", wServerRst.GetResult(), null);
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wServerRst.getFaultCode(), wServerRst.GetResult(),
						null);
			}

		} catch (Exception e) {
			logger.error(e.toString());
			wResult = this.GetResult(RetCode.SERVER_CODE_ERR, e.toString());
		}
		return wResult;
	}

	@PostMapping("/SaveLevel")
	public Object SaveLevel(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (this.CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wBMSEmployee = this.GetSession(request);

			if (!wParam.containsKey("data")) {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			List<EXCOptionItem> wEXCExceptionTypeList = CloneTool.CloneArray(wParam.get("data"), EXCOptionItem.class);

			ServiceResult<List<EXCOptionItem>> wServerRst = wEXCService.EXC_UpdateRespondLevelList(wBMSEmployee,
					wEXCExceptionTypeList);

			if (StringUtils.isEmpty(wServerRst.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "", wServerRst.GetResult(), null);
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wServerRst.getFaultCode(), wServerRst.GetResult(),
						null);
			}

		} catch (Exception e) {
			logger.error(e.toString());
			wResult = this.GetResult(RetCode.SERVER_CODE_ERR, e.toString());
		}
		return wResult;
	}
}
