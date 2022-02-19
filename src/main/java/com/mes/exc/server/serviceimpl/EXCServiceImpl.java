package com.mes.exc.server.serviceimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mes.exc.server.serviceimpl.utils.MESServer;
import com.mes.exc.server.serviceimpl.utils.exc.EXCConstants;
import com.mes.exc.server.serviceimpl.utils.exc.EXCManagerUtils;
import com.mes.exc.server.utils.RetCode;
import com.mes.exc.server.service.EXCService;
import com.mes.exc.server.service.mesenum.APSShiftPeriod;
import com.mes.exc.server.service.mesenum.BFCMessageType;
import com.mes.exc.server.service.mesenum.BPMEventModule;
import com.mes.exc.server.service.mesenum.EXCCallTaskBPMStatus;
import com.mes.exc.server.service.mesenum.FMCShiftLevel;
import com.mes.exc.server.service.mesenum.MESException;
import com.mes.exc.server.service.po.APIResult;
import com.mes.exc.server.service.po.OutResult;
import com.mes.exc.server.service.po.ServiceResult;
import com.mes.exc.server.service.po.aps.APSBOMItem;
import com.mes.exc.server.service.po.bfc.BFCMessage;
import com.mes.exc.server.service.po.bfc.QRTypes;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.service.po.bms.BMSPosition;
import com.mes.exc.server.service.po.bpm.BPMTaskBase;
import com.mes.exc.server.service.po.exc.EXCCallTaskBPM;
import com.mes.exc.server.service.po.exc.EXCLevelType;
import com.mes.exc.server.service.po.exc.EXCOptionItem;
import com.mes.exc.server.service.po.exc.EXCRunConfig;
import com.mes.exc.server.service.po.exc.EXCTimeItem;
import com.mes.exc.server.service.po.exc.EXCTypeOption;
import com.mes.exc.server.service.po.exc.action.EXCCallAction;
import com.mes.exc.server.service.po.exc.action.EXCCallApply;
import com.mes.exc.server.service.po.exc.base.EXCAlarmRule;
import com.mes.exc.server.service.po.exc.base.EXCExceptionRule;
import com.mes.exc.server.service.po.exc.base.EXCExceptionTemplate;
import com.mes.exc.server.service.po.exc.base.EXCExceptionType;
import com.mes.exc.server.service.po.exc.base.EXCStation;
import com.mes.exc.server.service.po.exc.base.EXCStationType;
import com.mes.exc.server.service.po.exc.define.EXCActionTypes;
import com.mes.exc.server.service.po.exc.define.EXCAndonTypes;
import com.mes.exc.server.service.po.exc.define.EXCApplyStatus;
import com.mes.exc.server.service.po.exc.define.EXCCallStatus;
import com.mes.exc.server.service.po.exc.define.EXCResourceTypes;
import com.mes.exc.server.service.po.exc.define.TagTypes;
import com.mes.exc.server.service.po.exc.define.TaskRelevancyTypes;
import com.mes.exc.server.service.po.exc.tree.EXCAndon;
import com.mes.exc.server.service.po.exc.tree.EXCCallDispatch;
import com.mes.exc.server.service.po.exc.tree.EXCCallTask;
import com.mes.exc.server.service.po.exc.tree.EXCMessage;
import com.mes.exc.server.service.po.fmc.FMCWorkspace;
import com.mes.exc.server.service.po.fpc.FPCPart;
import com.mes.exc.server.service.utils.StringUtils;
import com.mes.exc.server.serviceimpl.dao.exc.EXCCallTaskBPMDAO;
import com.mes.exc.server.serviceimpl.dao.exc.action.EXCActionDAO;
import com.mes.exc.server.serviceimpl.dao.exc.action.EXCCallApplyDAO;
import com.mes.exc.server.serviceimpl.dao.exc.base.EXCAlarmRuleDAO;
import com.mes.exc.server.serviceimpl.dao.exc.base.EXCExceptionRuleDAO;
import com.mes.exc.server.serviceimpl.dao.exc.base.EXCExceptionTypeDAO;
import com.mes.exc.server.serviceimpl.dao.exc.base.EXCStationDAO;
import com.mes.exc.server.serviceimpl.dao.exc.base.EXCStationTypeDAO;
import com.mes.exc.server.serviceimpl.dao.exc.tree.EXCAndonDAO;
import com.mes.exc.server.serviceimpl.dao.exc.tree.EXCCallDispatchDAO;
import com.mes.exc.server.serviceimpl.dao.exc.tree.EXCCallTaskDAO;
import com.mes.exc.server.serviceimpl.dao.exc.tree.EXCMessageDAO;

@Service
public class EXCServiceImpl implements EXCService {

	private static Logger logger = LoggerFactory.getLogger(EXCServiceImpl.class);
	private static EXCService _instance;

	public static EXCService getInstance() {
		if (_instance == null)
			_instance = new EXCServiceImpl();

		return _instance;
	}

	public EXCServiceImpl() {
	}

