package com.mes.exc.server.serviceimpl.utils.exc;

import java.util.ArrayList;
import java.util.List;

import com.mes.exc.server.service.po.exc.EXCOptionItem;
import com.mes.exc.server.service.po.exc.EXCTimeItem;
import com.mes.exc.server.service.po.exc.EXCTypeOption;
import com.mes.exc.server.service.utils.StringUtils; 

public class EXCStrClaConverter {

	public EXCStrClaConverter() {
		// TODO Auto-generated constructor stub
	}

	public static <T> String ToString(List<T> wTList) {
		if (wTList == null || wTList.size() < 1)
			return "";

		List<String> wStringList = new ArrayList<String>();

		wTList.forEach(p -> wStringList.add(p.toString()));

		return StringUtils.Join("+|;|+", wStringList);

	}

	public static String EXCOptionItemToString(List<EXCOptionItem> wEXCOptionItemList)

	{

		if (wEXCOptionItemList == null || wEXCOptionItemList.size() < 1)
			return "";

		List<String> wStringList = new ArrayList<String>();

		wEXCOptionItemList.forEach(p -> wStringList.add(p.toString()));

		return StringUtils.Join("+|;|+", wStringList);

	}

	public static List<EXCOptionItem> EXCOptionItemToList(String wString) {
		List<EXCOptionItem> wResult = new ArrayList<EXCOptionItem>();
		if (wString == null || wString.isEmpty())
			return wResult;

		String[] wStringList = wString.split("\\+\\|;\\|\\+");

		if (wStringList == null || wStringList.length < 1)
			return wResult;

		for (String wStringItem : wStringList) {
			EXCOptionItem wEXCOptionItem = new EXCOptionItem(wStringItem);
			wResult.add(wEXCOptionItem);
		}
		return wResult;
	}

	public static String EXCTypeOptionToString(List<EXCTypeOption> wEXCTypeOptionList) {
		if (wEXCTypeOptionList == null || wEXCTypeOptionList.size() < 1)
			return "";

		List<String> wStringList = new ArrayList<String>();

		wEXCTypeOptionList.forEach(p -> wStringList.add(p.toString()));

		return StringUtils.Join("+|;|+", wStringList);
	}

	public static List<EXCTypeOption> EXCTypeOptionToList(String wString) {
		List<EXCTypeOption> wResult = new ArrayList<EXCTypeOption>();
		if (wString == null || wString.isEmpty())
			return wResult;

		String[] wStringList = wString.split("\\+\\|;\\|\\+");

		if (wStringList == null || wStringList.length < 1)
			return wResult;

		for (String wStringItem : wStringList) {
			EXCTypeOption wEXCTypeOption = new EXCTypeOption(wStringItem);
			wResult.add(wEXCTypeOption);
		}
		return wResult;
	}

	public static String EXCTimeItemToString(List<EXCTimeItem> wEXCTimeItemList) {
		if (wEXCTimeItemList == null || wEXCTimeItemList.size() < 1)
			return "";

		List<String> wStringList = new ArrayList<String>();

		wEXCTimeItemList.forEach(p -> wStringList.add(p.toString()));

		return StringUtils.Join("+|;|+", wStringList);
	}

	public static List<EXCTimeItem> EXCTimeItemToList(String wString) {
		List<EXCTimeItem> wResult = new ArrayList<EXCTimeItem>();
		if (wString == null || wString.isEmpty())
			return wResult;

		String[] wStringList = wString.split("\\+\\|;\\|\\+");

		if (wStringList == null || wStringList.length < 1)
			return wResult;

		for (String wStringItem : wStringList) {
			EXCTimeItem wEXCTimeItem = new EXCTimeItem(wStringItem);
			wResult.add(wEXCTimeItem);
		}
		return wResult;
	}

}
