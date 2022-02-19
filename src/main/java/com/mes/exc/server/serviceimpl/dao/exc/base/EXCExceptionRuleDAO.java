package com.mes.exc.server.serviceimpl.dao.exc.base;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.mes.exc.server.serviceimpl.dao.BaseDAO;
import com.mes.exc.server.serviceimpl.utils.exc.EXCConstants;
import com.mes.exc.server.serviceimpl.utils.exc.EXCStrClaConverter;
import com.mes.exc.server.service.po.OutResult;
import com.mes.exc.server.service.po.ServiceResult;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.service.po.exc.EXCTimeItem;
import com.mes.exc.server.service.po.exc.base.EXCExceptionRule;
import com.mes.exc.server.service.po.exc.base.EXCExceptionTemplate;
import com.mes.exc.server.service.po.exc.define.EXCActionTypes;
import com.mes.exc.server.service.po.exc.define.EXCResourceTypes;
import com.mes.exc.server.service.mesenum.MESDBSource;
import com.mes.exc.server.service.utils.StringUtils;

/**
 * 异常发起规则管理
 * 
 * @author ShrisJava
 *
 */
public class EXCExceptionRuleDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(EXCExceptionRuleDAO.class);

	private static EXCExceptionRuleDAO Instance;

	/**
	 * 权限码
	 */
	private static int AccessCode = 710003;

	public List<EXCExceptionRule> SelectAll(BMSEmployee wLoginUser, List<Long> wID, String wName, long wExceptionType,
			int wRespondLevel, List<Integer> wEXCTemplateList, int wActive, OutResult<Integer> wErrorCode) {
		List<EXCExceptionRule> wResult = new ArrayList<EXCExceptionRule>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wID == null)
				wID = new ArrayList<Long>();
			if (wEXCTemplateList == null)
				wEXCTemplateList = new ArrayList<Integer>();

			String wSQL = StringUtils.Format("SELECT exc_ex_rule.ID,  	exc_ex_rule.Name, "
					+ "    exc_ex_rule.ExceptionType,  	exc_ex_type.Name as ExceptionTypeName, "
					+ "    exc_ex_rule.RespondLevel,      exc_ex_rule.TimeOutList, "
					+ "    exc_ex_rule.ReportTimes,      exc_ex_rule.ForwardTimes, "
					+ "    exc_ex_rule.EXCTemplate,       exc_ex_rule.CreatorID, "
					+ "    exc_ex_rule.CreateTime,      exc_ex_rule.EditorID, "
					+ "    exc_ex_rule.EditTime,      exc_ex_rule.Active " + "FROM {0}.exc_ex_rule ,{0}.exc_ex_type  "
					+ "WHERE  exc_ex_rule.ExceptionType= exc_ex_type.ID   "
					+ "and ( :wID is null or :wID = '''' or exc_ex_rule.ID IN( {1} ) )   "
					+ "and ( :wName is null or :wName = '''' or exc_ex_rule.Name =  :wName )    "
					+ "and ( :wExceptionType<= 0 or exc_ex_rule.ExceptionType =:wExceptionType)  "
					+ "and ( :wRespondLevel<= 0 or exc_ex_rule.RespondLevel =:wRespondLevel)   "
					+ "and ( :wEXCTemplate is null or :wEXCTemplate = '''' or exc_ex_rule.EXCTemplate IN( {2} ) )  "
					+ "and ( :wActive< 0 or exc_ex_rule.Active =:wActive)  ", wInstance.Result,
					wID.size() > 0 ? StringUtils.Join(",", wID) : "0",
					wEXCTemplateList.size() > 0 ? StringUtils.Join(",", wEXCTemplateList) : "0");
			wSQL = this.DMLChange(wSQL);
			Map<String, Object> wParamMap = new HashMap<String, Object>();
			wParamMap.put("wID", StringUtils.Join(",", wID));
			wParamMap.put("wName", wName);
			wParamMap.put("wExceptionType", wExceptionType);
			wParamMap.put("wRespondLevel", wRespondLevel);
			wParamMap.put("wEXCTemplate", StringUtils.Join(",", wEXCTemplateList));
			wParamMap.put("wActive", wActive);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
			// wReader\[\"(\w+)\"\]
			Map<Integer, EXCExceptionTemplate> wEXCExceptionTemplateDic = EXCConstants.getEXCExceptionTemplateList()
					.stream().collect(Collectors.toMap(EXCExceptionTemplate::getEXCTemplate, account -> account));
			for (Map<String, Object> wReader : wQueryResult) {

				long wIDSql = StringUtils.parseLong(wReader.get("ID"));
				String wNameSql = StringUtils.parseString(wReader.get("Name"));
				long wExceptionTypeSql = StringUtils.parseLong(wReader.get("ExceptionType"));
				String wExceptionTypeNameSql = StringUtils.parseString(wReader.get("ExceptionTypeName"));
				int wRespondLevelSql = StringUtils.parseInt(wReader.get("RespondLevel"));
				String wTimeOutListSql = StringUtils.parseString(wReader.get("TimeOutList"));
				int wReportTimesSql = StringUtils.parseInt(wReader.get("ReportTimes"));
				int wForwardTimesSql = StringUtils.parseInt(wReader.get("ForwardTimes"));
				int wEXCTemplateSql = StringUtils.parseInt(wReader.get("EXCTemplate"));
				long wCreatorIDSql = StringUtils.parseLong(wReader.get("CreatorID"));
				Calendar wCreateTimeSql = StringUtils.parseCalendar(wReader.get("CreateTime"));
				long wEditorIDSql = StringUtils.parseLong(wReader.get("EditorID"));
				Calendar wEditTimeSql = StringUtils.parseCalendar(wReader.get("EditTime"));
				int wActiveSql = StringUtils.parseInt(wReader.get("Active"));

				EXCExceptionRule wDevicePropertyModel = new EXCExceptionRule();

				wDevicePropertyModel.ID = wIDSql;
				wDevicePropertyModel.Name = wNameSql;
				wDevicePropertyModel.ExceptionType = wExceptionTypeSql;
				wDevicePropertyModel.ExceptionTypeName = wExceptionTypeNameSql;
				wDevicePropertyModel.RespondLevel = wRespondLevelSql;
				wDevicePropertyModel.TimeOutList = EXCStrClaConverter.EXCTimeItemToList(wTimeOutListSql);
				wDevicePropertyModel.ReportTimes = wReportTimesSql;
				wDevicePropertyModel.ForwardTimes = wForwardTimesSql;
				wDevicePropertyModel.EXCTemplate = wEXCTemplateSql;
				wDevicePropertyModel.EXCRequestType = wEXCExceptionTemplateDic.containsKey(wEXCTemplateSql)
						? wEXCExceptionTemplateDic.get(wEXCTemplateSql).EXCRequestType
						: 0;
				wDevicePropertyModel.EXCResponseType = wEXCExceptionTemplateDic.containsKey(wEXCTemplateSql)
						? wEXCExceptionTemplateDic.get(wEXCTemplateSql).EXCResponseType
						: 0;
				wDevicePropertyModel.EXCConfirmType = wEXCExceptionTemplateDic.containsKey(wEXCTemplateSql)
						? wEXCExceptionTemplateDic.get(wEXCTemplateSql).EXCConfirmType
						: 0;
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

	public List<EXCExceptionRule> SelectAll(BMSEmployee wLoginUser, String wName, long wExceptionType,
			int wRespondLevel, EXCResourceTypes wRequestType, EXCResourceTypes wResponseType,
			EXCResourceTypes wConfirmType, int wActive, OutResult<Integer> wErrorCode) {
		List<EXCExceptionRule> wResult = new ArrayList<EXCExceptionRule>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wRequestType == EXCResourceTypes.None && wResponseType == EXCResourceTypes.None
					&& wConfirmType == EXCResourceTypes.None) {
				wResult = SelectAll(wLoginUser, null, wName, wExceptionType, wRespondLevel, null, wActive, wErrorCode);
			} else {
				List<EXCExceptionTemplate> wEXCExceptionTemplateList = EXCConstants.getEXCExceptionTemplateList()
						.stream()
						.filter((EXCExceptionTemplate p) -> (wConfirmType == EXCResourceTypes.None
								|| p.EXCConfirmType == (int) wConfirmType.getValue())
								&& (wRequestType == EXCResourceTypes.None
										|| p.EXCRequestType == (int) wRequestType.getValue())
								&& (wResponseType == EXCResourceTypes.None
										|| p.EXCResponseType == (int) wResponseType.getValue()))
						.collect(Collectors.toList());
				;

				if (wEXCExceptionTemplateList == null || wEXCExceptionTemplateList.size() < 1)
					return wResult;
				wResult = SelectAll(
						wLoginUser, null, wName, wExceptionType, wRespondLevel, wEXCExceptionTemplateList.stream()
								.map(EXCExceptionTemplate::getEXCTemplate).collect(Collectors.toList()),
						wActive, wErrorCode);
			}

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public EXCExceptionRule Select(BMSEmployee wLoginUser, long wID, OutResult<Integer> wErrorCode) {
		EXCExceptionRule wResult = new EXCExceptionRule();
		try {
			List<EXCExceptionRule> wEXCExceptionRuleList = SelectAll(wLoginUser,
					StringUtils.parseList(new Long[] { wID }), "", -1, -1, null, -1, wErrorCode);
			if (wEXCExceptionRuleList.size() != 1)
				return wResult;

			wResult = wEXCExceptionRuleList.get(0);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;

	}

	public EXCExceptionRule Select(BMSEmployee wLoginUser, long wExceptionType, int wRespondLevel,
			EXCResourceTypes wRequestType, EXCResourceTypes wResponseType, EXCResourceTypes wConfirmType,
			OutResult<Integer> wErrorCode) {
		EXCExceptionRule wResult = new EXCExceptionRule();  
		try {
			List<EXCExceptionTemplate> wEXCExceptionTemplateList = EXCConstants.getEXCExceptionTemplateList().stream()
					.filter(p -> p.EXCConfirmType == (int) wConfirmType.getValue()
							&& p.EXCRequestType == (int) wRequestType.getValue()
							&& p.EXCResponseType == (int) wResponseType.getValue())
					.collect(Collectors.toList());

			if (wEXCExceptionTemplateList == null || wEXCExceptionTemplateList.size() != 1)
				return wResult;

			List<EXCExceptionRule> wEXCExceptionRuleList = SelectAll(wLoginUser, null, "", wExceptionType,
					wRespondLevel,
					wEXCExceptionTemplateList.stream().map(p -> p.EXCTemplate).collect(Collectors.toList()), 1,
					wErrorCode);

			if (wEXCExceptionRuleList.size() != 1)
				return wResult;

			wResult = wEXCExceptionRuleList.get(0);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;

	}

	/**
	 * 
	 * @param wExceptionType
	 * @param wRespondLevel
	 * @param wRequestType
	 * @param wResponseType
	 * @param wConfirmType
	 * @param wActionType
	 * @param ReportLevel    上报层级
	 * @return
	 */
	public EXCTimeItem SelectEXCTimeItem(BMSEmployee wLoginUser, long wExceptionType, int wRespondLevel,
			EXCResourceTypes wRequestType, EXCResourceTypes wResponseType, EXCResourceTypes wConfirmType,
			EXCActionTypes wActionType, int wReportLevel, OutResult<Integer> wErrorCode) {
		EXCTimeItem wResult = new EXCTimeItem();
		try {
			EXCExceptionRule wEXCExceptionRule = Select(wLoginUser, wExceptionType, wRespondLevel, wRequestType,
					wResponseType, wConfirmType, wErrorCode);

			if (wEXCExceptionRule == null || wEXCExceptionRule.ID < 0 || wEXCExceptionRule.TimeOutList == null
					|| wEXCExceptionRule.TimeOutList.size() < 1)
				return wResult;

			Optional<EXCTimeItem> wEXCTimeItemFirst = wEXCExceptionRule.TimeOutList.stream()
					.filter(p -> p.ID == wActionType.getValue()
							&& p.Responselevel == wEXCExceptionRule.getReportTimes() - wReportLevel)
					.findFirst();

			if (wEXCTimeItemFirst.isPresent())
				wResult = wEXCTimeItemFirst.get();

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public EXCTimeItem SelectEXCTimeItem(BMSEmployee wLoginUser, EXCExceptionRule wEXCExceptionRule,
			EXCActionTypes wActionType, int wReportLevel) {
		EXCTimeItem wResult = new EXCTimeItem();
		try {

			if (wEXCExceptionRule == null || wEXCExceptionRule.ID < 0 || wEXCExceptionRule.TimeOutList == null
					|| wEXCExceptionRule.TimeOutList.size() < 1)
				return wResult;

			for (EXCTimeItem wEXCTimeItem : wEXCExceptionRule.TimeOutList) {
				if (wEXCTimeItem.ID == wActionType.getValue()
						&& wEXCTimeItem.Responselevel == (wEXCExceptionRule.getReportTimes() - wReportLevel + 1)) {
					wResult = wEXCTimeItem;
				}
			}

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public long Update(BMSEmployee wLoginUser, EXCExceptionRule wDeviceModel, OutResult<Integer> wErrorCode) {
		long wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wDeviceModel == null)
				return 0L;

			if (wDeviceModel.TimeOutList == null)
				wDeviceModel.TimeOutList = new ArrayList<EXCTimeItem>();

			String wSQL = "";

			if (wDeviceModel.getID() <= 0) {

				wSQL = StringUtils.Format(
						"INSERT INTO {0}.exc_ex_rule  (  Name, "
								+ "ExceptionType,  RespondLevel,  TimeOutList,  ReportTimes, "
								+ "ForwardTimes,  EXCTemplate,   CreatorID,  CreateTime,  EditorID, "
								+ "EditTime,  Active)  VALUES  (  :wName,  :wExceptionType, "
								+ ":wRespondLevel,  :wTimeOutList,  :wReportTimes,  :wForwardTimes, "
								+ ":wEXCTemplate,   :wCreatorID ,  now() ,  :wCreatorID,  now() ,  :wActive );",
						wInstance.Result);
			} else {
				wSQL = StringUtils.Format(
						"UPDATE {0}.exc_ex_rule  SET   Name=:wName, RespondLevel = :wRespondLevel, "
								+ "TimeOutList =:wTimeOutList,  ReportTimes = :wReportTimes, "
								+ "ForwardTimes = :wForwardTimes,  EXCTemplate = :wEXCTemplate,  "
								+ "EditorID = :wEditorID,  EditTime = now(),  Active = :wActive   WHERE ID =:wID;",
						wInstance.Result);
			}
			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();
			wParamMap.put("wID", wDeviceModel.ID);
			wParamMap.put("wName", wDeviceModel.Name);
			wParamMap.put("wExceptionType", wDeviceModel.ExceptionType);
			wParamMap.put("wRespondLevel", wDeviceModel.RespondLevel);
			wParamMap.put("wTimeOutList", EXCStrClaConverter.EXCTimeItemToString(wDeviceModel.TimeOutList));
			wParamMap.put("wReportTimes", wDeviceModel.ReportTimes);
			wParamMap.put("wForwardTimes", wDeviceModel.ForwardTimes);
			wParamMap.put("wEXCTemplate", wDeviceModel.EXCTemplate);
			wParamMap.put("wCreatorID", wDeviceModel.CreatorID);
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
			String wSql = StringUtils.Format("UPDATE {0}.exc_ex_rule SET Active ={1} WHERE ID IN({2}) ;",
					wInstance.Result, String.valueOf(wActive), StringUtils.Join(",", wIDList));

			ExecuteSqlTransaction(wSql);
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	private EXCExceptionRuleDAO() {
		super();
	}

	public static EXCExceptionRuleDAO getInstance() {
		if (Instance == null)
			Instance = new EXCExceptionRuleDAO();
		return Instance;
	}

}
