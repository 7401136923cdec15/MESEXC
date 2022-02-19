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
import com.mes.exc.server.service.po.exc.base.EXCStationType;
import com.mes.exc.server.service.mesenum.MESDBSource;
import com.mes.exc.server.service.utils.StringUtils;

/**
 * 异常点管理
 * 
 * @author ShrisJava
 *
 */
public class EXCStationTypeDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(EXCStationTypeDAO.class);

	private static EXCStationTypeDAO Instance;

	/**
	 * 权限码
	 */
	private static int AccessCode = 710001;

	public List<EXCStationType> SelectAll(BMSEmployee wLoginUser, List<Long> wID, String wName, QRTypes wRelevancyType,
			int wActive, OutResult<Integer> wErrorCode) {
		List<EXCStationType> wResult = new ArrayList<EXCStationType>();
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
					"SELECT exc_station_type.ID,     exc_station_type.Name, "
							+ "    exc_station_type.RelevancyType,     exc_station_type.CreatorID, "
							+ "    exc_station_type.CreateTime,     exc_station_type.EditorID, "
							+ "    exc_station_type.EditTime,     exc_station_type.Active "
							+ "FROM {0}.exc_station_type  WHERE  1=1  "
							+ "and (  :wID is null or  :wID = '''' or exc_station_type.ID IN( {1} ) )   "
							+ "and (  :wName is null or  :wName = '''' or exc_station_type.Name =   :wName )    "
							+ "and (  :wRelevancyType<= 0 or exc_station_type.RelevancyType = :wRelevancyType)   "
							+ "and (  :wActive< 0 or exc_station_type.Active = :wActive) ",
					wInstance.Result, wID.size() > 0 ? StringUtils.Join(",", wID) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", StringUtils.Join(",", wID));
			wParamMap.put("wRelevancyType", wRelevancyType.getValue());
			wParamMap.put("wName", wName);
			wParamMap.put("wActive", wActive);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
			// wReader\[\"(\w+)\"\]

			for (Map<String, Object> wReader : wQueryResult) {

				long wIDSql = StringUtils.parseLong(wReader.get("ID"));
				String wNameSql = StringUtils.parseString(wReader.get("Name"));
				int wRelevancyTypeSql = StringUtils.parseInt(wReader.get("RelevancyType"));
				long wCreatorIDSql = StringUtils.parseLong(wReader.get("CreatorID"));
				Calendar wCreateTimeSql = StringUtils.parseCalendar(wReader.get("CreateTime"));
				long wEditorIDSql = StringUtils.parseLong(wReader.get("EditorID"));
				Calendar wEditTimeSql = StringUtils.parseCalendar(wReader.get("EditTime"));
				int wActiveSql = StringUtils.parseInt(wReader.get("Active"));

				EXCStationType wDevicePropertyModel = new EXCStationType();

				wDevicePropertyModel.ID = wIDSql;
				wDevicePropertyModel.Name = wNameSql;
				wDevicePropertyModel.RelevancyType = wRelevancyTypeSql;
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

//	public List<EXCStationType> SelectAll(BMSEmployee wLoginUser, String wName, QRTypes wRelevancyType, int wActive) {
//		List<EXCStationType> wResult = new ArrayList<EXCStationType>();
//		try {
//			this.SelectAll(wLoginUser, null, wName, wRelevancyType, wActive);
//		} catch (Exception e) {
//			logger.error(e.toString());
//		}
//		return wResult;
//	}

	public EXCStationType Select(BMSEmployee wLoginUser, long wID, OutResult<Integer> wErrorCode) {
		EXCStationType wResult = new EXCStationType();
		try {
			List<EXCStationType> wEXCStationTypeList = SelectAll(wLoginUser, StringUtils.parseList(new Long[] { wID }),
					"", QRTypes.Default, -1, wErrorCode);
			if (wEXCStationTypeList.size() != 1)
				return wResult;

			wResult = wEXCStationTypeList.get(0);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;

	}

	public EXCStationType Select(BMSEmployee wLoginUser, String wName, QRTypes wRelevancyType,
			OutResult<Integer> wErrorCode) {
		EXCStationType wResult = new EXCStationType();
		try {
			List<EXCStationType> wEXCStationTypeList = SelectAll(wLoginUser, null, wName, wRelevancyType, -1,
					wErrorCode);
			if (wEXCStationTypeList.size() != 1)
				return wResult;

			wResult = wEXCStationTypeList.get(0);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;

	}

	public long Update(BMSEmployee wLoginUser, EXCStationType wDeviceModel, OutResult<Integer> wErrorCode) {
		long wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wDeviceModel == null)
				return 0L;

			String wSQL = "";

			if (wDeviceModel.getID() <= 0) {
				wSQL = StringUtils.Format("INSERT INTO {0}.exc_station_type(Name,"
						+ "RelevancyType,CreatorID,CreateTime,EditorID,EditTime,"
						+ "Active)VALUES(  :Name ,:RelevancyType ,:CreatorID ," + "now() ,:CreatorID,now() ,:Active );",
						wInstance.Result);
			} else {
				wSQL = StringUtils.Format("UPDATE {0}.exc_station_type SET Name = :Name,"
						+ "RelevancyType = :RelevancyType, EditorID = :EditorID,"
						+ "EditTime = now(),Active = :Active WHERE ID =:ID;", wInstance.Result);
			}
			wSQL = this.DMLChange(wSQL);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new BeanPropertySqlParameterSource(wDeviceModel);

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
			String wSql = StringUtils.Format("UPDATE {0}.exc_station_type SET Active ={1} WHERE ID IN({2}) ;",
					wInstance.Result, String.valueOf(wActive), StringUtils.Join(",", wIDList));

			this.ExecuteSqlTransaction(wSql);
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	private EXCStationTypeDAO() {
		super();
	}

	public static EXCStationTypeDAO getInstance() {
		if (Instance == null)
			Instance = new EXCStationTypeDAO();
		return Instance;
	}

}
