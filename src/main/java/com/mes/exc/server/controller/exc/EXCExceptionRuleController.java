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

import com.mes.exc.server.service.po.exc.base.EXCExceptionRule;
import com.mes.exc.server.service.po.exc.base.EXCExceptionTemplate;
import com.mes.exc.server.service.po.exc.define.EXCResourceTypes;
import com.mes.exc.server.controller.BaseController;
import com.mes.exc.server.service.EXCService;
import com.mes.exc.server.service.po.ServiceResult;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.service.utils.CloneTool;
import com.mes.exc.server.service.utils.StringUtils;
import com.mes.exc.server.utils.RetCode;

@RestController
@RequestMapping("/api/EXCExceptionRule")
public class EXCExceptionRuleController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(EXCCallTaskController.class);

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

			long wExceptionType = StringUtils.parseLong(request.getParameter("ExceptionType"));
			String wName = StringUtils.parseString(request.getParameter("Name"));
			int wRespondLevel = StringUtils.parseInt(request.getParameter("RespondLevel"));
			int wRequestType = StringUtils.parseInt(request.getParameter("RequestType"));
			int wResponseType = StringUtils.parseInt(request.getParameter("ResponseType"));
			int wConfirmType = StringUtils.parseInt(request.getParameter("ConfirmType"));
			int wActive = StringUtils.parseInt(request.getParameter("Active"));

			ServiceResult<List<EXCExceptionRule>> wServerRst = wEXCService.EXC_GetExceptionRuleList(wBMSEmployee,wName,
					wExceptionType, wRespondLevel, EXCResourceTypes.getEnumType(wRequestType),
					EXCResourceTypes.getEnumType(wResponseType), EXCResourceTypes.getEnumType(wConfirmType), wActive);

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

	@GetMapping("/Template")
	public Object Template(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (this.CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wBMSEmployee = this.GetSession(request);

			ServiceResult<List<EXCExceptionTemplate>> wServerRst = wEXCService.EXC_GetExceptionTemplateList(wBMSEmployee);

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

			ServiceResult<EXCExceptionRule> wServerRst = wEXCService.EXC_GetExceptionRuleByID(wBMSEmployee,wID);

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

			EXCExceptionRule wEXCExceptionRule = CloneTool.Clone(wParam.get("data"), EXCExceptionRule.class);

			if (wEXCExceptionRule.ID > 0) {
				wEXCExceptionRule.EditorID = wBMSEmployee.getID();
				wEXCExceptionRule.EditTime = Calendar.getInstance();
			} else {
				wEXCExceptionRule.CreatorID = wBMSEmployee.getID();
				wEXCExceptionRule.CreateTime = Calendar.getInstance();
			}
			ServiceResult<Long> wServerRst = wEXCService.EXC_UpdateExceptionRule(wBMSEmployee,wEXCExceptionRule);
			wEXCExceptionRule.setID(wServerRst.GetResult());

			if (StringUtils.isEmpty(wServerRst.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "", null, wEXCExceptionRule);
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wServerRst.getFaultCode(), null, wEXCExceptionRule);
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

			List<EXCExceptionRule> wEXCExceptionRuleList = CloneTool.CloneArray(wParam.get("data"),
					EXCExceptionRule.class);
			int wActive = wParam.containsKey("Active") ? (int) wParam.get("Active") : 0;

			ServiceResult<Integer> wServerRst = wEXCService.EXC_ActiveExceptionRule(wBMSEmployee,
					wEXCExceptionRuleList.stream().map(p -> p.ID).collect(Collectors.toList()), wActive);

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

}