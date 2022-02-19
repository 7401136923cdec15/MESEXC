package com.mes.exc.server.serviceimpl.dao.exc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.mes.exc.server.service.mesenum.BFCMessageType;
import com.mes.exc.server.service.mesenum.BPMEventModule;
import com.mes.exc.server.service.mesenum.MESDBSource;
import com.mes.exc.server.service.mesenum.MESException;
import com.mes.exc.server.service.po.OutResult;
import com.mes.exc.server.service.po.ServiceResult;
import com.mes.exc.server.service.po.bfc.BFCMessage;
import com.mes.exc.server.service.po.bms.BMSDepartment;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.service.po.bpm.BPMTaskBase;
import com.mes.exc.server.service.po.exc.EXCCallTaskBPM;
import com.mes.exc.server.service.po.exc.EXCOptionItem;
import com.mes.exc.server.service.utils.CloneTool;
import com.mes.exc.server.service.utils.StringUtils;
import com.mes.exc.server.serviceimpl.CoreServiceImpl;
import com.mes.exc.server.serviceimpl.EXCServiceImpl;
import com.mes.exc.server.serviceimpl.dao.BaseDAO;
import com.mes.exc.server.serviceimpl.dao.TaskBaseDAO;
import com.mes.exc.server.serviceimpl.utils.exc.EXCConstants;

/**
 * 异常呼叫(流程引擎版)
 * 
 * @author ShrisJava
 *
 */
public class EXCCallTaskBPMDAO extends BaseDAO implements TaskBaseDAO {

	private static Logger logger = LoggerFactory.getLogger(EXCCallTaskBPMDAO.class);

	private static EXCCallTaskBPMDAO Instance = null;

	private EXCCallTaskBPMDAO() {
		super();
	}

	public static EXCCallTaskBPMDAO getInstance() {
		if (Instance == null)
			Instance = new EXCCallTaskBPMDAO();
		return Instance;
	}

	/**
	 * 添加或修改
	 * 
	 * @param wEXCCallTaskBPM
	 * @return
	 */
	public EXCCallTaskBPM Update(BMSEmployee wLoginUser, EXCCallTaskBPM wEXCCallTaskBPM,
			OutResult<Integer> wErrorCode) {
		EXCCallTaskBPM wResult = new EXCCallTaskBPM();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wEXCCallTaskBPM == null)
				return wResult;

			if (wEXCCallTaskBPM.FollowerID == null) {
				wEXCCallTaskBPM.FollowerID = new ArrayList<Integer>();
			}

