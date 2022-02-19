package com.mes.exc.server.serviceimpl.utils.exc;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.util.ResourceUtils;

import com.mes.exc.server.service.po.BPMResource;
import com.mes.exc.server.service.po.OutResult;
import com.mes.exc.server.service.po.bfc.QRTypes;
import com.mes.exc.server.service.po.bms.BMSDepartment;
import com.mes.exc.server.service.po.bms.BMSEmployee;
import com.mes.exc.server.service.po.exc.EXCCallTaskBPM;
import com.mes.exc.server.service.po.exc.EXCOptionItem;
import com.mes.exc.server.service.po.exc.EXCRunConfig;
import com.mes.exc.server.service.po.exc.base.EXCExceptionTemplate;
import com.mes.exc.server.service.po.exc.base.EXCExceptionType;
import com.mes.exc.server.service.po.exc.base.EXCStationType;
import com.mes.exc.server.service.po.exc.define.EXCActionTypes;
import com.mes.exc.server.service.po.exc.define.EXCResourceTypes;
import com.mes.exc.server.service.po.exc.define.EXCTemplates;
import com.mes.exc.server.service.po.exc.define.TaskRelevancyTypes;
import com.mes.exc.server.service.po.fmc.FMCWorkspace;
import com.mes.exc.server.service.po.fpc.FPCPart;
import com.mes.exc.server.service.po.fpc.FPCProduct;
import com.mes.exc.server.serviceimpl.CoreServiceImpl;
import com.mes.exc.server.serviceimpl.dao.BaseDAO;
import com.mes.exc.server.serviceimpl.dao.exc.base.EXCExceptionTypeDAO;
import com.mes.exc.server.serviceimpl.dao.exc.base.EXCStationTypeDAO;
import com.mes.exc.server.serviceimpl.dao.exc.tree.EXCAndonDAO;
import com.mes.exc.server.service.utils.StringUtils;
import com.mes.exc.server.service.utils.XmlTool;

public class EXCConstants {

	public EXCConstants() {
		// TODO Auto-generated constructor stub
	}

	private static String ConfigPath = null;

	public static synchronized String getConfigPath() {
		if (ConfigPath == null) {
			try {
				ConfigPath = ResourceUtils.getURL("classpath:config").getPath().replace("%20", " ");

				if (ConfigPath != null && ConfigPath.length() > 3 && ConfigPath.indexOf(":") > 0) {
					if (ConfigPath.indexOf("/") == 0)
						ConfigPath = ConfigPath.substring(1);

					if (!ConfigPath.endsWith("/"))
						ConfigPath = ConfigPath + "/";
				}
			} catch (FileNotFoundException e) {
				return "config/";
			}
		}
		return ConfigPath;
	}

	private static List<EXCExceptionTemplate> EXCExceptionTemplateList;

	public static List<EXCExceptionTemplate> getEXCExceptionTemplateList() {
		if (EXCExceptionTemplateList == null)
			EXCExceptionTemplateList = new ArrayList<EXCExceptionTemplate>();
		if (EXCExceptionTemplateList.size() < 1) {

			EXCExceptionTemplateList.add(new EXCExceptionTemplate(EXCTemplates.Artificial, EXCResourceTypes.Artificial,
					EXCResourceTypes.Artificial, EXCResourceTypes.Artificial));

			EXCExceptionTemplateList.add(new EXCExceptionTemplate(EXCTemplates.Artificial_System,
					EXCResourceTypes.Artificial, EXCResourceTypes.System, EXCResourceTypes.System));

			EXCExceptionTemplateList.add(new EXCExceptionTemplate(EXCTemplates.System, EXCResourceTypes.System,
					EXCResourceTypes.System, EXCResourceTypes.System));

			EXCExceptionTemplateList.add(new EXCExceptionTemplate(EXCTemplates.System_Artificial,
					EXCResourceTypes.System, EXCResourceTypes.Artificial, EXCResourceTypes.Artificial));

			EXCExceptionTemplateList.add(new EXCExceptionTemplate(EXCTemplates.System_Artificial_System,
					EXCResourceTypes.System, EXCResourceTypes.Artificial, EXCResourceTypes.System));
		}
		return EXCExceptionTemplateList;

	}

