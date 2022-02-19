package com.mes.exc.server.serviceimpl.utils.exc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mes.exc.server.service.CoreService;
import com.mes.exc.server.service.po.OutResult;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.service.po.exc.EXCTypeOption;
import com.mes.exc.server.service.po.exc.action.EXCCallApply;
import com.mes.exc.server.service.po.exc.base.EXCExceptionRule;
import com.mes.exc.server.service.po.exc.base.EXCExceptionType;
import com.mes.exc.server.service.po.exc.define.EXCActionTypes;
import com.mes.exc.server.service.po.exc.define.EXCCallStatus;
import com.mes.exc.server.service.po.exc.define.EXCResourceTypes;
import com.mes.exc.server.service.po.exc.define.TaskRelevancyTypes;
import com.mes.exc.server.service.po.exc.tree.EXCCallDispatch;
import com.mes.exc.server.service.po.exc.tree.EXCCallTask;
import com.mes.exc.server.service.po.exc.tree.EXCMessage;
import com.mes.exc.server.service.po.sch.SCHWorker;
import com.mes.exc.server.serviceimpl.dao.exc.base.EXCExceptionRuleDAO;
import com.mes.exc.server.serviceimpl.dao.exc.base.EXCExceptionTypeDAO;
import com.mes.exc.server.serviceimpl.dao.exc.tree.EXCCallDispatchDAO;
import com.mes.exc.server.serviceimpl.dao.exc.tree.EXCCallTaskDAO;
import com.mes.exc.server.serviceimpl.dao.exc.tree.EXCMessageDAO;
import com.mes.exc.server.service.mesenum.BPMEventModule;
import com.mes.exc.server.service.utils.StringUtils;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;
import static java.util.Comparator.comparing;

@Component
public class EXCManagerUtils {

	private static Logger logger = LoggerFactory.getLogger(EXCManagerUtils.class);

	private static EXCManagerUtils Instance;

	@PostConstruct
	public void init() {
		Instance = this;
		Instance.wCoreService = this.wCoreService;
		// 初使化时将已静态化的testService实例化
	}

	@Autowired
	CoreService wCoreService;

