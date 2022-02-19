package com.mes.exc.server.controller.exc;

import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mes.exc.server.controller.BaseController;
import com.mes.exc.server.service.EXCService;
import com.mes.exc.server.service.po.ServiceResult;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.service.po.bpm.BPMTaskBase;
import com.mes.exc.server.service.po.exc.EXCCallTaskBPM;
import com.mes.exc.server.service.utils.StringUtils;
import com.mes.exc.server.utils.RetCode;

/**
 * 异常呼叫(流程引擎版)
 * 
 * @author PengYouWang
 * @CreateTime 2020-8-4 14:21:48
 * @LastEditTime 2020-8-4 14:21:52
 */
@RestController
@RequestMapping("/api/EXCCallTaskBPM")
public class EXCCallTaskBPMController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(EXCCallTaskBPMController.class);

	@Autowired
	EXCService wEXCService;

	/**
	 * 查历史记录
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/History")
	public Object History(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wID = StringUtils.parseInt(request.getParameter("ID"));
			String wCode = StringUtils.parseString(request.getParameter("Code"));
			int wUpFlowID = StringUtils.parseInt(request.getParameter("UpFlowID"));
			int wShiftID = StringUtils.parseInt(request.getParameter("ShiftID"));
			String wPartNo = StringUtils.parseString(request.getParameter("PartNo"));
			int wPlaceID = StringUtils.parseInt(request.getParameter("PlaceID"));
			int wResponseLevel = StringUtils.parseInt(request.getParameter("ResponseLevel"));
			int wExceptionType = StringUtils.parseInt(request.getParameter("ExceptionType"));
			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			ServiceResult<List<EXCCallTaskBPM>> wServiceResult = wEXCService.EXC_QueryCallTaskBPMHistory(wLoginUser,
					wID, wCode, wUpFlowID, wShiftID, wPartNo, wPlaceID, wStartTime, wEndTime, wResponseLevel,
					wExceptionType);

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
	 * 人员获取任务
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/EmployeeAll")
	public Object EmployeeAll(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wTagTypes = StringUtils.parseInt(request.getParameter("TagTypes"));
			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			ServiceResult<List<BPMTaskBase>> wServiceResult = wEXCService.EXC_QueryCallTaskBPMEmployeeAll(wLoginUser,
					wTagTypes, wStartTime, wEndTime);

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
	 * 所有数据(待办、已办、发起)
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/EmployeeAllList")
	public Object EmployeeAllList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数(4已办 1待办 2发起)
			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));
			int wLevel = StringUtils.parseInt(request.getParameter("Level"));
			int wExceptionType = StringUtils.parseInt(request.getParameter("ExceptionType"));
			int wStatus = StringUtils.parseInt(request.getParameter("Status"));

			ServiceResult<List<EXCCallTaskBPM>> wServiceResult = wEXCService.EXC_QueryCallTaskBPMEmployeeAllList(
					wLoginUser, wStartTime, wEndTime, wPartID, wLevel, wExceptionType, wStatus);

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
	 * 查单条
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/Info")
	public Object Info(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wID = StringUtils.parseInt(request.getParameter("ID"));

			ServiceResult<EXCCallTaskBPM> wServiceResult = wEXCService.EXC_GetCallTaskBPM(wLoginUser, wID);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, wServiceResult.Result);
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
	 * 获取上级通知人员
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/NoticeList")
	public Object NoticeList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			ServiceResult<List<BMSEmployee>> wServiceResult = wEXCService.EXC_QueryNoticeList(wLoginUser);

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
	 * 所有数据(待办、已办、发起)
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/EmployeeAllWeb")
	public Object EmployeeAllWeb(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数(4已办 1待办 2发起)
			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));
			int wPlaceID = StringUtils.parseInt(request.getParameter("PlaceID"));
			String wPartNo = StringUtils.parseString(request.getParameter("PartNo"));
			int wStationID = StringUtils.parseInt(request.getParameter("StationID"));
			int wLevel = StringUtils.parseInt(request.getParameter("Level"));
			int wExceptionType = StringUtils.parseInt(request.getParameter("ExceptionType"));
			int wStatus = StringUtils.parseInt(request.getParameter("Status"));

			ServiceResult<List<EXCCallTaskBPM>> wServiceResult = wEXCService.EXC_QueryCallTaskBPMList(wLoginUser,
					wExceptionType, wLevel, wPartNo, wStationID, wPlaceID, wStartTime, wEndTime, wStatus);

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
