package com.mes.exc.server.serviceimpl.dao.exc.tree;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder; 

import com.mes.exc.server.serviceimpl.dao.BaseDAO;
import com.mes.exc.server.serviceimpl.utils.exc.EXCConstants;
import com.mes.exc.server.service.po.OutResult;
import com.mes.exc.server.service.po.ServiceResult;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.service.po.exc.action.EXCCallAction;
import com.mes.exc.server.service.po.exc.define.EXCCallStatus;
import com.mes.exc.server.service.po.exc.tree.EXCCallDispatch;
import com.mes.exc.server.service.mesenum.MESDBSource;
import com.mes.exc.server.service.utils.StringUtils;

public class EXCCallDispatchDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(EXCCallDispatchDAO.class);

	private static EXCCallDispatchDAO Instance;

	/**
	 * 权限码
	 */
	private static int AccessCode = 0;

	public List<EXCCallDispatch> SelectAll(BMSEmployee wLoginUser, List<Long> wID, long wTaskID, long wOperatorID,
			long wCreatorID, int wShiftID, Calendar wStartTime, Calendar wEndTime, List<Integer> wStatus,
			OutResult<Integer> wErrorCode) {
		List<EXCCallDispatch> wResult = new ArrayList<EXCCallDispatch>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wID == null)
				wID = new ArrayList<Long>();
			if (wStatus == null)
				wStatus = new ArrayList<Integer>();

			String wSQL = StringUtils.Format("SELECT exc_dispatch.ID,     exc_dispatch.TaskID, "
					+ "    exc_dispatch.CompanyID,     exc_dispatch.CreatorID, "
					+ "    exc_dispatch.OperatorID,     exc_dispatch.CreateTime, "
					+ "    exc_dispatch.EditTime,     exc_dispatch.Status, "
					+ "    exc_dispatch.ShiftID FROM {0}.exc_dispatch  WHERE  1 = 1 "
					+ "and ( :wID is null or :wID = '''' or exc_dispatch.ID IN( {1} ) )    "
					+ "and ( :wStatus is null or :wStatus = '''' or exc_dispatch.Status IN( {2} ) )  "
					+ "and ( :wTaskID<= 0 or exc_dispatch.TaskID =:wTaskID)     "
					+ "and ( :wShiftID<= 0 or exc_dispatch.ShiftID =:wShiftID)  "
					+ "and ( :wCompanyID<= 0 or exc_dispatch.CompanyID =:wCompanyID)  "
					+ "and ( :wCreatorID<= 0 or exc_dispatch.CreatorID =:wCreatorID)  "
					+ "and ( :wOperatorID<= 0 or exc_dispatch.OperatorID =:wOperatorID)     "
					+ "and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <= exc_dispatch.EditTime) "
					+ "and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >= exc_dispatch.CreateTime) ",
					wInstance.Result, wID.size() > 0 ? StringUtils.Join(",", wID) : "0",
					wStatus.size() > 0 ? StringUtils.Join(",", wStatus) : "0");
			wSQL = this.DMLChange(wSQL);

			// wReader\[\"(\w+)\"\]
			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", StringUtils.Join(",", wID));
			wParamMap.put("wCompanyID", wLoginUser.getCompanyID());
			wParamMap.put("wTaskID", wTaskID);
			wParamMap.put("wOperatorID", wOperatorID);
			wParamMap.put("wCreatorID", wCreatorID);
			wParamMap.put("wShiftID", wShiftID);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wStatus", StringUtils.Join(",", wStatus));

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
			// wReader\[\"(\w+)\"\]
			for (Map<String, Object> wReader : wQueryResult) {

				long wIDSql = StringUtils.parseLong(wReader.get("ID"));
				long wTaskIDSql = StringUtils.parseLong(wReader.get("TaskID"));
				int wCompanyIDSql = StringUtils.parseInt(wReader.get("CompanyID"));
				long wCreatorIDSql = StringUtils.parseLong(wReader.get("CreatorID"));
				long wOperatorIDSql = StringUtils.parseLong(wReader.get("OperatorID"));
				Calendar wCreateTimeSql = StringUtils.parseCalendar(wReader.get("CreateTime"));
				Calendar wEditTimeSql = StringUtils.parseCalendar(wReader.get("EditTime"));
				int wStatusSql = StringUtils.parseInt(wReader.get("Status"));
				int wShiftIDSql = StringUtils.parseInt(wReader.get("ShiftID"));

				EXCCallDispatch wDevicePropertyModel = new EXCCallDispatch();

				wDevicePropertyModel.ID = wIDSql;
				wDevicePropertyModel.CompanyID = wCompanyIDSql;
				wDevicePropertyModel.TaskID = wTaskIDSql;
				wDevicePropertyModel.CreatorID = wCreatorIDSql;
				wDevicePropertyModel.OperatorID = wOperatorIDSql;
				wDevicePropertyModel.CreateTime = wCreateTimeSql;
				wDevicePropertyModel.EditTime = wEditTimeSql;
				wDevicePropertyModel.Status = wStatusSql;
				wDevicePropertyModel.ShiftID = wShiftIDSql;
				wDevicePropertyModel.ActionList = new ArrayList<EXCCallAction>();

				wResult.add(wDevicePropertyModel);
			}

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public List<EXCCallDispatch> SelectAll(BMSEmployee wLoginUser, long wTaskID, long wOperatorID, long wCreatorID,
			int wShiftID, Calendar wStartTime, Calendar wEndTime, EXCCallStatus wStatus,
			OutResult<Integer> wErrorCode) {
		List<EXCCallDispatch> wResult = new ArrayList<EXCCallDispatch>();

		try {
			List<Integer> wStatusList = new ArrayList<Integer>();
			if (wStatus != EXCCallStatus.Default)
				wStatusList.add(wStatus.getValue());

			wResult = SelectAll(wLoginUser, null, wTaskID, wOperatorID, wCreatorID, wShiftID, wStartTime, wEndTime,
					wStatusList, wErrorCode);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public EXCCallDispatch Select(BMSEmployee wLoginUser, long wID, OutResult<Integer> wErrorCode) {
		EXCCallDispatch wResult = new EXCCallDispatch();
		try {
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 1, 1);
			List<EXCCallDispatch> wEXCCallDispatchList = SelectAll(wLoginUser,
					StringUtils.parseList(new Long[] { wID }), -1, -1, -1, -1, wBaseTime, wBaseTime, null, wErrorCode);

			if (wEXCCallDispatchList.size() != 1)
				return wResult;

			wResult = wEXCCallDispatchList.get(0);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;

	}

	public void Update(BMSEmployee wLoginUser, List<EXCCallDispatch> wEXCCallDispatchList,
			OutResult<Integer> wErrorCode) {
		try {
			if (wEXCCallDispatchList == null || wEXCCallDispatchList.size() < 0) {
				return;
			}

			for (EXCCallDispatch excCallDispatch : wEXCCallDispatchList) {
				Update(wLoginUser, excCallDispatch, wErrorCode);
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	public long Update(BMSEmployee wLoginUser, EXCCallDispatch wEXCCallDispatch, OutResult<Integer> wErrorCode) {
		long wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wEXCCallDispatch == null)
				return 0L;

			if (wEXCCallDispatch.ID <= 0) {
				Calendar wBaseTime = Calendar.getInstance();
				wBaseTime.set(2000, 1, 1);
				// 插入时判断是否已存在接收人列表中
				List<EXCCallDispatch> wEXCCallDispatchList = SelectAll(wLoginUser, null, wEXCCallDispatch.TaskID,
						wEXCCallDispatch.OperatorID, -1, EXCConstants.getShiftID(wLoginUser), wBaseTime, wBaseTime,
						StringUtils.parseList(new Integer[] { (int) EXCCallStatus.Default.getValue(),
								(int) EXCCallStatus.WaitRespond.getValue(),
								(int) EXCCallStatus.NoticeWaitRespond.getValue(),
								(int) EXCCallStatus.OnSiteRespond.getValue(),
								(int) EXCCallStatus.WaitConfirm.getValue(), (int) EXCCallStatus.Rejected.getValue() }),
						wErrorCode);

				if (wEXCCallDispatchList.size() > 0) {
					logger.info(StringUtils.Format("insert EXCCallDispatch error : Dispatcher: {0} is exits DB",
							wEXCCallDispatch.OperatorID));
					return 0L;
				}

			}

			String wSQL = "";

			if (wEXCCallDispatch.getID() <= 0) {
				wSQL = StringUtils.Format(
						" INSERT INTO {0}.exc_dispatch ( TaskID, CompanyID, "
								+ "CreatorID, OperatorID, CreateTime, EditTime, Status, ShiftID) VALUES ( :wTaskID, "
								+ ":wCompanyID, :wCreatorID, :wOperatorID, now(), now(), :wStatus, :wShiftID);",
						wInstance.Result);
			} else {
				wSQL = StringUtils.Format(
						"UPDATE {0}.exc_dispatch SET  "
								+ " EditTime=now(), Status = :wStatus, ShiftID = :wShiftID WHERE ID = :wID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);
			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wEXCCallDispatch.ID);
			wParamMap.put("wCompanyID", wEXCCallDispatch.CompanyID);
			wParamMap.put("wTaskID", wEXCCallDispatch.TaskID);
			wParamMap.put("wCreatorID", wEXCCallDispatch.CreatorID);
			wParamMap.put("wOperatorID", wEXCCallDispatch.OperatorID);
			wParamMap.put("wShiftID", wEXCCallDispatch.ShiftID);
			wParamMap.put("wStatus", wEXCCallDispatch.Status);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			wResult = nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);
			if (wEXCCallDispatch.getID() <= 0) {
				wResult = keyHolder.getKey().longValue();
				wEXCCallDispatch.setID(wResult);

			} else {
				wResult = wEXCCallDispatch.getID();
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wEXCCallDispatch.ID;
	}

	public void UpdateByTask(BMSEmployee wLoginUser, long wTaskID, EXCCallStatus wStatus,
			OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSqlString = StringUtils.Format(
					"UPDATE {0}.exc_dispatch SET EditTime=now(),  Status = {1}  WHERE ID > 0 AND  TaskID={2}; ",
					wInstance.Result, String.valueOf(wStatus.getValue()), String.valueOf(wTaskID));
			ExecuteSqlTransaction(wSqlString);
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	public void Insert(BMSEmployee wLoginUser, List<EXCCallDispatch> wEXCCallDispatchList,
			OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			if (wEXCCallDispatchList == null || wEXCCallDispatchList.size() < 1)
				return;

			String wSqlString = "INSERT INTO {0}.exc_dispatch ( TaskID,CompanyID,CreatorID,OperatorID,CreateTime,EditTime,Status,ShiftID) VALUES {1} ";

			List<String> wSqlValues = new ArrayList<String>();

			for (EXCCallDispatch wEXCCallDispatch : wEXCCallDispatchList) {
				wSqlValues.add(StringUtils.Format("({0},{1},{2},{3},now(),now(),{4},{5})",
						String.valueOf(wEXCCallDispatch.TaskID), String.valueOf(wEXCCallDispatch.CompanyID),
						String.valueOf(wEXCCallDispatch.CreatorID), String.valueOf(wEXCCallDispatch.OperatorID),
						String.valueOf(wEXCCallDispatch.Status), String.valueOf(wEXCCallDispatch.ShiftID)));
			}

			wSqlString = StringUtils.Format(wSqlString, wInstance.Result, StringUtils.Join(",", wSqlValues));

			ExecuteSqlTransaction(wSqlString);
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	private EXCCallDispatchDAO() {
		super();
	}

	public static EXCCallDispatchDAO getInstance() {
		if (Instance == null)
			Instance = new EXCCallDispatchDAO();
		return Instance;
	}

}
