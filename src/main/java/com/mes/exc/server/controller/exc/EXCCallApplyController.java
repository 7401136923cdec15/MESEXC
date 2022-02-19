package com.mes.exc.server.controller.exc;

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

import com.mes.exc.server.service.po.exc.EXCTypeOption;
import com.mes.exc.server.service.po.exc.action.EXCCallApply;
import com.mes.exc.server.service.po.exc.base.EXCExceptionType;
import com.mes.exc.server.service.po.exc.base.EXCStation;
import com.mes.exc.server.service.po.exc.base.EXCStationType;
import com.mes.exc.server.service.po.exc.define.EXCApplyStatus;
import com.mes.exc.server.service.po.exc.define.TaskRelevancyTypes;
import com.mes.exc.server.controller.BaseController;
import com.mes.exc.server.service.CoreService;
import com.mes.exc.server.service.EXCService;
import com.mes.exc.server.service.po.APIResult;
import com.mes.exc.server.service.po.ServiceResult;
import com.mes.exc.server.service.po.bfc.QRTypes;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.service.po.dms.DMSDeviceLedger;
import com.mes.exc.server.service.po.dms.DMSLedgerStatus;
import com.mes.exc.server.service.po.fmc.FMCWorkspace;
import com.mes.exc.server.service.po.fpc.FPCProduct;
import com.mes.exc.server.service.po.sch.SCHWorker;
import com.mes.exc.server.utils.RetCode;
import com.mes.exc.server.service.utils.CloneTool;
import com.mes.exc.server.service.utils.StringUtils;