	@Override
	public ServiceResult<List<EXCAlarmRule>> EXC_GetAlarmRuleList(BMSEmployee wLoginUser, long wStationType,
			int wRespondLevel, int wActive) {
		ServiceResult<List<EXCAlarmRule>> wResult = new ServiceResult<List<EXCAlarmRule>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(EXCAlarmRuleDAO.getInstance().SelectAll(wLoginUser, wStationType, wRespondLevel, wActive,
					wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<EXCAlarmRule> EXC_GetAlarmRule(BMSEmployee wLoginUser, long wID, long wAlarmID,
			String wAlarmCode) {
		ServiceResult<EXCAlarmRule> wResult = new ServiceResult<EXCAlarmRule>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(EXCAlarmRuleDAO.getInstance().Select(wLoginUser, wID, wAlarmID, wAlarmCode, wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Long> EXC_UpdateAlarmRule(BMSEmployee wLoginUser, EXCAlarmRule wEXCAlarmRule) {
		ServiceResult<Long> wResult = new ServiceResult<Long>(0L);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(EXCAlarmRuleDAO.getInstance().Update(wLoginUser, wEXCAlarmRule, wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> Active(BMSEmployee wLoginUser, List<Long> wIDList, int wActive) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			EXCAlarmRuleDAO.getInstance().Active(wLoginUser, wIDList, wActive, wErrorCode);
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<EXCAndon>> EXC_GetAndonList(BMSEmployee wLoginUser, int wWorkshopID, int wLineID,
			int wPlaceID, String wStationCode, long wSourceID, EXCAndonTypes wSourceType, int wShiftID,
			Calendar wStartTime, Calendar wEndTime, List<Integer> wStatus) {
		ServiceResult<List<EXCAndon>> wResult = new ServiceResult<List<EXCAndon>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(EXCAndonDAO.getInstance().SelectAll(wLoginUser, wWorkshopID, wLineID, wPlaceID,
					wStationCode, wSourceID, wSourceType, wShiftID, wStartTime, wEndTime, wStatus, wErrorCode));

			if (wResult.Result != null && (wResult.Result).size() > 0) {
				for (EXCAndon wEXCAndon : wResult.Result) {
					if (wEXCAndon.Status == EXCCallStatus.WaitConfirm.getValue()
							|| wEXCAndon.Status == EXCCallStatus.Confirmed.getValue()) {
						EXCCallTask wCallTask = EXCCallTaskDAO.getInstance().Select(wLoginUser, wEXCAndon.SourceID,
								wErrorCode);
						if (wCallTask != null && wCallTask.ID > 0L) {
							wEXCAndon.OperatorID = new ArrayList<>(Arrays.asList(Long.valueOf(wCallTask.ConfirmID)));
						}
					}
				}
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<EXCMessage>> EXC_GetMessageList(BMSEmployee wLoginUser, int wResponsorID, int wModuleID,
			int wActive, int wShiftID, Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<EXCMessage>> wResult = new ServiceResult<List<EXCMessage>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(EXCMessageDAO.getInstance().EXC_GetMessageList(wLoginUser, wResponsorID, wModuleID,
					wActive, wShiftID, wStartTime, wEndTime, wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> EXC_SendMessageList(BMSEmployee wLoginUser, List<EXCMessage> wEXCMessageList) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			if (wEXCMessageList != null && wEXCMessageList.size() > 0) {
				for (EXCMessage wEXCMessage : wEXCMessageList) {
					EXCMessageDAO.getInstance().SendMessageToExternal(wLoginUser, wEXCMessage);
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
			wResult.setResult(-1);
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> EXC_UpdateMessageList(BMSEmployee wLoginUser, List<EXCMessage> wEXCMessageList) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			if (wEXCMessageList != null && wEXCMessageList.size() > 0) {
				for (EXCMessage wEXCMessage : wEXCMessageList) {
					EXCMessageDAO.getInstance().Update(wLoginUser, wEXCMessage, wErrorCode);
				}
			}
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			logger.error(e.toString());
			wResult.setResult(-1);
		}
		return wResult;
	}

	@Override
	public ServiceResult<Map<Integer, Integer>> EXC_GetMessagCount(BMSEmployee wLoginUser, int wResponsorID,
			int wShfitID) {
		ServiceResult<Map<Integer, Integer>> wResult = new ServiceResult<>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(
					EXCMessageDAO.getInstance().EXC_GetMessageCount(wLoginUser, wResponsorID, wShfitID, wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> EXC_UpdateAndon(BMSEmployee wLoginUser, EXCAndon wEXCAndon) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			EXCAndonDAO.getInstance().Update(wLoginUser, wEXCAndon, wErrorCode);
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> EXC_UpdateAndonStatus(BMSEmployee wLoginUser) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			EXCAndonDAO.getInstance().UpdateStatus(wLoginUser, wErrorCode);
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<EXCCallAction>> EXC_GetCallActionList(BMSEmployee wLoginUser, long wTaskID,
			EXCActionTypes wActionType, long wOperatorID, long wForwarder, long wDispatchID) {
		ServiceResult<List<EXCCallAction>> wResult = new ServiceResult<List<EXCCallAction>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(EXCActionDAO.getInstance().SelectAll(wLoginUser, wTaskID, wActionType, wOperatorID,
					wForwarder, wDispatchID, wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<EXCCallAction> EXC_GetCallActionByID(BMSEmployee wLoginUser, long wID) {
		ServiceResult<EXCCallAction> wResult = new ServiceResult<EXCCallAction>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(EXCActionDAO.getInstance().Select(wLoginUser, wID, wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Long> EXC_UpdateCallAction(BMSEmployee wLoginUser, EXCCallAction wEXCCallAction, int wShiftID,
			boolean wIsOverShift) {
		ServiceResult<Long> wResult = new ServiceResult<Long>(0L);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(
					EXCActionDAO.getInstance().Update(wLoginUser, wEXCCallAction, wShiftID, wIsOverShift, wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<EXCOptionItem>> EXC_GetActionTypeList(BMSEmployee wLoginUser) {
		ServiceResult<List<EXCOptionItem>> wResult = new ServiceResult<List<EXCOptionItem>>();
		try {
			wResult.setResult(EXCConstants.getEXCActionTypeList());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<EXCCallApply>> EXC_GetCallApplyList(BMSEmployee wLoginUser, String wStationNo,
			long wStationType, long wStationID, int wRespondLevel, int wDisplayBoard, int wOnSite, long wApplicantID,
			long wApproverID, long wConfirmID, String wPartNo, Calendar wStartTime, Calendar wEndTime, int wStatus) {
		ServiceResult<List<EXCCallApply>> wResult = new ServiceResult<List<EXCCallApply>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>();
			wResult.setResult(EXCCallApplyDAO.getInstance().SelectAll(wLoginUser, wStationNo, wStationType, wStationID,
					wRespondLevel, wDisplayBoard, wOnSite, wApplicantID, wApproverID, wConfirmID, wPartNo, wStartTime,
					wEndTime, wStatus, wErrorCode));
			wResult.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
		}

		return wResult;
	}

	@Override
	public ServiceResult<EXCCallApply> EXC_GetCallApplyByID(BMSEmployee wLoginUser, long wID) {
		ServiceResult<EXCCallApply> wResult = new ServiceResult<EXCCallApply>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>();
			wResult.setResult(EXCCallApplyDAO.getInstance().Select(wLoginUser, wID, wErrorCode));
			wResult.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
		}

		return wResult;
	}

	@Override
	public ServiceResult<Long> EXC_UpdateCallApply(BMSEmployee wLoginUser, EXCCallApply wEXCCallApply, int wShiftID) {
		ServiceResult<Long> wResult = new ServiceResult<Long>(0L);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>();
			wResult.setResult(EXCCallApplyDAO.getInstance().Update(wLoginUser, wEXCCallApply, wShiftID, wErrorCode));
			wResult.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
		}
		return wResult;
	}

	@Override
	public ServiceResult<EXCStation> EXC_GetStation(BMSEmployee wLoginUser, long wQRCodeID, String wQRCode,
			QRTypes wQRType) {
		ServiceResult<EXCStation> wResult = new ServiceResult<EXCStation>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			EXCStation wStation = new EXCStation();

			// 获取工位(工序段表)
			APIResult wPartPointResult = CoreServiceImpl.getInstance().FPC_GetPartByID(wLoginUser, wQRCodeID);
			if (wPartPointResult == null)
				return wResult;

			FPCPart wFPCPart = wPartPointResult.Info(FPCPart.class);
			if (wFPCPart == null || wFPCPart.ID <= 0)
				return wResult;
			String wPartPointName = wFPCPart.Name;

			EXCStation wTempStation = EXCStationDAO.getInstance().Select(wLoginUser, wQRType, wQRCodeID, wErrorCode);
			if (wTempStation == null || wTempStation.ID <= 0) {
				wStation.Active = 1;
				wStation.CreateTime = Calendar.getInstance();
				wStation.CreatorID = wLoginUser.getID();
				wStation.EditorID = wLoginUser.getID();
				wStation.EditTime = Calendar.getInstance();
				wStation.ID = 0;
				wStation.RelevancyID = wQRCodeID;
				wStation.RelevancyType = wQRType.getValue();
				wStation.StationName = wPartPointName;
				wStation.StationNo = wQRCode;
				wStation.StationType = 1;

				long wID = EXCStationDAO.getInstance().Update(wLoginUser, wStation, wErrorCode);
				wStation.ID = wID;
			} else {
				wStation = wTempStation;
			}
			wResult.Result = wStation;

//			String wQRName = "";
//			APIResult wQRString = CoreServiceImpl.getInstance().BFC_GetQRCode(wLoginUser, (QRTypes) wQRType, wQRCodeID);
//
//			Map<String, Object> wQRCodeResult = wQRString.Info(Map.class);
//
//			if (wQRCodeResult == null || wQRCodeResult.size() <= 0 || !wQRCodeResult.containsKey("Code")) {
//				wQRCodeResult = new HashMap<String, Object>();
//				wQRCodeResult.put("Code", "");
//			}
//			if (!wQRCode.equalsIgnoreCase(StringUtils.parseString(wQRCodeResult.get("Code")))) {
//				wResult.setFaultCode(
//						StringUtils.Format("输入参数QRCode:{0} 与实际查询的编码{1}不一致！", wQRCode, wQRCodeResult.get("Code")));
//				return wResult;
//			}
//			wQRName = wQRCodeResult.containsKey("Name") ? StringUtils.parseString(wQRCodeResult.get("Name")) : "";
//
//			wResult.setResult(EXCStationDAO.getInstance().Select(wLoginUser, wQRType, wQRCodeID));
//
//			if (wResult == null || wResult.GetResult().ID <= 0) {
//				// 如果没有此异常点 查询对应编码的异常点并改变对应异常点的编码
//
//				wResult.setResult(EXCStationDAO.getInstance().Select(wLoginUser, -1, wQRCode));
//				if (wResult.GetResult() != null && wResult.GetResult().ID > 0) {
//
//					// 编码值从QR中获取 更改此条数据编码
//					wQRString = CoreServiceImpl.getInstance().BFC_GetQRCode(wLoginUser,
//							QRTypes.getEnumType(wResult.GetResult().RelevancyType), wResult.GetResult().RelevancyID);
//
//					wQRCodeResult = wQRString.Info(Map.class);
//
//					wQRName = (wQRCodeResult != null && wQRCodeResult.containsKey("Name"))
//							? StringUtils.parseString(wQRCodeResult.get("Name"))
//							: "";
//
//					wResult.GetResult().StationNo = wQRCode;
//
//					EXCStationDAO.getInstance().Update(wLoginUser, wResult.GetResult());
//
//					wResult.GetResult().ID = 0;
//				}
//
//			} else if (wResult.GetResult().StationNo != wQRCode) {
//				wResult.GetResult().StationNo = wQRCode;
//				EXCStationDAO.getInstance().Update(wLoginUser, wResult.GetResult());
//
//			}
//			if (wResult == null || wResult.GetResult().ID <= 0) {
//				String wQRTypeDesc = wQRType.getLable();
//				EXCStationType wEXCStationType = EXCStationTypeDAO.getInstance().Select(wLoginUser, wQRTypeDesc,
//						wQRType);
//				if (wEXCStationType == null || wEXCStationType.ID <= 0) {
//					wEXCStationType = new EXCStationType();
//
//					wEXCStationType.Active = 1;
//					wEXCStationType.CreateTime = Calendar.getInstance();
//					wEXCStationType.CreatorID = wLoginUser.getID();
//					wEXCStationType.EditorID = wLoginUser.getID();
//					wEXCStationType.EditTime = Calendar.getInstance();
//					wEXCStationType.ID = 0;
//					wEXCStationType.Name = wQRTypeDesc;
//					wEXCStationType.RelevancyType = wQRType.getValue();
//
//					// 插入 wEXCStationType
//					EXCStationTypeDAO.getInstance().Update(wLoginUser, wEXCStationType);
//				}
//				if (wEXCStationType == null || wEXCStationType.ID < 0) {
//					wResult.setFaultCode("StationType不存在！");
//					return wResult;
//				}
//				// 创建 EXCStation 并存入
//				wResult.setResult(new EXCStation());
//
//				wResult.GetResult().Active = 1;
//				wResult.GetResult().CreateTime = Calendar.getInstance();
//				wResult.GetResult().CreatorID = wLoginUser.getID();
//				wResult.GetResult().EditorID = wLoginUser.getID();
//				wResult.GetResult().EditTime = Calendar.getInstance();
//				wResult.GetResult().ID = 0;
//				wResult.GetResult().RelevancyID = wQRCodeID;
//				wResult.GetResult().RelevancyType = wQRType.getValue();
//				wResult.GetResult().StationName = wQRName;
//				wResult.GetResult().StationNo = wQRCode;
//				wResult.GetResult().StationType = wEXCStationType.ID;
//
//				EXCStationDAO.getInstance().Update(wLoginUser, wResult.GetResult());
//
//			}
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			logger.error(e.toString());
		}

		return wResult;
	}

	@Override
	public ServiceResult<List<EXCExceptionType>> EXC_GetExceptionTypeList(BMSEmployee wLoginUser, String wName,
			long wStationType, TaskRelevancyTypes wRelevancyTaskType, int wActive) {
		ServiceResult<List<EXCExceptionType>> wResult = new ServiceResult<List<EXCExceptionType>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(EXCExceptionTypeDAO.getInstance().SelectAll(wLoginUser, wName, wStationType,
					wRelevancyTaskType, wActive, wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<EXCOptionItem>> EXC_GetRespondLevelList(BMSEmployee wLoginUser) {
		ServiceResult<List<EXCOptionItem>> wResult = new ServiceResult<List<EXCOptionItem>>();
		try {
			wResult.setResult(EXCConstants.getEXCRespondLevelList());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<EXCOptionItem>> EXC_UpdateRespondLevelList(BMSEmployee wLoginUser,
			List<EXCOptionItem> wRespondLevelList) {
		ServiceResult<List<EXCOptionItem>> wResult = new ServiceResult<List<EXCOptionItem>>();
		try {
			EXCConstants.setEXCRespondLevelList(wRespondLevelList);
			wResult.setResult(EXCConstants.getEXCRespondLevelList());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Boolean> EXC_IsAllowApply(BMSEmployee wLoginUser, EXCCallApply wEXCCallApply, int wShiftID) {

		ServiceResult<Boolean> wResult = new ServiceResult<Boolean>(true);
		OutResult<Integer> wErrorCode = new OutResult<Integer>();
		try {

			// region 判断输入条件 即判断异常类型是否存在
			if (wEXCCallApply == null || wEXCCallApply.ExceptionTypeList == null
					|| wEXCCallApply.ExceptionTypeList.size() < 1) {
				wResult.setResult(false);
				wResult.setFaultCode("未选择异常类型！");
				return wResult;
			}
			List<EXCExceptionType> wEXCExceptionTypeList = EXCExceptionTypeDAO.getInstance().SelectAll(wLoginUser,
					wEXCCallApply.ExceptionTypeList.stream().map(p -> p.EXCTypeID).collect(Collectors.toList()),
					wErrorCode);

			if (wEXCExceptionTypeList == null || wEXCExceptionTypeList.size() < 1) {
				// wFaultCode = "您选择的异常类型不存在！ 即新异常允许申请直接返回True";
				return wResult;
			}
			// endRegion

			// region 判断任务单中是否有对应异常处于激活 即（未确认）非取消状态
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 1, 1);
			List<EXCCallTask> wEXCCallTaskList = EXCCallTaskDAO.getInstance().SelectAll(wLoginUser, null, 0,
					wEXCCallApply.StationNo, wEXCCallApply.StationType, wEXCCallApply.StationID, 0, -1, -1, -1, -1, -1,
					wShiftID, "", wBaseTime, wBaseTime,
					StringUtils.parseList(new Integer[] { EXCCallStatus.Default.getValue(),
							EXCCallStatus.WaitRespond.getValue(), EXCCallStatus.WaitConfirm.getValue(),
							EXCCallStatus.UpGraded.getValue(), EXCCallStatus.Rejected.getValue(),
							EXCCallStatus.OnSiteRespond.getValue(), EXCCallStatus.Forwarded.getValue() }),
					-1, wErrorCode);

			Map<Long, List<EXCCallTask>> wEXCCallTaskDic = wEXCCallTaskList.stream()
					.collect(Collectors.groupingBy(p -> p.ExceptionTypeID));

			for (EXCExceptionType wEXCExceptionType : wEXCExceptionTypeList) {
				if (wEXCCallTaskDic.containsKey(wEXCExceptionType.ID)) {
					wResult.setResult(false);
					wResult.setFaultCode(wResult.getFaultCode() + StringUtils.Format("异常点{0}已存在异常类型为{1}的异常任务！",
							wEXCCallApply.StationNo, wEXCExceptionType.Name));
				}
			}
			if (!wResult.GetResult())
				return wResult;
			// endRegion

			// region 判断申请单中是否有对应异常处于非确认和非取消状态
			Calendar wStartTime = Calendar.getInstance();
			wStartTime.add(Calendar.MILLISECOND, EXCConstants.getEXCRunConfig().getApplyOverTimeReject());
			Calendar wEndTime = Calendar.getInstance();
			wEndTime.add(Calendar.DAY_OF_MONTH, 1);
			List<EXCCallApply> wEXCCallApplyList = EXCCallApplyDAO.getInstance().SelectAll(wLoginUser, null,
					wEXCCallApply.StationNo, wEXCCallApply.StationType, wEXCCallApply.StationID, 0, -1, -1, -1, -1, -1,
					"", wStartTime, wEndTime, null, wErrorCode);
			wEXCCallApplyList.sort(Comparator.comparing(EXCCallApply::getApplicantTime));

			Map<Long, EXCTypeOption> wEXCTypeOptionDic = new HashMap<Long, EXCTypeOption>();

			Map<Long, Calendar> wEXCTypeLastCalendarDic = new HashMap<Long, Calendar>();

			for (EXCCallApply wEXCCallApplyTemp : wEXCCallApplyList) {

				if (wEXCCallApplyTemp.ExceptionTypeList == null || wEXCCallApplyTemp.ExceptionTypeList.size() < 1)
					continue;

				if (wEXCCallApplyTemp.Status == (int) EXCApplyStatus.Cancel.getValue()
						|| wEXCCallApplyTemp.Status == (int) EXCApplyStatus.Confirm.getValue())
					continue;
				for (EXCTypeOption wEXCTypeOption : wEXCCallApplyTemp.ExceptionTypeList) {
					if (!wEXCTypeLastCalendarDic.containsKey(wEXCTypeOption.EXCTypeID) || wEXCTypeLastCalendarDic
							.get(wEXCTypeOption.EXCTypeID).compareTo(wEXCCallApplyTemp.ApplicantTime) < 0)
						wEXCTypeLastCalendarDic.put(wEXCTypeOption.EXCTypeID, wEXCCallApplyTemp.ApplicantTime);

					if (wEXCCallApplyTemp.Status == EXCApplyStatus.Reject.getValue())
						continue;
					if (wEXCTypeOptionDic.containsKey(wEXCTypeOption.EXCTypeID))
						continue;
					wEXCTypeOptionDic.put(wEXCTypeOption.EXCTypeID, wEXCTypeOption);
				}
			}

			for (EXCExceptionType wEXCExceptionType : wEXCExceptionTypeList) {
				if (wEXCTypeOptionDic.containsKey(wEXCExceptionType.ID)) {
					wResult.setResult(false);
					wResult.setFaultCode(wResult.getFaultCode() + StringUtils.Format("异常点{0}已存在异常类型为{1}的异常申请单！",
							wEXCCallApply.StationNo, wEXCExceptionType.Name));
				}
			}
			if (!wResult.GetResult())
				return wResult;

			// endRegion

			// region 根据异常类型中的再次发起时间 判断是否符合再次发起条件

			Calendar wTimeTemp = Calendar.getInstance();
			for (EXCExceptionType wEXCExceptionType : wEXCExceptionTypeList) {
				if (wEXCTypeLastCalendarDic.containsKey(wEXCExceptionType.ID)) {
					wTimeTemp = (Calendar) wEXCTypeLastCalendarDic.get(wEXCExceptionType.ID).clone();
					wTimeTemp.add(Calendar.MILLISECOND, wEXCExceptionType.AgainInterval);
					if (wTimeTemp.compareTo(Calendar.getInstance()) > 0) {
						wResult.setResult(false);
						wResult.setFaultCode(wResult.getFaultCode()
								+ StringUtils.Format("异常点{0}已存在异常类型为{1}未超过发起间隔时间{2}！", wEXCCallApply.StationNo,
										wEXCExceptionType.Name, String.valueOf(wEXCExceptionType.AgainInterval)));
					}
				}
			}
			if (!wResult.GetResult())
				return wResult;

			// endRegion

		} catch (Exception e) {
			wResult.setFaultCode(wResult.getFaultCode() + e.toString());
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<EXCCallTask>> EXC_GetCallTaskListByStatus(BMSEmployee wLoginUser, long wApplyID,
			String wStationNo, long wStationType, long wStationID, int wRespondLevel, int wDisplayBoard, int wOnSite,
			long wApplicantID, long wOperatorID, long wConfirmID, int wShiftID, String wPartNo, Calendar wStartTime,
			Calendar wEndTime, EXCCallStatus wStatus, int wExceptionType) {
		ServiceResult<List<EXCCallTask>> wResult = new ServiceResult<List<EXCCallTask>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(EXCCallTaskDAO.getInstance().SelectAll(wLoginUser, wApplyID, wStationNo, wStationType,
					wStationID, wRespondLevel, wDisplayBoard, wOnSite, wApplicantID, wOperatorID, wConfirmID, wShiftID,
					wPartNo, wStartTime, wEndTime, wStatus, wExceptionType, wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<EXCCallTask>> EXC_GetCallTaskListByOperatorID(BMSEmployee wLoginUser, long wOperatorID,
			int wShiftID) {
		ServiceResult<List<EXCCallTask>> wResult = new ServiceResult<List<EXCCallTask>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(EXCCallTaskDAO.getInstance().SelectAll(wLoginUser, wOperatorID, wShiftID, wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<EXCCallTask>> EXC_GetCallTaskListByDispatcher(BMSEmployee wLoginUser, long wDispatcherID,
			int wShiftID, Calendar wStartTime, Calendar wEndTime, List<Integer> wStatus) {
		ServiceResult<List<EXCCallTask>> wResult = new ServiceResult<List<EXCCallTask>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(EXCCallTaskDAO.getInstance().SelectAllByDispatcher(wLoginUser, wDispatcherID, wShiftID,
					wStartTime, wEndTime, wStatus, wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<EXCCallTask>> EXC_GetCallTaskList(BMSEmployee wLoginUser, List<Long> wID, long wApplyID,
			String wStationNo, long wStationType, long wStationID, int wRespondLevel, int wDisplayBoard, int wOnSite,
			long wApplicantID, long wOperatorID, long wConfirmID, int wShiftID, String wPartNo, Calendar wStartTime,
			Calendar wEndTime, List<Integer> wStatus) {
		ServiceResult<List<EXCCallTask>> wResult = new ServiceResult<List<EXCCallTask>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(EXCCallTaskDAO.getInstance().SelectAll(wLoginUser, wID, wApplyID, wStationNo,
					wStationType, wStationID, wRespondLevel, wDisplayBoard, wOnSite, wApplicantID, wOperatorID,
					wConfirmID, wShiftID, wPartNo, wStartTime, wEndTime, wStatus, -1, wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<EXCCallTask> EXC_GetCallTaskByID(BMSEmployee wLoginUser, long wID) {
		ServiceResult<EXCCallTask> wResult = new ServiceResult<EXCCallTask>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(EXCCallTaskDAO.getInstance().Select(wLoginUser, wID, wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<EXCCallDispatch>> EXC_GetCallDispatchList(BMSEmployee wLoginUser, long wTaskID,
			long wOperatorID, long wCreatorID, int wShiftID, Calendar wStartTime, Calendar wEndTime,
			EXCCallStatus wStatus) {
		ServiceResult<List<EXCCallDispatch>> wResult = new ServiceResult<List<EXCCallDispatch>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(EXCCallDispatchDAO.getInstance().SelectAll(wLoginUser, wTaskID, wOperatorID, wCreatorID,
					wShiftID, wStartTime, wEndTime, wStatus, wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<EXCOptionItem>> EXC_GetRequestActions(BMSEmployee wLoginUser, long wDispatchID,
			boolean wIsOnSite, TagTypes wTagType, EXCCallStatus wEXCCallStatus,
			List<EXCCallDispatch> wEXCCallDispatchList, long wApplicantID, long wConfirmID) {
		ServiceResult<List<EXCOptionItem>> wResult = new ServiceResult<List<EXCOptionItem>>();
		try {
			wResult.setResult(new ArrayList<EXCOptionItem>());

			// 由于上级看不到下级的消息 能看到任务 所以这里为default时及从其他地方看到的消息跳转
			if (wTagType == TagTypes.Default && wDispatchID <= 0) {
				if (wApplicantID == wLoginUser.getID() && wEXCCallStatus == EXCCallStatus.WaitRespond) {
					wTagType = TagTypes.Applicant;
				} else if (wConfirmID == wLoginUser.getID()) {
					wTagType = TagTypes.Confirmer;
				} else {
					for (EXCCallDispatch wEXCCallDispatch : wEXCCallDispatchList) {
						if (wEXCCallDispatch.OperatorID == wLoginUser.getID()) {
							wTagType = TagTypes.Dispatcher;
							wDispatchID = wEXCCallDispatch.ID;
						}
					}
				}
			}
			switch (wTagType) {
			case Default:
				break;
			case Dispatcher:

				// region 接收人允许的操作

				for (EXCCallDispatch wEXCCallDispatch : wEXCCallDispatchList) {

					if (wDispatchID != wEXCCallDispatch.ID)
						continue;

					if (wEXCCallDispatch.CallActions == null)
						wEXCCallDispatch.CallActions = new ArrayList<EXCOptionItem>();

					switch (EXCCallStatus.getEnumType(wEXCCallDispatch.Status)) {
					case UpGraded: // 上报之后需重新开始流程
						break;
					case Default:
					case WaitRespond:
						if (wIsOnSite) {
							// 需要到场就有到场操作
							wEXCCallDispatch.CallActions.addAll(EXCConstants.getEXCActionTypeList().stream()
									.filter(p -> p.ID == (int) EXCActionTypes.Notice.getValue()
											|| p.ID == (int) EXCActionTypes.OnSite.getValue())
									.collect(Collectors.toList()));

						} else {
							wEXCCallDispatch.CallActions.addAll(EXCConstants.getEXCActionTypeList().stream()
									.filter(p -> p.ID == (int) EXCActionTypes.Notice.getValue()
											|| p.ID == (int) EXCActionTypes.Forward.getValue()
											|| p.ID == (int) EXCActionTypes.Respond.getValue())
									.collect(Collectors.toList()));
						}

						break;
					case OnSiteRespond:
						wEXCCallDispatch.CallActions.addAll(EXCConstants.getEXCActionTypeList().stream()
								.filter(p -> p.ID == (int) EXCActionTypes.Forward.getValue()
										|| p.ID == (int) EXCActionTypes.Respond.getValue())
								.collect(Collectors.toList()));
						break;
					case WaitConfirm:
						break;
					case Forwarded:
						// 转发自己
//						if (wEXCCallDispatch.OperatorID == wLoginUser.ID) {
//							wEXCCallDispatch.CallActions.addAll(EXCConstants.getEXCActionTypeList().stream()
//									.filter(p -> p.ID == (int) EXCActionTypes.Notice.getValue()
//											|| p.ID == (int) EXCActionTypes.Forward.getValue()
//											|| p.ID == (int) EXCActionTypes.Respond.getValue())
//									.collect(Collectors.toList()));
//						}
						break;
					case Confirmed:
						break;
					case Rejected:
					case NoticeWaitRespond:
						if (wIsOnSite) {
							wEXCCallDispatch.CallActions.addAll(EXCConstants.getEXCActionTypeList().stream()
									.filter(p -> p.ID == (int) EXCActionTypes.OnSite.getValue())
									.collect(Collectors.toList()));

						} else {
							wEXCCallDispatch.CallActions.addAll(EXCConstants.getEXCActionTypeList().stream()
									.filter(p -> p.ID == (int) EXCActionTypes.Forward.getValue()
											|| p.ID == (int) EXCActionTypes.Respond.getValue())
									.collect(Collectors.toList()));
						}

						break;
					case Cancel:
						break;
					default:
						break;
					}
				}
				// endRegion
				break;
			case Applicant:

				// region 发起人允许的操作
				switch (wEXCCallStatus) {
				case Default:
				case WaitRespond:
					wResult.GetResult().addAll(EXCConstants.getEXCActionTypeList().stream()
							.filter(p -> p.ID == (int) EXCActionTypes.Cancel.getValue()).collect(Collectors.toList()));
					break;
				case OnSiteRespond:
					break;
				case NoticeWaitRespond:
					break;
				case WaitConfirm:
					break;
				case Forwarded:
					break;
				case Confirmed:
					break;
				case Rejected:
					break;
				case UpGraded:
					break;
				case Cancel:
					break;
				default:
					break;
				}
				// endRegion

				break;
			case Confirmer:

				// region 确认人允许的操作
				for (EXCCallDispatch wEXCCallDispatch : wEXCCallDispatchList) {
					List<EXCOptionItem> wDActions = new ArrayList<EXCOptionItem>();
					switch (EXCCallStatus.getEnumType(wEXCCallDispatch.Status)) {
					case Default:
						break;
					case WaitRespond:
						break;
					case OnSiteRespond:
						break;
					case NoticeWaitRespond:
						break;
					case WaitConfirm:
						wDActions.addAll(EXCConstants.getEXCActionTypeList().stream()
								.filter(p -> (p.ID == (int) EXCActionTypes.Confirm.getValue())
										|| p.ID == (int) EXCActionTypes.Reject.getValue())
								.collect(Collectors.toList()));
						break;
					case Forwarded:
						break;
					case Confirmed:
						break;
					case Rejected:
						break;
					case UpGraded:
						break;
					case Cancel:
						break;
					default:
						break;
					}

					wEXCCallDispatch.CallActions = wDActions;
				}

				// endRegion

				break;
			default:
				break;
			}
		} catch (Exception e) {
			wResult.setFaultCode(wResult.getFaultCode() + e.toString());
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<EXCExceptionRule>> EXC_GetExceptionRuleList(BMSEmployee wLoginUser, String wName,
			long wExceptionType, int wRespondLevel, EXCResourceTypes wRequestType, EXCResourceTypes wResponseType,
			EXCResourceTypes wConfirmType, int wActive) {
		ServiceResult<List<EXCExceptionRule>> wResult = new ServiceResult<List<EXCExceptionRule>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(EXCExceptionRuleDAO.getInstance().SelectAll(wLoginUser, wName, wExceptionType,
					wRespondLevel, wRequestType, wResponseType, wConfirmType, wActive, wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<EXCExceptionTemplate>> EXC_GetExceptionTemplateList(BMSEmployee wLoginUser) {
		ServiceResult<List<EXCExceptionTemplate>> wResult = new ServiceResult<List<EXCExceptionTemplate>>();
		try {
			wResult.setResult(EXCConstants.getEXCExceptionTemplateList());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<EXCExceptionRule> EXC_GetExceptionRuleByID(BMSEmployee wLoginUser, long wID) {
		ServiceResult<EXCExceptionRule> wResult = new ServiceResult<EXCExceptionRule>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(EXCExceptionRuleDAO.getInstance().Select(wLoginUser, wID, wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Long> EXC_UpdateExceptionRule(BMSEmployee wLoginUser, EXCExceptionRule wEXCExceptionRule) {
		ServiceResult<Long> wResult = new ServiceResult<Long>(0L);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(EXCExceptionRuleDAO.getInstance().Update(wLoginUser, wEXCExceptionRule, wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> EXC_ActiveExceptionRule(BMSEmployee wLoginUser, List<Long> wIDList, int wActive) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			EXCExceptionRuleDAO.getInstance().Active(wLoginUser, wIDList, wActive, wErrorCode);
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<EXCExceptionType> EXC_GetExceptionTypeByID(BMSEmployee wLoginUser, long wID) {
		ServiceResult<EXCExceptionType> wResult = new ServiceResult<EXCExceptionType>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(EXCExceptionTypeDAO.getInstance().Select(wLoginUser, wID, wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Long> EXC_UpdateExceptionType(BMSEmployee wLoginUser, EXCExceptionType wEXCExceptionType) {
		ServiceResult<Long> wResult = new ServiceResult<Long>(0L);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(EXCExceptionTypeDAO.getInstance().Update(wLoginUser, wEXCExceptionType, wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> EXC_ActiveExceptionType(BMSEmployee wLoginUser, List<Long> wIDList, int wActive) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			EXCExceptionTypeDAO.getInstance().Active(wLoginUser, wIDList, wActive, wErrorCode);
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<EXCStation>> EXC_GetStationList(BMSEmployee wLoginUser, String wStationName,
			long wStationType, QRTypes wRelevancyType, long wRelevancyID, int wActive) {
		ServiceResult<List<EXCStation>> wResult = new ServiceResult<List<EXCStation>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(EXCStationDAO.getInstance().SelectAll(wLoginUser, wStationName, wStationType,
					wRelevancyType, wRelevancyID, wActive, wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<EXCStation> EXC_GetStationByID(BMSEmployee wLoginUser, long wID, String wStationNo) {
		ServiceResult<EXCStation> wResult = new ServiceResult<EXCStation>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(EXCStationDAO.getInstance().Select(wLoginUser, wID, wStationNo, wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<EXCStation> EXC_GetStationByRelevancyID(BMSEmployee wLoginUser, QRTypes wRelevancyType,
			long wRelevancyID) {
		ServiceResult<EXCStation> wResult = new ServiceResult<EXCStation>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(EXCStationDAO.getInstance().Select(wLoginUser, wRelevancyType, wRelevancyID, wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Long> EXC_UpdateStation(BMSEmployee wLoginUser, EXCStation wEXCStation) {
		ServiceResult<Long> wResult = new ServiceResult<Long>(0L);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(EXCStationDAO.getInstance().Update(wLoginUser, wEXCStation, wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> EXC_ActiveStation(BMSEmployee wLoginUser, List<Long> wIDList, int wActive) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			EXCStationDAO.getInstance().Active(wLoginUser, wIDList, wActive, wErrorCode);
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<EXCStationType>> EXC_GetStationTypeList(BMSEmployee wLoginUser, String wName,
			QRTypes wRelevancyType, int wActive) {
		ServiceResult<List<EXCStationType>> wResult = new ServiceResult<List<EXCStationType>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(EXCStationTypeDAO.getInstance().SelectAll(wLoginUser, null, wName, wRelevancyType,
					wActive, wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<EXCStationType> EXC_GetStationTypeByID(BMSEmployee wLoginUser, long wID) {
		ServiceResult<EXCStationType> wResult = new ServiceResult<EXCStationType>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(EXCStationTypeDAO.getInstance().Select(wLoginUser, wID, wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Long> EXC_UpdateStationType(BMSEmployee wLoginUser, EXCStationType wEXCStationType) {
		ServiceResult<Long> wResult = new ServiceResult<Long>(0L);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.setResult(EXCStationTypeDAO.getInstance().Update(wLoginUser, wEXCStationType, wErrorCode));
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> EXC_ActiveStationType(BMSEmployee wLoginUser, List<Long> wIDList, int wActive) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			EXCStationTypeDAO.getInstance().Active(wLoginUser, wIDList, wActive, wErrorCode);
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<EXCRunConfig> EXC_GetEXCRunConfig(BMSEmployee wLoginUser) {
		ServiceResult<EXCRunConfig> wResult = new ServiceResult<EXCRunConfig>();
		try {
			wResult.setResult(EXCConstants.getEXCRunConfig());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> EXC_SaveEXCRunConfig(BMSEmployee wLoginUser, EXCRunConfig wEXCRunConfig) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			EXCConstants.setEXCRunConfig(wEXCRunConfig);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> EXC_ReportAndOverShiftForward(BMSEmployee wLoginUser) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			EXCManagerUtils.getInstance().ReportAndOverShiftForward(wLoginUser, wErrorCode);
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> EXC_AutoOverTimeReportAndOverShiftForward(BMSEmployee wBMSEmployee) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			Calendar wNoUseTime = Calendar.getInstance();
			wNoUseTime.set(2000, 1, 1);

			Calendar wCalendar = Calendar.getInstance();

			// 每天一点以后开始检查
			Calendar wCheckStartTime = Calendar.getInstance();
			wCheckStartTime.set(wCalendar.get(Calendar.YEAR), wCalendar.get(Calendar.MONTH),
					wCalendar.get(Calendar.DATE), 1, 0, 0);
			if (wCalendar.compareTo(wCheckStartTime) < 0)
				return wResult;

			// 处理超时上报
			SimpleDateFormat wSdf = new SimpleDateFormat("yyyyMMdd1");
			String wCurTime = wSdf.format(new Date());
			int wShiftID = Integer.parseInt(wCurTime);
			List<EXCCallTask> wTaskList = EXCCallTaskDAO.getInstance().SelectAll(wBMSEmployee, null, 0, "", 0, 0, 0, -1,
					-1, -1, -1, -1, wShiftID, "", wNoUseTime, wNoUseTime, null, -1, wErrorCode);
			wTaskList = wTaskList.stream()
					.collect(Collectors.collectingAndThen(
							Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EXCCallTask::getID))),
							ArrayList::new));
			if (wTaskList != null && wTaskList.size() > 0) {
				// 异常规则表
				List<EXCExceptionRule> wRuleList = EXCExceptionRuleDAO.getInstance().SelectAll(wBMSEmployee, null, "",
						-1, -1, null, 1, wErrorCode);
				// 岗位列表
				APIResult wPoResult = CoreServiceImpl.getInstance().BMS_QueryPositionList(wBMSEmployee);
				List<BMSPosition> wPositionList = wPoResult.List(BMSPosition.class);

				List<Integer> wStatusList = new ArrayList<Integer>();
				wStatusList.add(EXCCallStatus.Default.getValue());
				wStatusList.add(EXCCallStatus.WaitRespond.getValue());
				wStatusList.add(EXCCallStatus.NoticeWaitRespond.getValue());

				wTaskList = wTaskList.stream().filter(
						p -> wCalendar.compareTo(p.ExpireTime) > 0 && wStatusList.stream().anyMatch(q -> q == p.Status))
						.collect(Collectors.toList());

				if (wTaskList != null && wTaskList.size() > 0) {
					// 处理超时上报
					HandleOverTimeReport(wBMSEmployee, wNoUseTime, wShiftID, wTaskList, wRuleList, wPositionList);
				}
			}
			// 处理超班转发
			HandleOverShiftForward(wBMSEmployee, wNoUseTime);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 处理超时上报
	 * 
	 * @param wBMSEmployee
	 * @param wNoUseTime
	 * @param wShiftID
	 * @param wTaskList
	 * @param wRuleList
	 * @param wPositionList
	 */
	private void HandleOverTimeReport(BMSEmployee wBMSEmployee, Calendar wNoUseTime, int wShiftID,
			List<EXCCallTask> wTaskList, List<EXCExceptionRule> wRuleList, List<BMSPosition> wPositionList) {
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			Optional<EXCExceptionRule> wOption = null;
			EXCExceptionRule wRule = null;
			EXCTimeItem wTimeItem = null;
			int wMinutes = 0;
			Optional<EXCTimeItem> wTimeOption = null;
			long wLeaderID = 0;
			List<EXCCallDispatch> wDispathcheList = null;
			EXCCallDispatch wOldDispatch = null;
			EXCCallDispatch wNewDispatch = null;
			long wNewID = 0;
			EXCAndon wAndon = null;
			List<EXCMessage> wMessageList = null;
			EXCMessage wMessage = null;
			EXCMessage wNewMessage = null;

			for (EXCCallTask wTask : wTaskList) {
				wOption = wRuleList.stream()
						.filter(p -> p.RespondLevel == wTask.RespondLevel && p.ExceptionType == wTask.ExceptionTypeID)
						.findFirst();
				if (!wOption.isPresent())
					continue;
				wRule = wOption.get();
				// 获取当前应上报的层数
				if (wRule.TimeOutList == null || wRule.TimeOutList.size() <= 0)
					continue;
				wTimeItem = wRule.TimeOutList.stream().max((a, b) -> a.Responselevel > b.Responselevel ? 1 : -1).get();
				// 获取当前处理人的层数
				int wCurLevel = GetCurLevel(wBMSEmployee, wTask.OperatorID, wPositionList);
				// 如果当前层数达到最大值直接返回
				if (wCurLevel >= wTimeItem.Responselevel)
					continue;
				// 修改EXCTask记录
				wTask.EditTime = wTask.ExpireTime;
				// 获取任务状态的默认时间
				wMinutes = 0;
				int wRealStatus = wTask.Status == 0 ? EXCCallStatus.WaitRespond.getValue() : wTask.Status;
				wTimeOption = wRule.TimeOutList.stream()
						.filter(p -> p.ID == wRealStatus && p.Responselevel == (wCurLevel + 1)).findFirst();
				if (wTimeOption.isPresent()) {
					wMinutes = wTimeOption.get().Time;
				} else {
					wMinutes = 60;
				}
				// 修改Task表的超时时间
				wTask.ExpireTime.add(Calendar.MINUTE, wMinutes);
				// 找到当前处理人的上级
				wLeaderID = GetLeaderID(wBMSEmployee, wTask.OperatorID);
				if (wLeaderID == 0)
					continue;
				wTask.OperatorID = new ArrayList<Long>();
				wTask.OperatorID.add(wLeaderID);
				EXCCallTaskDAO.getInstance().Update(wBMSEmployee, wTask, wErrorCode);

				// 处理Dispatch表
				wDispathcheList = EXCCallDispatchDAO.getInstance().SelectAll(wBMSEmployee, null, wTask.ID, -1, -1, -1,
						wNoUseTime, wNoUseTime, null, wErrorCode);
				if (wDispathcheList == null || wDispathcheList.size() <= 0)
					continue;
				wOldDispatch = wDispathcheList.get(wDispathcheList.size() - 1);
				// 新增
				wNewDispatch = new EXCCallDispatch();
				wNewDispatch.CreatorID = wOldDispatch.OperatorID;
				wNewDispatch.OperatorID = wLeaderID;
				wNewDispatch.Status = EXCCallStatus.WaitRespond.getValue();
				wNewDispatch.CreateTime = wTask.EditTime;
				wNewDispatch.EditTime = wTask.EditTime;
				wNewDispatch.TaskID = wTask.ID;
				wNewDispatch.ShiftID = wShiftID;
				wNewID = EXCCallDispatchDAO.getInstance().Update(wBMSEmployee, wNewDispatch, wErrorCode);
				if (wNewID <= 0)
					continue;
				if (wOldDispatch.ID <= 0)
					continue;
				// 修改
				wOldDispatch.EditTime = wTask.EditTime;
				wOldDispatch.Status = EXCCallStatus.UpGraded.getValue();
				EXCCallDispatchDAO.getInstance().Update(wBMSEmployee, wOldDispatch, wErrorCode);

				// 处理EXCAndon表
				wAndon = EXCAndonDAO.getInstance().Select(wBMSEmployee, wTask.ID, EXCAndonTypes.Default, wErrorCode);
				if (wAndon == null || wAndon.ID <= 0)
					continue;
				wAndon.OperatorID = wTask.OperatorID;
				wAndon.EditTime = wTask.EditTime;
				EXCAndonDAO.getInstance().Update(wBMSEmployee, wAndon, wErrorCode);

				// 处理EXCMessage表
				wMessageList = EXCMessageDAO.getInstance().EXC_GetMessageList(wBMSEmployee, -1, -1, "", -1, -1,
						(int) wTask.ID, -1, -1, wNoUseTime, wNoUseTime, wErrorCode);
				if (wMessageList == null || wMessageList.size() <= 0)
					continue;
				wMessage = wMessageList.get(0);
				wNewMessage = wMessage;
				wNewMessage.ID = 0;
				wNewMessage.ResponsorID = wLeaderID;
				wNewMessage.CreateTime = wTask.EditTime;
				wNewMessage.ShiftID = wShiftID;
				EXCMessageDAO.getInstance().Update(wBMSEmployee, wNewMessage, wErrorCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 处理超班转发
	 * 
	 * @param wBMSEmployee
	 * @param wNoUseTime
	 */
	private void HandleOverShiftForward(BMSEmployee wBMSEmployee, Calendar wNoUseTime) {
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			int wNowShiftID = Integer.parseInt(new SimpleDateFormat("yyyyMMdd1").format(new Date()));
			// 昨日shiftID
			Calendar wNowCalendar = Calendar.getInstance();
			wNowCalendar.add(Calendar.DATE, -1);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd1");
			String wYShiftIDStr = sdf.format(wNowCalendar.getTime());
			int wYShiftID = Integer.parseInt(wYShiftIDStr);

			// 昨日任务
			List<EXCCallTask> wYTaskList = EXCCallTaskDAO.getInstance().SelectAll(wBMSEmployee, null, 0, "", 0, 0, 0,
					-1, -1, -1, -1, -1, wYShiftID, "", wNoUseTime, wNoUseTime, null, -1, wErrorCode);
			wYTaskList = wYTaskList.stream()
					.collect(Collectors.collectingAndThen(
							Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EXCCallTask::getID))),
							ArrayList::new));
			if (wYTaskList == null || wYTaskList.size() <= 0)
				return;
			// 去除已撤销和已确认的任务
			wYTaskList = wYTaskList.stream().filter(
					p -> p.Status != EXCCallStatus.Confirmed.getValue() && p.Status != EXCCallStatus.Cancel.getValue())
					.collect(Collectors.toList());
			if (wYTaskList == null || wYTaskList.size() <= 0)
				return;
			// 遍历处理
			List<EXCCallDispatch> wDispathcheList = null;
			List<EXCCallDispatch> wDsiList = null;
			EXCCallDispatch wOldPatch = null;
			long wOldID = 0;
			EXCCallDispatch wNewPatch = null;
			long wNewID = 0;
			EXCAndon wAndon = null;
			for (EXCCallTask wTask : wYTaskList) {
				// 修改Task表
				wTask.ShiftID = wNowShiftID;
				wTask.EditTime = Calendar.getInstance();
				if (wTask.Status != EXCCallStatus.WaitConfirm.getValue())
					wTask.Status = EXCCallStatus.WaitRespond.getValue();
				EXCCallTaskDAO.getInstance().Update(wBMSEmployee, wTask, wErrorCode);

				// 处理Dispatch表
				wDispathcheList = EXCCallDispatchDAO.getInstance().SelectAll(wBMSEmployee, null, wTask.ID, -1, -1, -1,
						wNoUseTime, wNoUseTime, null, wErrorCode);
				if (wDispathcheList == null || wDispathcheList.size() <= 0)
					continue;

				wDsiList = wDispathcheList.stream().filter(p -> p.ShiftID == wYShiftID).collect(Collectors.toList());
				if (wDsiList == null || wDsiList.size() <= 0)
					continue;
				wOldPatch = wDsiList.get(wDsiList.size() - 1);
				if (wOldPatch == null || wOldPatch.ID <= 0)
					continue;

				wOldID = wOldPatch.ID;
				// 新增
				wNewPatch = wOldPatch;
				wNewPatch.ID = 0;
				wNewPatch.CreatorID = wOldPatch.OperatorID;
				wNewPatch.EditTime = Calendar.getInstance();
				wNewPatch.CreateTime = Calendar.getInstance();
				wNewPatch.ShiftID = wNowShiftID;
				wNewID = EXCCallDispatchDAO.getInstance().Update(wBMSEmployee, wNewPatch, wErrorCode);
				if (wNewID <= 0)
					continue;

				// 修改
				wOldPatch.ID = wOldID;
				wOldPatch.Status = EXCCallStatus.Forwarded.getValue();
				wOldPatch.EditTime = Calendar.getInstance();
				EXCCallDispatchDAO.getInstance().Update(wBMSEmployee, wOldPatch, wErrorCode);

				// 处理EXCAndon表
				wAndon = EXCAndonDAO.getInstance().Select(wBMSEmployee, wTask.ID, EXCAndonTypes.Default, wErrorCode);
				if (wAndon == null || wAndon.ID <= 0)
					continue;
				wAndon.ShiftID = wNowShiftID;
				wAndon.EditTime = wTask.EditTime;
				EXCAndonDAO.getInstance().Update(wBMSEmployee, wAndon, wErrorCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}

	}

	/**
	 * 获取上级岗位
	 * 
	 * @param wBMSEmployee
	 * @param wOperatorID
	 * @return
	 */
	private long GetLeaderID(BMSEmployee wBMSEmployee, List<Long> wOperatorID) {
		long wID = 0;
		try {
			if (wOperatorID == null || wOperatorID.size() <= 0)
				return wID;

			APIResult wAPIResult = CoreServiceImpl.getInstance().BMS_QueryEmployeeByID(wBMSEmployee,
					wOperatorID.get(0).intValue());
			BMSEmployee wEmployee = wAPIResult.Custom("info", BMSEmployee.class);
			if (wEmployee == null || wEmployee.ID <= 0)
				return wID;

			APIResult wResultList = CoreServiceImpl.getInstance().BMS_GetEmployeeAll(wBMSEmployee, -1,
					wEmployee.Manager, 1);
			List<BMSEmployee> wList = wResultList.List(BMSEmployee.class);
			if (wList == null || wList.size() <= 0)
				return wID;
			wID = wList.get(0).ID;
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wID;
	}

	/**
	 * 获取当前等级
	 * 
	 * @param wBMSEmployee
	 * @param wOperatorID
	 * @param wPositionList
	 * @return
	 */
	private int GetCurLevel(BMSEmployee wBMSEmployee, List<Long> wOperatorID, List<BMSPosition> wPositionList) {
		int wResult = 0;
		try {
			if (wOperatorID == null || wOperatorID.size() <= 0)
				return wResult;

			long wOpeID = wOperatorID.get(0);
			// 获取岗位
			APIResult wAPIResult = CoreServiceImpl.getInstance().BMS_QueryEmployeeByID(wBMSEmployee, (int) wOpeID);
			BMSEmployee wEmployee = wAPIResult.Custom("info", BMSEmployee.class);
			if (wEmployee == null || wEmployee.ID <= 0)
				return wResult;

			wResult = FindLevel(FindSubPoList(wEmployee.Position, wPositionList), 0, wPositionList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取子岗位列表
	 * 
	 * @param wPositionID
	 * @param wPositionList
	 * @return
	 */
	private List<BMSPosition> FindSubPoList(int wPositionID, List<BMSPosition> wPositionList) {
		List<BMSPosition> wResult = new ArrayList<BMSPosition>();
		try {
			if (wPositionList == null || wPositionList.size() <= 0)
				return wResult;

			wResult = wPositionList.stream().filter(p -> p.ParentID == wPositionID).collect(Collectors.toList());
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 获取级别
	 * 
	 * @param wList
	 * @param wNum
	 * @param wPositionList
	 * @return
	 */
	private int FindLevel(List<BMSPosition> wList, Integer wNum, List<BMSPosition> wPositionList) {
		try {
			if (wList != null && wList.size() > 0) {
				wNum++;
				List<Integer> wArrayList = new ArrayList<Integer>();
				for (BMSPosition wItem : wList) {
					wArrayList.add(FindLevel(FindSubPoList(wItem.ID, wPositionList), wNum, wPositionList));
				}
				wNum = wArrayList.stream().max((a, b) -> a > b ? 1 : -1).get();
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wNum;
	}

	@Override
	public ServiceResult<List<EXCCallTaskBPM>> EXC_QueryExceptionAll(BMSEmployee wLoginUser, Calendar wShiftDate,
			int wAPSShiftPeriod, int wLevel) {
		ServiceResult<List<EXCCallTaskBPM>> wResult = new ServiceResult<List<EXCCallTaskBPM>>();
		wResult.Result = new ArrayList<EXCCallTaskBPM>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			Calendar wLastTime = Calendar.getInstance();
			switch (APSShiftPeriod.getEnumType(wAPSShiftPeriod)) {
			case Month:
				wLastTime = this.getLastDayOfMonth(wShiftDate);
				wLastTime.set(Calendar.HOUR_OF_DAY, 23);
				wLastTime.set(Calendar.MINUTE, 59);
				wLastTime.set(Calendar.SECOND, 59);
				break;
			case Week:
				wLastTime = this.getLastOfWeek(wShiftDate);
				wLastTime.set(Calendar.HOUR_OF_DAY, 23);
				wLastTime.set(Calendar.MINUTE, 59);
				wLastTime.set(Calendar.SECOND, 59);
				break;
			case Day:
				wLastTime = wShiftDate;
				wLastTime.set(Calendar.HOUR_OF_DAY, 23);
				wLastTime.set(Calendar.MINUTE, 59);
				wLastTime.set(Calendar.SECOND, 59);
				break;
			default:
				break;
			}

			if (wLastTime.compareTo(Calendar.getInstance()) > 0) {
				wLastTime = Calendar.getInstance();
			}

//			wResult.Result = EXCCallTaskDAO.getInstance().SelectList(wLoginUser, wLastTime, wLevel, wErrorCode);

			// 查询未关闭的异常
			List<EXCCallTaskBPM> wNList = EXCCallTaskBPMDAO.getInstance().SelectList(wLoginUser, -1, "", -1, -1, "", -1,
					null, null, new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 22)), wErrorCode);
//			List<EXCCallTask> wNList = EXCCallTaskDAO.getInstance().SelectAll(wLoginUser, null, -1, "", -1, -1, -1, -1,
//					-1, -1, -1, -1, -1, "", null, null, new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 22)), -1,
//					wErrorCode);
			if (wNList.size() > 0) {
				Calendar wTime = wLastTime;
				wNList = wNList.stream().filter(p -> wTime.compareTo(p.CreateTime) > 0).collect(Collectors.toList());

				wResult.Result.addAll(wNList);
			}

			wResult.Result = new ArrayList<EXCCallTaskBPM>(wResult.Result.stream()
					.collect(Collectors.toMap(EXCCallTaskBPM::getID, account -> account, (k1, k2) -> k2)).values());

			// 响应级别列表
			List<EXCOptionItem> wResponseLevelList = this.EXC_GetRespondLevelList(wLoginUser).Result;

			if (wResult.Result != null && wResult.Result.size() > 0) {
				List<BMSEmployee> wUserList = CoreServiceImpl.getInstance().BMS_GetEmployeeAll(wLoginUser, -1, -1, 1)
						.List(BMSEmployee.class);
				if (wUserList == null || wUserList.size() <= 0) {
					return wResult;
				}
//				List<String> wNames = null;
				for (EXCCallTaskBPM wEXCCallTask : wResult.Result) {
					if (wUserList.stream().anyMatch(p -> p.ID == wEXCCallTask.ApplicantID)) {
						wEXCCallTask.ApplyName = wUserList.stream().filter(p -> p.ID == wEXCCallTask.ApplicantID)
								.findFirst().get().Name;
					}
					if (wUserList.stream().anyMatch(p -> p.ID == wEXCCallTask.ConfirmID)) {
						wEXCCallTask.ConfirmName = wUserList.stream().filter(p -> p.ID == wEXCCallTask.ConfirmID)
								.findFirst().get().Name;
					}
					if (wResponseLevelList != null
							&& wResponseLevelList.stream().anyMatch(p -> p.ID == wEXCCallTask.RespondLevel)) {
						wEXCCallTask.RespondLevelName = wResponseLevelList.stream()
								.filter(p -> p.ID == wEXCCallTask.RespondLevel).findFirst().get().Name;
					}

//					if (wEXCCallTask.OperatorID == null || wEXCCallTask.OperatorID.size() <= 0) {
//						continue;
//					}
//					wNames = new ArrayList<String>();
//					for (long wPersonID : wEXCCallTask.OperatorID) {
//						if (!wUserList.stream().anyMatch(p -> p.ID == wPersonID)) {
//							continue;
//						}
//						String wName = wUserList.stream().filter(p -> p.ID == wPersonID).findFirst().get().Name;
//						if (StringUtils.isNotEmpty(wName)) {
//							wNames.add(wName);
//						}
//					}
//					if (wNames.size() > 0) {
//						wEXCCallTask.Operators = StringUtils.Join(",", wNames);
//					}
				}
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 获取日期所在月最后一天
	 * 
	 * @param wDate
	 * @return
	 */
	public Calendar getLastDayOfMonth(Calendar wDate) {
		Calendar wResult = Calendar.getInstance();
		try {
			wResult = wDate;
			int wLast = wResult.getActualMaximum(Calendar.DAY_OF_MONTH);
			wResult.set(Calendar.DAY_OF_MONTH, wLast);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取指定日期所在周最后一天
	 * 
	 * @param dataStr
	 * @param dateFormat
	 * @param resultDateFormat
	 * @return
	 */
	public Calendar getLastOfWeek(Calendar wDate) {
		Calendar wResult = Calendar.getInstance();
		try {
			int wD = 0;
			if (wDate.get(Calendar.DAY_OF_WEEK) == 1) {
				wD = -6;
			} else {
				wD = 2 - wDate.get(Calendar.DAY_OF_WEEK);
			}
			wDate.add(Calendar.DAY_OF_WEEK, wD);
			wDate.add(Calendar.DAY_OF_WEEK, 6);
			wResult = wDate;
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<EXCLevelType>> EXC_QueryLevelTypeLists(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime) {
		ServiceResult<List<EXCLevelType>> wResult = new ServiceResult<List<EXCLevelType>>();
		wResult.Result = new ArrayList<EXCLevelType>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// 查询时间段内所有的
			List<EXCCallTaskBPM> wList = EXCCallTaskBPMDAO.getInstance().SelectList(wLoginUser, -1, "", -1, -1, "", -1,
					wStartTime, wEndTime, null, wErrorCode);
//			List<EXCCallTask> wList = EXCCallTaskDAO.getInstance().SelectAll(wLoginUser, null, -1, "", -1, -1, -1, -1,
//					-1, -1, -1, -1, -1, "", wStartTime, wEndTime, null, -1, wErrorCode);
			// 查询所有未关闭的

			List<EXCCallTaskBPM> wAllList = new ArrayList<EXCCallTaskBPM>();
			if (wList != null && wList.size() > 0) {
				wAllList.addAll(wList);
			}

			if (wAllList.size() <= 0) {
				return wResult;
			}

			wAllList = new ArrayList<EXCCallTaskBPM>(wAllList.stream()
					.collect(Collectors.toMap(EXCCallTaskBPM::getID, account -> account, (k1, k2) -> k2)).values());

			// 所有响应级别
			List<EXCOptionItem> wResponseLevelList = this.EXC_GetRespondLevelList(wLoginUser).Result;
			// 所有异常类型
			List<EXCExceptionType> wEXCExceptionTypeList = this.EXC_GetExceptionTypeList(wLoginUser, "", -1,
					TaskRelevancyTypes.Default, 1).Result;
			for (EXCOptionItem wEXCOptionItem : wResponseLevelList) {
				for (EXCExceptionType wEXCExceptionType : wEXCExceptionTypeList) {
					EXCLevelType wEXCLevelType = new EXCLevelType();
					wEXCLevelType.ResponseLevel = wEXCOptionItem.Name;
					wEXCLevelType.Type = wEXCExceptionType.Name;
					wEXCLevelType.Times = (int) wAllList.stream().filter(
							p -> p.RespondLevel == wEXCOptionItem.ID && p.ExceptionTypeID == wEXCExceptionType.ID)
							.count();
					wResult.Result.add(wEXCLevelType);
				}
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<EXCCallTaskBPM> EXC_QueryDefaultCallTaskBPM(BMSEmployee wLoginUser, int wEventID) {
		ServiceResult<EXCCallTaskBPM> wResult = new ServiceResult<EXCCallTaskBPM>();
		wResult.Result = new EXCCallTaskBPM();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<EXCCallTaskBPM> wList = EXCCallTaskBPMDAO.getInstance().SelectList(wLoginUser, -1, "", wLoginUser.ID,
					-1, "", -1, null, null, new ArrayList<Integer>(Arrays.asList(0)), wErrorCode);
			if (wList.size() > 0) {
				int wShiftID = MESServer.MES_QueryShiftID(wLoginUser.CompanyID, Calendar.getInstance(),
						APSShiftPeriod.Day, FMCShiftLevel.Day, 0);
				wResult.Result = wList.get(0);
				wResult.Result.ShiftID = wShiftID;
				wResult.Result.CreateTime = Calendar.getInstance();
				wResult.Result.ApplicantTime = Calendar.getInstance();
				wResult.Result.Code = EXCCallTaskBPMDAO.getInstance().GetNewCode(wLoginUser, wErrorCode);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public synchronized ServiceResult<EXCCallTaskBPM> EXC_CreateCallTaskBPM(BMSEmployee wLoginUser,
			BPMEventModule wEventID) {
		ServiceResult<EXCCallTaskBPM> wResult = new ServiceResult<EXCCallTaskBPM>();
		wResult.Result = new EXCCallTaskBPM();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result.ID = 0;
			wResult.Result.Code = EXCCallTaskBPMDAO.getInstance().GetNewCode(wLoginUser, wErrorCode);
			wResult.Result.UpFlowID = wLoginUser.ID;
			wResult.Result.UpFlowName = wLoginUser.Name;
			wResult.Result.CreateTime = Calendar.getInstance();
			wResult.Result.SubmitTime = Calendar.getInstance();
			wResult.Result.Status = EXCCallTaskBPMStatus.Default.getValue();
			wResult.Result.StatusText = "";
			wResult.Result.FlowType = wEventID.getValue();
			wResult.Result.ShiftID = MESServer.MES_QueryShiftID(wLoginUser.CompanyID, Calendar.getInstance(),
					APSShiftPeriod.Day, FMCShiftLevel.Day, 0);
			wResult.Result.ApplicantID = wLoginUser.ID;
			wResult.Result.ApplicantTime = Calendar.getInstance();
			wResult.Result.ConfirmID = wLoginUser.ID;
			BMSEmployee wEmployee = EXCConstants.GetBMSEmployee(wLoginUser.ID);
			if (wEmployee != null && wEmployee.ID > 0) {
				wResult.Result.OperatorID = wEmployee.SuperiorID > 0 ? String.valueOf(wEmployee.SuperiorID) : "";
			}

			wResult.Result = (EXCCallTaskBPM) EXCCallTaskBPMDAO.getInstance().BPM_UpdateTask(wLoginUser, wResult.Result,
					wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<EXCCallTaskBPM> EXC_SubmitCallTaskBPM(BMSEmployee wLoginUser, EXCCallTaskBPM wData) {
		ServiceResult<EXCCallTaskBPM> wResult = new ServiceResult<EXCCallTaskBPM>();
		wResult.Result = new EXCCallTaskBPM();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ①获取上报规则
			EXCExceptionRule wRule = EXCExceptionRuleDAO.getInstance().Select(wLoginUser, wData.ExceptionTypeID,
					wData.RespondLevel, EXCResourceTypes.Artificial, EXCResourceTypes.Artificial,
					EXCResourceTypes.Artificial, wErrorCode);
			if (wRule != null && wRule.ID > 0 && wRule.Active == 1 && wRule.TimeOutList != null
					&& wRule.TimeOutList.size() > 0
					&& wRule.TimeOutList.stream().anyMatch(p -> p.Responselevel == 1 && p.ID == 1)) {
				int wTime = wRule.TimeOutList.stream().filter(p -> p.Responselevel == 1 && p.ID == 1).findFirst()
						.get().Time;
				// ②处理剩余上报次数和过期时刻
				if (wData.Status == 1 && wData.OnSite == 1) {
					wData.ReportTimes = wRule.ReportTimes;
					wData.ExpireTime = Calendar.getInstance();
					wData.ExpireTime.add(Calendar.MINUTE, wTime);
				} else if (wData.Status == 1 && wData.OnSite == 1) {
					wData.ReportTimes = wRule.ReportTimes;
					wData.ExpireTime = Calendar.getInstance();
					wData.ExpireTime.add(Calendar.MINUTE, wTime);
				} else if (wData.Status == 2) {
					wData.ReportTimes = wRule.ReportTimes;
					wData.ExpireTime = Calendar.getInstance();
					wData.ExpireTime.add(Calendar.MINUTE, wTime);
				} else if (wData.Status == 22) {
					wData.ExpireTime = Calendar.getInstance();
					wData.ExpireTime.add(Calendar.MINUTE, wTime);
				}
			}

			if (wData.Status == 3) {
				wData.ReportTimes = 0;
				wData.ExpireTime.set(2000, 0, 1, 0, 0, 0);
			}

			// ①处理异常点
			if (wData.Status == 1) {
				FPCPart wPart = EXCConstants.GetFPCPart(wData.PartID);
				if (wPart != null && wPart.ID > 0) {
					ServiceResult<EXCStation> wServiceResult = this.EXC_GetStation(wLoginUser, wData.PartID, wPart.Code,
							QRTypes.Station);
					if (wServiceResult.Result != null && wServiceResult.Result.ID > 0) {
						wData.StationType = wServiceResult.Result.StationType;
						wData.StationID = wServiceResult.Result.ID;
					}
				}

				// 车型绑定
				if (StringUtils.isNotEmpty(wData.getPartNo()) && wData.PlaceID > 0 && !wData.getPartNo().equals("#")) {
					APIResult wAPIResult = CoreServiceImpl.getInstance().FMC_QueryWorkspace(wLoginUser,
							wData.getPlaceID(), "");

					FMCWorkspace wFMCWorkspace = wAPIResult.Info(FMCWorkspace.class);
					if (wAPIResult.getResultCode() == RetCode.SERVER_CODE_SUC && wFMCWorkspace != null
							&& wFMCWorkspace.getID() > 0) {
						wFMCWorkspace.setPartNo(wData.getPartNo());
						// 获取车型
						wFMCWorkspace.setProductID(EXCConstants.GetFPCProducID(wData.PartNo.split("#")[0]));

						CoreServiceImpl.getInstance().FMC_BindFMCWorkspace(wLoginUser, wFMCWorkspace);
					}
				}
			}

			// ①处理结束点
			if (wData.Status == 20) {
				wData.StatusText = EXCCallTaskBPMStatus.NomalClose.getLable();
				wData.ExpireTime.set(2000, 0, 1, 0, 0, 0);

				// 调用台车bom删除接口，删除指定台车bom
				if (wData.OnSite > 0) {
					int wSIndex = wData.Comment.indexOf("(");
					int wEIndex = wData.Comment.indexOf(")");
					String wMaterialNames = wData.Comment.substring(wSIndex + 1, wEIndex);
					if (StringUtils.isNotEmpty(wMaterialNames)) {
						List<FPCPart> wPartList = EXCConstants.GetFPCPartList().values().stream()
								.filter(p -> p.Active == 1).collect(Collectors.toList());
						if (wPartList.stream().anyMatch(p -> p.Code.equals(wData.StationNo))) {
							int wPartID = wPartList.stream().filter(p -> p.Code.equals(wData.StationNo)).findFirst()
									.get().ID;
							List<APSBOMItem> wItemList = CoreServiceImpl.getInstance()
									.APS_QueryDeleteList(wLoginUser, wData.OnSite, wPartID, wMaterialNames)
									.List(APSBOMItem.class);
							for (APSBOMItem wAPSBOMItem : wItemList) {
								CoreServiceImpl.getInstance().APS_DeleteBom(wLoginUser, wAPSBOMItem);
							}
						}
					}
				}
			} else if (wData.Status == 22) {
				wData.StatusText = EXCCallTaskBPMStatus.ToHandle.getLable();
			}

			wResult.Result = (EXCCallTaskBPM) EXCCallTaskBPMDAO.getInstance().BPM_UpdateTask(wLoginUser, wData,
					wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<EXCCallTaskBPM> EXC_GetCallTaskBPM(BMSEmployee wLoginUser, int wID) {
		ServiceResult<EXCCallTaskBPM> wResult = new ServiceResult<EXCCallTaskBPM>();
		wResult.Result = new EXCCallTaskBPM();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = (EXCCallTaskBPM) EXCCallTaskBPMDAO.getInstance().BPM_GetTaskInfo(wLoginUser, wID, "",
					wErrorCode);

			// 翻译工位
			if (wResult.Result.StationID > 0) {
				EXCStation wEXCStation = EXCStationDAO.getInstance().Select(wLoginUser, wResult.Result.StationID, "",
						wErrorCode);
				if (wEXCStation != null && wEXCStation.ID > 0) {
					wResult.Result.StationName = StringUtils.Format("{0}【{1}】", wEXCStation.StationName,
							wEXCStation.StationNo);
				}
			}
			// 翻译台位
			if (wResult.Result.PlaceID > 0) {
				List<FMCWorkspace> wList = CoreServiceImpl.getInstance()
						.FMC_GetFMCWorkspaceList(wLoginUser, -1, -1, "", -1, 1).List(FMCWorkspace.class);
				if (wList.stream().anyMatch(p -> p.ID == wResult.Result.PlaceID)) {
					FMCWorkspace wSpace = wList.stream().filter(p -> p.ID == wResult.Result.PlaceID).findFirst().get();
					wResult.Result.PlaceNo = StringUtils.Format("{0}【{1}】", wSpace.Name, wSpace.Code);
				}
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<BPMTaskBase>> EXC_QueryCallTaskBPMEmployeeAll(BMSEmployee wLoginUser, int wTagTypes,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<BPMTaskBase>> wResult = new ServiceResult<List<BPMTaskBase>>();
		wResult.Result = new ArrayList<BPMTaskBase>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			switch (TagTypes.getEnumType(wTagTypes)) {
			case Applicant:// 2发起
				wResult.Result = EXCCallTaskBPMDAO.getInstance().BPM_GetSendTaskList(wLoginUser, wLoginUser.ID,
						wStartTime, wEndTime, wErrorCode);
				break;
			case Dispatcher:// 1待做
				wResult.Result = EXCCallTaskBPMDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.ID,
						wErrorCode);
				break;
			case Approver:// 4已做
				wResult.Result = EXCCallTaskBPMDAO.getInstance().BPM_GetDoneTaskList(wLoginUser, wLoginUser.ID,
						wStartTime, wEndTime, wErrorCode);
				break;
			default:
				break;
			}

			if (wResult.Result.size() > 0) {
				wResult.Result = wResult.Result.stream().filter(p -> p.Status != 0).collect(Collectors.toList());

				List<BPMTaskBase> wDoneList = wResult.Result.stream().filter(p -> p.Status == 20
						&& wStartTime.compareTo(p.SubmitTime) <= 0 && wEndTime.compareTo(p.CreateTime) >= 0)
						.collect(Collectors.toList());
				List<BPMTaskBase> wToDoList = wResult.Result.stream().filter(p -> p.Status != 20)
						.collect(Collectors.toList());
				wResult.Result = new ArrayList<BPMTaskBase>();
				wResult.Result.addAll(wToDoList);
				wResult.Result.addAll(wDoneList);

				wResult.Result.sort(Comparator.comparing(BPMTaskBase::getCreateTime, Comparator.reverseOrder()));
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<EXCCallTaskBPM>> EXC_QueryCallTaskBPMHistory(BMSEmployee wLoginUser, int wID,
			String wCode, int wUpFlowID, int wShiftID, String wPartNo, int wPlaceID, Calendar wStartTime,
			Calendar wEndTime, int wResponseLevel, int wExceptionType) {
		ServiceResult<List<EXCCallTaskBPM>> wResult = new ServiceResult<List<EXCCallTaskBPM>>();
		wResult.Result = new ArrayList<EXCCallTaskBPM>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = EXCCallTaskBPMDAO.getInstance().SelectListHistory(wLoginUser, wID, wCode, wUpFlowID,
					wShiftID, wPartNo, wPlaceID, wStartTime, wEndTime, null, wResponseLevel, wExceptionType,
					wErrorCode);

			if (wResult.Result.size() > 0) {
				wResult.Result.removeIf(p -> p.Status == 0);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> EXC_OverTimeReportBPM(BMSEmployee adminUser) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			// ①获取状态==1或状态==2的呼叫数据集合
			List<EXCCallTaskBPM> wList = EXCCallTaskBPMDAO.getInstance().SelectList(adminUser, -1, "", -1, -1, "", -1,
					null, null, new ArrayList<Integer>(Arrays.asList(1, 2, 22)), wErrorCode);
			if (wList == null || wList.size() <= 0) {
				return wResult;
			}
			// ②遍历数据
			for (EXCCallTaskBPM wEXCCallTaskBPM : wList) {
				// ③获取上报规则
				EXCExceptionRule wRule = EXCExceptionRuleDAO.getInstance().Select(adminUser,
						wEXCCallTaskBPM.ExceptionTypeID, wEXCCallTaskBPM.RespondLevel, EXCResourceTypes.Artificial,
						EXCResourceTypes.Artificial, EXCResourceTypes.Artificial, wErrorCode);
				if (wRule == null || wRule.ID <= 0) {
					continue;
				}
				// ④判断是否到期且剩余上报次数大于0
				if (!(Calendar.getInstance().compareTo(wEXCCallTaskBPM.ExpireTime) > 0
						&& wEXCCallTaskBPM.ReportTimes > 0)) {
					continue;
				}
				int wReLevel = wRule.ReportTimes - wEXCCallTaskBPM.ReportTimes + 1;
				// ⑥根据上报层级获取上报人员ID
				int wOperatorID = this.GetOperatorID(wEXCCallTaskBPM.OperatorID);
				int wResponseID = this.GetResponseID(adminUser, wReLevel, wOperatorID);
				if (wResponseID <= 0) {
					continue;
				}
				// ⑦发送通知消息
				this.SendCallMessage(adminUser, wEXCCallTaskBPM, wResponseID);
				// ⑤根据上报次数获取超时配置
				if (!(wRule.TimeOutList != null && wRule.TimeOutList.size() > 0
						&& wRule.TimeOutList.stream().anyMatch(p -> p.ID == 1 && p.Responselevel == wReLevel))) {
					continue;
				}
				EXCTimeItem wTimeOut = wRule.TimeOutList.stream().filter(p -> p.ID == 1 && p.Responselevel == wReLevel)
						.findFirst().get();
				// ⑧修改剩余上报次数和下次过期时刻
				wEXCCallTaskBPM.ReportTimes--;
				wEXCCallTaskBPM.ExpireTime = Calendar.getInstance();
				wEXCCallTaskBPM.ExpireTime.add(Calendar.MINUTE, wTimeOut.Time);
				EXCCallTaskBPMDAO.getInstance().BPM_UpdateTask(adminUser, wEXCCallTaskBPM, wErrorCode);
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 获取人员ID
	 */
	private int GetOperatorID(String wOperatorID) {
		int wResult = 0;
		try {
			if (StringUtils.isEmpty(wOperatorID)) {
				return wResult;
			}

			String[] wStrs = wOperatorID.split(",");
			if (wStrs == null || wStrs.length <= 0) {
				return wResult;
			}

			wResult = Integer.parseInt(wStrs[0]);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 查找上级
	 */
	private int GetResponseID(BMSEmployee wLoginUser, int wReLevel, int wOperatorID) {
		int wResult = 0;
		try {
			if (wReLevel <= 0) {
				return wResult;
			}

			BMSEmployee wEmployee = EXCConstants.GetBMSEmployee(wOperatorID);
			if (wEmployee == null || wEmployee.ID <= 0) {
				return wResult;
			}

			if (wEmployee.SuperiorID <= 0) {
				wEmployee = CoreServiceImpl.getInstance().BMS_GetSuperior(wLoginUser, wEmployee)
						.Info(BMSEmployee.class);
				if (wEmployee == null || wEmployee.ID <= 0 || wEmployee.SuperiorID <= 0) {
					return wResult;
				}
			}

			wResult = wEmployee.SuperiorID;
			wReLevel--;

			if (wReLevel > 0) {
				wResult = GetResponseID(wLoginUser, wReLevel, wEmployee.SuperiorID);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 发送通知消息给上级
	 */
	private void SendCallMessage(BMSEmployee adminUser, EXCCallTaskBPM wEXCCallTaskBPM, int wResponseID) {
		try {
			List<BFCMessage> wBFCMessageList = new ArrayList<>();
			BFCMessage wMessage = null;
			int wShiftID = MESServer.MES_QueryShiftID(adminUser.CompanyID, Calendar.getInstance(), APSShiftPeriod.Day,
					FMCShiftLevel.Day, 0);
			SimpleDateFormat wSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// 发送任务消息到人员
			wMessage = new BFCMessage();
			wMessage.Active = 0;
			wMessage.CompanyID = 0;
			wMessage.CreateTime = Calendar.getInstance();
			wMessage.EditTime = Calendar.getInstance();
			wMessage.ID = 0;
			wMessage.MessageID = wEXCCallTaskBPM.ID;
			wMessage.Title = wEXCCallTaskBPM.Code;
			wMessage.MessageText = StringUtils.Format("模块：{0} 发起人：{1} 发起时刻：{2} {3}超时未处理",
					BPMEventModule.SCCall.getLable(), wEXCCallTaskBPM.UpFlowName,
					wSDF.format(wEXCCallTaskBPM.CreateTime.getTime()), wEXCCallTaskBPM.Operators);
			wMessage.ModuleID = BPMEventModule.SCCall.getValue();
			wMessage.ResponsorID = wResponseID;
			wMessage.ShiftID = wShiftID;
			wMessage.StationID = 0;
			wMessage.Type = BFCMessageType.Notify.getValue();
			wBFCMessageList.add(wMessage);

			CoreServiceImpl.getInstance().BFC_UpdateMessageList(adminUser, wBFCMessageList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public ServiceResult<Integer> EXC_DelegateTask(BMSEmployee wLoginUser, String wTaskID, String wUserID,
			int wMainTaskID, String wRemark) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			EXCCallTaskBPM wTask = (EXCCallTaskBPM) EXCCallTaskBPMDAO.getInstance().BPM_GetTaskInfo(wLoginUser,
					wMainTaskID, "", wErrorCode);
			if (wTask == null || wTask.ID <= 0) {
				wResult.FaultCode += "提示：主单据不存在!";
				return wResult;
			}

			APIResult wAPIResult = BPMServiceImpl.getInstance().BPM_DelegateTask(wLoginUser, wTaskID, wUserID, wRemark);
			String wMsg = wAPIResult.getMsg();
			if (StringUtils.isNotEmpty(wMsg)) {
				wResult.FaultCode = wMsg;
				return wResult;
			}

			wTask.OperatorID = wUserID;
			EXCCallTaskBPMDAO.getInstance().Update(wLoginUser, wTask, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<EXCCallTaskBPM>> EXC_GetAndonList(BMSEmployee wLoginUser) {
		ServiceResult<List<EXCCallTaskBPM>> wResult = new ServiceResult<List<EXCCallTaskBPM>>();
		wResult.Result = new ArrayList<EXCCallTaskBPM>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = EXCCallTaskBPMDAO.getInstance().SelectList(wLoginUser, -1, "", -1, -1, "", -1, null, null,
					new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 22)), wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<BMSEmployee>> EXC_QueryNoticeList(BMSEmployee wLoginUser) {
		ServiceResult<List<BMSEmployee>> wResult = new ServiceResult<List<BMSEmployee>>();
		wResult.Result = new ArrayList<BMSEmployee>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<BMSEmployee> wUserList = EXCConstants.GetBMSEmployeeList().values().stream().filter(p -> p.Active == 1)
					.collect(Collectors.toList());
			wResult.Result = GetSuperiorList(wLoginUser.ID, wUserList, wLoginUser.ID);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 获取上级
	 */
	private List<BMSEmployee> GetSuperiorList(int wID, List<BMSEmployee> wUserList, int wFixedID) {
		List<BMSEmployee> wResult = new ArrayList<BMSEmployee>();
		try {
			if (wUserList.stream().anyMatch(p -> p.ID == wID)) {
				BMSEmployee wUser = wUserList.stream().filter(p -> p.ID == wID).findFirst().get();
				if (wUser.SuperiorID > 0 && wUser.SuperiorID != wID
						&& wUserList.stream().anyMatch(p -> p.ID == wUser.SuperiorID && p.Position != 13)) {
					BMSEmployee wItem = wUserList.stream().filter(p -> p.ID == wUser.SuperiorID).findFirst().get();

					if (wItem.ID == wFixedID) {
						return wResult;
					}

					wResult.add(wItem);

					wResult.addAll(GetSuperiorList(wItem.ID, wUserList, wFixedID));
				} else {
					return wResult;
				}
			} else {
				return wResult;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<EXCCallTaskBPM>> EXC_QueryCallTaskBPMEmployeeAllList(BMSEmployee wLoginUser,
			Calendar wStartTime, Calendar wEndTime, int wPartID, int wLevel, int wExceptionType, int wStatus) {
		ServiceResult<List<EXCCallTaskBPM>> wResult = new ServiceResult<List<EXCCallTaskBPM>>();
		wResult.Result = new ArrayList<EXCCallTaskBPM>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {

			List<Integer> wIDList = new ArrayList<Integer>();

			List<BPMTaskBase> wToDoList = EXCCallTaskBPMDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.ID,
					wErrorCode);
			for (BPMTaskBase wBPMTaskBase : wToDoList) {
				if (wIDList.contains(wBPMTaskBase.ID))
					continue;
				wIDList.add(wBPMTaskBase.ID);
				EXCCallTaskBPM wEXCCallTaskBPM = (EXCCallTaskBPM) wBPMTaskBase;
				wEXCCallTaskBPM.TagTypes = 1;
				wResult.Result.add(wEXCCallTaskBPM);
			}

			List<BPMTaskBase> wDoneList = EXCCallTaskBPMDAO.getInstance().BPM_GetDoneTaskList(wLoginUser, wLoginUser.ID,
					wStartTime, wEndTime, wErrorCode);
			for (BPMTaskBase wBPMTaskBase : wDoneList) {
				if (wIDList.contains(wBPMTaskBase.ID))
					continue;
				wIDList.add(wBPMTaskBase.ID);
				EXCCallTaskBPM wEXCCallTaskBPM = (EXCCallTaskBPM) wBPMTaskBase;
				wEXCCallTaskBPM.TagTypes = 4;
				wResult.Result.add(wEXCCallTaskBPM);
			}

			List<BPMTaskBase> wSendList = EXCCallTaskBPMDAO.getInstance().BPM_GetSendTaskList(wLoginUser, wLoginUser.ID,
					wStartTime, wEndTime, wErrorCode);

			for (BPMTaskBase wBPMTaskBase : wSendList) {
				if (wIDList.contains(wBPMTaskBase.ID))
					continue;
				wIDList.add(wBPMTaskBase.ID);
				EXCCallTaskBPM wEXCCallTaskBPM = (EXCCallTaskBPM) wBPMTaskBase;
				wEXCCallTaskBPM.TagTypes = 2;
				wResult.Result.add(wEXCCallTaskBPM);
			}

			// ③剔除状态为0的单据
			wResult.Result.removeIf(p -> p.Status == 0);

			// 条件筛选
			if (wPartID > 0) {
				wResult.Result = wResult.Result.stream().filter(p -> p.StationID == wPartID)
						.collect(Collectors.toList());
			}
			if (wLevel > 0) {
				wResult.Result = wResult.Result.stream().filter(p -> p.RespondLevel == wLevel)
						.collect(Collectors.toList());
			}
			if (wExceptionType > 0) {
				wResult.Result = wResult.Result.stream().filter(p -> p.ExceptionTypeID == wExceptionType)
						.collect(Collectors.toList());
			}
			if (wStatus == 1) {
				wResult.Result = wResult.Result.stream().filter(p -> p.Status < 20).collect(Collectors.toList());
			} else if (wStatus == 2) {
				wResult.Result = wResult.Result.stream().filter(p -> p.Status >= 20).collect(Collectors.toList());
			}

			List<EXCCallTaskBPM> wList1 = wResult.Result.stream().filter(p -> p.Status == 20
					&& wStartTime.compareTo(p.SubmitTime) <= 0 && wEndTime.compareTo(p.CreateTime) >= 0)
					.collect(Collectors.toList());

			List<EXCCallTaskBPM> wList2 = wResult.Result.stream().filter(p -> p.Status != 20)
					.collect(Collectors.toList());

			wResult.Result.clear();
			wResult.Result.addAll(wList1);
			wResult.Result.addAll(wList2);

			wResult.Result.sort((o1, o2) -> o2.CreateTime.compareTo(o1.CreateTime));
			wResult.Result.sort((o1, o2) -> {
				if (o1.TagTypes == 1) {
					return -1;
				} else if (o2.TagTypes == 1) {
					return 1;
				}
				return 0;
			});
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<EXCCallTaskBPM>> EXC_QueryCallTaskBPMList(BMSEmployee wLoginUser, int wExceptionType,
			int wResonseLevel, String wPartNo, int wStationID, int wPlaceID, Calendar wStartTime, Calendar wEndTime,
			int wStatus) {
		ServiceResult<List<EXCCallTaskBPM>> wResult = new ServiceResult<List<EXCCallTaskBPM>>();
		wResult.Result = new ArrayList<EXCCallTaskBPM>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {

			switch (wStatus) {
			case 1:

				wResult.Result.addAll(EXCCallTaskBPMDAO.getInstance().SelectList(wLoginUser, wResonseLevel,
						wExceptionType, wPartNo, wStationID, wPlaceID,
						StringUtils.parseListArgs(EXCCallTaskBPMStatus.NomalClose.getValue()), null, wStartTime,
						wEndTime, wErrorCode));
				break;
			case 0:
				wResult.Result.addAll(EXCCallTaskBPMDAO.getInstance().SelectList(wLoginUser, wResonseLevel,
						wExceptionType, wPartNo, wStationID, wPlaceID, null,
						StringUtils.parseListArgs(EXCCallTaskBPMStatus.NomalClose.getValue(),
								EXCCallTaskBPMStatus.ExceptionClose.getValue(),
								EXCCallTaskBPMStatus.Canceled.getValue(), EXCCallTaskBPMStatus.Default.getValue()),
						wStartTime, wEndTime, wErrorCode));
				break;
			default:
				wResult.Result.addAll(EXCCallTaskBPMDAO.getInstance().SelectList(wLoginUser, wResonseLevel,
						wExceptionType, wPartNo, wStationID, wPlaceID, null, null, wStartTime, wEndTime, wErrorCode));

			}
			wResult.Result.sort((o1, o2) -> o2.SubmitTime.compareTo(o1.SubmitTime));

			if (wResult.Result.size() <= 0 || wStatus == 1) {
				return wResult;
			}
			List<BPMTaskBase> wBaseList = EXCCallTaskBPMDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.ID,
					wErrorCode);

			if (wBaseList == null || wBaseList.size() <= 0) {
				return wResult;
			}
			for (BPMTaskBase wTaskBase : wBaseList) {

				if (wTaskBase instanceof EXCCallTaskBPM) {
					EXCCallTaskBPM wRROTask = (EXCCallTaskBPM) wTaskBase;
					wRROTask.TagTypes = TagTypes.Dispatcher.getValue();
					for (int i = 0; i < wResult.Result.size(); i++) {
						if (wResult.Result.get(i).ID == wRROTask.ID)
							wResult.Result.set(i, wRROTask);
					}
				}

			}

			wResult.FaultCode += MESException.getEnumType(wErrorCode.get()).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}
}
