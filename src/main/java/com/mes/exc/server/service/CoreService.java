package com.mes.exc.server.service;

import java.util.Calendar;
import java.util.List;

import com.mes.exc.server.service.po.APIResult;
import com.mes.exc.server.service.po.ServiceResult;
import com.mes.exc.server.service.po.aps.APSBOMItem;
import com.mes.exc.server.service.po.bfc.BFCMessage;
import com.mes.exc.server.service.po.bfc.BFCMessageResult;
import com.mes.exc.server.service.po.bfc.QRTypes;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.service.po.dms.DMSDeviceLedger;
import com.mes.exc.server.service.po.dms.DMSLedgerStatus;
import com.mes.exc.server.service.po.fmc.FMCWorkspace;
import com.mes.exc.server.service.po.fpc.FPCProduct;
import com.mes.exc.server.utils.Configuration;

public interface CoreService {

	static String ServerUrl = Configuration.readConfigString("core.server.url", "config/config");

	static String ServerName = Configuration.readConfigString("core.server.project.name", "config/config");

	// Function01:User Manager Interface
	APIResult BMS_LoginEmployee(String wLoginName, String wPassword, String wToken, long wMac, int wnetJS);

	APIResult BMS_GetEmployeeAll(BMSEmployee wLoginUser, int wDepartmentID, int wPosition, int wActive);

	APIResult BMS_QueryEmployeeByID(BMSEmployee wLoginUser, int wID);

	APIResult BMS_CheckPowerByAuthorityID(int wCompanyID, int wUserID, int wFunctionID, int wRangeID, int wTypeID);

	APIResult SFC_QueryShiftID(BMSEmployee wLoginUser, int Shifts);

	APIResult FMC_QueryWorkspace(BMSEmployee wLoginUser, int wID, String wCode);

	APIResult FMC_GetFMCWorkspaceList(BMSEmployee wLoginUser, int wProductID, int wPartID, String wPartNo,
			int wPlaceType, int wActive);

	APIResult SCH_QueryLeadWorkerByPositionID(BMSEmployee wLoginUser, int wPositionID, int wShiftID);

	APIResult SCH_QueryWorkerByPositionID(BMSEmployee wLoginUser, int wPositionID, int wShiftID);

	APIResult BFC_GetQRCode(BMSEmployee wLoginUser, QRTypes wQRTypes, long wQRCodeID);

	APIResult BFC_GetQRType(BMSEmployee wLoginUser, String wQRCode);

	APIResult FMC_SaveFMCWorkspace(BMSEmployee wBMSEmployee, FMCWorkspace wFMCWorkspace);

	ServiceResult<List<DMSDeviceLedger>> DMS_GetDeviceLedgerList(BMSEmployee wBMSEmployee, int wBusinessUnitID,
			int wBaseID, int wFactoryID, int wWorkShopID, int wLineID, int wModelID, DMSLedgerStatus wDMSLedgerStatus);

	ServiceResult<List<FPCProduct>> FPC_QueryProductList(BMSEmployee wBMSEmployee, int wBusinessUnitID,
			int wProductTypeID);

	APIResult FPC_GetPartByID(BMSEmployee wLoginEmployee, long wID);

	APIResult BMS_QueryPositionList(BMSEmployee wLoginUser);

	APIResult BMS_QueryDepartmentList(BMSEmployee wLoginUser);

	APIResult FMC_BindFMCWorkspace(BMSEmployee wBMSEmployee, FMCWorkspace wFMCWorkspace);

	APIResult BFC_UpdateMessageList(BMSEmployee wLoginUser, List<BFCMessage> wBFCMessageList);

	APIResult BFC_GetMessageList(BMSEmployee wLoginUser, int wResponsorID, int wModuleID, List<Integer> wMessageID,
			int wType, int wActive);

	APIResult BFC_GetMessageList(BMSEmployee wLoginUser, int wResponsorID, int wModuleID, int wMessageID, int wType,
			int wActive, int wShiftID, Calendar wStartTime, Calendar wEndTime);

	APIResult BFC_GetMessageList(BMSEmployee wLoginUser, int wResponsorID, int wModuleID, int wType, int wActive,
			int wShiftID, Calendar wStartTime, Calendar wEndTime);

	APIResult FMC_QueryWorkShopList(BMSEmployee wLoginUser);

	APIResult FMC_QueryLineList(BMSEmployee wLoginUser);

	APIResult FPC_QueryPartList(BMSEmployee wLoginUser);

	APIResult FPC_QueryPartPointList(BMSEmployee wLoginUser);

	APIResult FPC_QueryProductList(BMSEmployee wLoginUser);

	APIResult FMC_QueryWorkChargeList(BMSEmployee wLoginUser);

	APIResult CRM_QueryCustomerList(BMSEmployee wLoginUser);

	APIResult CFG_QueryUnitList(BMSEmployee wLoginUser);

	APIResult BMS_GetSuperior(BMSEmployee wLoginUser, BMSEmployee wData);

	APIResult APS_QueryDeleteList(BMSEmployee wLoginUser, int wOrderID, int wPartID, String wMaterialNos);

	APIResult APS_DeleteBom(BMSEmployee wLoginUser, APSBOMItem wAPSBOMItem);

	BFCMessageResult BFC_MessageSend(BMSEmployee wLoginUser, String wLoginID, String wTitle, String wContent);
}