	public List<EXCCallTask> EXCApplyConfirm(BMSEmployee wLoginUser, EXCCallApply wEXCCallApply, int wShiftID,
			OutResult<Integer> wErrorCode) {
		List<EXCCallTask> wResult = new ArrayList<EXCCallTask>();
		try {
			if (wEXCCallApply == null || wEXCCallApply.ID < 0 || wEXCCallApply.ExceptionTypeList == null
					|| wEXCCallApply.ExceptionTypeList.size() < 1)
				return wResult;

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 1, 1);

			List<EXCCallTask> wEXCCallTaskList = EXCCallTaskDAO.getInstance().SelectAll(wLoginUser, wEXCCallApply.ID,
					"", -1, -1, -1, -1, -1, -1, -1, -1, -1, "", wBaseTime, wBaseTime, EXCCallStatus.Default, -1,
					wErrorCode);

			if (wEXCCallTaskList.size() > 0)
				return wResult;

			for (EXCTypeOption wEXCTypeOption : wEXCCallApply.ExceptionTypeList) {
				EXCCallTask wEXCCallTask = new EXCCallTask();
				EXCExceptionRule wEXCExceptionRule = EXCExceptionRuleDAO.getInstance().Select(wLoginUser,
						wEXCTypeOption.EXCTypeID, wEXCTypeOption.RespondLevel,
						(wEXCCallApply.ApplicantID > 0 ? EXCResourceTypes.Artificial : EXCResourceTypes.System),
						((wEXCTypeOption.OperatorIDList != null && wEXCTypeOption.OperatorIDList.size() > 0
								&& !wEXCTypeOption.OperatorIDList.contains(0L)) ? EXCResourceTypes.Artificial
										: EXCResourceTypes.System),
						(wEXCTypeOption.ConfirmID > 0 ? EXCResourceTypes.Artificial : EXCResourceTypes.System),
						wErrorCode);

				if (wEXCTypeOption.Remark == null)
					wEXCTypeOption.Remark = "";

				wEXCCallTask.ID = 0;
				wEXCCallTask.ApplicantID = wEXCCallApply.ApplicantID;
				wEXCCallTask.CompanyID = wEXCCallApply.CompanyID;
				wEXCCallTask.ApplyID = wEXCCallApply.ID;
				wEXCCallTask.StationNo = wEXCCallApply.StationNo;
				wEXCCallTask.ExceptionTypeID = wEXCTypeOption.EXCTypeID;
				wEXCCallTask.Comment = wEXCCallApply.Comment;
				wEXCCallTask.ExceptionTypeName = wEXCTypeOption.EXCTypeName;
				wEXCCallTask.OperatorID = wEXCTypeOption.OperatorIDList;
				wEXCCallTask.ConfirmID = wEXCTypeOption.ConfirmID;
				wEXCCallTask.Remark = wEXCTypeOption.Remark;
				wEXCCallTask.ImageList = wEXCTypeOption.ImageList;
				wEXCCallTask.RespondLevel = wEXCTypeOption.RespondLevel;
				wEXCCallTask.PartNo = wEXCCallApply.PartNo;
				wEXCCallTask.ReportTimes = wEXCExceptionRule.ReportTimes;
				wEXCCallTask.ForwardTimes = wEXCExceptionRule.ForwardTimes;
				wEXCCallTask.ShiftID = wShiftID;
				wEXCCallTask.PlaceNo = wEXCCallApply.PlaceNo;
				wEXCCallTask.PlaceID = wEXCCallApply.PlaceID;
				wEXCCallTask.DisplayBoard = wEXCCallApply.DisplayBoard;
				wEXCCallTask.Status = EXCCallStatus.Default.getValue();
				wEXCCallTask.ExpireTime = Calendar.getInstance();
				wEXCCallTask.ExpireTime.add(Calendar.MINUTE,
						EXCExceptionRuleDAO.getInstance().SelectEXCTimeItem(wLoginUser, wEXCExceptionRule,
								EXCActionTypes.Request, wEXCExceptionRule.ReportTimes).Time);

				wEXCCallTask.ID = EXCCallTaskDAO.getInstance().Update(wLoginUser, wEXCCallTask, wErrorCode);

				List<EXCCallDispatch> wEXCCallDispatchList = new ArrayList<EXCCallDispatch>();

				for (long wOperatorID : wEXCTypeOption.OperatorIDList) {
					wEXCCallDispatchList.add(new EXCCallDispatch(wEXCCallTask, wOperatorID));

					EXCMessage wEXCMessage = new EXCMessage();
					wEXCMessage.Active = 0;
					wEXCMessage.CompanyID = wEXCCallTask.CompanyID;
					wEXCMessage.CreateTime = Calendar.getInstance();
					wEXCMessage.EditTime = Calendar.getInstance();
					wEXCMessage.MessageID = wEXCCallTask.ID;
					wEXCMessage.MessageText = wEXCCallTask.getRemark();
					wEXCMessage.ShiftID = wShiftID;
					wEXCMessage.ModuleID = BPMEventModule.SCCall.getValue();
					wEXCMessage.StationID = wEXCCallTask.StationID;
					wEXCMessage.StationNo = wEXCCallTask.getStationNo();
					wEXCMessage.Title = StringUtils.Format("({0}){1}",
							EXCConstants.getEXCRespondLevel(wEXCCallTask.RespondLevel).Name,
							wEXCCallTask.ExceptionTypeName);
					wEXCMessage.Type = 0;
					wEXCMessage.ResponsorID = wOperatorID;
					EXCMessageDAO.getInstance().Update(wLoginUser, wEXCMessage, wErrorCode);
				}
				EXCCallDispatchDAO.getInstance().Insert(wLoginUser, wEXCCallDispatchList, wErrorCode);
			}

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public void ReportAndOverShiftForward(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		try {
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 1, 1);
			/// 获当班超时待处理记录 并且转发次数和上报次数不为0

			int wShiftID = EXCConstants.getShiftID(wLoginUser);
			int wLeftShiftID = EXCConstants.getLeftShiftID(wLoginUser);

			if (wShiftID <= 0 || wLeftShiftID <= 0)
				return;
			List<EXCCallDispatch> wReportList = EXCCallDispatchDAO.getInstance().SelectAll(wLoginUser, null, 0, 0, 0,
					wShiftID, wBaseTime, wBaseTime,
					StringUtils.parseList(new Integer[] { (int) EXCCallStatus.Default.getValue(),
							(int) EXCCallStatus.WaitRespond.getValue(), (int) EXCCallStatus.Rejected.getValue(),
							(int) EXCCallStatus.NoticeWaitRespond.getValue(),
							(int) EXCCallStatus.OnSiteRespond.getValue() }),
					wErrorCode);

			// 上个班次待超班转发记录
			List<EXCCallDispatch> wForwardList = EXCCallDispatchDAO.getInstance().SelectAll(wLoginUser, null, -1, -1,
					-1, wLeftShiftID, wBaseTime, wBaseTime,
					StringUtils.parseList(new Integer[] { (int) EXCCallStatus.Default.getValue(),
							(int) EXCCallStatus.WaitRespond.getValue(), (int) EXCCallStatus.Rejected.getValue(),
							(int) EXCCallStatus.NoticeWaitRespond.getValue(),
							(int) EXCCallStatus.OnSiteRespond.getValue() }),
					wErrorCode);

			// region 获取所有带操作任务
			List<Long> wTaskIDList = new ArrayList<Long>();

			wTaskIDList = wReportList.stream().map(p -> p.getTaskID()).collect(Collectors.toList());

			wTaskIDList.addAll(wForwardList.stream().map(p -> p.TaskID).distinct().collect(Collectors.toList()));

			if (wTaskIDList.size() < 1)
				return;

			List<EXCCallTask> wEXCCallTaskList = EXCCallTaskDAO.getInstance().SelectAll(wLoginUser, wTaskIDList,
					wErrorCode);
			if (wEXCCallTaskList == null || wEXCCallTaskList.size() < 1)
				return;
			wEXCCallTaskList.removeIf(p -> p.ForwardTimes == 0 && p.ReportTimes == 0);

			Map<Long, EXCCallTask> wEXCCallTaskDic = wEXCCallTaskList.stream()
					.collect(Collectors.toMap(EXCCallTask::getID, account -> account, (k1, k2) -> k2));

			if (wEXCCallTaskList == null || wEXCCallTaskList.size() < 1)
				return;

			// endRegion

			// Key TaskID
			Map<Long, List<Integer>> wForwardUserList = new HashMap<Long, List<Integer>>();

			/// Key EXCCallDispatch ID
			Map<Long, List<Integer>> wReportUserList = new HashMap<Long, List<Integer>>();

			// 上级不是自己就上报
			// region 判断是否上报
			// 每个人只有一个上级

			wReportList = wReportList.stream().filter(p -> {
				if (p.OperatorID == 0)
					return false;

				if (wReportUserList.containsKey(p.ID))
					return false;
				if (!wEXCCallTaskDic.containsKey(p.TaskID))
					return false;
				EXCCallTask wEXCCallTask = wEXCCallTaskDic.get(p.TaskID);

				// 判断是否超时
				if (wEXCCallTask.getExpireTime().compareTo(wEXCCallTask.getCreateTime()) <= 0
						|| wEXCCallTask.getExpireTime().compareTo(Calendar.getInstance()) > 0) {
					return false;
				}

				EXCExceptionType wEXCExceptionType = EXCExceptionTypeDAO.getInstance().Select(wLoginUser,
						wEXCCallTask.ExceptionTypeID, wErrorCode);
				if (wEXCExceptionType == null || wEXCExceptionType.ID <= 0)
					return false;
				List<SCHWorker> wSCHWorkerList = new ArrayList<SCHWorker>();
				for (int wPositionID : wEXCExceptionType.DutyPositionID) {

					List<SCHWorker> wSCHWorkerListTemp = Instance.wCoreService.SCH_QueryLeadWorkerByPositionID(
							wLoginUser, wPositionID, EXCConstants.getShiftID(wLoginUser)).List(SCHWorker.class);

					if (wSCHWorkerListTemp == null || wSCHWorkerListTemp.size() <= 0)
						continue;
					wSCHWorkerList.addAll(wSCHWorkerListTemp);

				}

				if (wSCHWorkerList.size() <= 0) {
					return false;
				} else {
					wReportUserList.put(p.ID, wSCHWorkerList.stream().map((SCHWorker q) -> q.WorkerID).distinct()
							.collect(Collectors.toList()));
				}

				return true;
			}).collect(Collectors.toList());

			// endRegion

			// 当班是不是自己都得转发
			// region 转发
			wForwardList = wForwardList.stream().filter(p -> {
				if (!wForwardUserList.containsKey(p.ID)) {
					if (!wEXCCallTaskDic.containsKey(p.TaskID))
						return false;

					if (!wEXCCallTaskDic.containsKey(p.TaskID))
						return false;
					EXCCallTask wEXCCallTask = wEXCCallTaskDic.get(p.TaskID);
					if (wEXCCallTask.getForwardTimes() <= 0) {
						// 判断是否具备转发条件
						return false;
					}
					EXCExceptionType wEXCExceptionType = EXCExceptionTypeDAO.getInstance().Select(wLoginUser,
							wEXCCallTask.ExceptionTypeID, wErrorCode);
					if (wEXCExceptionType == null || wEXCExceptionType.ID <= 0)
						return false;
					List<SCHWorker> wSCHWorkerList = new ArrayList<SCHWorker>();
					for (int wPositionID : wEXCExceptionType.DutyPositionID) {

						List<SCHWorker> wSCHWorkerListTemp = Instance.wCoreService
								.SCH_QueryWorkerByPositionID(wLoginUser, wPositionID, wShiftID).List(SCHWorker.class);

						if (wSCHWorkerListTemp == null || wSCHWorkerListTemp.size() <= 0)
							continue;
						wSCHWorkerList.addAll(wSCHWorkerListTemp);

					}

					if (wSCHWorkerList.size() < 1) {
						wForwardUserList.put(p.ID, StringUtils.parseList(new Integer[] { (int) p.OperatorID }));
					} else {
						wForwardUserList.put(p.ID, wSCHWorkerList.stream().map((SCHWorker q) -> q.WorkerID).distinct()
								.collect(Collectors.toList()));
					}
				}
				return true;
			}).collect(Collectors.toList());
			// endRegion

			if (wReportList.size() > 0) {
				ReportDispatch(wLoginUser, wReportList, wReportUserList, wEXCCallTaskDic, wErrorCode);
			}
			if (wForwardList.size() > 0) {
				OverShiftForward(wLoginUser, wForwardList, wForwardUserList, wEXCCallTaskDic, wErrorCode);
			}

		} catch (

		Exception e) {
			logger.error(e.toString());
		}
	}

