package com.mes.exc.server.controller.exc;

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

import com.mes.exc.server.service.po.exc.EXCOptionItem;
import com.mes.exc.server.service.po.exc.action.EXCCallAction;
import com.mes.exc.server.service.po.exc.define.EXCActionTypes;
import com.mes.exc.server.controller.BaseController;
import com.mes.exc.server.service.EXCService;
import com.mes.exc.server.service.po.ServiceResult;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.utils.RetCode;
import com.mes.exc.server.service.utils.CloneTool;
import com.mes.exc.server.service.utils.StringUtils;

@RestController
@RequestMapping("/api/EXCCallAction")
public class EXCCallActionController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(EXCCallActionController.class);

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

			long wTaskID = StringUtils.parseLong(request.getParameter("TaskID"));
			int wActionType = StringUtils.parseInt(request.getParameter("ActionType"));
			long wOpreatorID = StringUtils.parseLong(request.getParameter("OperatorID"));
			long wForwarder = StringUtils.parseLong(request.getParameter("Forwarder"));
			long wDispatchID = StringUtils.parseLong(request.getParameter("DispatchID"));

			ServiceResult<List<EXCCallAction>> wServiceResult = wEXCService.EXC_GetCallActionList(wBMSEmployee, wTaskID,
					EXCActionTypes.getEnumType(wActionType), wOpreatorID, wForwarder, wDispatchID);

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {

				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.GetResult(), null);
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode(),
						wServiceResult.GetResult(), null);
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

			ServiceResult<EXCCallAction> wServiceResult = wEXCService.EXC_GetCallActionByID(wBMSEmployee, wID);

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "", null, wServiceResult.GetResult());
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode(), null,
						wServiceResult.GetResult());
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
			EXCCallAction wEXCAction = CloneTool.Clone(wParam.get("data"), EXCCallAction.class);

			wEXCAction.CreateTime = Calendar.getInstance();
			wEXCAction.OperatorID = wBMSEmployee.getID();
			wEXCAction.CompanyID = wBMSEmployee.getCompanyID();
			ServiceResult<Long> wServiceResult = wEXCService.EXC_UpdateCallAction(wBMSEmployee, wEXCAction,
					GetShiftID(wBMSEmployee, wBMSEmployee.getID()), false);

			wEXCAction.setID(wServiceResult.GetResult());

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "", null, wEXCAction);
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode(), null, wEXCAction);
			}

		} catch (Exception e) {
			logger.error(e.toString());
			wResult = this.GetResult(RetCode.SERVER_CODE_ERR, e.toString(), null, null);
		}
		return wResult;
	}

	@GetMapping("/Type")
	public Object Type(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (this.CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wBMSEmployee = this.GetSession(request);

			ServiceResult<List<EXCOptionItem>> wServerRst = wEXCService.EXC_GetActionTypeList(wBMSEmployee);

			if (StringUtils.isEmpty(wServerRst.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "", wServerRst, null);
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

}