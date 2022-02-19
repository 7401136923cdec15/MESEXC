package com.mes.exc.server.controller.exc;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mes.exc.server.service.po.exc.EXCCallTaskBPM;
import com.mes.exc.server.service.po.exc.EXCLevelType;
import com.mes.exc.server.service.po.exc.define.EXCAndonTypes;
import com.mes.exc.server.service.po.exc.tree.EXCAndon;
import com.mes.exc.server.service.po.exc.tree.EXCMessage;
import com.mes.exc.server.service.po.fmc.FMCWorkspace;
import com.mes.exc.server.controller.BaseController;
import com.mes.exc.server.service.EXCService;
import com.mes.exc.server.service.po.APIResult;
import com.mes.exc.server.service.po.ServiceResult;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.utils.RetCode;
import com.mes.exc.server.service.utils.CloneTool;
import com.mes.exc.server.service.utils.StringUtils;
import com.mes.exc.server.serviceimpl.CoreServiceImpl;

@RestController
@RequestMapping("/api/EXCAndon")
public class EXCAndonController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(EXCAlarmRuleController.class);

	@Autowired
	EXCService wEXCService;

	@GetMapping("/All")
	public Object All(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (this.CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wBMSEmployee = this.GetSession(request);

			int wWorkShopID = StringUtils.parseInt(request.getParameter("WorkShopID"));
			int wLineID = StringUtils.parseInt(request.getParameter("LineID"));
			int wPlaceID = StringUtils.parseInt(request.getParameter("PlaceID"));
			String wStationCode = StringUtils.parseString(request.getParameter("StationCode"));
			long wSourceID = StringUtils.parseLong(request.getParameter("SourceID"));
			int wSourceType = StringUtils.parseInt(request.getParameter("SourceType"));
			int wShiftID = StringUtils.parseInt(request.getParameter("ShiftID"));
			int wStatus = StringUtils.parseInt(request.getParameter("Status"));
			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			if (wShiftID == 0) {
				wShiftID = GetShiftID(wBMSEmployee, 0);
			}

			ServiceResult<List<EXCAndon>> wServiceResult = wEXCService.EXC_GetAndonList(wBMSEmployee, wWorkShopID,
					wLineID, wPlaceID, wStationCode, wSourceID, EXCAndonTypes.getEnumType(wSourceType), wShiftID,
					wStartTime, wEndTime, StringUtils.parseList(new Integer[] { wStatus }));

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.GetResult(), null);
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode(),
						wServiceResult.GetResult(), null);
			}

			// 翻译台位
			if (wServiceResult.Result != null && wServiceResult.Result.size() > 0) {
				APIResult wAPIResult = CoreServiceImpl.getInstance().FMC_GetFMCWorkspaceList(wBMSEmployee, -1, -1, "",
						-1, 1);
				List<FMCWorkspace> wFMCWorkspaceList = wAPIResult.List(FMCWorkspace.class);
				if (wFMCWorkspaceList != null && wFMCWorkspaceList.size() > 0) {
					for (EXCAndon wEXCAndon : wServiceResult.Result) {
						if (wEXCAndon.PlaceID > 0)
							continue;
						Optional<FMCWorkspace> wOption = wFMCWorkspaceList.stream()
								.filter(p -> p.PartNo.equals(wEXCAndon.CarName)).findFirst();
						if (wOption.isPresent()) {
							wEXCAndon.PlaceID = wOption.get().ID;
						}
					}
				}
			}
		} catch (Exception e) {

			logger.error(e.toString());

			wResult = GetResult(RetCode.SERVER_CODE_ERR, e.toString(), null, null);
		}
		return wResult;
	}
	
	@GetMapping("/BPMAll")
	public Object BPMAll(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (this.CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wBMSEmployee = this.GetSession(request);

			ServiceResult<List<EXCCallTaskBPM>> wServiceResult = wEXCService.EXC_GetAndonList(wBMSEmployee);

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.GetResult(), null);
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode(),
						wServiceResult.GetResult(), null);
			}
		} catch (Exception e) {

			logger.error(e.toString());

			wResult = GetResult(RetCode.SERVER_CODE_ERR, e.toString(), null, null);
		}
		return wResult;
	}

	@GetMapping("/Message")
	public Object Message(HttpServletRequest request) {

		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (this.CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wBMSEmployee = this.GetSession(request);

			int wResponsorID = StringUtils.parseInt(request.getParameter("ResponsorID"));
			int wModuleID = StringUtils.parseInt(request.getParameter("ModuleID"));
			int wShiftID = StringUtils.parseInt(request.getParameter("ShiftID"));
			int wActive = StringUtils.parseInt(request.getParameter("Active"));
			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			if (wShiftID == 0) {
				wShiftID = GetShiftID(wBMSEmployee, 0);
			}
			if (wResponsorID <= 0) {
				wResponsorID = wBMSEmployee.getID();
			}

			ServiceResult<List<EXCMessage>> wServiceResult = wEXCService.EXC_GetMessageList(wBMSEmployee, wResponsorID,
					wModuleID, wActive, wShiftID, wStartTime, wEndTime);

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.GetResult(), null);
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode(),
						wServiceResult.GetResult(), null);
			}

		} catch (Exception e) {

			logger.error(e.toString());

			wResult = GetResult(RetCode.SERVER_CODE_ERR, e.toString(), null, null);
		}
		return wResult;
	}

	@PostMapping("/UpdateMessage")
	public Object UpdateMessage(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
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

			List<EXCMessage> wEXCMessageList = CloneTool.CloneArray(wParam.get("data"), EXCMessage.class);

			ServiceResult<Integer> wServiceResult = wEXCService.EXC_UpdateMessageList(wBMSEmployee, wEXCMessageList);
			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "", wEXCMessageList, null);
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode(), wEXCMessageList, null);
			}
		} catch (Exception e) {
			logger.error(e.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, e.toString());
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

			EXCAndon wEXCAndon = CloneTool.Clone(wParam.get("data"), EXCAndon.class);

			ServiceResult<Integer> wServiceResult = wEXCService.EXC_UpdateAndon(wBMSEmployee, wEXCAndon);

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "", null, wEXCAndon);
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode(), null, wEXCAndon);
			}

		} catch (Exception e) {

			logger.error(e.toString());

			wResult = GetResult(RetCode.SERVER_CODE_ERR, e.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 根据ShiftDate和等级获取异常列表
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/ExceptionAll")
	public Object ExceptionAll(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			Calendar wShiftDate = StringUtils.parseCalendar(request.getParameter("ShiftDate"));
			int wAPSShiftPeriod = StringUtils.parseInt(request.getParameter("APSShiftPeriod"));
			int wLevel = StringUtils.parseInt(request.getParameter("Level"));

			ServiceResult<List<EXCCallTaskBPM>> wServiceResult = wEXCService.EXC_QueryExceptionAll(wLoginUser, wShiftDate,
					wAPSShiftPeriod, wLevel);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.FaultCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 获取异常等级类型频次
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/ExcLevelTypeTimes")
	public Object ExcLevelTypeTimes(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			ServiceResult<List<EXCLevelType>> wServiceResult = wEXCService.EXC_QueryLevelTypeLists(wLoginUser,
					wStartTime, wEndTime);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.FaultCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}
}