			String wSQL = "";
			if (wEXCCallTaskBPM.getID() <= 0) {
				wSQL = StringUtils.Format(
						"INSERT INTO {0}.exc_calltaskbpm(Code,FlowType,FlowID,UpFlowID,FollowerID,Status,"
								+ "StatusText,CreateTime,SubmitTime,StationType,StationID,ExceptionTypeID,"
								+ "ApplicantID,ConfirmID,OperatorID,ApplicantTime,RespondLevel,OnSite,DisplayBoard,"
								+ "EditTime,Remark,Comment,ImageList,ReportTimes,ForwardTimes,ShiftID,PartNo,PlaceID,ExpireTime) "
								+ "VALUES(:Code,:FlowType,:FlowID,:UpFlowID,:FollowerID,:Status,:StatusText,now(),"
								+ ":SubmitTime,:StationType,:StationID,:ExceptionTypeID,:ApplicantID,:ConfirmID,:OperatorID,"
								+ ":ApplicantTime,:RespondLevel,:OnSite,:DisplayBoard,:EditTime,:Remark,:Comment,:ImageList,"
								+ ":ReportTimes,:ForwardTimes,:ShiftID,:PartNo,:PlaceID,:ExpireTime);",
						wInstance.Result);
			} else {
				wSQL = StringUtils.Format("UPDATE {0}.exc_calltaskbpm SET Code = :Code,FlowType = :FlowType,"
						+ "FlowID = :FlowID,UpFlowID = :UpFlowID,FollowerID = :FollowerID,Status = :Status,"
						+ "StatusText = :StatusText,CreateTime=:CreateTime,SubmitTime = now(),StationType = :StationType,"
						+ "StationID = :StationID,ExceptionTypeID = :ExceptionTypeID,ApplicantID = :ApplicantID,"
						+ "ConfirmID = :ConfirmID,OperatorID = :OperatorID,ApplicantTime = :ApplicantTime,RespondLevel = :RespondLevel,"
						+ "OnSite = :OnSite,DisplayBoard = :DisplayBoard,EditTime = now(),Remark = :Remark,Comment = :Comment,"
						+ "ImageList = :ImageList,ReportTimes = :ReportTimes,ForwardTimes = :ForwardTimes,ShiftID = :ShiftID,"
						+ "PartNo = :PartNo,PlaceID = :PlaceID,ExpireTime=:ExpireTime WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wEXCCallTaskBPM.ID);
			wParamMap.put("Code", wEXCCallTaskBPM.Code);
			wParamMap.put("FlowType", wEXCCallTaskBPM.FlowType);
			wParamMap.put("FlowID", wEXCCallTaskBPM.FlowID);
			wParamMap.put("UpFlowID", wEXCCallTaskBPM.UpFlowID);
			wParamMap.put("FollowerID", StringUtils.Join(",", wEXCCallTaskBPM.FollowerID));
			wParamMap.put("Status", wEXCCallTaskBPM.Status);
			wParamMap.put("StatusText", wEXCCallTaskBPM.StatusText);
			wParamMap.put("CreateTime", wEXCCallTaskBPM.CreateTime);
			wParamMap.put("SubmitTime", wEXCCallTaskBPM.SubmitTime);
			wParamMap.put("StationType", wEXCCallTaskBPM.StationType);
			wParamMap.put("StationID", wEXCCallTaskBPM.StationID);
			wParamMap.put("ExceptionTypeID", wEXCCallTaskBPM.ExceptionTypeID);
			wParamMap.put("ApplicantID", wEXCCallTaskBPM.ApplicantID);
			wParamMap.put("ConfirmID", wEXCCallTaskBPM.ConfirmID);
			wParamMap.put("OperatorID", wEXCCallTaskBPM.OperatorID);
			wParamMap.put("ApplicantTime", wEXCCallTaskBPM.ApplicantTime);
			wParamMap.put("RespondLevel", wEXCCallTaskBPM.RespondLevel);
			wParamMap.put("OnSite", wEXCCallTaskBPM.OnSite);
			wParamMap.put("DisplayBoard", wEXCCallTaskBPM.DisplayBoard ? 1 : 0);
			wParamMap.put("EditTime", wEXCCallTaskBPM.EditTime);
			wParamMap.put("Remark", wEXCCallTaskBPM.Remark);
			wParamMap.put("Comment", wEXCCallTaskBPM.Comment);
			wParamMap.put("ImageList", wEXCCallTaskBPM.ImageList);
			wParamMap.put("ReportTimes", wEXCCallTaskBPM.ReportTimes);
			wParamMap.put("ForwardTimes", wEXCCallTaskBPM.ForwardTimes);
			wParamMap.put("ShiftID", wEXCCallTaskBPM.ShiftID);
			wParamMap.put("PartNo", wEXCCallTaskBPM.PartNo);
			wParamMap.put("PlaceID", wEXCCallTaskBPM.PlaceID);
			wParamMap.put("ExpireTime", wEXCCallTaskBPM.ExpireTime);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wEXCCallTaskBPM.getID() <= 0) {
				wEXCCallTaskBPM.setID(keyHolder.getKey().intValue());
			}
			wResult = wEXCCallTaskBPM;
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	public List<EXCCallTaskBPM> SelectListHistory(BMSEmployee wLoginUser, int wID, String wCode, int wUpFlowID,
			int wShiftID, String wPartNo, int wPlaceID, Calendar wStartTime, Calendar wEndTime,
			List<Integer> wStateIDList, int wResonseLevel, int wExceptionType, OutResult<Integer> wErrorCode) {
		return this.SelectList(wLoginUser, wID, wCode, wUpFlowID, wShiftID, wPartNo, -1, wPlaceID, wStartTime, wEndTime,
				wStateIDList, null, wResonseLevel, wExceptionType, wErrorCode);
	}

	public List<EXCCallTaskBPM> SelectList(BMSEmployee wLoginUser, int wResonseLevel, int wExceptionType,
			String wPartNo, int wStationID, int wPlaceID, List<Integer> wStateIDList, List<Integer> wNoStateIDList,
			Calendar wStartTime, Calendar wEndTime, OutResult<Integer> wErrorCode) {
		return this.SelectList(wLoginUser, -1, "", -1, -1, wPartNo, wStationID, wPlaceID, wStartTime, wEndTime,
				wStateIDList, null, wResonseLevel, wExceptionType, wErrorCode);
	}

	/**
	 * 条件查询集合
	 * 
	 * @return
	 */
	public List<EXCCallTaskBPM> SelectList(BMSEmployee wLoginUser, int wID, String wCode, int wUpFlowID, int wShiftID,
			String wPartNo, int wPlaceID, Calendar wStartTime, Calendar wEndTime, List<Integer> wStateIDList,
			OutResult<Integer> wErrorCode) {
		List<EXCCallTaskBPM> wResultList = new ArrayList<EXCCallTaskBPM>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 0, 1, 0, 0, 0);
			if (wStartTime == null) {
				wStartTime = wBaseTime;
			}
			if (wEndTime == null) {
				wEndTime = wBaseTime;
			}
			if (wStartTime.compareTo(wEndTime) > 0) {
				return wResultList;
			}

			if (wStateIDList == null) {
				wStateIDList = new ArrayList<Integer>();
			}

			String wSQL = StringUtils.Format(
					"SELECT t.*,t1.StationNo,t1.StationName FROM {0}.exc_calltaskbpm t left join {0}.exc_station t1 on t.StationID=t1.ID  WHERE  1=1  "
							+ "and ( :wID <= 0 or :wID = t.ID ) " + "and (:wCode = '''' or :wCode = t.Code ) "
							+ "and ( :wUpFlowID <= 0 or :wUpFlowID = t.UpFlowID ) "
							+ "and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <=  t.SubmitTime ) "
							+ "and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >=  t.CreateTime ) "
							+ "and ( :wShiftID <= 0 or :wShiftID = t.ShiftID ) "
							+ "and ( :wPartNo = '''' or :wPartNo = t.PartNo ) "
							+ "and ( :wPlaceID <= 0 or :wPlaceID = t.PlaceID ) "
							+ "and ( :wStatus = '''' or t.Status in ({1}));",
					wInstance.Result, wStateIDList.size() > 0 ? StringUtils.Join(",", wStateIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wCode", wCode);
			wParamMap.put("wUpFlowID", wUpFlowID);
			wParamMap.put("wShiftID", wShiftID);
			wParamMap.put("wPartNo", wPartNo);
			wParamMap.put("wPlaceID", wPlaceID);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wStatus", StringUtils.Join(",", wStateIDList));

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wLoginUser, wResultList, wQueryResult, wErrorCode);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 条件查询集合
	 * 
	 * @return
	 */
	private List<EXCCallTaskBPM> SelectList(BMSEmployee wLoginUser, int wID, String wCode, int wUpFlowID, int wShiftID,
			String wPartNo, int wStationID, int wPlaceID, Calendar wStartTime, Calendar wEndTime,
			List<Integer> wStateIDList, List<Integer> wNoStateIDList, int wResonseLevel, int wExceptionType,
			OutResult<Integer> wErrorCode) {
		List<EXCCallTaskBPM> wResultList = new ArrayList<EXCCallTaskBPM>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 0, 1, 0, 0, 0);
			if (wStartTime == null) {
				wStartTime = wBaseTime;
			}
			if (wEndTime == null) {
				wEndTime = wBaseTime;
			}
			if (wStartTime.compareTo(wEndTime) > 0) {
				return wResultList;
			}

			if (wStateIDList == null) {
				wStateIDList = new ArrayList<Integer>();
			}
			wStateIDList.removeIf(p -> p < 0);
			if (wNoStateIDList == null) {
				wNoStateIDList = new ArrayList<Integer>();
			}
			wNoStateIDList.removeIf(p -> p < 0);
			String wSQL = StringUtils.Format(
					"SELECT t.*,t1.StationNo,t1.StationName FROM {0}.exc_calltaskbpm t left join {0}.exc_station t1 on t.StationID=t1.ID  WHERE  1=1  "
							+ " and ( :wID <= 0 or :wID = t.ID ) "
							+ " and ( :wCode is null or :wCode = '''' or :wCode = t.Code ) "
							+ " and ( :wUpFlowID <= 0 or :wUpFlowID = t.UpFlowID ) "
							+ " and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <=  t.SubmitTime ) "
							+ " and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >=  t.CreateTime ) "
							+ " and ( :wShiftID <= 0 or :wShiftID = t.ShiftID ) "
							+ " and ( :wPartNo = '''' or :wPartNo = t.PartNo ) "
							+ " and ( :wStationID <= 0 or :wStationID = t.StationID ) "
							+ " and ( :wPlaceID <= 0 or :wPlaceID = t.PlaceID ) "
							+ " and ( :wResonseLevel <= 0 or :wResonseLevel = t.RespondLevel ) "
							+ " and ( :wExceptionType <= 0 or :wExceptionType = t.ExceptionTypeID ) "
							+ " and ( :wStatus = '''' or t.Status in ({1}))  and ( :wNoStatus = '''' or t.Status not in ({2}));",
					wInstance.Result, wStateIDList.size() > 0 ? StringUtils.Join(",", wStateIDList) : "0",
					wNoStateIDList.size() > 0 ? StringUtils.Join(",", wNoStateIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wCode", wCode);
			wParamMap.put("wUpFlowID", wUpFlowID);
			wParamMap.put("wShiftID", wShiftID);
			wParamMap.put("wPartNo", wPartNo);
			wParamMap.put("wPlaceID", wPlaceID);
			wParamMap.put("wStationID", wStationID);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wResonseLevel", wResonseLevel);
			wParamMap.put("wExceptionType", wExceptionType);
			wParamMap.put("wStatus", StringUtils.Join(",", wStateIDList));
			wParamMap.put("wNoStatus", StringUtils.Join(",", wNoStateIDList));

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wLoginUser, wResultList, wQueryResult, wErrorCode);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 赋值
	 */
	private void SetValue(BMSEmployee wLoginUser, List<EXCCallTaskBPM> wResultList,
			List<Map<String, Object>> wQueryResult, OutResult<Integer> wErrorCode) {
		try {
			for (Map<String, Object> wReader : wQueryResult) {
				EXCCallTaskBPM wItem = new EXCCallTaskBPM();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.Code = StringUtils.parseString(wReader.get("Code"));
				wItem.FlowType = StringUtils.parseInt(wReader.get("FlowType"));
				wItem.FlowID = StringUtils.parseInt(wReader.get("FlowID"));
				wItem.UpFlowID = StringUtils.parseInt(wReader.get("UpFlowID"));
				wItem.FollowerID = StringUtils
						.parseIntList(StringUtils.parseString(wReader.get("FollowerID")).split(",|;"));
				wItem.Status = StringUtils.parseInt(wReader.get("Status"));
				wItem.StatusText = StringUtils.parseString(wReader.get("StatusText"));
				wItem.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wItem.SubmitTime = StringUtils.parseCalendar(wReader.get("SubmitTime"));
				wItem.StationType = StringUtils.parseInt(wReader.get("StationType"));
				wItem.StationID = StringUtils.parseInt(wReader.get("StationID"));
				wItem.ExceptionTypeID = StringUtils.parseInt(wReader.get("ExceptionTypeID"));
				wItem.ApplicantID = StringUtils.parseInt(wReader.get("ApplicantID"));
				wItem.ConfirmID = StringUtils.parseInt(wReader.get("ConfirmID"));
				wItem.OperatorID = StringUtils.parseString(wReader.get("OperatorID"));
				wItem.ApplicantTime = StringUtils.parseCalendar(wReader.get("ApplicantTime"));
				wItem.RespondLevel = StringUtils.parseInt(wReader.get("RespondLevel"));
				wItem.OnSite = StringUtils.parseInt(wReader.get("OnSite"));
				wItem.DisplayBoard = StringUtils.parseInt(wReader.get("DisplayBoard")) == 1 ? true : false;
				wItem.EditTime = StringUtils.parseCalendar(wReader.get("EditTime"));
				wItem.Remark = StringUtils.parseString(wReader.get("Remark"));
				wItem.Comment = StringUtils.parseString(wReader.get("Comment"));
				wItem.ImageList = StringUtils.parseString(wReader.get("ImageList"));
				wItem.ReportTimes = StringUtils.parseInt(wReader.get("ReportTimes"));
				wItem.ForwardTimes = StringUtils.parseInt(wReader.get("ForwardTimes"));
				wItem.ShiftID = StringUtils.parseInt(wReader.get("ShiftID"));
				wItem.PartNo = StringUtils.parseString(wReader.get("PartNo"));
				wItem.PlaceID = StringUtils.parseInt(wReader.get("PlaceID"));
				wItem.ExpireTime = StringUtils.parseCalendar(wReader.get("ExpireTime"));
				wItem.StationNo = StringUtils.parseString(wReader.get("StationNo"));
				wItem.StationName = StringUtils.parseString(wReader.get("StationName"));
				// 辅助属性
				wItem.UpFlowName = EXCConstants.GetBMSEmployeeName(wItem.UpFlowID);
				wItem.FollowerName = EXCConstants.GetBMSDepartmentName(wItem.FollowerID);

				wItem.StationTypeName = EXCConstants.GetEXCStationTypeName(wItem.StationType);

				wItem.ExceptionTypeName = EXCConstants.GetEXCExceptionTypeName(wItem.ExceptionTypeID);
				wItem.Operators = this.GetNames(StringUtils.parseIntList(wItem.OperatorID.split(",|;")));
				wItem.PlaceNo = "";
				wItem.ApplyName = EXCConstants.GetBMSEmployeeName((int) wItem.ApplicantID);
				wItem.ConfirmName = StringUtils.Format("{0}({1})",
						EXCConstants.GetBMSEmployeeName((int) wItem.ConfirmID), EXCConstants
								.GetBMSDepartmentName(EXCConstants.GetBMSEmployee((int) wItem.ConfirmID).DepartmentID));
				wItem.RespondLevelName = this.GetResponseLevelName(wLoginUser, wItem.RespondLevel);

				if (wItem.StationID > 0 && StringUtils.isEmpty(wItem.StationNo)
						&& StringUtils.isEmpty(wItem.StationName)) {

					wItem.StationName = StringUtils.Format("{0}【{1}】", wItem.StationName, wItem.StationNo);

				}

				if (wItem.PlaceID > 0) {
					wItem.PlaceNo = StringUtils.Format("{0}【{1}】", EXCConstants.GetFMCWorkspace(wItem.PlaceID).Name,
							EXCConstants.GetFMCWorkspace(wItem.PlaceID).Code);
				}

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 获取响应级别
	 */
	private String GetResponseLevelName(BMSEmployee wLoginUser, int respondLevel) {
		String wResult = "";
		try {
			List<EXCOptionItem> wList = EXCServiceImpl.getInstance().EXC_GetRespondLevelList(wLoginUser).Result;
			if (wList == null || wList.size() <= 0 || !wList.stream().anyMatch(p -> p.ID == respondLevel)) {
				return wResult;
			}

			wResult = wList.stream().filter(p -> p.ID == respondLevel).findFirst().get().Name;
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取处理人名称(多人)
	 */
	private String GetNames(List<Integer> wIDList) {
		String wResult = "";
		try {
			if (wIDList == null || wIDList.size() <= 0) {
				return wResult;
			}

			List<String> wNames = new ArrayList<String>();
			wIDList.forEach(p -> {
				BMSEmployee wEmp = EXCConstants.GetBMSEmployee(p);
				if (wEmp != null && wEmp.ID > 0) {
					String wDepName = "";
					BMSDepartment wDep = EXCConstants.GetBMSDepartment(wEmp.DepartmentID);
					if (wDep != null && wDep.ID > 0) {
						wDepName = wDep.Name;
					}
					wNames.add(StringUtils.Format("{0}({1})", wEmp.Name, wDepName));
				}
			});

			if (wNames.size() > 0) {
				wResult = StringUtils.Join(",", wNames);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * ID集合获取任务集合
	 */
	private List<EXCCallTaskBPM> SelectList(BMSEmployee wLoginUser, List<Integer> wTaskIDList, Calendar wStartTime,
			Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<EXCCallTaskBPM> wResultList = new ArrayList<EXCCallTaskBPM>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			if (wTaskIDList == null || wTaskIDList.size() <= 0) {
				return wResultList;
			}

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 0, 1, 0, 0, 0);
			if (wStartTime == null) {
				wStartTime = wBaseTime;
			}
			if (wEndTime == null) {
				wEndTime = wBaseTime;
			}
			if (wStartTime.compareTo(wEndTime) > 0) {
				return wResultList;
			}

			String wSQL = StringUtils.Format(
					"SELECT t.*,t1.StationNo,t1.StationName FROM {0}.exc_calltaskbpm t left join {0}.exc_station t1 on t.StationID=t1.ID  WHERE  1=1  "

							+ "and ( :wStartTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or :wStartTime <= t.SubmitTime) "
							+ "and ( :wEndTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or :wEndTime >= t.CreateTime) "

							+ "and ( :wIDs = '''' or t.ID in ({1}));",
					wInstance.Result, wTaskIDList.size() > 0 ? StringUtils.Join(",", wTaskIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);

			wParamMap.put("wIDs", StringUtils.Join(",", wTaskIDList));

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wLoginUser, wResultList, wQueryResult, wErrorCode);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 获取最新的编码
	 */
	public String GetNewCode(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		String wResult = "";
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			// 本月时间
			int wYear = Calendar.getInstance().get(Calendar.YEAR);
			int wMonth = Calendar.getInstance().get(Calendar.MONTH);
			Calendar wSTime = Calendar.getInstance();
			wSTime.set(wYear, wMonth, 1, 0, 0, 0);
			Calendar wETime = Calendar.getInstance();
			wETime.set(wYear, wMonth + 1, 1, 23, 59, 59);
			wETime.add(Calendar.DATE, -1);

			String wSQL = StringUtils.Format(
					"select count(*)+1 as Number from {0}.exc_calltaskbpm where CreateTime > :wSTime and CreateTime < :wETime;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();
			wParamMap.put("wSTime", wSTime);
			wParamMap.put("wETime", wETime);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			int wNumber = 0;
			for (Map<String, Object> wReader : wQueryResult) {
				if (wReader.containsKey("Number")) {
					wNumber = StringUtils.parseInt(wReader.get("Number"));
					break;
				}
			}

			wResult = StringUtils.Format("EX{0}{1}{2}", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)),
					String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1),
					String.format("%04d", wNumber));
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public List<BPMTaskBase> BPM_GetUndoTaskList(BMSEmployee wLoginUser, int wResponsorID,
			OutResult<Integer> wErrorCode) {
		List<EXCCallTaskBPM> wResult = new ArrayList<EXCCallTaskBPM>();
		try {
			// 获取所有任务消息
			List<BFCMessage> wMessageList = CoreServiceImpl.getInstance()
					.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.SCCall.getValue(), -1,
							BFCMessageType.Task.getValue(), 0, -1, null, null)
					.List(BFCMessage.class);
			wMessageList.addAll(CoreServiceImpl.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
					BPMEventModule.SCCall.getValue(), -1, BFCMessageType.Task.getValue(), 1, -1, null, null)
					.List(BFCMessage.class));
			wMessageList.addAll(CoreServiceImpl.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
					BPMEventModule.SCCall.getValue(), -1, BFCMessageType.Task.getValue(), 2, -1, null, null)
					.List(BFCMessage.class));

			List<Integer> wTaskIDList = wMessageList.stream().map(p -> (int) p.MessageID).distinct()
					.collect(Collectors.toList());

			// 所有未完成的任务
			Map<Integer, EXCCallTaskBPM> wTaskMap = new HashMap<Integer, EXCCallTaskBPM>();
			if (wTaskIDList != null && wTaskIDList.size() > 0) {
				List<EXCCallTaskBPM> wMTCTaskListTemp = this.SelectList(wLoginUser, wTaskIDList, null, null,
						wErrorCode);

				wTaskMap = wMTCTaskListTemp.stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));

			}
			EXCCallTaskBPM wTaskTemp = null;
			for (BFCMessage wBFCMessage : wMessageList) {
				if (!wTaskMap.containsKey((int) wBFCMessage.getMessageID()))
					continue;

				wTaskTemp = CloneTool.Clone(wTaskMap.get((int) wBFCMessage.getMessageID()), EXCCallTaskBPM.class);
				wTaskTemp.StepID = wBFCMessage.getStepID();
				wResult.add(wTaskTemp);
			}

			wResult.sort(Comparator.comparing(EXCCallTaskBPM::getSubmitTime).reversed());
			// 剔除任务状态为0的任务（废弃任务）
			if (wResult != null && wResult.size() > 0) {
				wResult = wResult.stream().filter(p -> p.Status != 0).collect(Collectors.toList());
			}
		} catch (Exception e) {
			wErrorCode.set(MESException.Exception.getValue());
			logger.error(e.toString());
		}
		return new ArrayList<BPMTaskBase>(wResult);
	}

	@Override
	public List<BPMTaskBase> BPM_GetDoneTaskList(BMSEmployee wLoginUser, int wResponsorID, Calendar wStartTime,
			Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<EXCCallTaskBPM> wResult = new ArrayList<EXCCallTaskBPM>();
		wErrorCode.set(0);
		try {
			List<EXCCallTaskBPM> wTaskList = new ArrayList<EXCCallTaskBPM>();
			// 获取所有任务消息
			List<BFCMessage> wMessageList = CoreServiceImpl.getInstance()
					.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.SCCall.getValue(), -1,
							BFCMessageType.Task.getValue(), 3, -1, wStartTime, wEndTime)
					.List(BFCMessage.class);
			wMessageList
					.addAll(CoreServiceImpl.getInstance()
							.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.SCCall.getValue(), -1,
									BFCMessageType.Task.getValue(), 4, -1, wStartTime, wEndTime)
							.List(BFCMessage.class));

			List<Integer> wTaskIDList = wMessageList.stream().map(p -> (int) p.MessageID).distinct()
					.collect(Collectors.toList());

			wTaskList = this.SelectList(wLoginUser, wTaskIDList, wStartTime, wEndTime, wErrorCode);

			wTaskList.sort(Comparator.comparing(EXCCallTaskBPM::getSubmitTime).reversed());

			wResult = wTaskList;
			// 剔除任务状态为0的任务（废弃任务）
			if (wResult != null && wResult.size() > 0) {
				wResult = wResult.stream().filter(p -> p.Status != 0).collect(Collectors.toList());
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return new ArrayList<BPMTaskBase>(wResult);
	}

	@Override
	public List<BPMTaskBase> BPM_GetSendTaskList(BMSEmployee wLoginUser, int wResponsorID, Calendar wStartTime,
			Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<EXCCallTaskBPM> wResult = new ArrayList<EXCCallTaskBPM>();
		try {
			wResult = this.SelectList(wLoginUser, -1, "", wResponsorID, -1, "", -1, wStartTime, wEndTime, null,
					wErrorCode);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return new ArrayList<BPMTaskBase>(wResult);
	}

	@Override
	public BPMTaskBase BPM_UpdateTask(BMSEmployee wLoginUser, BPMTaskBase wTask, OutResult<Integer> wErrorCode) {
		BPMTaskBase wResult = new BPMTaskBase();
		try {
			wResult = this.Update(wLoginUser, (EXCCallTaskBPM) wTask, wErrorCode);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public BPMTaskBase BPM_GetTaskInfo(BMSEmployee wLoginUser, int wTaskID, String wCode,
			OutResult<Integer> wErrorCode) {
		EXCCallTaskBPM wResult = new EXCCallTaskBPM();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<EXCCallTaskBPM> wList = this.SelectList(wLoginUser, wTaskID, wCode, -1, -1, "", -1, null, null, null,
					wErrorCode);
			if (wList == null || wList.size() != 1)
				return wResult;
			wResult = wList.get(0);
		} catch (Exception e) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(e.toString());
		}
		return wResult;
	}
}
