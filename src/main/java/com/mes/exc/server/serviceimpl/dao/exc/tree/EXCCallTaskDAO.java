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

import com.mes.exc.server.serviceimpl.CoreServiceImpl;
import com.mes.exc.server.serviceimpl.dao.BaseDAO;
import com.mes.exc.server.serviceimpl.utils.exc.EXCConstants;
import com.mes.exc.server.service.po.APIResult;
import com.mes.exc.server.service.po.OutResult;
import com.mes.exc.server.service.po.ServiceResult;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.service.po.exc.define.EXCAndonTypes;
import com.mes.exc.server.service.po.exc.define.EXCCallStatus;
import com.mes.exc.server.service.po.exc.tree.EXCAndon;
import com.mes.exc.server.service.po.exc.tree.EXCCallTask;
import com.mes.exc.server.service.po.fmc.FMCWorkspace;
import com.mes.exc.server.service.mesenum.MESDBSource;
import com.mes.exc.server.service.utils.StringUtils;

public class EXCCallTaskDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(EXCCallTaskDAO.class);

	private static EXCCallTaskDAO Instance;

	/**
	 * 权限码
	 */
	private static int AccessCode = 0;

	public List<EXCCallTask> SelectAll(BMSEmployee wLoginUser, List<Long> wID, long wApplyID, String wStationNo,
			long wStationType, long wStationID, int wRespondLevel, int wDisplayBoard, int wOnSite, long wApplicantID,
			long wOperatorID, long wConfirmID, int wShiftID, String wPartNo, Calendar wStartTime, Calendar wEndTime,
			List<Integer> wStatus, int wExceptionType, OutResult<Integer> wErrorCode) {
		List<EXCCallTask> wResult = new ArrayList<EXCCallTask>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wID == null)
				wID = new ArrayList<Long>();
			if (wPartNo == null)
				wPartNo = "";
			if (wID.contains(0L) && wID.size() == 1)
				wID.clear();

			if (wStatus == null)
				wStatus = new ArrayList<Integer>();

			if (wStatus.contains(0) && wStatus.size() == 1)
				wStatus.clear();

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

			String wSQL = StringUtils.Format("SELECT  exc_task.ID ,      exc_task.ApplyID ,"
					+ "     exc_task.CompanyID ,      exc_station.StationType ,"
					+ "     exc_station_type.Name  as StationTypeName,      exc_apply.StationID ,"
					+ "     exc_station.StationNo ,      exc_task.ExceptionTypeID , "
					+ "     exc_ex_type.Name  as ExceptionTypeName, 	 exc_apply.ApplicantID ,"
					+ "     exc_apply.ApplicantTime ,      exc_task.OperatorID ,"
					+ "     exc_task.ConfirmID ,      exc_task.RespondLevel ,      exc_apply.OnSite ,"
					+ "     exc_apply.DisplayBoard ,      exc_task.CreateTime ,      exc_task.EditTime ,"
					+ "     exc_task.ExpireTime ,      exc_task.ReportTimes ,"
					+ "     exc_task.ForwardTimes ,      exc_apply.Comment , exc_apply.PartNo , exc_apply.PlaceID ,"
					+ "     exc_task.Remark ,      exc_task.ImageList ,"
					+ "     exc_task.Status ,      exc_task.ShiftID  FROM  {0}.exc_task  ,  {0}.exc_apply  , "
					+ "	  {0}.exc_station  ,	 {0}.exc_station_type  ,	 {0}.exc_ex_type "
					+ "WHERE   exc_task.ApplyID  =  exc_apply.ID  "
					+ "and    exc_apply.StationID  =  exc_station.ID      "
					+ "and    exc_station.StationType =  exc_station_type.ID   "
					+ "and    exc_task.ExceptionTypeID  =  exc_ex_type.ID   "
					+ "and ( :wID is null or :wID = '''' or  exc_task.ID  IN( {1} ) )   "
					+ "and ( :wStatus is null or :wStatus = '''' or  exc_task.Status  IN( {2} ) )  "
					+ "and ( :wCompanyID<= 0 or  exc_task.CompanyID  =:wCompanyID)   "
					+ "and ( :wApplyID<= 0 or  exc_task.ApplyID  =:wApplyID)   "
					+ "and ( :wStationNo is null or :wStationNo = '''' or  exc_station.StationNo  =  :wStationNo )  "
					+ "and ( :wPartNo is null or :wPartNo = '''' or  exc_apply.PartNo  =  :wPartNo )  "
					+ "and ( :wStationType<= 0 or  exc_station.StationType  =:wStationType) "
					+ "and ( :wStationID<= 0 or  exc_apply.StationID  =:wStationID)  "
					+ "and ( :wRespondLevel<= 0 or  exc_task.RespondLevel  =:wRespondLevel)  "
					+ "and ( :wDisplayBoard< 0 or  exc_apply.DisplayBoard  =:wDisplayBoard)  "
					+ "and ( :wOnSite< 0 or  exc_apply.OnSite  =:wOnSite)  "
					+ "and ( :wApplicantID <= 0 or  exc_apply.ApplicantID   = :wApplicantID)   "
					+ "and ( :wOperatorID <= 0 or  exc_task.OperatorID   = :wOperatorID)  "
					+ "and ( :wConfirmID <= 0 or  exc_task.ConfirmID   = :wConfirmID)   "

					+ "and ( :wExceptionType <= 0 or  exc_task.ExceptionTypeID   = :wExceptionType)   "

					+ "and ( :wShiftID <= 0 or  exc_task.ShiftID   = :wShiftID)   "
					+ "and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <=  exc_task.EditTime )"
					+ "and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >=  exc_task.CreateTime )  ",
					wInstance.Result, wID.size() > 0 ? StringUtils.Join(",", wID) : "0",
					wStatus.size() > 0 ? StringUtils.Join(",", wStatus) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", StringUtils.Join(",", wID));
			wParamMap.put("wCompanyID", wLoginUser.getCompanyID());
			wParamMap.put("wApplyID", wApplyID);
			wParamMap.put("wStationNo", wStationNo);
			wParamMap.put("wStationType", wStationType);
			wParamMap.put("wStationID", wStationID);
			wParamMap.put("wRespondLevel", wRespondLevel);
			wParamMap.put("wDisplayBoard", wDisplayBoard);
			wParamMap.put("wOnSite", wOnSite);
			wParamMap.put("wApplicantID", wApplicantID);
			wParamMap.put("wOperatorID", wOperatorID);
			wParamMap.put("wConfirmID", wConfirmID);
			wParamMap.put("wShiftID", wShiftID);
			wParamMap.put("wPartNo", wPartNo);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wExceptionType", wExceptionType);

			wParamMap.put("wStatus", StringUtils.Join(",", wStatus));

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
			// wReader\[\"(\w+)\"\]
			for (Map<String, Object> wReader : wQueryResult) {

				EXCCallTask wEXCCallTask = new EXCCallTask();

				wEXCCallTask.ID = StringUtils.parseLong(wReader.get("ID"));
				wEXCCallTask.Remark = StringUtils.parseString(wReader.get("Remark"));
				wEXCCallTask.CompanyID = StringUtils.parseInt(wReader.get("CompanyID"));
				wEXCCallTask.ApplyID = StringUtils.parseLong(wReader.get("ApplyID"));
				wEXCCallTask.StationType = StringUtils.parseLong(wReader.get("StationType"));
				wEXCCallTask.StationTypeName = StringUtils.parseString(wReader.get("StationTypeName"));
				wEXCCallTask.StationID = StringUtils.parseLong(wReader.get("StationID"));
				wEXCCallTask.StationNo = StringUtils.parseString(wReader.get("StationNo"));
				wEXCCallTask.ExceptionTypeID = StringUtils.parseLong(wReader.get("ExceptionTypeID"));
				wEXCCallTask.ExceptionTypeName = StringUtils.parseString(wReader.get("ExceptionTypeName"));
				wEXCCallTask.RespondLevel = StringUtils.parseInt(wReader.get("RespondLevel"));
				wEXCCallTask.ApplicantID = StringUtils.parseLong(wReader.get("ApplicantID"));
				wEXCCallTask.ApplicantTime = StringUtils.parseCalendar(wReader.get("ApplicantTime"));
				wEXCCallTask.OperatorID = StringUtils
						.parseLongList(StringUtils.parseString(wReader.get("OperatorID")).split(",|;"));
				wEXCCallTask.ConfirmID = StringUtils.parseLong(wReader.get("ConfirmID"));
				wEXCCallTask.OnSite = StringUtils.parseInt(wReader.get("OnSite")) == 1;
				wEXCCallTask.DisplayBoard = StringUtils.parseInt(wReader.get("DisplayBoard")) == 1;
				wEXCCallTask.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wEXCCallTask.EditTime = StringUtils.parseCalendar(wReader.get("EditTime"));
				wEXCCallTask.ExpireTime = StringUtils.parseCalendar(wReader.get("ExpireTime"));
				wEXCCallTask.ReportTimes = StringUtils.parseInt(wReader.get("ReportTimes"));
				wEXCCallTask.ForwardTimes = StringUtils.parseInt(wReader.get("ForwardTimes"));
				wEXCCallTask.Comment = StringUtils.parseString(wReader.get("Comment"));
				wEXCCallTask.ShiftID = StringUtils.parseInt(wReader.get("ShiftID"));
				wEXCCallTask.PlaceID = StringUtils.parseInt(wReader.get("PlaceID"));

				APIResult wPlaceResult = CoreServiceImpl.getInstance().FMC_QueryWorkspace(wLoginUser,
						wEXCCallTask.PlaceID, "");
				FMCWorkspace wSpace = wPlaceResult.Custom("list", FMCWorkspace.class);
				if (wSpace != null && wSpace.ID > 0) {
					wEXCCallTask.PlaceNo = wSpace.Code;
				}
//				wEXCCallTask.PlaceNo = CoreServiceImpl.getInstance().FMC_QueryWorkspace(wLoginUser,
//						wEXCCallTask.PlaceID,"").Info(FMCWorkspace.class).Code;

				wEXCCallTask.PartNo = StringUtils.parseString(wReader.get("PartNo"));
				wEXCCallTask.ImageList = StringUtils
						.parseList(StringUtils.parseString(wReader.get("ImageList")).split(" \\|;\\| "));
				wEXCCallTask.Status = StringUtils.parseInt(wReader.get("Status"));
				if (StringUtils.isEmpty(wEXCCallTask.Comment)) {
					wEXCCallTask.Comment = wEXCCallTask.Remark;
				}

				wResult.add(wEXCCallTask);

			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public List<EXCCallTask> SelectAllByDispatcher(BMSEmployee wLoginUser, long wDispatcherID, int wShiftID,
			Calendar wStartTime, Calendar wEndTime, List<Integer> wStatus, OutResult<Integer> wErrorCode) {
		List<EXCCallTask> wResult = new ArrayList<EXCCallTask>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wStatus == null)
				wStatus = new ArrayList<Integer>();

			String wSQL = StringUtils.Format("SELECT  exc_task.ID ,      exc_task.ApplyID ,"
					+ "     exc_task.CompanyID ,      exc_station.StationType ,"
					+ "     exc_station_type.Name  as StationTypeName,      exc_apply.StationID ,"
					+ "     exc_station.StationNo ,      exc_task.ExceptionTypeID , "
					+ "     exc_ex_type.Name  as ExceptionTypeName,    exc_apply.ApplicantID ,"
					+ "     exc_apply.ApplicantTime ,      exc_dispatch.OperatorID ,"
					+ "     exc_task.ConfirmID ,      exc_task.RespondLevel ,      exc_apply.OnSite ,"
					+ "     exc_apply.DisplayBoard ,      exc_task.CreateTime ,      exc_task.EditTime ,"
					+ "     exc_task.ExpireTime ,      exc_task.ReportTimes ,      exc_task.ForwardTimes ,"
					+ "     exc_apply.Comment , exc_apply.PartNo , exc_apply.PlaceID , "
					+ "     exc_task.Remark,     exc_task.ImageList ,      exc_dispatch.Status ,"
					+ "     exc_dispatch.ID  as DispatchID,      exc_dispatch.ShiftID "
					+ "FROM  {0}.exc_task  ,  {0}.exc_apply  ,   {0}.exc_dispatch  , "
					+ "    {0}.exc_station  ,   {0}.exc_station_type  ,   {0}.exc_ex_type "
					+ "WHERE   exc_task.ApplyID  =  exc_apply.ID  "
					+ "and    exc_apply.StationID  =  exc_station.ID     "
					+ "and    exc_task.ID  =  {0}.exc_dispatch.TaskID "
					+ "and    exc_station.StationType =  exc_station_type.ID   "
					+ "and    exc_task.ExceptionTypeID  =  exc_ex_type.ID    "
					+ "and ( :wStatus is null or :wStatus = '''' or  exc_task.Status  IN( {1} ) )  "
					+ "and ( :wCompanyID<= 0 or  exc_task.CompanyID  =:wCompanyID)    "
					+ "and ( :wDispatcherID <= 0 or  exc_dispatch.OperatorID   = :wDispatcherID)   "
					+ "and ( :wShiftID <= 0 or  exc_dispatch.ShiftID   = :wShiftID)   "
					+ "and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <=  exc_dispatch.CreateTime )"
					+ "and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >=  exc_dispatch.CreateTime )  "
					+ "order by exc_dispatch.ID desc", wInstance.Result,
					wStatus.size() > 0 ? StringUtils.Join(",", wStatus) : "0");
			wSQL = this.DMLChange(wSQL);
			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wCompanyID", wLoginUser.getCompanyID());
			wParamMap.put("wDispatcherID", wDispatcherID);
			wParamMap.put("wShiftID", wShiftID);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wStatus", StringUtils.Join(",", wStatus));

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
			// wReader\[\"(\w+)\"\]
			for (Map<String, Object> wReader : wQueryResult) {

				long wIDSql = StringUtils.parseLong(wReader.get("ID"));
				long wApplyIDSql = StringUtils.parseLong(wReader.get("ApplyID"));

				long wDispatchIDSql = StringUtils.parseLong(wReader.get("DispatchID"));

				int wCompanyIDSql = StringUtils.parseInt(wReader.get("CompanyID"));
				long wStationTypeSql = StringUtils.parseLong(wReader.get("StationType"));
				String wStationTypeNameSql = StringUtils.parseString(wReader.get("StationTypeName"));
				long wStationIDSql = StringUtils.parseLong(wReader.get("StationID"));
				String wStationNoSql = StringUtils.parseString(wReader.get("StationNo"));
				long wExceptionTypeIDSql = StringUtils.parseLong(wReader.get("ExceptionTypeID"));
				String wExceptionTypeNameSql = StringUtils.parseString(wReader.get("ExceptionTypeName"));
				long wApplicantIDSql = StringUtils.parseLong(wReader.get("ApplicantID"));
				Calendar wApplicantTimeSql = StringUtils.parseCalendar(wReader.get("ApplicantTime"));
				String wOperatorIDSql = StringUtils.parseString(wReader.get("OperatorID"));
				long wConfirmIDSql = StringUtils.parseLong(wReader.get("ConfirmID"));
				int wRespondLevelSql = StringUtils.parseInt(wReader.get("RespondLevel"));
				boolean wOnSiteSql = StringUtils.parseInt(wReader.get("OnSite")) == 1;
				boolean wDisplayBoardSql = StringUtils.parseInt(wReader.get("DisplayBoard")) == 1;
				Calendar wCreateTimeSql = StringUtils.parseCalendar(wReader.get("CreateTime"));
				Calendar wEditTimeSql = StringUtils.parseCalendar(wReader.get("EditTime"));
				Calendar wExpireTimeSql = StringUtils.parseCalendar(wReader.get("ExpireTime"));
				int wReportTimesSql = StringUtils.parseInt(wReader.get("ReportTimes"));
				int wForwardTimesSql = StringUtils.parseInt(wReader.get("ForwardTimes"));
				String wCommentSql = StringUtils.parseString(wReader.get("Comment"));
				String wRemarkSql = StringUtils.parseString(wReader.get("Remark"));
				String wImageListSql = StringUtils.parseString(wReader.get("ImageList"));
				int wStatusSql = StringUtils.parseInt(wReader.get("Status"));
				int wShiftIDSql = StringUtils.parseInt(wReader.get("ShiftID"));
				int wPlaceIDSql = StringUtils.parseInt(wReader.get("PlaceID"));
				String wPartNoSql = StringUtils.parseString(wReader.get("PartNo"));

				EXCCallTask wEXCCallTask = new EXCCallTask();

				wEXCCallTask.ID = wIDSql;
				wEXCCallTask.ApplyID = wApplyIDSql;
				wEXCCallTask.DispatchID = wDispatchIDSql;
				wEXCCallTask.CompanyID = wCompanyIDSql;
				wEXCCallTask.StationType = wStationTypeSql;
				wEXCCallTask.StationTypeName = wStationTypeNameSql;
				wEXCCallTask.StationID = wStationIDSql;
				wEXCCallTask.StationNo = wStationNoSql;
				wEXCCallTask.ExceptionTypeID = wExceptionTypeIDSql;
				wEXCCallTask.ExceptionTypeName = wExceptionTypeNameSql;
				wEXCCallTask.RespondLevel = wRespondLevelSql;
				wEXCCallTask.ApplicantID = wApplicantIDSql;
				wEXCCallTask.ApplicantTime = wApplicantTimeSql;
				wEXCCallTask.OperatorID = StringUtils.parseLongList(wOperatorIDSql.split(",|;"));
				wEXCCallTask.ConfirmID = wConfirmIDSql;
				wEXCCallTask.OnSite = wOnSiteSql;
				wEXCCallTask.DisplayBoard = wDisplayBoardSql;
				wEXCCallTask.CreateTime = wCreateTimeSql;
				wEXCCallTask.EditTime = wEditTimeSql;
				wEXCCallTask.ExpireTime = wExpireTimeSql;
				wEXCCallTask.ReportTimes = wReportTimesSql;
				wEXCCallTask.ForwardTimes = wForwardTimesSql;
				wEXCCallTask.PartNo = wPartNoSql;
				wEXCCallTask.Remark = wRemarkSql;
				wEXCCallTask.Comment = wCommentSql;
				wEXCCallTask.ShiftID = wShiftIDSql;
				wEXCCallTask.ImageList = StringUtils.parseList(wImageListSql.split(" \\|;\\| "));
				wEXCCallTask.Status = wStatusSql;
				wEXCCallTask.PlaceID = wPlaceIDSql;

				APIResult wApiResult = CoreServiceImpl.getInstance().FMC_QueryWorkspace(wLoginUser,
						wEXCCallTask.PlaceID, "");
				FMCWorkspace wSpace = wApiResult.Custom("list", FMCWorkspace.class);
				if (wSpace != null && wSpace.ID > 0) {
					wEXCCallTask.PlaceNo = wSpace.Code;
				}

//				wEXCCallTask.PlaceNo = wApiResult.Info(FMCWorkspace.class).PartNo;
				if (StringUtils.isEmpty(wEXCCallTask.Comment)) {
					wEXCCallTask.Comment = wEXCCallTask.Remark;
				}
				wResult.add(wEXCCallTask);

			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public List<EXCCallTask> SelectAll(BMSEmployee wLoginUser, List<Long> wIDList, OutResult<Integer> wErrorCode) {
		List<EXCCallTask> wResult = new ArrayList<EXCCallTask>();
		try {
			if (wIDList == null || wIDList.size() < 0)
				return wResult;
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 1, 1);
			List<Long> wSelectList = new ArrayList<Long>();
			for (int i = 0; i < wIDList.size(); i++) {
				wSelectList.add(wIDList.get(i));
				if (i % 25 == 0) {
					wResult.addAll(SelectAll(wLoginUser, wSelectList, 0, "", 0, 0, 0, -1, -1, 0, 0, 0, 0, "", wBaseTime,
							wBaseTime, new ArrayList<Integer>(), -1, wErrorCode));

					wSelectList.clear();
				}
				if (i == wIDList.size() - 1) {
					if (wSelectList.size() > 0)
						wResult.addAll(SelectAll(wLoginUser, wSelectList, 0, "", 0, 0, 0, -1, -1, 0, 0, 0, 0, "",
								wBaseTime, wBaseTime, new ArrayList<Integer>(), -1, wErrorCode));
					break;
				}
			}

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public List<EXCCallTask> SelectAll(BMSEmployee wLoginUser, long wApplyID, String wStationNo, long wStationType,
			long wStationID, int wRespondLevel, int wDisplayBoard, int wOnSite, long wApplicantID, long wOperatorID,
			long wConfirmID, int wShiftID, String wPartNo, Calendar wStartTime, Calendar wEndTime,
			EXCCallStatus wStatus, int wExceptionType, OutResult<Integer> wErrorCode) {
		List<EXCCallTask> wResult = new ArrayList<EXCCallTask>();
		try {
			wResult = SelectAll(wLoginUser, null, wApplyID, wStationNo, wStationType, wStationID, wRespondLevel,
					wDisplayBoard, wOnSite, wApplicantID, wOperatorID, wConfirmID, wShiftID, wPartNo, wStartTime,
					wEndTime,
					((int) wStatus.getValue()) >= 0 ? StringUtils.parseList(new Integer[] { (int) wStatus.getValue() })
							: null,
					wExceptionType, wErrorCode);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public List<EXCCallTask> SelectAll(BMSEmployee wLoginUser, long wOperatorID, int wShiftID,
			OutResult<Integer> wErrorCode) {
		List<EXCCallTask> wResult = new ArrayList<EXCCallTask>();
		try {
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 1, 1);

			wResult = SelectAll(wLoginUser, null, 0, "", 0, 0, 0, -1, -1, wOperatorID, -1, -1, wShiftID, "", wBaseTime,
					wBaseTime, null, -1, wErrorCode);

			wResult.addAll(SelectAll(wLoginUser, null, 0, "", 0, 0, 0, -1, -1, -1, -1, wOperatorID, wShiftID, "",
					wBaseTime, wBaseTime, null, -1, wErrorCode));

			wResult.addAll(
					SelectAllByDispatcher(wLoginUser, wOperatorID, wShiftID, wBaseTime, wBaseTime, null, wErrorCode));
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public EXCCallTask Select(BMSEmployee wLoginUser, long wID, OutResult<Integer> wErrorCode) {
		EXCCallTask wResult = new EXCCallTask();
		try {
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 1, 1);

			List<EXCCallTask> wEXCCallTaskList = SelectAll(wLoginUser, StringUtils.parseList(new Long[] { wID }), 0, "",
					0, 0, 0, -1, -1, -1, -1, -1, -1, "", wBaseTime, wBaseTime, null, -1, wErrorCode);

			if (wEXCCallTaskList.size() != 1)
				return wResult;

			wResult = wEXCCallTaskList.get(0);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;

	}

	public int GetMessageCount(BMSEmployee wLoginUser, long wOperatorID, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT count( exc_task.ID ) as  MsgNum "
					+ "FROM  {0}.exc_task  ,  {0}.exc_dispatch  WHERE   exc_task.ID  =  {0}.exc_dispatch.TaskID  "
					+ "and   exc_task.CompanyID  =:wCompanyID"
					+ "and  ( ( exc_task.ConfirmID   = :wOperatorID and   exc_task.Status  IN( 4 )  ) OR  ( exc_dispatch.OperatorID   = :wOperatorID and   exc_dispatch.Status  IN(  0,1,2,3,7 )  ))"
					+ "and  exc_task.ShiftID   = :wShiftID ;   ", wInstance.Result);

			wSQL = this.DMLChange(wSQL);
			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wCompanyID", wLoginUser.getCompanyID());
			wParamMap.put("wOperatorID", wOperatorID);
			wParamMap.put("wShiftID", EXCConstants.getShiftID(wLoginUser));

			wResult = nameJdbcTemplate.queryForObject(wSQL, wParamMap, Integer.class);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public long Update(BMSEmployee wLoginUser, EXCCallTask wEXCCallTask, OutResult<Integer> wErrorCode) {
		long wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wEXCCallTask == null)
				return 0L;
			if (wEXCCallTask.ImageList == null)
				wEXCCallTask.ImageList = new ArrayList<String>();

			boolean wIsInsert = wEXCCallTask.ID <= 0;
			if (wEXCCallTask.Remark == null)
				wEXCCallTask.Remark = "";
			String wSQL = "";

			if (wEXCCallTask.getID() <= 0) {
				wSQL = StringUtils.Format("  INSERT INTO  {0}.exc_task  (   ApplyID ,  CompanyID ,"
						+ " ExceptionTypeID ,  OperatorID ,  ConfirmID ,  CreateTime ,"
						+ " EditTime ,     ExpireTime ,  ReportTimes ,  ForwardTimes ,"
						+ " Status ,  ShiftID ,Remark,RespondLevel,ImageList) VALUES (  :wApplyID, :wCompanyID,"
						+ ":wExceptionTypeID, :wOperatorID, :wConfirmID, now(), now(),"
						+ ":wExpireTime, :wReportTimes, :wForwardTimes, :wStatus, :wShiftID,:wRemark,:wRespondLevel,:wImageList);",
						wInstance.Result);
			} else {
				wSQL = StringUtils.Format("UPDATE  {0}.exc_task  SET    CompanyID  = :wCompanyID, "
						+ " OperatorID  = :wOperatorID,  ConfirmID  = :wConfirmID,    ExpireTime =:wExpireTime,"
						+ " ReportTimes =:wReportTimes,  ForwardTimes =:wForwardTimes,  EditTime  = now(),"
						+ " Status  = :wStatus,   ShiftID  = :wShiftID , Remark=:wRemark , RespondLevel=:wRespondLevel,ImageList=:wImageList  WHERE  ID  =:wID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);
			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wEXCCallTask.ID);
			wParamMap.put("wCompanyID", wEXCCallTask.CompanyID);
			wParamMap.put("wApplyID", wEXCCallTask.ApplyID);
			wParamMap.put("wExceptionTypeID", wEXCCallTask.ExceptionTypeID);
			wParamMap.put("wOperatorID", StringUtils.Join(",", wEXCCallTask.OperatorID));
			wParamMap.put("wConfirmID", wEXCCallTask.ConfirmID);
			wParamMap.put("wExpireTime", wEXCCallTask.ExpireTime);
			wParamMap.put("wReportTimes", wEXCCallTask.ReportTimes);
			wParamMap.put("wForwardTimes", wEXCCallTask.ForwardTimes);
			wParamMap.put("wShiftID", wEXCCallTask.ShiftID);
			wParamMap.put("wStatus", wEXCCallTask.Status);
			wParamMap.put("wRemark", wEXCCallTask.Remark);
			wParamMap.put("wRespondLevel", wEXCCallTask.RespondLevel);
			wParamMap.put("wImageList", StringUtils.Join(" |;| ", wEXCCallTask.ImageList));

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			wResult = nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);
			if (wEXCCallTask.getID() <= 0) {
				wResult = keyHolder.getKey().longValue();
				wEXCCallTask.setID(wResult);
			} else {
				wResult = wEXCCallTask.getID();
			}

			if (wEXCCallTask.DisplayBoard && wEXCCallTask.ID > 0) {
				EXCAndon wEXCAndon = new EXCAndon();
				if (wIsInsert) {
					wEXCAndon.ID = 0;
					wEXCAndon.Comment = StringUtils.isEmpty(wEXCCallTask.getRemark()) ? wEXCCallTask.Comment
							: StringUtils.Format("【{0}】 {1}", wEXCCallTask.getRemark(), wEXCCallTask.Comment);
					wEXCAndon.CompanyID = wEXCCallTask.CompanyID;
					wEXCAndon.CreateTime = wEXCCallTask.CreateTime;
					wEXCAndon.CreatorID = wEXCCallTask.ApplicantID;
					wEXCAndon.EditTime = wEXCCallTask.EditTime;
					wEXCAndon.EXType = wEXCCallTask.ExceptionTypeName;
					wEXCAndon.PlaceID = wEXCCallTask.PlaceID;
					wEXCAndon.PlaceNo = wEXCCallTask.PlaceNo;
					wEXCAndon.OperatorID = wEXCCallTask.OperatorID;
					wEXCAndon.ShiftID = wEXCCallTask.ShiftID;
					wEXCAndon.ShiftTimes = wEXCCallTask.ForwardTimes;
					wEXCAndon.SourceID = wEXCCallTask.ID;
					wEXCAndon.ResponseLevel = wEXCCallTask.RespondLevel;
					wEXCAndon.SourceType = EXCAndonTypes.Call.getValue();
					wEXCAndon.StationCode = wEXCCallTask.StationNo;
					wEXCAndon.Status = wEXCCallTask.Status;
					wEXCAndon.CarName = wEXCCallTask.PartNo;
				} else {
					wEXCAndon = EXCAndonDAO.getInstance().Select(wLoginUser, wEXCCallTask.ID, EXCAndonTypes.Call,
							wErrorCode);
					wEXCAndon.OperatorID = wEXCCallTask.OperatorID;
					wEXCAndon.Status = wEXCCallTask.Status;
					wEXCAndon.PlaceID = wEXCCallTask.PlaceID;
					wEXCAndon.PlaceNo = wEXCCallTask.PlaceNo;
					wEXCAndon.EditTime = Calendar.getInstance();
				}

				EXCAndonDAO.getInstance().Update(wLoginUser, wEXCAndon, wErrorCode);
			}

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public List<EXCCallTask> SelectList(BMSEmployee wLoginUser, Calendar wLastTime, int wLevel,
			OutResult<Integer> wErrorCode) {
		List<EXCCallTask> wResult = new ArrayList<EXCCallTask>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 1, 1);
			if (wLastTime == null || wLastTime.compareTo(wBaseTime) < 0)
				wLastTime = wBaseTime;

			String wSQL = StringUtils.Format("SELECT  exc_task.ID ,      exc_task.ApplyID ,"
					+ "     exc_task.CompanyID ,      exc_station.StationType ,"
					+ "     exc_station_type.Name  as StationTypeName,      exc_apply.StationID ,"
					+ "     exc_station.StationNo ,      exc_task.ExceptionTypeID , "
					+ "     exc_ex_type.Name  as ExceptionTypeName, 	 exc_apply.ApplicantID ,"
					+ "     exc_apply.ApplicantTime ,      exc_task.OperatorID ,"
					+ "     exc_task.ConfirmID ,      exc_task.RespondLevel ,      exc_apply.OnSite ,"
					+ "     exc_apply.DisplayBoard ,      exc_task.CreateTime ,      exc_task.EditTime ,"
					+ "     exc_task.ExpireTime ,      exc_task.ReportTimes ,"
					+ "     exc_task.ForwardTimes ,      exc_apply.Comment , exc_apply.PartNo , exc_apply.PlaceID ,"
					+ "     exc_task.Remark ,      exc_task.ImageList ,"
					+ "     exc_task.Status ,      exc_task.ShiftID  FROM  {0}.exc_task  ,  {0}.exc_apply  , "
					+ "	  {0}.exc_station  ,	 {0}.exc_station_type  ,	 {0}.exc_ex_type "
					+ "WHERE   exc_task.ApplyID  =  exc_apply.ID  "
					+ "and    exc_apply.StationID  =  exc_station.ID      "
					+ "and    exc_station.StationType =  exc_station_type.ID   "
					+ "and    exc_task.ExceptionTypeID  =  exc_ex_type.ID   "
					+ "and ( :wRespondLevel<= 0 or  exc_apply.RespondLevel  =:wRespondLevel)  "
					+ "and ( :wLastTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wLastTime <=  exc_task.EditTime )"
					+ "and ( :wLastTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wLastTime >=  exc_task.CreateTime )  ",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wLastTime", wLastTime);
			wParamMap.put("wRespondLevel", wLevel);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
			for (Map<String, Object> wReader : wQueryResult) {

				EXCCallTask wEXCCallTask = new EXCCallTask();

				wEXCCallTask.ID = StringUtils.parseLong(wReader.get("ID"));
				wEXCCallTask.Remark = StringUtils.parseString(wReader.get("Remark"));
				wEXCCallTask.CompanyID = StringUtils.parseInt(wReader.get("CompanyID"));
				wEXCCallTask.ApplyID = StringUtils.parseLong(wReader.get("ApplyID"));
				wEXCCallTask.StationType = StringUtils.parseLong(wReader.get("StationType"));
				wEXCCallTask.StationTypeName = StringUtils.parseString(wReader.get("StationTypeName"));
				wEXCCallTask.StationID = StringUtils.parseLong(wReader.get("StationID"));
				wEXCCallTask.StationNo = StringUtils.parseString(wReader.get("StationNo"));
				wEXCCallTask.ExceptionTypeID = StringUtils.parseLong(wReader.get("ExceptionTypeID"));
				wEXCCallTask.ExceptionTypeName = StringUtils.parseString(wReader.get("ExceptionTypeName"));
				wEXCCallTask.RespondLevel = StringUtils.parseInt(wReader.get("RespondLevel"));
				wEXCCallTask.ApplicantID = StringUtils.parseLong(wReader.get("ApplicantID"));
				wEXCCallTask.ApplicantTime = StringUtils.parseCalendar(wReader.get("ApplicantTime"));
				wEXCCallTask.OperatorID = StringUtils
						.parseLongList(StringUtils.parseString(wReader.get("OperatorID")).split(",|;"));
				wEXCCallTask.ConfirmID = StringUtils.parseLong(wReader.get("ConfirmID"));
				wEXCCallTask.OnSite = StringUtils.parseInt(wReader.get("OnSite")) == 1;
				wEXCCallTask.DisplayBoard = StringUtils.parseInt(wReader.get("DisplayBoard")) == 1;
				wEXCCallTask.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wEXCCallTask.EditTime = StringUtils.parseCalendar(wReader.get("EditTime"));
				wEXCCallTask.ExpireTime = StringUtils.parseCalendar(wReader.get("ExpireTime"));
				wEXCCallTask.ReportTimes = StringUtils.parseInt(wReader.get("ReportTimes"));
				wEXCCallTask.ForwardTimes = StringUtils.parseInt(wReader.get("ForwardTimes"));
				wEXCCallTask.Comment = StringUtils.parseString(wReader.get("Comment"));
				wEXCCallTask.ShiftID = StringUtils.parseInt(wReader.get("ShiftID"));
				wEXCCallTask.PlaceID = StringUtils.parseInt(wReader.get("PlaceID"));

				APIResult wPlaceResult = CoreServiceImpl.getInstance().FMC_QueryWorkspace(wLoginUser,
						wEXCCallTask.PlaceID, "");
				FMCWorkspace wSpace = wPlaceResult.Custom("list", FMCWorkspace.class);
				if (wSpace != null && wSpace.ID > 0) {
					wEXCCallTask.PlaceNo = wSpace.Code;
				}

				wEXCCallTask.PartNo = StringUtils.parseString(wReader.get("PartNo"));
				wEXCCallTask.ImageList = StringUtils
						.parseList(StringUtils.parseString(wReader.get("ImageList")).split(" \\|;\\| "));
				wEXCCallTask.Status = StringUtils.parseInt(wReader.get("Status"));
				if (StringUtils.isEmpty(wEXCCallTask.Comment)) {
					wEXCCallTask.Comment = wEXCCallTask.Remark;
				}

				wResult.add(wEXCCallTask);

			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	private EXCCallTaskDAO() {
		super();
	}

	public static EXCCallTaskDAO getInstance() {
		if (Instance == null)
			Instance = new EXCCallTaskDAO();
		return Instance;
	}
}
