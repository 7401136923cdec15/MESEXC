package com.mes.exc.server.service.po.oms;

import java.util.Calendar;

public class OMSMonthOrder {

	public OMSMonthOrder() {
		// TODO Auto-generated constructor stub
	}

	public int WorkShopID = 0;
	
	public int LineID = 0;

	public int ProductID = 0;

	public int CustomerID = 0;

	public int FQTY = 0;

	public Calendar StartTime = Calendar.getInstance();

	public String Condition = "";

	public String Remark = "";
	
	public int ShiftID = 0;
	
	public Calendar CreateTime = Calendar.getInstance(); // 创建时间

	public Calendar AuditTime = Calendar.getInstance(); // 审核时间
	
}
