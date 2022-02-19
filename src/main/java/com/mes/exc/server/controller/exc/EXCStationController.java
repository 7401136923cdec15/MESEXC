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

import com.mes.exc.server.service.po.exc.base.EXCStation;
import com.mes.exc.server.controller.BaseController;
import com.mes.exc.server.service.EXCService;
import com.mes.exc.server.service.po.ServiceResult;
import com.mes.exc.server.service.po.bfc.QRTypes;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.service.utils.CloneTool;
import com.mes.exc.server.service.utils.StringUtils;
import com.mes.exc.server.utils.RetCode;

@RestController
@RequestMapping("/api/EXCStation")
public class EXCStationController extends BaseController {

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
			long wRelevancyID = StringUtils.parseLong(request.getParameter("RelevancyID"));
			long wStationType = StringUtils.parseLong(request.getParameter("StationType"));
			String wStationName = StringUtils.parseString(request.getParameter("StationName"));
			int wActive = StringUtils.parseInt(request.getParameter("Active"));

			ServiceResult<List<EXCStation>> wServerRst = wEXCService.EXC_GetStationList(wBMSEmployee, wStationName,
					wStationType, QRTypes.getEnumType(wRelevancyType), wRelevancyID, wActive);

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
			String wStationNo = StringUtils.parseString(request.getParameter("StationNo"));

			int wRelevancyType = StringUtils.parseInt(request.getParameter("RelevancyType"));
			long wRelevancyID = StringUtils.parseLong(request.getParameter("RelevancyID"));

			ServiceResult<EXCStation> wServerRst = new ServiceResult<EXCStation>(new EXCStation());
			if (wID > 0 && !StringUtils.isEmpty(wStationNo)) {
				wServerRst = wEXCService.EXC_GetStationByID(wBMSEmployee, wID, wStationNo);
			} else if (wRelevancyID > 0 && wRelevancyType > 0) {
				wServerRst = wEXCService.EXC_GetStationByRelevancyID(wBMSEmployee, QRTypes.getEnumType(wRelevancyType),
						wRelevancyID);
			}

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

			EXCStation wEXCStation = CloneTool.Clone(wParam.get("data"), EXCStation.class);

			if (wEXCStation.ID > 0) {
				wEXCStation.EditorID = wBMSEmployee.getID();
				wEXCStation.EditTime = Calendar.getInstance();
			} else {
				wEXCStation.CreatorID = wBMSEmployee.getID();
				wEXCStation.CreateTime = Calendar.getInstance();
			}
			ServiceResult<Long> wServerRst = wEXCService.EXC_UpdateStation(wBMSEmployee, wEXCStation);
			wEXCStation.setID(wServerRst.GetResult());
			if (StringUtils.isEmpty(wServerRst.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "", null, wEXCStation);
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wServerRst.getFaultCode(), null, wEXCStation);
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

			List<EXCStation> wEXCStationList = CloneTool.CloneArray(wParam.get("data"), EXCStation.class);

			int wActive = wParam.containsKey("Active") ? (int) wParam.get("Active") : 0;

			ServiceResult<Integer> wServerRst = wEXCService.EXC_ActiveStation(wBMSEmployee,
					wEXCStationList.stream().map(p -> p.ID).collect(Collectors.toList()), wActive);

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