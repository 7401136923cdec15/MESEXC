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

import com.mes.exc.server.service.po.exc.base.EXCStationType;
import com.mes.exc.server.controller.BaseController;
import com.mes.exc.server.service.EXCService;
import com.mes.exc.server.service.po.ServiceResult;
import com.mes.exc.server.service.po.bfc.QRTypes;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.service.utils.CloneTool;
import com.mes.exc.server.service.utils.StringUtils;
import com.mes.exc.server.utils.RetCode;

@RestController
@RequestMapping("/api/EXCStationType")
public class EXCStationTypeController extends BaseController {

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

			int wRelevancyType = StringUtils.parseInt(request.getParameter("RelevancyType"));
			String wName = StringUtils.parseString(request.getParameter("Name"));
			int wActive = StringUtils.parseInt(request.getParameter("Active"));

			ServiceResult<List<EXCStationType>> wServerRst = wEXCService.EXC_GetStationTypeList(wBMSEmployee, wName,
					QRTypes.getEnumType(wRelevancyType), wActive);

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

			ServiceResult<EXCStationType> wServerRst = wEXCService.EXC_GetStationTypeByID(wBMSEmployee, wID);

			if (StringUtils.isEmpty(wServerRst.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "", null, wServerRst.GetResult());
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wServerRst.getFaultCode(), null,
						wServerRst.GetResult());
			}

		} catch (Exception e) {
			logger.error(e.toString());
			wResult = this.GetResult(RetCode.SERVER_CODE_ERR, e.toString());
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

			EXCStationType wEXCStationType = CloneTool.Clone(wParam.get("data"), EXCStationType.class);

			if (wEXCStationType.ID > 0) {
				wEXCStationType.EditorID = wBMSEmployee.getID();
				wEXCStationType.EditTime = Calendar.getInstance();
			} else {
				wEXCStationType.CreatorID = wBMSEmployee.getID();
				wEXCStationType.CreateTime = Calendar.getInstance();
			}
			ServiceResult<Long> wServerRst = wEXCService.EXC_UpdateStationType(wBMSEmployee, wEXCStationType);
			wEXCStationType.setID(wServerRst.GetResult());
			if (StringUtils.isEmpty(wServerRst.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "", null, wEXCStationType);
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wServerRst.getFaultCode(), null, wEXCStationType);
			}

		} catch (Exception e) {
			logger.error(e.toString());
			wResult = this.GetResult(RetCode.SERVER_CODE_ERR, e.toString());
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

			List<EXCStationType> wEXCStationTypeList = CloneTool.CloneArray(wParam.get("data"), EXCStationType.class);

			int wActive = wParam.containsKey("Active") ? (int) wParam.get("Active") : 0;

			ServiceResult<Integer> wServerRst = wEXCService.EXC_ActiveStationType(wBMSEmployee,
					wEXCStationTypeList.stream().map(p -> p.ID).collect(Collectors.toList()), wActive);

			if (StringUtils.isEmpty(wServerRst.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "");
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wServerRst.getFaultCode());
			}

		} catch (Exception e) {
			logger.error(e.toString());
			wResult = this.GetResult(RetCode.SERVER_CODE_ERR, e.toString());
		}
		return wResult;
	}

}