	private static int ShiftID = 0;

	public static int getShiftID(BMSEmployee wLoginUser) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

		int wResult = CoreServiceImpl.getInstance().SFC_QueryShiftID(wLoginUser, 0).Info(Integer.class);
		if (wResult > 0 && wResult != ShiftID) {
			LeftShiftID = ShiftID;
			ShiftID = wResult;
			EXCAndonDAO.getInstance().UpdateShiftID(wLoginUser, ShiftID, LeftShiftID, wErrorCode);
		}
		return ShiftID;

	}

	private static int LeftShiftID = 0;

	public static int getLeftShiftID(BMSEmployee wLoginUser) {

		if (LeftShiftID == 0) {
			getShiftID(wLoginUser);
			if (LeftShiftID == 0) {
				int wResult = CoreServiceImpl.getInstance().SFC_QueryShiftID(wLoginUser, -1).Info(Integer.class);
				if (wResult > 0 && wResult != ShiftID)
					LeftShiftID = wResult;
			}
		}

		return LeftShiftID;

	}

	private static List<EXCOptionItem> EXCActionTypeList;

	public static List<EXCOptionItem> getEXCActionTypeList() {

		if (EXCActionTypeList == null)
			EXCActionTypeList = new ArrayList<EXCOptionItem>();
		if (EXCActionTypeList.size() < 1) {

			for (EXCActionTypes wEXCActionTypes : EXCActionTypes.values()) {
				EXCOptionItem wEXCOptionItem = new EXCOptionItem();

				wEXCOptionItem.ID = (int) wEXCActionTypes.getValue();
				wEXCOptionItem.Sign = wEXCActionTypes.toString();
				wEXCOptionItem.Name = wEXCActionTypes.getLable();

				EXCActionTypeList.add(wEXCOptionItem);
			}

		}
		return EXCActionTypeList;

	}

	private static EXCRunConfig EXCRunConfig;

	public static EXCRunConfig getEXCRunConfig() {

		if (EXCRunConfig == null) {
			EXCRunConfig = XmlTool.ReadXml(getConfigPath() + "EXCRunConfig.xml");

			if (EXCRunConfig == null) {
				EXCRunConfig = new EXCRunConfig();
				XmlTool.SaveXml(getConfigPath() + "EXCRunConfig.xml", EXCRunConfig);
			}

		}

		return EXCRunConfig;

	}

	public static synchronized void setEXCRunConfig(EXCRunConfig eXCRunConfig) {
		EXCRunConfig = eXCRunConfig;
		XmlTool.SaveXml(getConfigPath() + "EXCRunConfig.xml", EXCRunConfig);
	}

	private static List<EXCOptionItem> EXCRespondLevelList;

	public static List<EXCOptionItem> getEXCRespondLevelList() {

		if (EXCRespondLevelList == null) {
			EXCRespondLevelList = XmlTool.ReadXml(getConfigPath() + "EXCRespondLevel.xml");

			if (EXCRespondLevelList == null) {
				EXCRespondLevelList = new ArrayList<EXCOptionItem>();
				XmlTool.SaveXml(getConfigPath() + "EXCRespondLevel.xml", EXCRespondLevelList);
			}
		}

		return EXCRespondLevelList;
	}

	private static Map<Integer, EXCOptionItem> EXCRespondLevelDic = null;

	public static Map<Integer, EXCOptionItem> getEXCRespondLevelDic() {
		if (EXCRespondLevelDic == null)
			EXCRespondLevelDic = new HashMap<Integer, EXCOptionItem>();

		List<EXCOptionItem> wEXCOptionItemList = getEXCRespondLevelList();
		if (EXCRespondLevelDic.size() != wEXCOptionItemList.size()) {
			EXCRespondLevelDic = wEXCOptionItemList.stream().collect(Collectors.toMap(p -> p.ID, k -> k));
		}

		return EXCRespondLevelDic;
	}

	public static EXCOptionItem getEXCRespondLevel(int wKey) {
		EXCOptionItem wEXCOptionItem = new EXCOptionItem();
		if (getEXCRespondLevelDic().containsKey(wKey))
			wEXCOptionItem = getEXCRespondLevelDic().get(wKey);
		return wEXCOptionItem;
	}

	public static synchronized void setEXCRespondLevelList(List<EXCOptionItem> eXCRespondLevelList) {
		EXCRespondLevelList = eXCRespondLevelList;
		XmlTool.SaveXml(getConfigPath() + "EXCRespondLevel.xml", EXCRespondLevelList);
	}

	// region 用户全局数据
	private static Calendar RefreshEmployeeTime = Calendar.getInstance();

	private static Map<Integer, BMSEmployee> BMSEmployeeDic = new HashMap<Integer, BMSEmployee>();

	public static synchronized Map<Integer, BMSEmployee> GetBMSEmployeeList() {
		if (BMSEmployeeDic == null || BMSEmployeeDic.size() <= 0
				|| RefreshEmployeeTime.compareTo(Calendar.getInstance()) < 0) {
			List<BMSEmployee> wBMSEmployeeList = CoreServiceImpl.getInstance()
					.BMS_GetEmployeeAll(BaseDAO.SysAdmin, 0, 0, 1).List(BMSEmployee.class);
			if (wBMSEmployeeList != null && wBMSEmployeeList.size() > 0) {
				BMSEmployeeDic = wBMSEmployeeList.stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
			}
			RefreshEmployeeTime = Calendar.getInstance();
			RefreshEmployeeTime.add(Calendar.MINUTE, 3);
		}
		return BMSEmployeeDic;
	}

	public static BMSEmployee GetBMSEmployee(int wID) {
		BMSEmployee wResult = new BMSEmployee();
		if (EXCConstants.GetBMSEmployeeList().containsKey(wID)) {
			if (EXCConstants.GetBMSEmployeeList().get(wID) != null) {
				wResult = EXCConstants.GetBMSEmployeeList().get(wID);
			}
		}
		return wResult;
	}

	public static String GetBMSEmployeeName(int wID) {
		String wResult = "";
		if (EXCConstants.GetBMSEmployeeList().containsKey(wID)) {
			if (EXCConstants.GetBMSEmployeeList().get(wID) != null) {
				wResult = EXCConstants.GetBMSEmployeeList().get(wID).getName();
			}
		}
		return wResult;
	}

	public static String GetBMSEmployeeName(List<Integer> wIDList) {
		String wResult = "";
		if (wIDList == null || wIDList.size() <= 0)
			return wResult;

		List<String> wNames = new ArrayList<String>();
		for (Integer integer : wIDList) {
			if (integer <= 0)
				continue;

			if (EXCConstants.GetBMSEmployeeList().containsKey(integer)) {
				if (EXCConstants.GetBMSEmployeeList().get(integer) != null) {
					wNames.add(EXCConstants.GetBMSEmployeeList().get(integer).getName());
				}
			}

		}
		wResult = StringUtils.Join(",", wNames);

		return wResult;
	}
	// endRegion

	// region 部门全局数据
	private static Calendar RefreshDeptTime = Calendar.getInstance();

	private static Map<Integer, BMSDepartment> BMSDepartmentDic = new HashMap<Integer, BMSDepartment>();

	public static synchronized Map<Integer, BMSDepartment> GetBMSDepartmentList() {
		if (BMSDepartmentDic == null || BMSDepartmentDic.size() <= 0
				|| RefreshDeptTime.compareTo(Calendar.getInstance()) < 0) {
			List<BMSDepartment> wBMSDepartmentList = CoreServiceImpl.getInstance()
					.BMS_QueryDepartmentList(BaseDAO.SysAdmin).List(BMSDepartment.class);
			if (wBMSDepartmentList != null && wBMSDepartmentList.size() > 0) {
				BMSDepartmentDic = wBMSDepartmentList.stream()
						.collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
			}
			RefreshDeptTime = Calendar.getInstance();
			RefreshDeptTime.add(Calendar.MINUTE, 3);
		}
		return BMSDepartmentDic;
	}

	public static String GetBMSDepartmentName(List<Integer> wIDList) {
		String wResult = "";
		if (wIDList == null || wIDList.size() <= 0)
			return wResult;

		List<String> wNames = new ArrayList<String>();
		for (Integer integer : wIDList) {
			if (integer <= 0)
				continue;

			if (EXCConstants.GetBMSDepartmentList().containsKey(integer)) {
				if (EXCConstants.GetBMSDepartmentList().get(integer) != null) {
					wNames.add(EXCConstants.GetBMSDepartmentList().get(integer).getName());
				}
			}

		}
		wResult = StringUtils.Join(",", wNames);

		return wResult;
	}

	public static String GetBMSDepartmentName(int wID) {
		String wResult = "";
		if (EXCConstants.GetBMSDepartmentList().containsKey(wID)) {
			if (EXCConstants.GetBMSDepartmentList().get(wID) != null) {
				wResult = EXCConstants.GetBMSDepartmentList().get(wID).getName();
			}
		}
		return wResult;
	}

	public static BMSDepartment GetBMSDepartment(int wID) {
		BMSDepartment wResult = new BMSDepartment();
		if (EXCConstants.GetBMSDepartmentList().containsKey(wID)) {
			if (EXCConstants.GetBMSDepartmentList().get(wID) != null) {
				wResult = EXCConstants.GetBMSDepartmentList().get(wID);
			}
		}
		return wResult;
	}

	// endRegion

	/**
	 * 工位全局数据
	 */
	private static Calendar RefreshPartTime = Calendar.getInstance();

	private static Map<Integer, FPCPart> FPCPartDic = new HashMap<Integer, FPCPart>();

	public static synchronized Map<Integer, FPCPart> GetFPCPartList() {
		if (FPCPartDic == null || FPCPartDic.size() <= 0 || RefreshPartTime.compareTo(Calendar.getInstance()) < 0) {
			List<FPCPart> wFPCPartList = CoreServiceImpl.getInstance().FPC_QueryPartList(BaseDAO.SysAdmin)
					.List(FPCPart.class);
			if (wFPCPartList != null && wFPCPartList.size() > 0) {
				FPCPartDic = wFPCPartList.stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
			}
			RefreshPartTime = Calendar.getInstance();
			RefreshPartTime.add(Calendar.MINUTE, 3);
		}
		return FPCPartDic;
	}

	public static String GetFPCPartName(int wID) {
		String wResult = "";
		if (EXCConstants.GetFPCPartList().containsKey(wID)) {
			if (EXCConstants.GetFPCPartList().get(wID) != null) {
				wResult = EXCConstants.GetFPCPartList().get(wID).getName();
			}
		}
		return wResult;
	}

	public static FPCPart GetFPCPart(int wID) {
		FPCPart wResult = new FPCPart();
		if (EXCConstants.GetFPCPartList().containsKey(wID)) {
			if (EXCConstants.GetFPCPartList().get(wID) != null) {
				wResult = EXCConstants.GetFPCPartList().get(wID);
			}
		}
		return wResult;
	}

	/**
	 * 产品型号全局数据
	 */
	private static Calendar RefreshProductTime = Calendar.getInstance();

	private static Map<Integer, FPCProduct> FPCProductDic = new HashMap<Integer, FPCProduct>();

	public static synchronized Map<Integer, FPCProduct> GetFPCProductList() {
		if (FPCProductDic == null || FPCProductDic.size() <= 0
				|| RefreshProductTime.compareTo(Calendar.getInstance()) < 0) {
			List<FPCProduct> wFPCProductList = CoreServiceImpl.getInstance().FPC_QueryProductList(BaseDAO.SysAdmin)
					.List(FPCProduct.class);
			if (wFPCProductList != null && wFPCProductList.size() > 0) {
				FPCProductDic = wFPCProductList.stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
			}
			RefreshProductTime = Calendar.getInstance();
			RefreshProductTime.add(Calendar.MINUTE, 3);
		}
		return FPCProductDic;
	}

	public static String GetFPCProductName(int wID) {
		String wResult = "";
		if (EXCConstants.GetFPCProductList().containsKey(wID)) {
			if (EXCConstants.GetFPCProductList().get(wID) != null) {
				wResult = EXCConstants.GetFPCProductList().get(wID).getProductName();
			}
		}
		return wResult;
	}

	public static FPCProduct GetFPCProduct(int wID) {
		FPCProduct wResult = new FPCProduct();
		if (EXCConstants.GetFPCProductList().containsKey(wID)) {
			if (EXCConstants.GetFPCProductList().get(wID) != null) {
				wResult = EXCConstants.GetFPCProductList().get(wID);
			}
		}
		return wResult;
	}

	public static int GetFPCProducID(String wProductNo) {
		int wResult = 0;
		for (int wProductID : EXCConstants.GetFPCProductList().keySet()) {
			if (EXCConstants.GetFPCProductList().get(wProductID).ProductNo.equals(wProductNo)) {
				return wProductID;
			}
		}
		return wResult;
	}

	public static BPMResource<EXCCallTaskBPM> EXCTaskAutoJudgeResource = new BPMResource<EXCCallTaskBPM>();

	/**
	 * 台位全局数据
	 */
	private static Calendar RefreshWorkspaceTime = Calendar.getInstance();

	private static List<FMCWorkspace> FMCWorkspaceList = new ArrayList<FMCWorkspace>();

	public static synchronized List<FMCWorkspace> GetFMCWorkspaceList() {
		if (FMCWorkspaceList == null || FMCWorkspaceList.size() <= 0
				|| RefreshWorkspaceTime.compareTo(Calendar.getInstance()) < 0) {
			List<FMCWorkspace> wFMCWorkspaceList = CoreServiceImpl.getInstance()
					.FMC_GetFMCWorkspaceList(BaseDAO.SysAdmin, -1, -1, "", -1, 1).List(FMCWorkspace.class);
			RefreshWorkspaceTime = Calendar.getInstance();
			RefreshWorkspaceTime.add(Calendar.MINUTE, 3);
			FMCWorkspaceList = wFMCWorkspaceList;
		}
		return FMCWorkspaceList;
	}

	public static String GetFMCWorkspaceName(int wID) {
		String wResult = "";
		if (EXCConstants.GetFMCWorkspaceList().stream().anyMatch(p -> p.ID == wID)) {
			wResult = EXCConstants.GetFMCWorkspaceList().stream().filter(p -> p.ID == wID).findFirst().get().Name;
		}
		return wResult;
	}

	public static FMCWorkspace GetFMCWorkspace(int wID) {
		FMCWorkspace wResult = new FMCWorkspace();
		if (EXCConstants.GetFMCWorkspaceList().stream().anyMatch(p -> p.ID == wID)) {
			wResult = EXCConstants.GetFMCWorkspaceList().stream().filter(p -> p.ID == wID).findFirst().get();
		}
		return wResult;
	}

	/**
	 * 异常地点类型数据
	 */
	private static Calendar RefreshStationTypeTime = Calendar.getInstance();

	private static Map<Long, EXCStationType> EXCStationTypeDic = new HashMap<Long, EXCStationType>();

	public static synchronized Map<Long, EXCStationType> GetEXCStationTypeList() {
		if (EXCStationTypeDic == null || EXCStationTypeDic.size() <= 0
				|| RefreshStationTypeTime.compareTo(Calendar.getInstance()) < 0) {

			OutResult<Integer> wErrorCode = new OutResult<Integer>();
			List<EXCStationType> wEXCStationTypeList = EXCStationTypeDAO.getInstance().SelectAll(BaseDAO.SysAdmin, null,
					"", QRTypes.Default, -1, wErrorCode);
			if (wEXCStationTypeList != null && wEXCStationTypeList.size() > 0) {
				EXCStationTypeDic = wEXCStationTypeList.stream()
						.collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
			}
			RefreshStationTypeTime = Calendar.getInstance();
			RefreshStationTypeTime.add(Calendar.MINUTE, 3);
		}
		return EXCStationTypeDic;
	}

	public static String GetEXCStationTypeName(Long wID) {
		String wResult = "";
		if (EXCConstants.GetEXCStationTypeList().containsKey(wID)) {
			if (EXCConstants.GetEXCStationTypeList().get(wID) != null) {
				wResult = EXCConstants.GetEXCStationTypeList().get(wID).getName();
			}
		}
		return wResult;
	}

	public static EXCStationType GetEXCStationType(Long wID) {
		EXCStationType wResult = new EXCStationType();
		if (EXCConstants.GetEXCStationTypeList().containsKey(wID)) {
			if (EXCConstants.GetEXCStationTypeList().get(wID) != null) {
				wResult = EXCConstants.GetEXCStationTypeList().get(wID);
			}
		}
		return wResult;
	}

	/**
	 * 异常类型数据
	 */
	private static Calendar RefreshExceptionTypeTime = Calendar.getInstance();

	private static Map<Long, EXCExceptionType> EXCExceptionTypeDic = new HashMap<Long, EXCExceptionType>();

	public static synchronized Map<Long, EXCExceptionType> GetEXCExceptionTypeList() {
		if (EXCExceptionTypeDic == null || EXCExceptionTypeDic.size() <= 0
				|| RefreshExceptionTypeTime.compareTo(Calendar.getInstance()) < 0) {

			OutResult<Integer> wErrorCode = new OutResult<Integer>();
			List<EXCExceptionType> wEXCExceptionTypeList = EXCExceptionTypeDAO.getInstance().SelectAll(BaseDAO.SysAdmin,
					"", -1, TaskRelevancyTypes.Default, -1, wErrorCode);
			if (wEXCExceptionTypeList != null && wEXCExceptionTypeList.size() > 0) {
				EXCExceptionTypeDic = wEXCExceptionTypeList.stream()
						.collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));
			}
			RefreshExceptionTypeTime = Calendar.getInstance();
			RefreshExceptionTypeTime.add(Calendar.MINUTE, 3);
		}
		return EXCExceptionTypeDic;
	}

	public static String GetEXCExceptionTypeName(Long wID) {
		String wResult = "";
		if (EXCConstants.GetEXCExceptionTypeList().containsKey(wID)) {
			if (EXCConstants.GetEXCExceptionTypeList().get(wID) != null) {
				wResult = EXCConstants.GetEXCExceptionTypeList().get(wID).getName();
			}
		}
		return wResult;
	}

	public static EXCExceptionType GetEXCExceptionType(Long wID) {
		EXCExceptionType wResult = new EXCExceptionType();
		if (EXCConstants.GetEXCExceptionTypeList().containsKey(wID)) {
			if (EXCConstants.GetEXCExceptionTypeList().get(wID) != null) {
				wResult = EXCConstants.GetEXCExceptionTypeList().get(wID);
			}
		}
		return wResult;
	}

}
