package com.mes.exc.server.serviceimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.mes.exc.server.service.CoreService;
import com.mes.exc.server.service.mesenum.BPMEventModule;
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
import com.mes.exc.server.service.po.sch.SCHWorker;
import com.mes.exc.server.service.utils.CloneTool;
import com.mes.exc.server.service.utils.RemoteInvokeUtils;
import com.mes.exc.server.service.utils.StringUtils;
import com.mes.exc.server.serviceimpl.dao.BaseDAO;
import com.mes.exc.server.serviceimpl.utils.exc.EXCConstants;

@Service
public class CoreServiceImpl implements CoreService {
	private static Logger logger = LoggerFactory.getLogger(CoreServiceImpl.class);

	public CoreServiceImpl() {
	}

	private static CoreService Instance;

	public static CoreService getInstance() {
		if (Instance == null)
			Instance = new CoreServiceImpl();
		return Instance;
	}

	@Override
	public APIResult BMS_LoginEmployee(String wLoginName, String wPassword, String wToken, long wMac, int wnetJS) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("user_id", wLoginName);
			wParms.put("passWord", wPassword);
			wParms.put("token", wToken);
			wParms.put("PhoneMac", wMac);
			wParms.put("netJS", wMac);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(RemoteInvokeUtils.CoreServer_Url,
					RemoteInvokeUtils.CoreServerName, "api/User/Login", wParms, HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BMS_GetEmployeeAll(BMSEmployee wLoginUser, int wDepartmentID, int wPosition, int wActive) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("active", wActive);
			wParms.put("DepartmentID", wDepartmentID);
			wParms.put("Position", wPosition);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(RemoteInvokeUtils.CoreServer_Url,
					RemoteInvokeUtils.CoreServerName, StringUtils.Format("api/User/All?cadv_ao={0}&cade_po={1}",
							wLoginUser.getLoginName(), wLoginUser.getPassword()),
					wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BMS_QueryEmployeeByID(BMSEmployee wLoginUser, int wID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("user_info", wID);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(RemoteInvokeUtils.CoreServer_Url,
					RemoteInvokeUtils.CoreServerName, StringUtils.Format("api/User/Info?cadv_ao={0}&cade_po={1}",
							wLoginUser.getLoginName(), wLoginUser.getPassword()),
					wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BMS_CheckPowerByAuthorityID(int wCompanyID, int wUserID, int wFunctionID, int wRangeID,
			int wTypeID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("AuthortyID", wFunctionID);
			wParms.put("RangeID", wRangeID);
			wParms.put("TypeID", wTypeID);
			wParms.put("CompanyID", wCompanyID);
			wParms.put("UserID", wUserID);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(RemoteInvokeUtils.CoreServer_Url,
					RemoteInvokeUtils.CoreServerName, "api/Role/Check", wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SFC_QueryShiftID(BMSEmployee wLoginUser, int Shifts) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			String wUri = StringUtils.Format(
					"MESCore/api/SCHShift/CurrentShiftID?cadv_ao={0}&cade_po={1}&company_id={2}", wLoginUser.LoginName,
					wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI("", wUri, wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult FMC_QueryWorkspace(BMSEmployee wLoginUser, int wID, String wCode) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ID", wID);
			wParms.put("Code", wCode);
			String wUri = StringUtils.Format("MESCore/api/FMCWorkspace/Info?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI("", wUri, wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SCH_QueryLeadWorkerByPositionID(BMSEmployee wLoginUser, int wPositionID, int wShiftID) {
		APIResult wResult = new APIResult();
		try {
//			Map<String, Object> wParms = new HashMap<String, Object>();
//			wParms.put("PositionID", wPositionID);
//			wParms.put("ShiftID", wShiftID);
//			String wUri = StringUtils.Format("MESCore/api/SCHWorker/PositionWorker?cadv_ao={0}&cade_po={1}&company_id={2}",
//					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
//			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI("", wUri, wParms, HttpMethod.GET);

			// 自己构造APIResult
			APIResult wResultList = BMS_GetEmployeeAll(wLoginUser, -1, wLoginUser.Manager, 1);
			List<BMSEmployee> wList = wResultList.List(BMSEmployee.class);
			List<SCHWorker> wWorkerList = new ArrayList<SCHWorker>();
			if (wList != null && wList.size() > 0) {
				for (BMSEmployee wItem : wList) {
					SCHWorker wSCHWorker = new SCHWorker();
					wSCHWorker.WorkerID = wItem.ID;
					wSCHWorker.WorkerName = wItem.Name;
					wWorkerList.add(wSCHWorker);
				}
			}
			Map<String, Object> wReturnObject = new HashMap<String, Object>();
			wReturnObject.put("msg", "");
			wReturnObject.put("list", wWorkerList);
			wReturnObject.put("info", null);
			Map<String, Object> wResultData = new HashMap<String, Object>();
			wResultData.put("resultCode", 1000);
			wResultData.put("returnObject", wReturnObject);
			// 转换
			wResult = CloneTool.Clone(wResultData, APIResult.class);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SCH_QueryWorkerByPositionID(BMSEmployee wLoginUser, int wPositionID, int wShiftID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("PositionID", wPositionID);
			wParms.put("ShiftID", wShiftID);
			String wUri = StringUtils.Format(
					"MESCore/api/SCHWorker/PositionLeader?cadv_ao={0}&cade_po={1}&company_id={2}", wLoginUser.LoginName,
					wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI("", wUri, wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BFC_GetQRCode(BMSEmployee wLoginUser, QRTypes wQRType, long wQRCodeID) {
		APIResult wResult = new APIResult();
		try {
//			Map<String, Object> wParms = new HashMap<String, Object>();
//			wParms.put("Type", wQRType);
//			wParms.put("ID", wQRCodeID);
//			String wUri = StringUtils.Format("MESCore/api/BFCQR/Code?cadv_ao={0}&cade_po={1}&company_id={2}",
//					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
//			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI("", wUri, wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult FMC_GetFMCWorkspaceList(BMSEmployee wLoginUser, int wProductID, int wPartID, String wPartNo,
			int wPlaceType, int wActive) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ProductID", wProductID);
			wParms.put("PartID", wPartID);
			wParms.put("PartNo", wPartNo);
			wParms.put("PlaceType", wPlaceType);
			wParms.put("Active", wActive);
			String wUri = StringUtils.Format("MESCore/api/FMCWorkspace/All?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI("", wUri, wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult FMC_SaveFMCWorkspace(BMSEmployee wBMSEmployee, FMCWorkspace wFMCWorkspace) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wFMCWorkspace);
			String wUri = StringUtils.Format("MESCore/api/FMCWorkspace/Update?cadv_ao={0}&cade_po={1}&company_id={2}",
					wBMSEmployee.LoginName, wBMSEmployee.Password, wBMSEmployee.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI("", wUri, wParms, HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult FMC_BindFMCWorkspace(BMSEmployee wBMSEmployee, FMCWorkspace wFMCWorkspace) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wFMCWorkspace);
			String wUri = StringUtils.Format("MESCore/api/FMCWorkspace/Bind?cadv_ao={0}&cade_po={1}&company_id={2}",
					BaseDAO.SysAdmin.LoginName, BaseDAO.SysAdmin.Password, BaseDAO.SysAdmin.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI("", wUri, wParms, HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<DMSDeviceLedger>> DMS_GetDeviceLedgerList(BMSEmployee wBMSEmployee, int wBusinessUnitID,
			int wBaseID, int wFactoryID, int wWorkShopID, int wLineID, int wModelID, DMSLedgerStatus wDMSLedgerStatus) {
		ServiceResult<List<DMSDeviceLedger>> wResult = new ServiceResult<List<DMSDeviceLedger>>();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ModelID", wModelID);
			wParms.put("WorkShopID", wWorkShopID);
			wParms.put("LineID", wLineID);
			wParms.put("BusinessUnitID", wBusinessUnitID);
			wParms.put("BaseID", wBaseID);
			wParms.put("FactoryID", wFactoryID);
			wParms.put("Status", wDMSLedgerStatus.getValue());
			String wUri = StringUtils.Format("MESCore/api/DeviceLedger/All?cadv_ao={0}&cade_po={1}&company_id={2}",
					wBMSEmployee.LoginName, wBMSEmployee.Password, wBMSEmployee.CompanyID);
			APIResult wApiResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI("", wUri, wParms, HttpMethod.GET);
			wResult.Result = wApiResult.List(DMSDeviceLedger.class);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<FPCProduct>> FPC_QueryProductList(BMSEmployee wBMSEmployee, int wBusinessUnitID,
			int wProductTypeID) {
		ServiceResult<List<FPCProduct>> wResult = new ServiceResult<List<FPCProduct>>();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("BusinessUnitID", wBusinessUnitID);
			wParms.put("ProductTypeID", wProductTypeID);
			wParms.put("OAGetType", -1);
			String wUri = StringUtils.Format("MESCore/api/FPCProduct/All?cadv_ao={0}&cade_po={1}&company_id={2}",
					wBMSEmployee.LoginName, wBMSEmployee.Password, wBMSEmployee.CompanyID);
			APIResult wApiResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI("", wUri, wParms, HttpMethod.GET);
			wResult.Result = wApiResult.List(FPCProduct.class);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BFC_GetQRType(BMSEmployee wLoginUser, String wQRCode) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("QRCode", wQRCode);
			String wUri = StringUtils.Format("MESCore/api/BFCQR/QRType?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI("", wUri, wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据ID获取工位
	 * 
	 * @param wLoginUser
	 * @param wID
	 * @return
	 */
	@Override
	public APIResult FPC_GetPartByID(BMSEmployee wLoginUser, long wID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ID", wID);
			String wUri = StringUtils.Format("MESCore/api/FPCPart/Info?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI("", wUri, wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取岗位列表
	 * 
	 * @param wLoginUser
	 * @param wCompanyID
	 * @return
	 */
	public APIResult BMS_QueryPositionList(BMSEmployee wLoginUser) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			String wUri = StringUtils.Format(
					"MESCore/api/Department/AllPosition?cadv_ao={0}&cade_po={1}&company_id={2}", wLoginUser.LoginName,
					wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI("", wUri, wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取部门列表
	 * 
	 * @param wLoginUser
	 * @param wCompanyID
	 * @param wLoginID
	 * @return
	 */
	public APIResult BMS_QueryDepartmentList(BMSEmployee wLoginUser) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			String wUri = StringUtils.Format(
					"MESCore/api/Department/AllDepartment?cadv_ao={0}&cade_po={1}&company_id={2}", wLoginUser.LoginName,
					wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI("", wUri, wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BFC_UpdateMessageList(BMSEmployee wLoginUser, List<BFCMessage> wBFCMessageList) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wBFCMessageList);
			wParms.put("Send", 0);
			String wUri = StringUtils.Format("api/HomePage/MsgUpdate?cadv_ao={0}&cade_po={1}", wLoginUser.LoginName,
					wLoginUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 发送消息给中车门户
	 */
	@SuppressWarnings("unused")
	private void SendMessageToCRRC(List<BFCMessage> wBFCMessageList) {
		try {
			for (BFCMessage wBFCMessage : wBFCMessageList) {
				String wModuleName = BPMEventModule.getEnumType((int) wBFCMessage.ModuleID).getLable();
				String wLoginID = EXCConstants.GetBMSEmployee((int) wBFCMessage.ResponsorID).LoginID;
				String wContent = "";
				String wTitle = "";
				if (wBFCMessage.Type == 1) {
					wTitle = StringUtils.Format("你收到一则【{0}】通知", wModuleName);
					wContent = "请及时查阅。";
				} else if (wBFCMessage.Type == 2) {
					wTitle = StringUtils.Format("你收到一条【{0}】待办", wModuleName);
					wContent = "请及时处理。";
				}
				BFCMessageResult wResult = CoreServiceImpl.getInstance().BFC_MessageSend(BaseDAO.SysAdmin, wLoginID,
						wTitle, wContent);

				String wName = EXCConstants.GetBMSEmployeeName((int) wBFCMessage.ResponsorID);
				String wMsg = StringUtils.Format("【{0}】-【{1}】-【{2}】", wBFCMessage.Title, wName, wResult.msg);
				logger.info(wMsg);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public APIResult BFC_GetMessageList(BMSEmployee wLoginUser, int wResponsorID, int wModuleID, int wType, int wActive,
			int wShiftID, Calendar wStartTime, Calendar wEndTime) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ResponsorID", wResponsorID);
			wParms.put("ModuleID", wModuleID);
			wParms.put("ShiftID", wShiftID);
			wParms.put("Type", wType);
			wParms.put("Active", wActive);
			wParms.put("StartTime", wStartTime);
			wParms.put("EndTime", wEndTime);
			String wUri = StringUtils.Format("api/HomePage/MsgAll?cadv_ao={0}&cade_po={1}", wLoginUser.LoginName,
					wLoginUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BFC_GetMessageList(BMSEmployee wLoginUser, int wResponsorID, int wModuleID, int wMessageID,
			int wType, int wActive, int wShiftID, Calendar wStartTime, Calendar wEndTime) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ResponsorID", wResponsorID);
			wParms.put("ModuleID", wModuleID);
			wParms.put("ShiftID", wShiftID);
			wParms.put("MessageID", wMessageID);
			wParms.put("Type", wType);
			wParms.put("Active", wActive);
			wParms.put("StairtTime", wStartTime);
			wParms.put("EndTime", wEndTime);
			String wUri = StringUtils.Format("/api/HomePage/MsgAll?cadv_ao={0}&cade_po={1}", wLoginUser.LoginName,
					wLoginUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BFC_GetMessageList(BMSEmployee wLoginUser, int wResponsorID, int wModuleID,
			List<Integer> wMessageID, int wType, int wActive) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ResponsorID", wResponsorID);
			wParms.put("ModuleID", wModuleID);
			wParms.put("ShiftID", -1);
			wParms.put("MessageID", StringUtils.Join(",", wMessageID));
			wParms.put("Type", wType);
			wParms.put("Active", wActive);
			String wUri = StringUtils.Format("/api/HomePage/MsgAll?cadv_ao={0}&cade_po={1}", wLoginUser.LoginName,
					wLoginUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult FMC_QueryWorkShopList(BMSEmployee wLoginUser) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("FactoryID", 0);
			wParms.put("BusinessUnitID", 0);

			String wUri = StringUtils.Format("api/FMCWorkShop/All?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult FMC_QueryLineList(BMSEmployee wLoginUser) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("FactoryID", 0);
			wParms.put("BusinessUnitID", 0);
			wParms.put("WorkShopID", 0);

			String wUri = StringUtils.Format("api/FMCLine/All?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult FPC_QueryPartList(BMSEmployee wLoginUser) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("FactoryID", 0);
			wParms.put("BusinessUnitID", 0);

			String wUri = StringUtils.Format("api/FPCPart/All?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult FPC_QueryPartPointList(BMSEmployee wLoginUser) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("FactoryID", 0);
			wParms.put("BusinessUnitID", 0);
			wParms.put("ProductTypeID", 0);

			String wUri = StringUtils.Format("api/FPCPartPoint/All?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult FPC_QueryProductList(BMSEmployee wLoginUser) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("BusinessUnitID", -1);
			wParms.put("ProductTypeID", -1);
			wParms.put("OAGetType", -1);
			String wUri = StringUtils.Format("api/FPCProduct/All?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult FMC_QueryWorkChargeList(BMSEmployee wLoginUser) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("StationID", 0);
			wParms.put("Active", 1);
			wParms.put("ClassID", 0);

			String wUri = StringUtils.Format("api/WorkCharge/All?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult CRM_QueryCustomerList(BMSEmployee wLoginUser) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("active", 2);

			String wUri = StringUtils.Format("api/Customer/All?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult CFG_QueryUnitList(BMSEmployee wLoginUser) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			String wUri = StringUtils.Format("api/Unit/All?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BMS_GetSuperior(BMSEmployee wLoginUser, BMSEmployee wData) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wData);
			wParms.put("DutyOrders", new ArrayList<Integer>(Arrays.asList(4, 2, 1)));

			String wUri = StringUtils.Format("api/User/GetSuperior?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult APS_QueryDeleteList(BMSEmployee wLoginUser, int wOrderID, int wPartID, String wMaterialNos) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("OrderID", wOrderID);
			wParms.put("PartID", wPartID);
			wParms.put("MaterialNos", wMaterialNos);

			String wUri = StringUtils.Format("api/APSBOM/QueryDeleteList?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, "MESLOCOAPS", wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult APS_DeleteBom(BMSEmployee wLoginUser, APSBOMItem wAPSBOMItem) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("data", wAPSBOMItem);

			String wUri = StringUtils.Format("api/APSBOM/Delete?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, "MESLOCOAPS", wUri, wParms,
					HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * http://10.200.3.7:8081/gxhspush/platform/platformPushMsg?userAccountList=012100010418&title=%E6%A0%87%E9%A2%98
	 * &content=%E6%B6%88%E6%81%AF%E5%86%85%E5%AE%B9&appName=com.crrcgzgs.portal&insideAppName=mail&type=1
	 * 
	 * @return
	 */
	@Override
	public BFCMessageResult BFC_MessageSend(BMSEmployee wLoginUser, String wLoginID, String wTitle, String wContent) {
		BFCMessageResult wResult = new BFCMessageResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("userAccountList", wLoginID);
			wParms.put("title", wTitle);
			wParms.put("content", wContent);
			wParms.put("appName", "com.crrcgzgs.portal");
			wParms.put("insideAppName", "mail");
			wParms.put("type", 1);

			@SuppressWarnings("unchecked")
			Map<String, Object> wMap = RemoteInvokeUtils.getInstance().HttpInvoke(
					"http://10.200.3.7:8081/gxhspush/platform/platformPushMsg", wParms, HttpMethod.GET, HashMap.class);

			wResult = CloneTool.Clone(wMap, BFCMessageResult.class);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}
}
