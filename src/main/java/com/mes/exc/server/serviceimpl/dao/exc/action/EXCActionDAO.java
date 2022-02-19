package com.mes.exc.server.serviceimpl.dao.exc.action;

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
import com.mes.exc.server.service.po.exc.EXCTimeItem;
import com.mes.exc.server.service.po.exc.action.EXCCallAction;
import com.mes.exc.server.service.po.exc.define.EXCActionTypes;
import com.mes.exc.server.service.po.exc.define.EXCCallStatus;
import com.mes.exc.server.service.po.exc.define.EXCResourceTypes;
import com.mes.exc.server.service.po.exc.tree.EXCCallDispatch;
import com.mes.exc.server.service.po.exc.tree.EXCCallTask;
import com.mes.exc.server.serviceimpl.dao.exc.base.EXCExceptionRuleDAO;
import com.mes.exc.server.serviceimpl.dao.exc.tree.EXCCallDispatchDAO;
import com.mes.exc.server.serviceimpl.dao.exc.tree.EXCCallTaskDAO;
import com.mes.exc.server.service.mesenum.MESDBSource;
import com.mes.exc.server.service.utils.StringUtils;

public class EXCActionDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(EXCActionDAO.class);

	private static EXCActionDAO Instance;

	/**
	 * 权限码
	 */
	private static int AccessCode = 0;

	public List<EXCCallAction> SelectAll(BMSEmployee wLoginUser, List<Long> wID, long wTaskID,
			EXCActionTypes wActionType, long wOperatorID, long wForwarder, long wDispatchID,
			OutResult<Integer> wErrorCode) {
		List<EXCCallAction> wResult = new ArrayList<EXCCallAction>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wID == null)
				wID = new ArrayList<Long>();
			String wSQL = StringUtils.Format(
					"SELECT  exc_action.ID ,      exc_action.TaskID , "
							+ "     exc_action.ActionType ,      exc_action.CompanyID ,      exc_action.OperatorID , "
							+ "     exc_action.DispatchID ,      exc_action.Forwarder ,      exc_action.Comment , "
							+ "     exc_action.ImageList ,      exc_action.CreateTime  FROM {0}.exc_action   "
							+ "WHERE  1 = 1 and ( :wID is null or :wID = '''' or  exc_action.ID  IN( {1} ) )    "
							+ "and ( :wTaskID<= 0 or  exc_action.TaskID  =:wTaskID)    "
							+ "and ( :wActionType<= 0 or  exc_action.ActionType  =:wActionType)  "
							+ "and ( :wCompanyID<= 0 or  exc_action.CompanyID  =:wCompanyID)  "
							+ "and ( :wOperatorID<= 0 or  exc_action.OperatorID  =:wOperatorID)  "
							+ "and ( :wDispatchID<= 0 or  exc_action.DispatchID  =:wDispatchID)  "
							+ "and ( :wForwarder<= 0 or  exc_action.Forwarder  =:wForwarder)  ",
					wInstance.Result, wID.size() > 0 ? StringUtils.Join(",", wID) : "0");
			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", StringUtils.Join(",", wID));
			wParamMap.put("wTaskID", wTaskID);
			wParamMap.put("wActionType", wActionType.getValue());
			wParamMap.put("wCompanyID", wLoginUser.getCompanyID());
			wParamMap.put("wOperatorID", wOperatorID);
			wParamMap.put("wForwarder", wForwarder);
			wParamMap.put("wDispatchID", wDispatchID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
			// wReader\[\"(\w+)\"\]

			for (Map<String, Object> wReader : wQueryResult) {

				long wIDSql = StringUtils.parseLong(wReader.get("ID"));
				long wTaskIDSql = StringUtils.parseLong(wReader.get("TaskID"));
				int wActionTypeSql = StringUtils.parseInt(wReader.get("ActionType"));
				int wCompanyIDSql = StringUtils.parseInt(wReader.get("CompanyID"));
				long wOperatorIDSql = StringUtils.parseLong(wReader.get("OperatorID"));
				long wDispatchIDSql = StringUtils.parseLong(wReader.get("DispatchID"));
				String wForwarderSql = StringUtils.parseString(wReader.get("Forwarder"));
				String wCommentSql = StringUtils.parseString(wReader.get("Comment"));
				String wImageListSql = StringUtils.parseString(wReader.get("ImageList"));
				Calendar wCreateTimeSql = StringUtils.parseCalendar(wReader.get("CreateTime"));

				EXCCallAction wEXCCallAction = new EXCCallAction();
				// \s+\=\s+(\w+)\,
				wEXCCallAction.setID(wIDSql);
				wEXCCallAction.setTaskID(wTaskIDSql);
				wEXCCallAction.setActionType(wActionTypeSql);
				wEXCCallAction.setCompanyID(wCompanyIDSql);
				wEXCCallAction.setOperatorID(wOperatorIDSql);
				wEXCCallAction.setDispatchID(wDispatchIDSql);
				wEXCCallAction.setForwarder(StringUtils.parseLongList(wForwarderSql.split(",")));
				wEXCCallAction.setComment(wCommentSql);
				wEXCCallAction.setImageList(StringUtils.parseList(wImageListSql.split(" \\|;\\| ")));
				wEXCCallAction.setCreateTime(wCreateTimeSql);

				wResult.add(wEXCCallAction);
			}

		} catch (Exception e) {

			logger.error(e.toString());
		}
		return wResult;
	}

	public List<EXCCallAction> SelectAll(BMSEmployee wLoginUser, long wTaskID, EXCActionTypes wActionType,
			long wOperatorID, long wForwarder, long wDispatchID, OutResult<Integer> wErrorCode) {
		List<EXCCallAction> wResult = new ArrayList<EXCCallAction>();
		try {
			wResult = SelectAll(wLoginUser, null, wTaskID, wActionType, wOperatorID, wForwarder, wDispatchID,
					wErrorCode);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public EXCCallAction Select(BMSEmployee wLoginUser, long wID, OutResult<Integer> wErrorCode) {
		EXCCallAction wResult = new EXCCallAction();
		try {
			if (wID <= 0)
				return wResult;
			List<Long> wIDList = new ArrayList<Long>();
			wIDList.add(wID);
			List<EXCCallAction> wEXCCallActionList = SelectAll(wLoginUser, wIDList, -1, EXCActionTypes.Default, -1, -1,
					-1, wErrorCode);

			if (wEXCCallActionList.size() != 1)
				return wResult;

			wResult = wEXCCallActionList.get(0);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;

	}

	public long Update(BMSEmployee wLoginUser, EXCCallAction wEXCCallAction, int wShiftID, boolean wIsOverShift,
			OutResult<Integer> wErrorCode) {
		long wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wEXCCallAction == null)
				return 0L;
			if (wEXCCallAction.ImageList == null)
				wEXCCallAction.ImageList = new ArrayList<String>();

			HandleSave(wLoginUser, wEXCCallAction, wShiftID, wIsOverShift, wErrorCode);

			String wSQL = "";

			if (wEXCCallAction.getID() <= 0) {
				wSQL = StringUtils.Format(
						"INSERT INTO {0}.exc_action(TaskID," + "ActionType,CompanyID,OperatorID,DispatchID,Forwarder,"
								+ "Comment,ImageList,CreateTime)VALUES(:wTaskID,:wActionType,"
								+ ":wCompanyID,:wOperatorID,:wDispatchID,:wForwarder,:wComment,:wImageList,now()); ",
						wInstance.Result);
			} else {
				wSQL = StringUtils.Format(
						"UPDATE  {0}.exc_action  SET  TaskID  = :wTaskID, "
								+ "		 ActionType  = :wActionType, 		 CompanyID  = :wCompanyID, "
								+ "		 OperatorID  = :wOperatorID, 		 DispatchID  = :wDispatchID, "
								+ "		 Forwarder  = :wForwarder, 		 Comment  = :wComment, "
								+ "		 ImageList  = :wImageList, 		 CreateTime  = now() WHERE  ID  = wID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();
			wParamMap.put("wID", wEXCCallAction.ID);
			wParamMap.put("wTaskID", wEXCCallAction.TaskID);
			wParamMap.put("wActionType", wEXCCallAction.ActionType);
			wParamMap.put("wCompanyID", wEXCCallAction.CompanyID);
			wParamMap.put("wOperatorID", wEXCCallAction.OperatorID);
			wParamMap.put("wForwarder", StringUtils.Join(",", wEXCCallAction.Forwarder));
			wParamMap.put("wDispatchID", wEXCCallAction.DispatchID);
			wParamMap.put("wComment", wEXCCallAction.Comment);
			wParamMap.put("wImageList", StringUtils.Join(" |;| ", wEXCCallAction.ImageList));

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			wResult = nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);
			if (wEXCCallAction.getID() <= 0) {
				wResult = keyHolder.getKey().longValue();
				wEXCCallAction.setID(wResult);
			} else {
				wResult = wEXCCallAction.getID();
			}

		} catch (

		Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	private String HandleSave(BMSEmployee wLoginUser, EXCCallAction wEXCAction, int wShiftID, boolean wIsOverShift,
			OutResult<Integer> wErrorCode) {
		String wResult = "";

		EXCCallTask wEXCCallTask = EXCCallTaskDAO.getInstance().Select(wLoginUser, wEXCAction.TaskID, wErrorCode);

		if (wEXCCallTask == null || wEXCCallTask.ID <= 0) {
			wResult = "此异常任务不存在！";
			return wResult;
		}
		if (wEXCAction.DispatchID <= 0 && wEXCAction.ActionType == EXCActionTypes.Cancel.getValue()) {
			wEXCCallTask.Status = EXCCallStatus.Cancel.getValue();
			wEXCCallTask.ExpireTime = Calendar.getInstance();
			wEXCCallTask.ExpireTime.set(2000, 1, 1);
			EXCCallTaskDAO.getInstance().Update(wLoginUser, wEXCCallTask, wErrorCode);

			EXCCallDispatchDAO.getInstance().UpdateByTask(wLoginUser, wEXCCallTask.ID, EXCCallStatus.Cancel,
					wErrorCode);
			return wResult;
		}
		EXCCallDispatch wEXCCallDispatch = EXCCallDispatchDAO.getInstance().Select(wLoginUser, wEXCAction.DispatchID,
				wErrorCode);
		if (wEXCCallTask == null || wEXCCallTask.ID <= 0) {
			wResult = "此异常接收记录不存在！";
			return wResult;
		}
		List<EXCCallDispatch> wEXCCallDispatchInsertList = EXCCallDispatch.ForwarderDispatch(wEXCAction, wShiftID);

		EXCTimeItem wEXCTimeItem = EXCExceptionRuleDAO.getInstance().SelectEXCTimeItem(wLoginUser,
				wEXCCallTask.ExceptionTypeID, wEXCCallTask.RespondLevel,
				(wEXCCallTask.ApplicantID > 0 ? EXCResourceTypes.Artificial : EXCResourceTypes.System),
				((wEXCCallTask.OperatorID != null && wEXCCallTask.OperatorID.size() > 0
						&& !wEXCCallTask.OperatorID.contains(0L)) ? EXCResourceTypes.Artificial
								: EXCResourceTypes.System),
				(wEXCCallTask.ConfirmID > 0 ? EXCResourceTypes.Artificial : EXCResourceTypes.System),
				EXCActionTypes.getEnumType(wEXCAction.ActionType), wEXCCallTask.ReportTimes, wErrorCode);

		switch (EXCActionTypes.getEnumType(wEXCAction.ActionType)) {
		case Default:
		case Request:
			break;
		case Notice:
			wEXCCallTask.ExpireTime = Calendar.getInstance();
			wEXCCallTask.ExpireTime.add(Calendar.MINUTE, wEXCTimeItem.Time);
			wEXCCallTask.Status = EXCCallStatus.NoticeWaitRespond.getValue();
			wEXCCallDispatch.Status = (int) EXCCallStatus.NoticeWaitRespond.getValue();
			break;
		case OnSite:
			wEXCCallTask.ExpireTime = Calendar.getInstance();
			wEXCCallTask.ExpireTime.add(Calendar.MINUTE, wEXCTimeItem.Time);
			wEXCCallTask.Status = (int) EXCCallStatus.OnSiteRespond.getValue();
			wEXCCallDispatch.Status = (int) EXCCallStatus.OnSiteRespond.getValue();
			break;
		case Respond:
			wEXCCallTask.ExpireTime = Calendar.getInstance();
			wEXCCallTask.ExpireTime.add(Calendar.MINUTE, wEXCTimeItem.Time);
			wEXCCallTask.Status = (int) EXCCallStatus.WaitConfirm.getValue();
			wEXCCallDispatch.Status = (int) EXCCallStatus.WaitConfirm.getValue();
			break;
		case Forward:
			wEXCCallTask.ExpireTime = Calendar.getInstance();
			wEXCCallTask.ExpireTime.add(Calendar.MINUTE, wEXCTimeItem.Time);
			wEXCCallTask.Status = (int) EXCCallStatus.WaitRespond.getValue();
			wEXCCallTask.OperatorID.addAll(wEXCAction.Forwarder);
			wEXCCallTask.OperatorID.remove(wEXCAction.OperatorID);

			wEXCCallTask.OperatorID = wEXCCallTask.OperatorID.stream().distinct().collect(Collectors.toList());
			wEXCCallDispatch.Status = (int) EXCCallStatus.Forwarded.getValue();
			if (wIsOverShift)
				wEXCCallTask.ForwardTimes--;

			// 添加Dispatch
			EXCCallDispatchDAO.getInstance().Update(wLoginUser, wEXCCallDispatchInsertList, wErrorCode);
			break;
		case Confirm:
			wEXCCallTask.ExpireTime = Calendar.getInstance();
			wEXCCallTask.ExpireTime.set(2000, 1, 1);
			wEXCCallTask.Status = (int) EXCCallStatus.Confirmed.getValue();
			wEXCCallDispatch.Status = (int) EXCCallStatus.Confirmed.getValue();

			break;
		case Reject:
			wEXCCallTask.ExpireTime = Calendar.getInstance();
			wEXCCallTask.ExpireTime.add(Calendar.MINUTE, wEXCTimeItem.Time);
			wEXCCallTask.Status = (int) EXCCallStatus.Rejected.getValue();
			wEXCCallDispatch.Status = (int) EXCCallStatus.Rejected.getValue();
			break;
		case UpGrade:
			wEXCCallTask.ExpireTime = Calendar.getInstance();
			wEXCCallTask.ExpireTime.add(Calendar.MINUTE, wEXCTimeItem.Time);
			wEXCCallTask.Status = (int) EXCCallStatus.WaitRespond.getValue();
			wEXCCallTask.ReportTimes--;
			wEXCCallDispatch.Status = (int) EXCCallStatus.UpGraded.getValue();

			EXCCallDispatchDAO.getInstance().Update(wLoginUser, wEXCCallDispatchInsertList, wErrorCode);
			// 添加Dispatch
			break;
		default:
			break;
		}
		// 改变任务状态
		EXCCallTaskDAO.getInstance().Update(wLoginUser, wEXCCallTask, wErrorCode);
		// 改变Dispatch状态
		EXCCallDispatchDAO.getInstance().Update(wLoginUser, wEXCCallDispatch, wErrorCode);

		return wResult;

	}

	private EXCActionDAO() {
		super();
	}

	public static EXCActionDAO getInstance() {
		if (Instance == null)
			Instance = new EXCActionDAO();
		return Instance;
	}

}