	/**
	 * 上报 可能同个任务的上报人相同 重复上报在更新Dispatch时会阻拦
	 * 
	 * @param wEXCCallDispatchList
	 * @param wUserListDic
	 * @param wEXCCallTaskDic
	 */
	private void ReportDispatch(BMSEmployee wLoginUser, List<EXCCallDispatch> wEXCCallDispatchList,
			Map<Long, List<Integer>> wUserListDic, Map<Long, EXCCallTask> wEXCCallTaskDic,
			OutResult<Integer> wErrorCode) {
		// 上报之后将本记录的状态改掉
		if (wEXCCallDispatchList == null || wEXCCallDispatchList.size() < 1)
			return;
		if (wUserListDic == null || wUserListDic.size() < 1)
			return;
		if (wEXCCallTaskDic == null || wEXCCallTaskDic.size() < 1)
			return;

		List<Long> wLongList = new ArrayList<Long>();

		for (EXCCallDispatch wEXCCallDispatch : wEXCCallDispatchList) {
			if (!wEXCCallTaskDic.containsKey(wEXCCallDispatch.TaskID))
				continue;

			EXCCallTask wEXCCallTask = wEXCCallTaskDic.get(wEXCCallDispatch.TaskID);
			if (wEXCCallTask.ReportTimes == 0)
				continue;
			if (!wUserListDic.containsKey(wEXCCallDispatch.ID) || wUserListDic.get(wEXCCallDispatch.ID).size() <= 0) {
				logger.info(StringUtils.Format("dispatchID: {0} operatorID:{1} not found leader", wEXCCallDispatch.ID,
						wEXCCallDispatch.OperatorID));
				continue;
			}

			for (int wReporterID : wUserListDic.get(wEXCCallDispatch.ID)) {
				if (wReporterID == wEXCCallDispatch.OperatorID)
					continue;

				EXCCallDispatch wInsertEXCCallDispatch = new EXCCallDispatch(wEXCCallTask, wReporterID);
				wInsertEXCCallDispatch.CreatorID = wEXCCallDispatch.OperatorID;
				wInsertEXCCallDispatch.ShiftID = EXCConstants.getShiftID(wLoginUser);

				EXCMessage wEXCMessage = new EXCMessage();
				wEXCMessage.Active = 0;
				wEXCMessage.CompanyID = wEXCCallTask.CompanyID;
				wEXCMessage.CreateTime = Calendar.getInstance();
				wEXCMessage.EditTime = Calendar.getInstance();
				wEXCMessage.MessageID = wEXCCallTask.ID;
				wEXCMessage.MessageText = wEXCCallTask.getRemark();
				wEXCMessage.ShiftID = EXCConstants.getShiftID(wLoginUser);
				wEXCMessage.ModuleID = BPMEventModule.SCCall.getValue();
				wEXCMessage.StationID = wEXCCallTask.StationID;
				wEXCMessage.StationNo = wEXCCallTask.getStationNo();
				wEXCMessage.Title = StringUtils.Format("({0}){1}",
						EXCConstants.getEXCRespondLevel(wEXCCallTask.RespondLevel).Name,
						wEXCCallTask.ExceptionTypeName);
				wEXCMessage.Type = 0;
				wEXCMessage.ResponsorID = wReporterID;

				EXCMessageDAO.getInstance().Update(wLoginUser, wEXCMessage, wErrorCode);

				EXCCallDispatchDAO.getInstance().Update(wLoginUser, wInsertEXCCallDispatch, wErrorCode);
			}

			wEXCCallDispatch.Status = (int) EXCCallStatus.UpGraded.getValue();
			EXCCallDispatchDAO.getInstance().Update(wLoginUser, wEXCCallDispatch, wErrorCode);

			if (!wLongList.contains(wEXCCallDispatch.TaskID)) {
				wEXCCallTask.ReportTimes--;
				EXCCallTaskDAO.getInstance().Update(wLoginUser, wEXCCallTask, wErrorCode);
				wLongList.add(wEXCCallDispatch.TaskID);
			}

		}

	}

