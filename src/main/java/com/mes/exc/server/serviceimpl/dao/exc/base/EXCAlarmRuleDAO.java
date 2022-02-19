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
import com.mes.exc.server.service.po.exc.base.EXCAlarmRule;
import com.mes.exc.server.service.mesenum.MESDBSource;
import com.mes.exc.server.service.utils.StringUtils;

/**
 * 异常预警规则
 * 
 * @author ShrisJava
 *
 */
public class EXCAlarmRuleDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(EXCAlarmRuleDAO.class);

	private static EXCAlarmRuleDAO Instance;

	/**
	 * 权限码
	 */
	private static int AccessCode = 710004;

	public List<EXCAlarmRule> SelectAll(BMSEmployee wLoginUser, List<Long> wID, long wAlarmID, String wAlarmCode,
			long wStationType, int wRespondLevel, int wActive, OutResult<Integer> wErrorCode) {
		List<EXCAlarmRule> wResult = new ArrayList<EXCAlarmRule>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wID == null)
				wID = new ArrayList<Long>();

			String wSQL = StringUtils.Format("SELECT exc_alarm_rule.ID,      exc_alarm_rule.AlarmID, "
					+ "    exc_alarm_rule.AlarmCode,      exc_alarm_rule.AlarmText, "
					+ "    exc_alarm_rule.StationType,      exc_alarm_rule.StationID, "
					+ "    exc_station.StationNo,      exc_alarm_rule.ExceptionTypeList, "
					+ "    exc_alarm_rule.RespondLevel,      exc_alarm_rule.CreatorID, "
					+ "    exc_alarm_rule.CreateTime,      exc_alarm_rule.EditorID, "
					+ "    exc_alarm_rule.EditTime,      exc_alarm_rule.Active "
					+ "FROM {0}.exc_alarm_rule ,{0}.exc_station  "
					+ "WHERE  exc_alarm_rule.StationID= exc_station.ID   "
					+ "and ( :wID is null or :wID = '''' or exc_alarm_rule.ID IN( {1} ) )   "
					+ "and ( :wAlarmCode is null or :wAlarmCode = '''' or exc_alarm_rule.AlarmCode =  :wAlarmCode )   "
					+ "and ( :wAlarmID<= 0 or exc_alarm_rule.AlarmID =:wAlarmID)  "
					+ "and ( :wStationType< 0 or exc_alarm_rule.StationType =:wStationType)  "
					+ "and ( :wRespondLevel<= 0 or exc_alarm_rule.RespondLevel =:wRespondLevel)   "
					+ "and ( :wActive< 0 or exc_alarm_rule.Active =:wActive)   ", wInstance.Result,
					wID.size() > 0 ? StringUtils.Join(",", wID) : "0");
			wSQL = this.DMLChange(wSQL);
			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", StringUtils.Join(",", wID));
			wParamMap.put("wAlarmID", wAlarmID);
			wParamMap.put("wAlarmCode", wAlarmCode);
			wParamMap.put("wStationType", wStationType);
			wParamMap.put("wRespondLevel", wRespondLevel);
			wParamMap.put("wActive", wActive);
			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
			// wReader\[\"(\w+)\"\]
			for (Map<String, Object> wReader : wQueryResult) {

				long wIDSql = StringUtils.parseLong(wReader.get("ID"));
				long wAlarmIDSql = StringUtils.parseLong(wReader.get("AlarmID"));
				String wAlarmCodeSql = StringUtils.parseString(wReader.get("AlarmCode"));
				String wAlarmTextSql = StringUtils.parseString(wReader.get("AlarmText"));
				long wStationTypeSql = StringUtils.parseLong(wReader.get("StationType"));
				long wStationIDSql = StringUtils.parseLong(wReader.get("StationID"));
				String wStationNoSql = StringUtils.parseString(wReader.get("StationNo"));
				String wExceptionTypeListSql = StringUtils.parseString(wReader.get("ExceptionTypeList"));
				int wRespondLevelSql = StringUtils.parseInt(wReader.get("RespondLevel"));
				long wCreatorIDSql = StringUtils.parseLong(wReader.get("CreatorID"));
				Calendar wCreateTimeSql = StringUtils.parseCalendar(wReader.get("CreateTime"));
				long wEditorIDSql = StringUtils.parseLong(wReader.get("EditorID"));
				Calendar wEditTimeSql = StringUtils.parseCalendar(wReader.get("EditTime"));
				int wActiveSql = StringUtils.parseInt(wReader.get("Active"));

				EXCAlarmRule wDevicePropertyModel = new EXCAlarmRule();

				wDevicePropertyModel.ID = wIDSql;
				wDevicePropertyModel.AlarmID = wAlarmIDSql;
				wDevicePropertyModel.AlarmCode = wAlarmCodeSql;
				wDevicePropertyModel.AlarmText = wAlarmTextSql;
				wDevicePropertyModel.StationType = wStationTypeSql;
				wDevicePropertyModel.StationID = wStationIDSql;
				wDevicePropertyModel.StationNo = wStationNoSql;
				wDevicePropertyModel.RespondLevel = wRespondLevelSql;
				wDevicePropertyModel.ExceptionTypeList = StringUtils.parseLongList(wExceptionTypeListSql.split(","));
				wDevicePropertyModel.CreatorID = wCreatorIDSql;
				wDevicePropertyModel.CreateTime = wCreateTimeSql;
				wDevicePropertyModel.EditorID = wEditorIDSql;
				wDevicePropertyModel.EditTime = wEditTimeSql;
				wDevicePropertyModel.Active = wActiveSql;

				wResult.add(wDevicePropertyModel);
			}

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public List<EXCAlarmRule> SelectAll(BMSEmployee wLoginUser, long wStationType, int wRespondLevel, int wActive,
			OutResult<Integer> wErrorCode) {
		List<EXCAlarmRule> wResult = new ArrayList<EXCAlarmRule>();
		try {
			wResult = SelectAll(wLoginUser, null, -1, "", wStationType, wRespondLevel, wActive, wErrorCode);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public EXCAlarmRule Select(BMSEmployee wLoginUser, long wID, long wAlarmID, String wAlarmCode,
			OutResult<Integer> wErrorCode) {
		EXCAlarmRule wResult = new EXCAlarmRule();
		try {
			List<EXCAlarmRule> wEXCAlarmRuleList = SelectAll(wLoginUser, StringUtils.parseList(new Long[] { wID }),
					wAlarmID, wAlarmCode, -1, -1, -1, wErrorCode);
			if (wEXCAlarmRuleList.size() != 1)
				return wResult;

			wResult = wEXCAlarmRuleList.get(0);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;

	}

	public long Update(BMSEmployee wLoginUser, EXCAlarmRule wEXCAlarmRule, OutResult<Integer> wErrorCode) {
		long wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wEXCAlarmRule == null)
				return 0L;

			if (wEXCAlarmRule.ExceptionTypeList == null)
				wEXCAlarmRule.ExceptionTypeList = new ArrayList<Long>();

			String wSQL = "";

			if (wEXCAlarmRule.getID() <= 0) {

				wSQL = StringUtils.Format(
						"INSERT INTO {0}.exc_alarm_rule  (  AlarmID, "
								+ "AlarmCode,  AlarmText,  StationType,  StationID, "
								+ "ExceptionTypeList,  RespondLevel,  CreatorID,  CreateTime, "
								+ "EditorID,  EditTime,  Active)  VALUES   (  :wAlarmID, "
								+ ":wAlarmCode,  :wAlarmText,  :wStationType,  :wStationID,  :wExceptionTypeList, "
								+ ":wRespondLevel,   :wCreatorID ,  now() ,  :wCreatorID,  now() ,  :wActive );",
						wInstance.Result);
			} else {
				wSQL = StringUtils.Format("UPDATE {0}.exc_alarm_rule  SET   AlarmID = :wAlarmID, "
						+ "AlarmCode = :wAlarmCode,  AlarmText = :wAlarmText,  StationType = :wStationType, "
						+ "StationID = :wStationID,  ExceptionTypeList = :wExceptionTypeList, "
						+ "RespondLevel = :wRespondLevel,    EditorID = :wEditorID,  EditTime = now(), "
						+ "Active = :wActive   WHERE ID =:wID;", wInstance.Result);
			}
			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wEXCAlarmRule.ID);
			wParamMap.put("wAlarmID", wEXCAlarmRule.AlarmID);
			wParamMap.put("wAlarmCode", wEXCAlarmRule.AlarmCode);
			wParamMap.put("wAlarmText", wEXCAlarmRule.AlarmText);
			wParamMap.put("wStationType", wEXCAlarmRule.StationType);
			wParamMap.put("wStationID", wEXCAlarmRule.StationID);
			wParamMap.put("wRespondLevel", wEXCAlarmRule.RespondLevel);
			wParamMap.put("wExceptionTypeList", StringUtils.Join(",", wEXCAlarmRule.ExceptionTypeList));
			wParamMap.put("wCreatorID", wEXCAlarmRule.CreatorID);
			wParamMap.put("wEditorID", wEXCAlarmRule.EditorID);
			wParamMap.put("wActive", wEXCAlarmRule.Active);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			wResult = nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);
			if (wEXCAlarmRule.getID() <= 0) {
				wResult = keyHolder.getKey().longValue();
				wEXCAlarmRule.setID(wResult);
			} else {
				wResult = wEXCAlarmRule.getID();
			}
		} catch (Exception e) {

			logger.error(e.toString());
		}
		return wResult;
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
			String wSql = StringUtils.Format("UPDATE {0}.exc_alarm_rule SET Active ={1} WHERE ID IN({2}) ;",
					wInstance.Result, String.valueOf(wActive), StringUtils.Join(",", wIDList));

			ExecuteSqlTransaction(wSql);
		} catch (Exception e) {

			logger.error(e.toString());
		}
	}

	private EXCAlarmRuleDAO() {
		super();
	}

	public static EXCAlarmRuleDAO getInstance() {
		if (Instance == null)
			Instance = new EXCAlarmRuleDAO();
		return Instance;
	}

}