@RestController
@RequestMapping("/api/EXCCallApply")
public class EXCCallApplyController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(EXCCallApplyController.class);

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

			int wStationType = StringUtils.parseInt(request.getParameter("StationType"));
			String wStationNo = StringUtils.parseString(request.getParameter("StationNo"));
			long wStationID = StringUtils.parseLong(request.getParameter("StationID"));
			int wRespondLevel = StringUtils.parseInt(request.getParameter("RespondLevel"));
			int wDisplayBoard = StringUtils.parseInt(request.getParameter("DisplayBoard"));
			int wOnSite = StringUtils.parseInt(request.getParameter("OnSite"));
			long wApplicantID = StringUtils.parseLong(request.getParameter("ApplicantID"));
			long wApproverID = StringUtils.parseLong(request.getParameter("ApproverID"));
			long wConfirmID = StringUtils.parseLong(request.getParameter("ConfirmID"));

			int wStatus = StringUtils.parseInt(request.getParameter("Status"));
			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			String wPartNo = StringUtils.parseString(request.getParameter("PartNo"));

			/*
			 * OAGetType wOAGetType =
			 * (OAGetType)StringUtils.parseInt(request.getParameter("OAGetType"));
			 * 
			 * switch (wOAGetType) { case OAGetType.Default: break; case OAGetType.Apply:
			 * wApplicantID = wUserID; break; case OAGetType.Aduit: wApproverID = wUserID;
			 * break; case OAGetType.Confirm: wConfirmID = wUserID; break; default: break; }
			 */
			ServiceResult<List<EXCCallApply>> wServerRst = wEXCService.EXC_GetCallApplyList(wBMSEmployee, wStationNo,
					wStationType, wStationID, wRespondLevel, wDisplayBoard, wOnSite, wApplicantID, wApproverID,
					wConfirmID, wPartNo, wStartTime, wEndTime, wStatus);

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
			String wFaultCode = "";
			BMSEmployee wBMSEmployee = this.GetSession(request);

			long wID = StringUtils.parseLong(request.getParameter("ID"));

			ServiceResult<EXCCallApply> wServerRst = wEXCService.EXC_GetCallApplyByID(wBMSEmployee, wID);

			if (StringUtils.isEmpty(wServerRst.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "", null, wServerRst.GetResult());
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wFaultCode, null, wServerRst.GetResult());
			}

		} catch (Exception e) {
			logger.error(e.toString());
			wResult = this.GetResult(RetCode.SERVER_CODE_ERR, e.toString(), null, null);
		}
		return wResult;
	}

	@GetMapping("/Create")
	public Object Create(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (this.CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wBMSEmployee = this.GetSession(request);

			int wCompanyID = wBMSEmployee.getCompanyID();
			long wQRCodeID = StringUtils.parseLong(request.getParameter("ID"));

			String wQRCode = StringUtils.parseString(request.getParameter("QRCode"));

			QRTypes wQRType = QRTypes.getEnumType(StringUtils.parseInt(request.getParameter("QRType")));

			EXCCallApply wServerRst = new EXCCallApply();
			if (wQRCodeID < 1 || StringUtils.isEmpty(wQRCode) || wQRType == QRTypes.Default) {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, "参数输入不完全！", null, wServerRst);
				return wResult;
			}

			ServiceResult<EXCStation> wServiceResult = wEXCService.EXC_GetStation(wBMSEmployee, wQRCodeID, wQRCode,
					wQRType);

			if (!StringUtils.isEmpty(wServiceResult.getFaultCode()) || wServiceResult.GetResult() == null
					|| wServiceResult.GetResult().ID <= 0) {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode(), null, wServerRst);
				return wResult;
			}

			ServiceResult<EXCStationType> wEXCStationType = wEXCService.EXC_GetStationTypeByID(wBMSEmployee,
					wServiceResult.GetResult().StationType);

			wServerRst = new EXCCallApply();

			wServerRst.ApplicantID = wBMSEmployee.getID();
			wServerRst.ApplicantTime = Calendar.getInstance();
			wServerRst.Comment = "";
			wServerRst.CompanyID = wCompanyID;
			wServerRst.ConfirmID = 0;
			wServerRst.DisplayBoard = true;
			wServerRst.ExceptionTypeList = new ArrayList<EXCTypeOption>();
			wServerRst.ID = 0;
			wServerRst.ImageList = new ArrayList<String>();
			wServerRst.OnSite = false;
			wServerRst.StationID = wServiceResult.GetResult().ID;
			wServerRst.StationNo = wServiceResult.GetResult().StationNo;
			wServerRst.StationType = wServiceResult.GetResult().StationType;
			wServerRst.StationTypeName = wEXCStationType.GetResult().Name;
			wServerRst.Status = EXCApplyStatus.Default.getValue();
			wServerRst.PlaceID = 0;
			wServerRst.PlaceNo = "";
			wServerRst.PartNo = "";

			ServiceResult<List<EXCExceptionType>> wEXCExceptionTypeList = wEXCService.EXC_GetExceptionTypeList(
					wBMSEmployee, "", wEXCStationType.GetResult().ID, TaskRelevancyTypes.Default, 1);

			ServiceResult<List<DMSDeviceLedger>> wDMSDeviceLedgerList = wCoreService
					.DMS_GetDeviceLedgerList(wBMSEmployee, 0, 0, 0, 0, 0, 0, DMSLedgerStatus.Using);
			ServiceResult<List<FPCProduct>> wFPCProductList = wCoreService.FPC_QueryProductList(wBMSEmployee, 0, 0);

			APIResult wAPIResult = wCoreService.FMC_GetFMCWorkspaceList(wBMSEmployee, 0, 0, "", 0, 1);

			List<FMCWorkspace> wFMCWorkspaceList = wAPIResult.List(FMCWorkspace.class);
			APIResult wLeadWorker = wCoreService.SCH_QueryLeadWorkerByPositionID(wBMSEmployee,
					wBMSEmployee.getPosition(), GetShiftID(wBMSEmployee, 0));

			List<SCHWorker> wLeader = wLeadWorker.List(SCHWorker.class);

			if (StringUtils.isEmpty(wEXCExceptionTypeList.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "", wEXCExceptionTypeList.GetResult(), wServerRst);
				wResult = this.SetResult(wResult, "RespondLevel",
						wEXCService.EXC_GetRespondLevelList(wBMSEmployee).GetResult());
				wResult = this.SetResult(wResult, "Device", wDMSDeviceLedgerList.GetResult());
				wResult = this.SetResult(wResult, "Product", wFPCProductList.GetResult());
				wResult = this.SetResult(wResult, "Leader", wLeader);
				wResult = this.SetResult(wResult, "Place", wFMCWorkspaceList);
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wEXCExceptionTypeList.getFaultCode(),
						wEXCExceptionTypeList.GetResult(), wServerRst);
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

			int wProductID = wParam.containsKey("ProductID") ? StringUtils.parseInt(wParam.get("ProductID")) : 0;
			EXCCallApply wEXCCallApply = CloneTool.Clone(wParam.get("data"), EXCCallApply.class);

			if (StringUtils.isNotEmpty(wEXCCallApply.getPartNo())) {

				APIResult wAPIResult = wCoreService.FMC_QueryWorkspace(wBMSEmployee, wEXCCallApply.getPlaceID(), "");

				FMCWorkspace wFMCWorkspace = wAPIResult.Info(FMCWorkspace.class);
				if (wAPIResult.getResultCode() == RetCode.SERVER_CODE_SUC && wFMCWorkspace != null
						&& wFMCWorkspace.getID() > 0) {
					wFMCWorkspace.setPartNo(wEXCCallApply.getPartNo());
					// 获取车型
					wFMCWorkspace.setProductID(wProductID);

					wCoreService.FMC_BindFMCWorkspace(wBMSEmployee, wFMCWorkspace);
				}
			}

			if (wEXCCallApply.ID <= 0) {
				wEXCCallApply.Status = EXCApplyStatus.Waiting.getValue();
				wEXCCallApply.ApplicantID = wBMSEmployee.getID();
				wEXCCallApply.ApplicantTime = Calendar.getInstance();
			}

			switch (EXCApplyStatus.getEnumType(wEXCCallApply.Status)) {
			case Default:
			case Waiting:
				wEXCCallApply.ApplicantTime = Calendar.getInstance();
				wEXCCallApply.ApplicantID = wBMSEmployee.getID();

				ServiceResult<Boolean> wIsAllowApplyResult = wEXCService.EXC_IsAllowApply(wBMSEmployee, wEXCCallApply,
						GetShiftID(wBMSEmployee, 0));
				if (!wIsAllowApplyResult.GetResult()) {
					wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wIsAllowApplyResult.getFaultCode(), null,
							wEXCCallApply);

					return wResult;
				}

				break;
			case Cancel:
				break;
			case Audit:
			case Reject:
				wEXCCallApply.ApproverTime = Calendar.getInstance();
				wEXCCallApply.ApproverID = wBMSEmployee.getID();
				break;
			case Confirm:
				wEXCCallApply.ConfirmTime = Calendar.getInstance();
				wEXCCallApply.ConfirmID = wBMSEmployee.getID();
				break;
			default:
				break;
			}

			if (wEXCCallApply.ID <= 0) {
				if (wEXCCallApply.ApproverID == 0 || wEXCCallApply.ApproverID == wEXCCallApply.ApplicantID) {
					wEXCCallApply.ApproverID = wBMSEmployee.getID();
					wEXCCallApply.ApproverTime = Calendar.getInstance();
					wEXCCallApply.Status = (int) EXCApplyStatus.Audit.getValue();
				}
				if (wEXCCallApply.ConfirmID == 0 || wEXCCallApply.ConfirmID == wEXCCallApply.ApplicantID) {
					wEXCCallApply.ConfirmID = wBMSEmployee.getID();
					wEXCCallApply.Status = (int) EXCApplyStatus.Confirm.getValue();
					wEXCCallApply.ConfirmTime = Calendar.getInstance();
				}
			}
			ServiceResult<Long> wUpdateCallApplyResult = wEXCService.EXC_UpdateCallApply(wBMSEmployee, wEXCCallApply,
					GetShiftID(wBMSEmployee, 0));

			wEXCCallApply.setID(wUpdateCallApplyResult.GetResult());

			if (StringUtils.isEmpty(wUpdateCallApplyResult.getFaultCode())) {
				wResult = this.GetResult(RetCode.SERVER_CODE_SUC, "", null, wEXCCallApply);
			} else {
				wResult = this.GetResult(RetCode.SERVER_CODE_ERR, wUpdateCallApplyResult.getFaultCode(), null,
						wEXCCallApply);
			}

		} catch (Exception e) {
			logger.error(e.toString());
			wResult = this.GetResult(RetCode.SERVER_CODE_ERR, e.toString(), null, null);
		}
		return wResult;
	}

}