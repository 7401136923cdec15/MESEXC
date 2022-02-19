package com.mes.exc.server.serviceimpl.dao.exc.base;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.mes.exc.server.serviceimpl.dao.BaseDAO;
import com.mes.exc.server.service.po.OutResult;
import com.mes.exc.server.service.po.ServiceResult;
import com.mes.exc.server.service.po.bfc.QRTypes;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.service.po.exc.base.EXCStation;
import com.mes.exc.server.service.mesenum.MESDBSource;
import com.mes.exc.server.service.utils.StringUtils;

/**
 * 异常点管理
 * 
 * @author ShrisJava
 *
 */
public class EXCStationDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(EXCStationDAO.class);

	private static EXCStationDAO Instance;

	/**
	 * 权限码
	 */
	private static int AccessCode = 0;

	public List<EXCStation> SelectAll(BMSEmployee wLoginUser, List<Long> wID, String wStationNo, String wStationName,
			long wStationType, QRTypes wRelevancyType, long wRelevancyID, int wActive, OutResult<Integer> wErrorCode) {
		List<EXCStation> wResult = new ArrayList<EXCStation>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wID == null)
				wID = new ArrayList<Long>();
			if (wStationNo == null)
				wStationNo = "";
			if (wStationName == null)
				wStationName = "";

			String wSQL = StringUtils.Format(
					"SELECT t.* FROM {0}.exc_station t  WHERE  1=1 "
							+ " and ( :wID = '''' or t.ID IN( {1} ) )  "
							+ " and ( :wStationNo = '''' or t.StationNo =  :wStationNo ) "
							+ " and ( :wStationName = '''' or t.StationName =  :wStationName ) "
							+ " and ( :wStationType<= 0 or t.StationType =:wStationType) "
							+ " and ( :wRelevancyType<= 0 or t.RelevancyType =:wRelevancyType) "
							+ " and ( :wRelevancyID<= 0 or t.RelevancyID =:wRelevancyID) "
							+ " and ( :wActive< 0 or t.Active =:wActive) ",
					wInstance.Result, wID.size() > 0 ? StringUtils.Join(",", wID) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", StringUtils.Join(",", wID));
			wParamMap.put("wRelevancyType", wRelevancyType.getValue());
			wParamMap.put("wStationNo", wStationNo);
			wParamMap.put("wStationName", wStationName);
			wParamMap.put("wStationType", wStationType);
			wParamMap.put("wRelevancyID", wRelevancyID);
			wParamMap.put("wActive", wActive);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
			// wReader\[\"(\w+)\"\]

			for (Map<String, Object> wReader : wQueryResult) {

				long wIDSql = StringUtils.parseLong(wReader.get("ID"));
				String wStationNoSql = StringUtils.parseString(wReader.get("StationNo"));
				String wStationNameSql = StringUtils.parseString(wReader.get("StationName"));
				long wStationTypeSql = StringUtils.parseLong(wReader.get("StationType"));
				int wRelevancyTypeSql = StringUtils.parseInt(wReader.get("RelevancyType"));
				long wRelevancyIDSql = StringUtils.parseLong(wReader.get("RelevancyID"));
				long wCreatorIDSql = StringUtils.parseLong(wReader.get("CreatorID"));
				Calendar wCreateTimeSql = StringUtils.parseCalendar(wReader.get("CreateTime"));
				long wEditorIDSql = StringUtils.parseLong(wReader.get("EditorID"));
				Calendar wEditTimeSql = StringUtils.parseCalendar(wReader.get("EditTime"));
				int wActiveSql = StringUtils.parseInt(wReader.get("Active"));

				EXCStation wEXCStation = new EXCStation();

				wEXCStation.ID = wIDSql;
				wEXCStation.StationNo = wStationNoSql;
				wEXCStation.StationType = wStationTypeSql;
				wEXCStation.StationName = wStationNameSql;
				wEXCStation.RelevancyType = wRelevancyTypeSql;
				wEXCStation.RelevancyID = wRelevancyIDSql;
				wEXCStation.CreatorID = wCreatorIDSql;
				wEXCStation.CreateTime = wCreateTimeSql;
				wEXCStation.EditorID = wEditorIDSql;
				wEXCStation.EditTime = wEditTimeSql;
				wEXCStation.Active = wActiveSql;

				wResult.add(wEXCStation);
			}

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public List<EXCStation> SelectAll(BMSEmployee wLoginUser, String wStationName, long wStationType,
			QRTypes wRelevancyType, long wRelevancyID, int wActive, OutResult<Integer> wErrorCode) {
		List<EXCStation> wResult = new ArrayList<EXCStation>();
		try {
			wResult = SelectAll(wLoginUser, null, null, wStationName, wStationType, wRelevancyType, wRelevancyID,
					wActive, wErrorCode);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public EXCStation Select(BMSEmployee wLoginUser, long wID, String wStationNo, OutResult<Integer> wErrorCode) {
		EXCStation wResult = new EXCStation();

		try {
			List<EXCStation> wEXCStationList = SelectAll(wLoginUser, StringUtils.parseList(new Long[] { wID }),
					wStationNo, "", -1, QRTypes.Default, -1, -1, wErrorCode);
			if (wEXCStationList.size() != 1)
				return wResult;

			wResult = wEXCStationList.get(0);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public EXCStation Select(BMSEmployee wLoginUser, QRTypes wRelevancyType, long wRelevancyID,
			OutResult<Integer> wErrorCode) {
		EXCStation wResult = new EXCStation();

		try {
			List<EXCStation> wEXCStationList = SelectAll(wLoginUser, "", -1, wRelevancyType, wRelevancyID, -1,
					wErrorCode);
			if (wEXCStationList.size() != 1)
				return wResult;

			wResult = wEXCStationList.get(0);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public long Update(BMSEmployee wLoginUser, EXCStation wEXCStation, OutResult<Integer> wErrorCode) {

		long wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wEXCStation == null)
				return 0L;

			String wSQL = "";

			if (wEXCStation.getID() <= 0) {
				wSQL = StringUtils.Format("INSERT INTO {0}.exc_station(StationNo,StationName,"
						+ "StationType,RelevancyType,RelevancyID,CreatorID,CreateTime,EditorID,EditTime,"
						+ "Active)VALUES(:StationNo,:StationName,"
						+ ":StationType,:RelevancyType,:RelevancyID,:CreatorID,now(),:CreatorID,now(),:Active);",
						wInstance.Result);
			} else {
				wSQL = StringUtils.Format(
						"UPDATE {0}.exc_station SET StationNo = :StationNo,"
								+ "StationName = :StationName,StationType = :StationType,"
								+ "RelevancyType= :RelevancyType,RelevancyID = :RelevancyID,"
								+ "EditorID = :EditorID,EditTime = now(),Active = :Active  WHERE ID =:ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new BeanPropertySqlParameterSource(wEXCStation);

			wResult = nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);
			if (wEXCStation.getID() <= 0) {
				wResult = keyHolder.getKey().longValue();
				wEXCStation.setID(wResult);
			} else {
				wResult = wEXCStation.getID();
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
			String wSql = StringUtils.Format("UPDATE {0}.exc_station SET Active ={1} WHERE ID IN({2}) ;",
					wInstance.Result, String.valueOf(wActive), StringUtils.Join(",", wIDList));

			this.ExecuteSqlTransaction(wSql);
		} catch (Exception e) {

			logger.error(e.toString());
		}
	}

	private EXCStationDAO() {
		super();
	}

	public static EXCStationDAO getInstance() {
		if (Instance == null)
			Instance = new EXCStationDAO();
		return Instance;
	}

}
