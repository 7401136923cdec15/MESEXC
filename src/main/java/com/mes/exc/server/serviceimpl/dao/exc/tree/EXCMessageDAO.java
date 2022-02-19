package com.mes.exc.server.serviceimpl.dao.exc.tree;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.mes.exc.server.service.po.OutResult;
import com.mes.exc.server.service.po.ServiceResult;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.serviceimpl.CoreServiceImpl;
import com.mes.exc.server.serviceimpl.dao.BaseDAO;
import com.mes.exc.server.utils.Configuration;
import com.alibaba.fastjson.JSON;
import com.mes.exc.server.service.po.exc.tree.EXCMessage;
import com.mes.exc.server.service.mesenum.MESDBSource;
import com.mes.exc.server.service.utils.RemoteInvokeUtils;
import com.mes.exc.server.service.utils.StringUtils;

public class EXCMessageDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(EXCMessageDAO.class);

	private static EXCMessageDAO Instance;

	/**
	 * 权限码
	 */
	private static int AccessCode = 0;

	public List<EXCMessage> EXC_GetMessageList(BMSEmployee wLoginUser, int wResponsorID, long wStationID,
			String wStationNo, long wType, int wModuleID, int wMessageID, int wActive, int wShiftID,
			Calendar wStartTime, Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<EXCMessage> wResult = new ArrayList<EXCMessage>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wStationNo == null)
				wStationNo = "";

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 1, 1);
			if (wStartTime == null || wStartTime.compareTo(wBaseTime) < 0)
				wStartTime = wBaseTime;
			if (wEndTime == null || wEndTime.compareTo(wBaseTime) < 0)
				wEndTime = wBaseTime;
			if (wStartTime.compareTo(wEndTime) > 0)
				return wResult;

			String wSQL = StringUtils.Format("SELECT  * FROM {0}.exc_message  WHERE 1=1 "

					+ " and ( :wCompanyID<= 0 or  exc_message.CompanyID  =:wCompanyID)   "
					+ " and ( :wResponsorID<= 0 or  exc_message.ResponsorID  =:wResponsorID)   "
					+ " and ( :wStationID<= 0 or  exc_message.StationID  =:wStationID)   "
					+ " and ( :wStationNo is null or :wStationNo = '''' or  exc_message.StationNo  =  :wStationNo )  "
					+ " and ( :wType<= 0 or  exc_message.Type  =:wType)   "
					+ " and ( :wModuleID<= 0 or  exc_message.ModuleID  =:wModuleID)   "
					+ " and ( :wMessageID<= 0 or  exc_message.MessageID  =:wMessageID)   "
					+ " and ( :wActive< 0 or  exc_message.Active  =:wActive)   "
					+ " and ( :wShiftID <= 0 or  exc_message.ShiftID   = :wShiftID)   "
					+ " and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <=  exc_message.CreateTime )"
					+ " and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >=  exc_message.CreateTime )  ",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wCompanyID", wLoginUser.getCompanyID());
			wParamMap.put("wResponsorID", wResponsorID);
			wParamMap.put("wStationNo", wStationNo);
			wParamMap.put("wStationID", wStationID);
			wParamMap.put("wStationID", wStationID);
			wParamMap.put("wType", wType);
			wParamMap.put("wModuleID", wModuleID);
			wParamMap.put("wMessageID", wMessageID);
			wParamMap.put("wActive", wActive);
			wParamMap.put("wShiftID", wShiftID);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
			// wReader\[\"(\w+)\"\]
			for (Map<String, Object> wReader : wQueryResult) {

				EXCMessage wEXCMessage = new EXCMessage();
				wEXCMessage.ID = StringUtils.parseLong(wReader.get("ID"));
				wEXCMessage.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wEXCMessage.CompanyID = StringUtils.parseInt(wReader.get("CompanyID"));
				wEXCMessage.EditTime = StringUtils.parseCalendar(wReader.get("EditTime"));
				wEXCMessage.MessageID = StringUtils.parseLong(wReader.get("MessageID"));
				wEXCMessage.MessageText = StringUtils.parseString(wReader.get("MessageText"));
				wEXCMessage.StationID = StringUtils.parseLong(wReader.get("StationID"));
				wEXCMessage.StationNo = StringUtils.parseString(wReader.get("StationNo"));
				wEXCMessage.Title = StringUtils.parseString(wReader.get("Title"));
				wEXCMessage.Type = StringUtils.parseInt(wReader.get("Type"));
				wEXCMessage.Active = StringUtils.parseInt(wReader.get("Active"));
				wEXCMessage.ResponsorID = StringUtils.parseInt(wReader.get("ResponsorID"));
				wEXCMessage.ModuleID = StringUtils.parseInt(wReader.get("ModuleID"));
				wResult.add(wEXCMessage);

			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public List<EXCMessage> EXC_GetMessageList(BMSEmployee wLoginUser, int wResponsorID, int wModuleID, int wActive,
			int wShiftID, Calendar wStartTime, Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<EXCMessage> wResult = new ArrayList<EXCMessage>();
		try {
			wResult = EXC_GetMessageList(wLoginUser, wResponsorID, 0, "", 0, wModuleID, 0, wActive, wShiftID,
					wStartTime, wEndTime, wErrorCode);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public List<EXCMessage> EXC_GetMessageList(BMSEmployee wLoginUser, int wResponsorID, int wActive,
			OutResult<Integer> wErrorCode) {
		List<EXCMessage> wResult = new ArrayList<EXCMessage>();
		try {
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 1, 1);
			wResult = EXC_GetMessageList(wLoginUser, wResponsorID, 0, wActive, 0, wBaseTime, wBaseTime, wErrorCode);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public Map<Integer, Integer> EXC_GetMessageCount(BMSEmployee wLoginUser, int wResponsorID, int wShiftID,
			OutResult<Integer> wErrorCode) {
		Map<Integer, Integer> wResult = new HashMap<>();

		try {
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 1, 1);
			List<EXCMessage> wEXCMessageList = EXC_GetMessageList(wLoginUser, wResponsorID, 0, 0, wShiftID, wBaseTime,
					wBaseTime, wErrorCode);

			Map<Long, List<EXCMessage>> collect = wEXCMessageList.stream()
					.collect(Collectors.groupingBy((EXCMessage p) -> p.ModuleID));

			for (Long wModuleID : collect.keySet()) {
				wResult.put(wModuleID.intValue(), collect.get(wModuleID).size());
			}

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public long Update(BMSEmployee wLoginUser, EXCMessage wEXCMessage, OutResult<Integer> wErrorCode) {

		long wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wEXCMessage == null)
				return 0L;
			if (wEXCMessage.Title == null)
				wEXCMessage.Title = "";
			if (wEXCMessage.MessageText == null)
				wEXCMessage.MessageText = "";
			if (wEXCMessage.StationNo == null)
				wEXCMessage.StationNo = "";

			boolean wIsInsert = wEXCMessage.ID <= 0;

			String wSQL = "";
			Map<String, Object> wParamMap = new HashMap<String, Object>();
			if (wEXCMessage.getID() <= 0) {

				// 判断是否存在
				wSQL = StringUtils.Format("select count(0) from   {0}.exc_message where MessageID= :wMessageID"
						+ " and ModuleID=:wModuleID and ResponsorID=:wResponsorID and CompanyID=:wCompanyID and Type=:wType ; ",
						wInstance.Result);
				wSQL = this.DMLChange(wSQL);
				wParamMap.put("wResponsorID", wEXCMessage.ResponsorID);
				wParamMap.put("wType", wEXCMessage.Type);
				wParamMap.put("wModuleID", wEXCMessage.ModuleID);
				wParamMap.put("wMessageID", wEXCMessage.MessageID);
				wParamMap.put("wCompanyID", wEXCMessage.CompanyID);

				long wCount = nameJdbcTemplate.queryForObject(wSQL, wParamMap, Long.class);

				if (wCount > 0)
					return wResult;

				wSQL = StringUtils.Format("  INSERT INTO  {0}.exc_message ( ResponsorID, Type, "
						+ "MessageText, Title, CreateTime, Active, EditTime, ModuleID, "
						+ "MessageID, StationID, StationNo, CompanyID, ShiftID) VALUES "
						+ "( :wResponsorID , :wType , :wMessageText , "
						+ ":wTitle , now() , 0 ,  now() , :wModuleID , :wMessageID , "
						+ ":wStationID , :wStationNo , :wCompanyID , :wShiftID );", wInstance.Result);
			} else {
				wSQL = StringUtils.Format(
						"UPDATE {0}.exc_message SET    EditTime = now() , Active = :wActive  WHERE ID>0 "
								+ " and ( :wID =ID or ( ModuleID =:wModuleID and MessageID=:wMessageID) );",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);
			wParamMap.clear();

			wParamMap.put("wID", wEXCMessage.ID);
			wParamMap.put("wResponsorID", wEXCMessage.ResponsorID);
			wParamMap.put("wType", wEXCMessage.Type);
			wParamMap.put("wMessageText", wEXCMessage.MessageText);
			wParamMap.put("wTitle", wEXCMessage.Title);
			wParamMap.put("wModuleID", wEXCMessage.ModuleID);
			wParamMap.put("wMessageID", wEXCMessage.MessageID);
			wParamMap.put("wStationID", wEXCMessage.StationID);
			wParamMap.put("wStationNo", wEXCMessage.StationNo);
			wParamMap.put("wCompanyID", wEXCMessage.CompanyID);
			wParamMap.put("wShiftID", wEXCMessage.ShiftID);
			wParamMap.put("wActive", wEXCMessage.Active);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			wResult = nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);
			if (wEXCMessage.getID() <= 0) {
				wResult = keyHolder.getKey().longValue();
				wEXCMessage.setID(wResult);
			} else {
				wResult = wEXCMessage.getID();
			}

			if (wIsInsert) {

				// 推送到中台
				SendMessageToExternal(wLoginUser, wEXCMessage);

			}

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public void SendMessageToExternal(BMSEmployee wLoginUser, EXCMessage wEXCMessage) {
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			BMSEmployee wBMSEmployee = CoreServiceImpl.getInstance()
					.BMS_QueryEmployeeByID(wLoginUser, (int) wEXCMessage.getResponsorID()).Info(BMSEmployee.class);

			if (ExternalMsgLocal > 0) {
				logger.info(StringUtils.Format("SendMessageToExternal Define ExternalMsgLocal :{0}", ExternalMsgLocal));
				return;
			}

			if (wErrorCode.Result != 0 || wBMSEmployee == null || wBMSEmployee.ID <= 0) {
				logger.info(StringUtils.Format("SendMessageToExternal info : ResponsorID={0} not found !!!",
						wEXCMessage.getResponsorID()));
				return;
			}
			Map<String, Object> wCustom = new HashMap<String, Object>();
			wCustom.put("type", ExternalMsgType);
			wCustom.put("insideAppName", ExternalMsgInsideAPPName);

			Map<String, Object> wParams = new HashMap<String, Object>();

			wParams.put("appName", ExternalMsgAPPName);
			wParams.put("custom", wCustom);
			wParams.put("param", "");
			wParams.put("userAccountList", wBMSEmployee.getLoginID());
			wParams.put("sender", "");
			wParams.put("title", wEXCMessage.Title);
			wParams.put("content", wEXCMessage.getMessageText());

			String wResultString = RemoteInvokeUtils.getInstance().HttpInvokeString(ExternalMsgUrl, wParams,
					HttpMethod.POST);

			logger.info("SendMessageToExternal Result  : " + wResultString);

			@SuppressWarnings("unchecked")
			Map<String, Object> wResult = StringUtils.isEmpty(wResultString) ? new HashMap<String, Object>()
					: JSON.parseObject(wResultString, Map.class);

			if (wResult == null || !wResult.containsKey("status")) {
				return;
			}

			if (StringUtils.parseInt(wResult.get("status")) == 1) {

				wEXCMessage.Active = 1;
				Update(wLoginUser, wEXCMessage, wErrorCode);

				return;
			}
		} catch (Exception e) {
			logger.error("SendMessageToExternal Error:" + e.toString());
		}
	}

	private static int ExternalMsgLocal = StringUtils
			.parseInt(Configuration.readConfigString("msg.external.local", "config/config"));

	private static String ExternalMsgUrl = Configuration.readConfigString("msg.external.url", "config/config");

	private static String ExternalMsgAPPName = Configuration.readConfigString("msg.external.app.name", "config/config");

	private static String ExternalMsgInsideAPPName = Configuration.readConfigString("msg.external.inside.app.name",
			"config/config");

	private static String ExternalMsgType = Configuration.readConfigString("msg.external.type", "config/config");

	private EXCMessageDAO() {
		super();
	}

	public static EXCMessageDAO getInstance() {
		if (Instance == null)
			Instance = new EXCMessageDAO();
		return Instance;
	}

}
