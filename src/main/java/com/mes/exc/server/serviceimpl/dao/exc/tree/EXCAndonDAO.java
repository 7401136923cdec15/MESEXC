package com.mes.exc.server.serviceimpl.dao.exc.tree;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mes.exc.server.serviceimpl.dao.BaseDAO;
import com.mes.exc.server.serviceimpl.utils.exc.EXCConstants;
import com.mes.exc.server.service.po.OutResult;
import com.mes.exc.server.service.po.ServiceResult;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.service.po.exc.define.EXCAndonTypes;
import com.mes.exc.server.service.po.exc.define.EXCCallStatus;
import com.mes.exc.server.service.po.exc.tree.EXCAndon;
import com.mes.exc.server.service.mesenum.MESDBSource;
import com.mes.exc.server.service.utils.StringUtils;

public class EXCAndonDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(EXCAndonDAO.class);

	private static EXCAndonDAO Instance;

	/**
	 * 权限码
	 */
	private static int AccessCode = 0;

	public List<EXCAndon> SelectAll(BMSEmployee wLoginUser, int wWorkshopID, int wLineID, int wPlaceID,
			String wStationCode, long wSourceID, EXCAndonTypes wSourceType, int wShiftID, Calendar wStartTime,
			Calendar wEndTime, List<Integer> wStatus, OutResult<Integer> wErrorCode) {
		List<EXCAndon> wResult = new ArrayList<EXCAndon>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wStatus == null)
				wStatus = new ArrayList<Integer>();

			wStatus.removeIf(p -> p <= 0);

			if (wStationCode == null)
				wStationCode = "";

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 1, 1);
			if (wStartTime == null || wStartTime.compareTo(wBaseTime) < 0)
				wStartTime = wBaseTime;
			if (wEndTime == null || wEndTime.compareTo(wBaseTime) < 0)
				wEndTime = wBaseTime;
			if (wStartTime.compareTo(wEndTime) > 0)
				return wResult;

			String wSQL = StringUtils.Format("SELECT *  FROM {0}.exc_andon WHERE 1=1 "
					+ "and ( :wCompanyID <=0  or exc_andon.CompanyID= :wCompanyID)   "
					+ "and ( :wWorkshopID <=0  or exc_andon.WorkshopID= :wWorkshopID)   "
					+ "and ( :wLineID <=0  or exc_andon.LineID= :wLineID)  "
					+ "and ( :wShiftID <=0  or exc_andon.ShiftID= :wShiftID)  "
					+ "and ( :wSourceID <=0  or exc_andon.SourceID= :wSourceID)  "
					+ "and ( :wPlaceID <=0  or exc_andon.PlaceID= :wPlaceID)  "
					+ "and ( :wSourceType <=0  or exc_andon.SourceType= :wSourceType)  "
					+ "and ( :wStartTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or exc_andon.EditTime>= :wStartTime)  "
					+ "and ( :wEndTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or exc_andon.CreateTime<= :wEndTime)  "
					+ "and ( :wStationCode is null or :wStationCode = '''' or exc_andon.StationCode =  :wStationCode )"
					+ "and ( :wStatus is null or :wStatus = '''' or exc_andon.Status IN ({1})  ) ; ", wInstance.Result,
					wStatus.size() > 0 ? StringUtils.Join(",", wStatus) : "0");

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();
			wParamMap.put("wCompanyID", wLoginUser.getCompanyID());
			wParamMap.put("wWorkshopID", wWorkshopID);
			wParamMap.put("wLineID", wLineID);
			wParamMap.put("wStationCode", wStationCode);
			wParamMap.put("wSourceID", wSourceID);
			wParamMap.put("wSourceType", (int) wSourceType.getValue());
			wParamMap.put("wShiftID", wShiftID);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wPlaceID", wPlaceID);
			wParamMap.put("wStatus", StringUtils.Join(",", wStatus));

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
			// wReader\[\"(\w+)\"\]

			for (Map<String, Object> wReader : wQueryResult) {

				EXCAndon wEXCAndon = new EXCAndon();

				wEXCAndon.ID = StringUtils.parseLong(wReader.get("ID"));
				wEXCAndon.CompanyID = StringUtils.parseInt(wReader.get("CompanyID"));
				wEXCAndon.WorkshopID = StringUtils.parseInt(wReader.get("WorkshopID"));
				wEXCAndon.LineID = StringUtils.parseInt(wReader.get("LineID"));
				wEXCAndon.StationCode = StringUtils.parseString(wReader.get("StationCode"));
				wEXCAndon.EXType = StringUtils.parseString(wReader.get("EXType"));
				wEXCAndon.ShiftID = StringUtils.parseInt(wReader.get("ShiftID"));
				wEXCAndon.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wEXCAndon.EditTime = StringUtils.parseCalendar(wReader.get("EditTime"));
				wEXCAndon.EndTime = StringUtils.parseCalendar(wReader.get("EndTime"));
				wEXCAndon.ResponseLevel = StringUtils.parseInt(wReader.get("ResponseLevel"));
				wEXCAndon.ShiftTimes = StringUtils.parseInt(wReader.get("ShiftTimes"));
				wEXCAndon.Comment = StringUtils.parseString(wReader.get("Comment"));
				wEXCAndon.Status = StringUtils.parseInt(wReader.get("Status"));
				wEXCAndon.OperatorID = StringUtils
						.parseLongList(StringUtils.parseString(wReader.get("OperatorID")).split(","));
				wEXCAndon.CreatorID = StringUtils.parseInt(wReader.get("CreatorID"));
				wEXCAndon.SourceID = StringUtils.parseLong(wReader.get("SourceID"));
				wEXCAndon.SourceType = StringUtils.parseInt(wReader.get("SourceType"));
				wEXCAndon.PlaceID = StringUtils.parseInt(wReader.get("PlaceID"));
				wEXCAndon.CarName = StringUtils.parseString(wReader.get("CarName"));
				wResult.add(wEXCAndon);
			}

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public List<EXCAndon> SelectAndon(BMSEmployee wLoginUser, int wWorkshopID, int wLineID, long wSourceID,
			EXCAndonTypes wSourceType, OutResult<Integer> wErrorCode) {
		List<EXCAndon> wResult = new ArrayList<EXCAndon>();
		try {
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 1, 1);
			wResult = SelectAll(wLoginUser, wWorkshopID, wLineID, 0, "", -1, wSourceType,
					EXCConstants.getShiftID(wLoginUser), wBaseTime, wBaseTime, null, wErrorCode);
		} catch (Exception e) {

			logger.error(e.toString());
		}
		return wResult;
	}

	public List<EXCAndon> SelectAll(BMSEmployee wLoginUser, int wWorkshopID, int wLineID, int wPlaceID,
			String wStationCode, EXCAndonTypes wSourceType, int wShiftID, Calendar wStartTime, Calendar wEndTime,
			EXCCallStatus wStatus, OutResult<Integer> wErrorCode) {
		List<EXCAndon> wResult = new ArrayList<EXCAndon>();
		try {
			wResult = SelectAll(wLoginUser, wWorkshopID, wLineID, wPlaceID, wStationCode, -1, EXCAndonTypes.Default,
					wShiftID, wStartTime, wEndTime, StringUtils.parseList(new Integer[] { wStatus.getValue() }),
					wErrorCode);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public List<EXCAndon> SelectAll(BMSEmployee wLoginUser, int wShiftID, List<Integer> wStatus,
			OutResult<Integer> wErrorCode) {
		List<EXCAndon> wResult = new ArrayList<EXCAndon>();
		try {
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 1, 1);
			wResult = SelectAll(wLoginUser, -1, -1, 0, "", -1, EXCAndonTypes.Default, wShiftID, wBaseTime, wBaseTime,
					wStatus, wErrorCode);
		} catch (Exception e) {

			logger.error(e.toString());
		}
		return wResult;
	}

	public EXCAndon Select(BMSEmployee wLoginUser, long wSourceID, EXCAndonTypes wSourceType,
			OutResult<Integer> wErrorCode) {
		EXCAndon wResult = new EXCAndon();
		try {

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 1, 1);
			List<EXCAndon> wEXCAndonList = SelectAll(wLoginUser, -1, -1, 0, "", wSourceID, wSourceType, -1, wBaseTime,
					wBaseTime, null, wErrorCode);

			if (wEXCAndonList.size() != 1)
				return wResult;

			wResult = wEXCAndonList.get(0);
		} catch (Exception e) {

			logger.error(e.toString());
		}
		return wResult;

	}

	public void Update(BMSEmployee wLoginUser, List<EXCAndon> wEXCAndonList, OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wUpdateString = "UPDATE {0}.exc_andon SET CompanyID = {1}, WorkshopID = {2}, LineID = {3}, StationCode = ''{4}'',"
					+ " EXType = ''{5}'', Comment = ''{6}'',ShiftID = {7}, ShiftTimes = {8}, EditTime = now(), OperatorID = ''{9}'',  Status = {10},"
					+ " SourceID = {11}, SourceType = {12} , EndTime = ''{13}'' ,PlaceID={14} ,  ResponseLevel={15},CarName=''{16}'' WHERE ID = {17};";

			String wInsertString = "INSERT INTO {0}.exc_andon (CompanyID, WorkshopID, LineID, StationCode, "
					+ " EXType, Comment, ShiftID, ShiftTimes, CreateTime, EditTime, OperatorID, CreatorID, Status, SourceID, SourceType ,EndTime,PlaceID,ResponseLevel,CarName ) VALUES {1};";

			String wInsertValueString = "({0},{1},{2},''{3}'',''{4}'',''{5}'',{6},{7},now(),now(),''{8}'',{9},{10},{11},{12},''{13}'',{14},{15},''{16}'')";

			List<String> wInsertValueList = new ArrayList<String>();
			List<String> wSqlStringList = new ArrayList<String>();
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 1, 1);
			for (EXCAndon wEXCAndon : wEXCAndonList) {
				if (wEXCAndon.EndTime == null || wEXCAndon.EndTime.compareTo(wBaseTime) < 0)
					wEXCAndon.EndTime = wBaseTime;

				if (wEXCAndon.ID > 0) {
					wSqlStringList.add(StringUtils.Format(wUpdateString, wInstance.Result, wEXCAndon.CompanyID,
							wEXCAndon.WorkshopID, wEXCAndon.LineID, wEXCAndon.StationCode, wEXCAndon.EXType,
							wEXCAndon.Comment, wEXCAndon.ShiftID, wEXCAndon.ShiftTimes,
							StringUtils.Join(",", wEXCAndon.OperatorID), wEXCAndon.Status, wEXCAndon.SourceID,
							wEXCAndon.SourceType,
							StringUtils.parseCalendarToString(wEXCAndon.EndTime, "yyyy/MM/dd HH:mm:ss"),
							wEXCAndon.PlaceID, wEXCAndon.ResponseLevel, wEXCAndon.CarName, wEXCAndon.ID));
				} else {
					wInsertValueList.add(StringUtils.Format(wInsertValueString, wEXCAndon.CompanyID,
							wEXCAndon.WorkshopID, wEXCAndon.LineID, wEXCAndon.StationCode, wEXCAndon.EXType,
							wEXCAndon.Comment, wEXCAndon.ShiftID, wEXCAndon.ShiftTimes,
							StringUtils.Join(",", wEXCAndon.OperatorID), wEXCAndon.CreatorID, wEXCAndon.Status,
							wEXCAndon.SourceID, wEXCAndon.SourceType,
							StringUtils.parseCalendarToString(wEXCAndon.EndTime, "yyyy/MM/dd HH:mm:ss"),
							wEXCAndon.PlaceID, wEXCAndon.ResponseLevel, wEXCAndon.CarName));
				}
			}

			if (wInsertValueList.size() > 0)
				wSqlStringList.add(
						StringUtils.Format(wInsertString, wInstance.Result, StringUtils.Join(",", wInsertValueList)));

			ExecuteSqlTransaction(wSqlStringList);
		} catch (Exception e) {

			logger.error(e.toString());
		}
	}

	public void Update(BMSEmployee wLoginUser, EXCAndon wEXCAndon, OutResult<Integer> wErrorCode) {
		try {
			if (wEXCAndon == null)
				return;
			Update(wLoginUser, StringUtils.parseList(new EXCAndon[] { wEXCAndon }), wErrorCode);
		} catch (Exception e) {

			logger.error(e.toString());
		}
	}

	public void UpdateShiftID(BMSEmployee wLoginUser, int wShiftID, int wLeftShiftID, OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			if (wLeftShiftID == 0 || wShiftID == 0)
				return;
			String wSqlString = StringUtils.Format(
					"UPDATE {0}.exc_andon SET ShiftID = {1}, ShiftTimes =ShiftTimes -1 "
							+ " WHERE ID > 0 AND ShiftTimes > 0 AND ShiftID= {2}  AND Status Not IN ( {3} );",
					wInstance.Result, wShiftID, wLeftShiftID,
					StringUtils.Join(",", StringUtils.parseList(new Integer[] { (int) EXCCallStatus.Cancel.getValue(),
							(int) EXCCallStatus.Confirmed.getValue() })));

			ExecuteSqlTransaction(wSqlString);

		} catch (Exception e) {

			logger.error(e.toString());
		}
	}

	public void UpdateStatus(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSqlString = StringUtils.Format(
					"UPDATE {0}.exc_andon  SET  EditTime = now(),  ShiftTimes = 0 ,`Status` = {1}  WHERE ID > 0  "
							+ " AND EndTime >= str_to_date(''2010-01-01'', ''%Y-%m-%d'') AND  EndTime <= now()"
							+ "   AND Status Not IN ( {2} );",
					wInstance.Result, EXCCallStatus.Confirmed.getValue(),
					StringUtils.Join(",", StringUtils.parseList(new Integer[] { (int) EXCCallStatus.Cancel.getValue(),
							(int) EXCCallStatus.Confirmed.getValue() })));

			ExecuteSqlTransaction(wSqlString);
		} catch (Exception e) {

			logger.error(e.toString());
		}
	}

	private EXCAndonDAO() {
		super();
	}

	public static EXCAndonDAO getInstance() {
		if (Instance == null)
			Instance = new EXCAndonDAO();
		return Instance;
	}

}
