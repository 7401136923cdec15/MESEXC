package com.mes.exc.server.serviceimpl.dao.exc.action;

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

import com.mes.exc.server.service.po.exc.action.EXCCallApply;
import com.mes.exc.server.service.po.exc.define.EXCApplyStatus;
import com.mes.exc.server.service.po.fmc.FMCWorkspace;
import com.mes.exc.server.service.mesenum.MESDBSource;
import com.mes.exc.server.service.mesenum.MESException;
import com.mes.exc.server.service.po.OutResult;
import com.mes.exc.server.service.po.ServiceResult;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.serviceimpl.CoreServiceImpl;
import com.mes.exc.server.serviceimpl.dao.BaseDAO;
import com.mes.exc.server.serviceimpl.utils.exc.EXCConstants;
import com.mes.exc.server.serviceimpl.utils.exc.EXCManagerUtils;
import com.mes.exc.server.serviceimpl.utils.exc.EXCStrClaConverter;
import com.mes.exc.server.service.utils.StringUtils;

public class EXCCallApplyDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(EXCCallApplyDAO.class);

	private static EXCCallApplyDAO Instance;

	/**
	 * 权限码
	 */
	private static int AccessCode = 0;

	public List<EXCCallApply> SelectAll(BMSEmployee wLoginUser, List<Long> wID, String wStationNo, long wStationType,
			long wStationID, int wRespondLevel, int wDisplayBoard, int wOnSite, long wApplicantID, long wApproverID,
			long wConfirmID, String wPartNo, Calendar wStartTime, Calendar wEndTime, List<Integer> wStatus,
			OutResult<Integer> wErrorCode) {
		List<EXCCallApply> wResult = new ArrayList<EXCCallApply>();

		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wID == null)
				wID = new ArrayList<Long>();

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

			String wSQL = StringUtils.Format("SELECT exc_apply.ID,      exc_apply.CompanyID, "
					+ "	exc_station.StationType,  	exc_station_type.Name as StationTypeName, "
					+ "    exc_apply.StationID,  	exc_station.StationNo,       exc_apply.ExceptionTypeList, "
					+ "    exc_apply.RespondLevel,      exc_apply.ApplicantID,      exc_apply.ApplicantTime, "
					+ "    exc_apply.ApproverID,      exc_apply.ApproverTime,      exc_apply.ConfirmID, "
					+ "    exc_apply.ConfirmTime,      exc_apply.OnSite,      exc_apply.DisplayBoard, "
					+ "    exc_apply.Comment,      exc_apply.ImageList,      exc_apply.Status "
					+ "FROM {0}.exc_apply  ,{0}.exc_station ,	{0}.exc_station_type "
					+ "WHERE  exc_apply.StationID = exc_station.ID  "
					+ "and exc_station.StationType= exc_station_type.ID   "
					+ "and ( :wID is null or :wID = '''' or exc_apply.ID IN( {1} ) )    "
					+ "and ( :wStatus is null or :wStatus = '''' or exc_apply.Status IN( {2} ) )   "
					+ "and ( :wCompanyID<= 0 or exc_apply.CompanyID =:wCompanyID)    "
					+ "and ( :wStationNo is null or :wStationNo = '''' or exc_station.StationNo =  :wStationNo )  "
					+ "and ( :wPartNo is null or :wPartNo = '''' or exc_apply.PartNo =  :wPartNo )  "
					+ "and ( :wStationType< 0 or exc_station.StationType =:wStationType)  "
					+ "and ( :wStationID<= 0 or exc_apply.StationID =:wStationID)   "
					+ "and ( :wRespondLevel<= 0 or exc_apply.RespondLevel =:wRespondLevel)   "
					+ "and ( :wDisplayBoard< 0 or exc_apply.DisplayBoard =:wDisplayBoard)   "
					+ "and ( :wOnSite< 0 or exc_apply.OnSite =:wOnSite)   "
					+ "and ( :wApplicantID <= 0 or exc_apply.ApplicantID  = :wApplicantID)  "
					+ "and ( :wApproverID <= 0 or exc_apply.ApproverID  = :wApproverID)   "
					+ "and ( :wConfirmID <= 0 or exc_apply.ConfirmID  = :wConfirmID)     "
					+ "and ( :wStartTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or :wStartTime <= exc_apply.ApplicantTime) "
					+ "and ( :wEndTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or :wEndTime >= exc_apply.ApplicantTime) ",
					wInstance.Result, wID.size() > 0 ? StringUtils.Join(",", wID) : "0",
					wStatus.size() > 0 ? StringUtils.Join(",", wStatus) : "0");
			wSQL = this.DMLChange(wSQL);
			Map<String, Object> wParamMap = new HashMap<String, Object>();
			wParamMap.put("wID", StringUtils.Join(",", wID));
			wParamMap.put("wCompanyID", wLoginUser.getCompanyID());
			wParamMap.put("wStationNo", wStationNo);
			wParamMap.put("wStationType", wStationType);
			wParamMap.put("wStationID", wStationID);
			wParamMap.put("wRespondLevel", wRespondLevel);
			wParamMap.put("wDisplayBoard", wDisplayBoard);
			wParamMap.put("wPartNo", wPartNo);
			wParamMap.put("wOnSite", wOnSite);
			wParamMap.put("wApplicantID", wApplicantID);
			wParamMap.put("wApproverID", wApproverID);
			wParamMap.put("wConfirmID", wConfirmID);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wStatus", StringUtils.Join(",", wStatus));

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {

				long wIDSql = StringUtils.parseLong(wReader.get("ID"));
				int wCompanyIDSql = StringUtils.parseInt(wReader.get("CompanyID"));
				long wStationTypeSql = StringUtils.parseLong(wReader.get("StationType"));
				String wStationTypeNameSql = StringUtils.parseString(wReader.get("StationTypeName"));
				long wStationIDSql = StringUtils.parseLong(wReader.get("StationID"));
				String wStationNoSql = StringUtils.parseString(wReader.get("StationNo"));
				String wExceptionTypeListSql = StringUtils.parseString(wReader.get("ExceptionTypeList"));
				int wRespondLevelSql = StringUtils.parseInt(wReader.get("RespondLevel"));
				long wApplicantIDSql = StringUtils.parseLong(wReader.get("ApplicantID"));
				Calendar wApplicantTimeSql = StringUtils.parseCalendar(wReader.get("ApplicantTime"));
				long wApproverIDSql = StringUtils.parseLong(wReader.get("ApproverID"));
				Calendar wApproverTimeSql = StringUtils.parseCalendar(wReader.get("ApproverTime"));
				long wConfirmIDSql = StringUtils.parseLong(wReader.get("ConfirmID"));
				Calendar wConfirmTimeSql = StringUtils.parseCalendar(wReader.get("ConfirmTime"));//
				boolean wOnSiteSql = StringUtils.parseInt(wReader.get("OnSite")) == 1;
				boolean wDisplayBoardSql = StringUtils.parseInt(wReader.get("DisplayBoard")) == 1;
				String wCommentSql = StringUtils.parseString(wReader.get("Comment"));
				String wImageListSql = StringUtils.parseString(wReader.get("ImageList"));
				int wStatusSql = StringUtils.parseInt(wReader.get("Status"));
				String wPartNoSql = StringUtils.parseString(wReader.get("PartNo"));
				int wPlaceIDSql = StringUtils.parseInt(wReader.get("PlaceID"));

				EXCCallApply wEXCCallApply = new EXCCallApply();

				wEXCCallApply.ID = wIDSql;
				wEXCCallApply.CompanyID = wCompanyIDSql;
				wEXCCallApply.StationType = wStationTypeSql;
				wEXCCallApply.StationTypeName = wStationTypeNameSql;
				wEXCCallApply.StationID = wStationIDSql;
				wEXCCallApply.StationNo = wStationNoSql;
				wEXCCallApply.ExceptionTypeList = EXCStrClaConverter.EXCTypeOptionToList(wExceptionTypeListSql);
				wEXCCallApply.RespondLevel = wRespondLevelSql;
				wEXCCallApply.ApplicantID = wApplicantIDSql;
				wEXCCallApply.ApplicantTime = wApplicantTimeSql;
				wEXCCallApply.ApproverID = wApproverIDSql;
				wEXCCallApply.ApproverTime = wApproverTimeSql;
				wEXCCallApply.ConfirmID = wConfirmIDSql;
				wEXCCallApply.ConfirmTime = wConfirmTimeSql;
				wEXCCallApply.OnSite = wOnSiteSql;
				wEXCCallApply.DisplayBoard = wDisplayBoardSql;
				wEXCCallApply.Comment = wCommentSql;
				wEXCCallApply.PartNo = wPartNoSql;
				wEXCCallApply.PlaceID = wPlaceIDSql;
				wEXCCallApply.PlaceNo = CoreServiceImpl.getInstance()
						.FMC_QueryWorkspace(wLoginUser, wEXCCallApply.PlaceID, "").Info(FMCWorkspace.class).Code;
				wEXCCallApply.ImageList = StringUtils.parseList(wImageListSql.split(" \\|;\\| "));
				wEXCCallApply.Status = wStatusSql;
				wResult.add(wEXCCallApply);
			}

			if (wResult != null && wResult.size() > 0) {

				for (EXCCallApply wEXCCallApply : wResult) {
					if (wEXCCallApply == null)
						continue;

					if (EXCApplyStatus.getEnumType(wEXCCallApply.Status) == EXCApplyStatus.Default
							|| EXCApplyStatus.getEnumType(wEXCCallApply.Status) == EXCApplyStatus.Waiting) {

						Calendar wNow = Calendar.getInstance();

						wNow.add(Calendar.SECOND, -EXCConstants.getEXCRunConfig().getApplyOverTimeReject());

						if (wEXCCallApply.ApplicantTime.compareTo(wNow) <= 0) {

							wEXCCallApply.Status = (int) EXCApplyStatus.Reject.getValue();
							wEXCCallApply.ApproverTime = Calendar.getInstance();
							OutResult<Integer> wFaultCode = new OutResult<Integer>(0);
							Update(wLoginUser, wEXCCallApply, 0, wFaultCode);
						}
					}
				}

			}
		} catch (Exception e) {
			logger.error(e.toString());
			wErrorCode.set(MESException.DBSQL.getValue());
		}
		return wResult;
	}

	public List<EXCCallApply> SelectAll(BMSEmployee wLoginUser, String wStationNo, long wStationType, long wStationID,
			int wRespondLevel, int wDisplayBoard, int wOnSite, long wApplicantID, long wApproverID, long wConfirmID,
			String wPartNo, Calendar wStartTime, Calendar wEndTime, int wStatus, OutResult<Integer> wErrorCode) {
		List<EXCCallApply> wResult = new ArrayList<EXCCallApply>();
		try {
			wResult = SelectAll(wLoginUser, null, wStationNo, wStationType, wStationID, wRespondLevel, wDisplayBoard,
					wOnSite, wApplicantID, wApproverID, wConfirmID, wPartNo, wStartTime, wEndTime,
					wStatus > 0 ? StringUtils.parseList(new Integer[] { wStatus }) : null, wErrorCode);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public EXCCallApply Select(BMSEmployee wLoginUser, long wID, OutResult<Integer> wErrorCode) {
		EXCCallApply wResult = new EXCCallApply();
		try {
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 1, 1);
			List<EXCCallApply> wEXCCallApplyList = SelectAll(wLoginUser, StringUtils.parseList(new Long[] { wID }), "",
					-1, -1, -1, -1, -1, -1, -1, -1, "", wBaseTime, wBaseTime, null, wErrorCode);

			if (wEXCCallApplyList.size() != 1)
				return wResult;

			wResult = wEXCCallApplyList.get(0);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public long Update(BMSEmployee wLoginUser, EXCCallApply wEXCCallApply, int wShiftID,
			OutResult<Integer> wErrorCode) {
		long wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wErrorCode.Result != 0)
				return wResult;

			if (wEXCCallApply == null)
				return 0L;
			if (wEXCCallApply.ImageList == null)
				wEXCCallApply.ImageList = new ArrayList<String>();

			if (wEXCCallApply.PartNo == null)
				wEXCCallApply.PartNo = "";
			if (wEXCCallApply.Status == EXCApplyStatus.Confirm.getValue()) {
				EXCManagerUtils.getInstance().ExcTypeAI(wLoginUser, wEXCCallApply, wErrorCode);
			}

			String wSQL = "";

			if (wEXCCallApply.getID() <= 0) {
				wSQL = StringUtils.Format("  INSERT INTO  {0}.exc_apply   ( "
						+ " CompanyID ,   StationID ,   ExceptionTypeList ,   RespondLevel , "
						+ " ApplicantID ,   ApplicantTime ,   ApproverID ,   ApproverTime , "
						+ " ConfirmID ,   ConfirmTime ,   OnSite ,   DisplayBoard , "
						+ " Comment , PartNo , PlaceID , ImageList ,   Status )  VALUES  (  "
						+ ":wCompanyID,  :wStationID,  :wExceptionTypeList,  :wRespondLevel, "
						+ ":wApplicantID,  :wApplicantTime,  :wApproverID,  :wApproverTime, "
						+ ":wConfirmID,  :wConfirmTime,  :wOnSite,  :wDisplayBoard,  "
						+ " :wComment,:wPartNo,:wPlaceID,  :wImageList,  :wStatus);", wInstance.Result);
			} else {
				wSQL = StringUtils.Format("UPDATE  {0}.exc_apply   SET    CompanyID  =:wCompanyID, "
						+ " StationID  = :wStationID,  PlaceID  = :wPlaceID,   ExceptionTypeList  = :wExceptionTypeList, "
						+ " RespondLevel  = :wRespondLevel,   ApplicantID  = :wApplicantID, "
						+ " ApplicantTime  = :wApplicantTime,   ApproverID  = :wApproverID, "
						+ " ApproverTime  = :wApproverTime,   ConfirmID  = :wConfirmID, "
						+ " ConfirmTime  = :wConfirmTime,   OnSite  = wOnSite, "
						+ " DisplayBoard  = :wDisplayBoard,    Comment  = :wComment,  PartNo  = :wPartNo, "
						+ " ImageList  = :wImageList,   Status  = :wStatus  WHERE  ID  = :wID;", wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();
			wParamMap.put("wID", wEXCCallApply.ID);
			wParamMap.put("wCompanyID", wEXCCallApply.CompanyID);
			wParamMap.put("wStationID", wEXCCallApply.StationID);
			wParamMap.put("wExceptionTypeList",
					EXCStrClaConverter.EXCTypeOptionToString(wEXCCallApply.ExceptionTypeList));
			wParamMap.put("wRespondLevel", wEXCCallApply.RespondLevel);
			wParamMap.put("wApplicantID", wEXCCallApply.ApplicantID);
			wParamMap.put("wApproverID", wEXCCallApply.ApproverID);
			wParamMap.put("wConfirmID", wEXCCallApply.ConfirmID);
			wParamMap.put("wApplicantTime", wEXCCallApply.ApplicantTime);
			wParamMap.put("wApproverTime", wEXCCallApply.ApproverTime);
			wParamMap.put("wConfirmTime", wEXCCallApply.ConfirmTime);
			wParamMap.put("wDisplayBoard", wEXCCallApply.DisplayBoard);
			wParamMap.put("wOnSite", wEXCCallApply.OnSite);
			wParamMap.put("wComment", wEXCCallApply.Comment);
			wParamMap.put("wPartNo", wEXCCallApply.PartNo);
			wParamMap.put("wImageList", StringUtils.Join(" |;| ", wEXCCallApply.ImageList));
			wParamMap.put("wStatus", wEXCCallApply.Status);
			wParamMap.put("wPlaceID", wEXCCallApply.PlaceID);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			wResult = nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);
			if (wEXCCallApply.getID() <= 0) {
				wResult = keyHolder.getKey().longValue();
				wEXCCallApply.setID(wResult);
			} else {
				wResult = wEXCCallApply.getID();
			}

			if (wEXCCallApply.ID > 0 && wEXCCallApply.Status == (int) EXCApplyStatus.Confirm.getValue()) {
				EXCManagerUtils.getInstance().EXCApplyConfirm(wLoginUser, wEXCCallApply, wShiftID, wErrorCode);
			}

		} catch (Exception e) {
			logger.error(e.toString());
			wErrorCode.set(MESException.DBSQL.getValue());

		}
		return wResult;
	}

	public long UpdatePartNo(BMSEmployee wLoginUser, int wWorkspaceID, int wProductID, String wPartNo,
			OutResult<Integer> wErrorCode) {
		long wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wErrorCode.Result != 0)
				return wResult;

			String wSQL = StringUtils.Format(
					"UPDATE  {0}.fmc_workspace SET ProductID=:ProductID,PartNo=:PartNo,EditorID=:EditorID,EditTime=now() WHERE  ID  = :wID;",
					wInstance.Result);

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();
			wParamMap.put("ProductID", wProductID);
			wParamMap.put("PartNo", wPartNo);
			wParamMap.put("wID", wWorkspaceID);
			wParamMap.put("EditorID", wLoginUser.ID);

			wResult = nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception e) {
			logger.error(e.toString());
			wErrorCode.set(MESException.DBSQL.getValue());

		}
		return wResult;
	}

	private EXCCallApplyDAO() {
		super();
	}

	public static EXCCallApplyDAO getInstance() {
		if (Instance == null)
			Instance = new EXCCallApplyDAO();
		return Instance;
	}
}