	/// <summary>
	/// 转发 转发有可能多个接收记录转发 转发给多个人 如果转发给同一个人时更新Dispatch时会阻拦
	/// </summary>
	/// <param name="wEXCCallDispatchList"></param>
	/// <param name="wUserListDic"></param>
	/// <param name="wEXCCallTaskDic"></param>
	private void OverShiftForward(BMSEmployee wLoginUser, List<EXCCallDispatch> wEXCCallDispatchList,
			Map<Long, List<Integer>> wUserListDic, Map<Long, EXCCallTask> wEXCCallTaskDic,
			OutResult<Integer> wErrorCode) {

		// 转发之后将本记录的状态改掉
		if (wEXCCallDispatchList == null || wEXCCallDispatchList.size() < 1)
			return;
		if (wUserListDic == null || wUserListDic.size() < 1)
			return;
		if (wEXCCallTaskDic == null || wEXCCallTaskDic.size() < 1)
			return;
		List<Long> wLongList = new ArrayList<Long>();
		for (EXCCallDispatch wEXCCallDispatch : wEXCCallDispatchList) {
			if (!wEXCCallTaskDic.containsKey(wEXCCallDispatch.TaskID))
				continue;

			EXCCallTask wEXCCallTask = wEXCCallTaskDic.get(wEXCCallDispatch.TaskID);
			if (wEXCCallTask.ForwardTimes == 0)
				continue;
			if (!wUserListDic.containsKey(wEXCCallDispatch.ID))
				continue;

			wEXCCallDispatch.Status = EXCCallStatus.Forwarded.getValue();

			EXCCallDispatchDAO.getInstance().Update(wLoginUser, wEXCCallDispatch, wErrorCode);

			for (int wForwarderID : wUserListDic.get(wEXCCallDispatch.ID)) {

				EXCCallDispatch wInsertEXCCallDispatch = new EXCCallDispatch(wEXCCallTask, wForwarderID);
				wInsertEXCCallDispatch.CreatorID = wEXCCallDispatch.OperatorID;
				wInsertEXCCallDispatch.ShiftID = EXCConstants.getShiftID(wLoginUser);
				EXCCallDispatchDAO.getInstance().Update(wLoginUser, wInsertEXCCallDispatch, wErrorCode);
				// 需要发消息给上级领导 判断是否发过

			}

			wEXCCallTask.ShiftID = EXCConstants.getShiftID(wLoginUser);
			wEXCCallTask.Status = EXCCallStatus.WaitRespond.getValue();

			if (!wLongList.contains(wEXCCallDispatch.TaskID)) {
				wEXCCallTask.ForwardTimes--;
				EXCCallTaskDAO.getInstance().Update(wLoginUser, wEXCCallTask, wErrorCode);
				wLongList.add(wEXCCallDispatch.TaskID);
			}
		}

	}

