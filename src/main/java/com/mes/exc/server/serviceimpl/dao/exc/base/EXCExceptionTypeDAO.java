package com.mes.exc.server.serviceimpl.dao.exc.base;

import java.util.ArrayList;

import java.util.Calendar;
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

import com.mes.exc.server.serviceimpl.dao.BaseDAO;
import com.mes.exc.server.service.po.OutResult;
import com.mes.exc.server.service.po.ServiceResult;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.service.po.exc.base.EXCExceptionType;
import com.mes.exc.server.service.po.exc.define.TaskRelevancyTypes;
import com.mes.exc.server.service.mesenum.MESDBSource;
import com.mes.exc.server.service.utils.StringUtils;

/**
 * 异常类型
 * 
 * @author ShrisJava
 *
 */
public class EXCExceptionTypeDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(EXCExceptionTypeDAO.class);

	private static EXCExceptionTypeDAO Instance;

	/**
	 * 权限码
	 */
	private static int AccessCode = 710002;

	public List<EXCExceptionType> SelectAll(BMSEmployee wLoginUser, List<Long> wID, String wName, long wStationType,
			TaskRelevancyTypes wRelevancyTaskType, int wActive, OutResult<Integer> wErrorCode) {
		List<EXCExceptionType> wResult = new ArrayList<EXCExceptionType>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wID == null)
				wID = new ArrayList<Long>();

			if (wName == null)
				wName = "";
			String wSQL = StringUtils.Format(
					"SELECT exc_ex_type.ID,       exc_ex_type.Name, "
							+ "    exc_ex_type.StationType,   	exc_station_type.Name as StationTypeName, "
							+ "    exc_ex_type.DutyPositionID,       exc_ex_type.ConfirmPositionID, "
							+ "    exc_ex_type.ApproverPositionID,       exc_ex_type.RelevancyTaskType, "
							+ "    exc_ex_type.AgainInterval,        exc_ex_type.CreatorID, "
							+ "    exc_ex_type.CreateTime,   exc_ex_type.ModeType,      exc_ex_type.EditorID, "
							+ "    exc_ex_type.EditTime,       exc_ex_type.Active "
							+ "FROM {0}.exc_ex_type  ,{0}.exc_station_type  "
							+ "WHERE  exc_ex_type.StationType= exc_station_type.ID   "
							+ "and ( :wID is null or :wID = '''' or exc_ex_type.ID IN( {1} ) )    "
							+ "and ( :wName is null or :wName = '''' or exc_ex_type.Name =  :wName )  "
							+ "and ( :wStationType< 0 or exc_ex_type.StationType =:wStationType)  "
							+ "and ( :wRelevancyTaskType<= 0 or exc_ex_type.RelevancyTaskType =:wRelevancyTaskType)   "
							+ "and ( :wActive< 0 or exc_ex_type.Active =:wActive) ",
					wInstance.Result, wID.size() > 0 ? StringUtils.Join(",", wID) : "0");
			wSQL = this.DMLChange(wSQL);
			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", StringUtils.Join(",", wID));
			wParamMap.put("wRelevancyTaskType", wRelevancyTaskType.getValue());
			wParamMap.put("wName", wName);
			wParamMap.put("wStationType", wStationType);
			wParamMap.put("wActive", wActive);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
			// wReader\[\"(\w+)\"\]

			for (Map<String, Object> wReader : wQueryResult) {

				long wIDSql = StringUtils.parseLong(wReader.get("ID"));
				String wNameSql = StringUtils.parseString(wReader.get("Name"));
				long wStationTypeSql = StringUtils.parseLong(wReader.get("StationType"));
				String wStationTypeNameSql = StringUtils.parseString(wReader.get("StationTypeName"));
				String wDutyPositionIDSql = StringUtils.parseString(wReader.get("DutyPositionID"));
				int wConfirmPositionIDSql = StringUtils.parseInt(wReader.get("ConfirmPositionID"));
				int wApproverPositionIDSql = StringUtils.parseInt(wReader.get("ApproverPositionID"));
				int wRelevancyTaskTypeSql = StringUtils.parseInt(wReader.get("RelevancyTaskType"));
				int wAgainIntervalSql = StringUtils.parseInt(wReader.get("AgainInterval"));
				long wCreatorIDSql = StringUtils.parseLong(wReader.get("CreatorID"));
				Calendar wCreateTimeSql = StringUtils.parseCalendar(wReader.get("CreateTime"));
				long wEditorIDSql = StringUtils.parseLong(wReader.get("EditorID"));
				Calendar wEditTimeSql = StringUtils.parseCalendar(wReader.get("EditTime"));
				int wActiveSql = StringUtils.parseInt(wReader.get("Active"));
				int wModeTypeSql = StringUtils.parseInt(wReader.get("ModeType"));

				EXCExceptionType wEXCExecptionType = new EXCExceptionType();

				wEXCExecptionType.ID = wIDSql;
				wEXCExecptionType.Name = wNameSql;
				wEXCExecptionType.StationType = wStationTypeSql;
				wEXCExecptionType.StationTypeName = wStationTypeNameSql;
				wEXCExecptionType.DutyPositionID = StringUtils.parseIntList(wDutyPositionIDSql.split(","));
				wEXCExecptionType.ConfirmPositionID = wConfirmPositionIDSql;
				wEXCExecptionType.ApproverPositionID = wApproverPositionIDSql;
				wEXCExecptionType.RelevancyTaskType = wRelevancyTaskTypeSql;
				wEXCExecptionType.AgainInterval = wAgainIntervalSql;
				wEXCExecptionType.CreatorID = wCreatorIDSql;
				wEXCExecptionType.CreateTime = wCreateTimeSql;
				wEXCExecptionType.EditorID = wEditorIDSql;
				wEXCExecptionType.EditTime = wEditTimeSql;
				wEXCExecptionType.Active = wActiveSql;
				wEXCExecptionType.ModeType = wModeTypeSql;

				wResult.add(wEXCExecptionType);
			}

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public List<EXCExceptionType> SelectAll(BMSEmployee wLoginUser, String wName, long wStationType,
			TaskRelevancyTypes wRelevancyTaskType, int wActive, OutResult<Integer> wErrorCode) {
		List<EXCExceptionType> wResult = new ArrayList<EXCExceptionType>();
		try {
			wResult = SelectAll(wLoginUser, null, wName, wStationType, wRelevancyTaskType, wActive, wErrorCode);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public EXCExceptionType Select(BMSEmployee wLoginUser, long wID, OutResult<Integer> wErrorCode) {
		EXCExceptionType wResult = new EXCExceptionType();
		try {
			List<EXCExceptionType> wEXCExceptionTypeList = SelectAll(wLoginUser,
					StringUtils.parseList(new Long[] { wID }), "", -1, TaskRelevancyTypes.Default, -1, wErrorCode);
			if (wEXCExceptionTypeList.size() != 1)
				return wResult;

			wResult = wEXCExceptionTypeList.get(0);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;

	}

	public List<EXCExceptionType> SelectAll(BMSEmployee wLoginUser, List<Long> wIDList, OutResult<Integer> wErrorCode) {
		List<EXCExceptionType> wResult = new ArrayList<EXCExceptionType>();
		try {
			if (wIDList == null)
				wIDList = new ArrayList<Long>();

			List<Long> wSelectList = new ArrayList<Long>();
			for (int i = 0; i < wIDList.size(); i++) {
				wSelectList.add(wIDList.get(i));
				if (i % 25 == 0) {
					wResult.addAll(
							SelectAll(wLoginUser, wSelectList, "", 0, TaskRelevancyTypes.Default, -1, wErrorCode));

					wSelectList.clear();
				}
				if (i == wIDList.size() - 1) {
					if (wSelectList.size() > 0)
						wResult.addAll(
								SelectAll(wLoginUser, wSelectList, "", 0, TaskRelevancyTypes.Default, -1, wErrorCode));
					break;
				}
			}

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public long Update(BMSEmployee wLoginUser, EXCExceptionType wDeviceModel, OutResult<Integer> wErrorCode) {
		long wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wDeviceModel == null)
				return 0L;

			if (wDeviceModel.DutyPositionID == null)
				wDeviceModel.DutyPositionID = new ArrayList<Integer>();

			String wSQL = "";

			if (wDeviceModel.getID() <= 0) {

				List<EXCExceptionType> wEXCExceptionTypeList = this.SelectAll(wLoginUser, wDeviceModel.getName(),
						wDeviceModel.getStationType(), TaskRelevancyTypes.Default, -1, wErrorCode);
				if (wEXCExceptionTypeList.size() > 0) {

					wResult = wEXCExceptionTypeList.get(0).getID();
					wDeviceModel.setID(wEXCExceptionTypeList.get(0).getID());

					return wResult;
				}

				wSQL = StringUtils.Format(
						"INSERT INTO {0}.exc_ex_type   	( Name, "
								+ "	StationType, DutyPositionID, ConfirmPositionID, "
								+ "	ApproverPositionID,RelevancyTaskType, ModeType,   	AgainInterval,  "
								+ "	CreatorID, 	CreateTime,	EditorID,EditTime, Active) "
								+ "	VALUES  ( :wName , :wStationType, :wDutyPositionID, "
								+ "	:wConfirmPositionID, :wApproverPositionID, :wRelevancyTaskType ,:wModeType, "
								+ "	:wAgainInterval,   	:wCreatorID , now() , :wCreatorID,now() ,:wActive );",
						wInstance.Result);
			} else {
				wSQL = StringUtils.Format("UPDATE {0}.exc_ex_type   SET    Name = :wName, "
						+ "StationType = :wStationType,   DutyPositionID = :wDutyPositionID, ModeType=:wModeType,   "
						+ "ConfirmPositionID = :wConfirmPositionID,   ApproverPositionID = :wApproverPositionID, "
						+ "RelevancyTaskType = :wRelevancyTaskType,     AgainInterval = :wAgainInterval,   "
						+ "EditorID = :wEditorID,   EditTime = now(),   Active = :wActive    WHERE ID =:wID;",
						wInstance.Result);
			}
			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();
			wParamMap.put("wID", wDeviceModel.ID);
			wParamMap.put("wName", wDeviceModel.Name);
			wParamMap.put("wStationType", wDeviceModel.StationType);
			wParamMap.put("wDutyPositionID", StringUtils.Join(",", wDeviceModel.DutyPositionID));
			wParamMap.put("wApproverPositionID", wDeviceModel.ApproverPositionID);
			wParamMap.put("wConfirmPositionID", wDeviceModel.ConfirmPositionID);
			wParamMap.put("wRelevancyTaskType", wDeviceModel.RelevancyTaskType);
			wParamMap.put("wAgainInterval", wDeviceModel.AgainInterval);
			wParamMap.put("wCreatorID", wDeviceModel.CreatorID);
			wParamMap.put("wModeType", wDeviceModel.ModeType);
			wParamMap.put("wEditorID", wDeviceModel.EditorID);
			wParamMap.put("wActive", wDeviceModel.Active);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			wResult = nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);
			if (wDeviceModel.getID() <= 0) {
				wResult = keyHolder.getKey().longValue();
				wDeviceModel.setID(wResult);
			} else {
				wResult = wDeviceModel.getID();
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wDeviceModel.ID;
	}

	public void Active(BMSEmployee wLoginUser, List<Long> wIDList, int wActive, OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			if (wIDList == null)
				wIDList = new ArrayList<Long>();
			wIDList = wIDList.stream().filter(p -> p > 0).collect(Collectors.toList());

			if (wIDList.size() <= 0)
				return;

			if (wActive < 0 || wActive > 1)
				return;
			String wSql = StringUtils.Format("UPDATE {0}.exc_ex_type SET Active ={1} WHERE ID IN({2}) ;",
					wInstance.Result, String.valueOf(wActive), StringUtils.Join(",", wIDList));

			ExecuteSqlTransaction(wSql);
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	private EXCExceptionTypeDAO() {
		super();
	}

	public static EXCExceptionTypeDAO getInstance() {
		if (Instance == null)
			Instance = new EXCExceptionTypeDAO();
		return Instance;
	}

}
