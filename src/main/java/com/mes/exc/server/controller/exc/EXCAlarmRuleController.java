
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

import com.mes.exc.server.service.po.exc.base.EXCAlarmRule;
import com.mes.exc.server.controller.BaseController;
import com.mes.exc.server.service.po.ServiceResult;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.utils.RetCode;
import com.mes.exc.server.service.utils.CloneTool;
import com.mes.exc.server.service.utils.StringUtils;

import com.mes.exc.server.service.EXCService;

@RestController
@RequestMapping("/api/EXCAlarmRule")
public class EXCAlarmRuleController extends BaseController {
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

			int wRespondLevel = StringUtils.parseInt(request.getParameter("RespondLevel"));
			int wStationType = StringUtils.parseInt(request.getParameter("StationType"));
			int wActive = StringUtils.parseInt(request.getParameter("Active"));

			ServiceResult<List<EXCAlarmRule>> wServerRst = wEXCService.EXC_GetAlarmRuleList(wBMSEmployee, wStationType,
					wRespondLevel, wActive);

			if (StringUtils.isEmpty(wServerRst.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServerRst.GetResult(), null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServerRst.getFaultCode(), wServerRst.GetResult(), null);
			}

		} catch (Exception e) {

			logger.error(e.toString());

			wResult = GetResult(RetCode.SERVER_CODE_ERR, e.toString(), null, null);
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
			long wAlarmID = StringUtils.parseLong(request.getParameter("AlarmID"));
			String wAlarmCode = StringUtils.parseString(request.getParameter("AlarmCode"));

			ServiceResult<EXCAlarmRule> wServiceResult = wEXCService.EXC_GetAlarmRule(wBMSEmployee, wID, wAlarmID,
					wAlarmCode);

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

			EXCAlarmRule wEXCAlarmRule = CloneTool.Clone(wParam.get("data"), EXCAlarmRule.class);

			if (wEXCAlarmRule.ID > 0) {
				wEXCAlarmRule.setEditorID(wBMSEmployee.getID());
				wEXCAlarmRule.EditTime = Calendar.getInstance();
			} else {
				wEXCAlarmRule.setCreatorID(wBMSEmployee.getID());
				wEXCAlarmRule.CreateTime = Calendar.getInstance();
			}
			ServiceResult<Long> wServiceResult = wEXCService.EXC_UpdateAlarmRule(wBMSEmployee, wEXCAlarmRule);
			wEXCAlarmRule.setID(wServiceResult.GetResult());
			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "", null, wEXCAlarmRule);
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode(), null, wEXCAlarmRule);
			}

		} catch (Exception e) {
			logger.error(e.toString());

			wResult = this.GetResult(RetCode.SERVER_CODE_ERR, e.toString(), null, null);
		}
		return wResult;
	}

	@PostMapping("/Login")

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

			List<EXCAlarmRule> wEXCAlarmRuleList = CloneTool.CloneArray(wParam.get("data"), EXCAlarmRule.class);
			int wActive = wParam.containsKey("Active") ? (int) wParam.get("Active") : 0;

			ServiceResult<Integer> wServiceResult = wEXCService.Active(wBMSEmployee,
					wEXCAlarmRuleList.stream().map(p -> p.ID).collect(Collectors.toList()), wActive);

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "");
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}

		} catch (Exception e) {
			logger.error(e.toString());

			wResult = this.GetResult(RetCode.SERVER_CODE_ERR, e.toString(), null, null);
		}
		return wResult;
	}

	@GetMapping("/Config")

	public Object Config(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (this.CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			@SuppressWarnings("unused")
			BMSEmployee wBMSEmployee = this.GetSession(request);

			// iPlant.Interface.Invoke.DataBaseService.ResAlarmList wServerRst =
			// ServiceClientDAO.getInstance().GetClient<iPlant.Interface.Invoke.DataBaseService.DataBaseServerClient>().GetAlarmDefine();

			/*
			 * if (wServerRst.ErrCode == RetCode.SERVER_CODE_SUC) { wResult =
			 * this.GetResult(RetCode.SERVER_CODE_SUC, "", wServerRst.DataList, null); }
			 * else { wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wServerRst.Message,
			 * wServerRst.DataList, null); }
			 */

		} catch (Exception e) {
			logger.error(e.toString());

			wResult = this.GetResult(RetCode.SERVER_CODE_ERR, e.toString(), null, null);
		}
		return wResult;
	}

}