	public void ExcTypeAI(BMSEmployee wLoginUser, EXCCallApply wEXCCallApply, OutResult<Integer> wErrorCode) {
		try {
			if (wEXCCallApply == null || wEXCCallApply.ExceptionTypeList == null
					|| wEXCCallApply.ExceptionTypeList.size() < 1)
				return;

			wEXCCallApply.ExceptionTypeList = wEXCCallApply.ExceptionTypeList.stream().collect(

					collectingAndThen(toCollection(() -> new TreeSet<>(comparing((EXCTypeOption p) -> p.EXCTypeName))),
							ArrayList::new));

			// .GroupBy(p => p.EXCTypeName).Select(p => p.First()).ToList();

			for (EXCTypeOption wEXCTypeOption : wEXCCallApply.ExceptionTypeList) {
				if (wEXCTypeOption.EXCTypeID > 0)
					continue;
				// 循环查询的意义在于大数据分开查询 查询数据不重复
				List<EXCExceptionType> wEXCExceptionTypeList = EXCExceptionTypeDAO.getInstance().SelectAll(wLoginUser,
						wEXCTypeOption.EXCTypeName, wEXCCallApply.StationType, TaskRelevancyTypes.Default, -1,
						wErrorCode);

				if (wEXCExceptionTypeList.size() > 0) {
					Optional<EXCExceptionType> wEXCExceptionTypeOptional = wEXCExceptionTypeList.stream()
							.filter(p -> p.Active == 1).findFirst();

					if (wEXCExceptionTypeOptional.isPresent() && wEXCExceptionTypeOptional.get().getID() > 0) {
						wEXCTypeOption.EXCTypeID = wEXCExceptionTypeOptional.get().getID();
					} else {
						wEXCTypeOption.EXCTypeID = wEXCExceptionTypeList.get(0).ID;
					}
				} else {
					EXCExceptionType wEXCExceptionType = new EXCExceptionType();

					wEXCExceptionType.ID = 0;
					wEXCExceptionType.Active = 1;
					wEXCExceptionType.AgainInterval = 0;
					wEXCExceptionType.ApproverPositionID = wEXCTypeOption.ApproverPositionID;
					wEXCExceptionType.ConfirmPositionID = wEXCTypeOption.ConfirmPositionID;
					wEXCExceptionType.DutyPositionID = wEXCTypeOption.DutyPositionID;
					wEXCExceptionType.CreateTime = Calendar.getInstance();
					wEXCExceptionType.CreatorID = wEXCCallApply.ApplicantID;
					wEXCExceptionType.EditorID = wEXCCallApply.ApplicantID;
					wEXCExceptionType.EditTime = Calendar.getInstance();
					wEXCExceptionType.Name = wEXCTypeOption.EXCTypeName;
					wEXCExceptionType.RelevancyTaskType = (int) TaskRelevancyTypes.Default.getValue();
					wEXCExceptionType.StationType = wEXCCallApply.StationType;
					wEXCExceptionType.StationTypeName = wEXCCallApply.StationTypeName;

					wEXCTypeOption.EXCTypeID = EXCExceptionTypeDAO.getInstance().Update(wLoginUser, wEXCExceptionType,
							wErrorCode);
				}

			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	public EXCManagerUtils() {
		super();
	}

	public static EXCManagerUtils getInstance() {
		if (Instance == null)
			Instance = new EXCManagerUtils();
		return Instance;
	}
}
