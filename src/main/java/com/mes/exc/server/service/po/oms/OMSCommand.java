package com.mes.exc.server.service.po.oms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OMSCommand implements Serializable {
	private static final long serialVersionUID = 1L;

	public int ID = 0;

	public String No="";

	public int CustomerID = 0;

	public String CustomerName = "";

	public String CustomerCode = "";

	public String ContactCode = "";

	public int Status = 0;

	public String StatusText = "";

	public int LinkManID = 0;

	public int EditorID = 0;

	public String Editor = "";

	public Calendar EditTime = Calendar.getInstance();

	public int Active = 0;

	public String LinkMan = "";

	public String LinkPhone = "";

	public List<OMSOrder> OrderList = new ArrayList<>();

	public int ErrorCode = 0;

	public int CreatorID = 0;

	public String Creator = "";

	public Calendar CreateTime = Calendar.getInstance();

	public int AuditorID = 0;

	public String Auditor = "";

	public Calendar AuditTime = Calendar.getInstance();

	public int FactoryID = 0;

	public int BusinessUnitID = 0;

	public String Factory = "";

	public String BusinessUnit = "";

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getNo() {
		return No;
	}

	public void setNo(String no) {
		No = no;
	}

	public int getCustomerID() {
		return CustomerID;
	}

	public void setCustomerID(int customerID) {
		CustomerID = customerID;
	}

	public String getCustomerName() {
		return CustomerName;
	}

	public void setCustomerName(String customerName) {
		CustomerName = customerName;
	}

	public String getCustomerCode() {
		return CustomerCode;
	}

	public void setCustomerCode(String customerCode) {
		CustomerCode = customerCode;
	}

	public String getContactCode() {
		return ContactCode;
	}

	public void setContactCode(String contactCode) {
		ContactCode = contactCode;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public String getStatusText() {
		return StatusText;
	}

	public void setStatusText(String statusText) {
		StatusText = statusText;
	}

	public int getLinkManID() {
		return LinkManID;
	}

	public void setLinkManID(int linkManID) {
		LinkManID = linkManID;
	}

	public int getEditorID() {
		return EditorID;
	}

	public void setEditorID(int editorID) {
		EditorID = editorID;
	}

	public String getEditor() {
		return Editor;
	}

	public void setEditor(String editor) {
		Editor = editor;
	}

	public Calendar getEditTime() {
		return EditTime;
	}

	public void setEditTime(Calendar editTime) {
		EditTime = editTime;
	}

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

	public String getLinkMan() {
		return LinkMan;
	}

	public void setLinkMan(String linkMan) {
		LinkMan = linkMan;
	}

	public String getLinkPhone() {
		return LinkPhone;
	}

	public void setLinkPhone(String linkPhone) {
		LinkPhone = linkPhone;
	}

	public List<OMSOrder> getOrderList() {
		return OrderList;
	}

	public void setOrderList(List<OMSOrder> orderList) {
		OrderList = orderList;
	}

	public int getErrorCode() {
		return ErrorCode;
	}

	public void setErrorCode(int errorCode) {
		ErrorCode = errorCode;
	}

	public int getCreatorID() {
		return CreatorID;
	}

	public void setCreatorID(int creatorID) {
		CreatorID = creatorID;
	}

	public String getCreator() {
		return Creator;
	}

	public void setCreator(String creator) {
		Creator = creator;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public int getAuditorID() {
		return AuditorID;
	}

	public void setAuditorID(int auditorID) {
		AuditorID = auditorID;
	}

	public String getAuditor() {
		return Auditor;
	}

	public void setAuditor(String auditor) {
		Auditor = auditor;
	}

	public Calendar getAuditTime() {
		return AuditTime;
	}

	public void setAuditTime(Calendar auditTime) {
		AuditTime = auditTime;
	}

	public int getFactoryID() {
		return FactoryID;
	}

	public void setFactoryID(int factoryID) {
		FactoryID = factoryID;
	}

	public int getBusinessUnitID() {
		return BusinessUnitID;
	}

	public void setBusinessUnitID(int businessUnitID) {
		BusinessUnitID = businessUnitID;
	}

	public String getFactory() {
		return Factory;
	}

	public void setFactory(String factory) {
		Factory = factory;
	}

	public String getBusinessUnit() {
		return BusinessUnit;
	}

	public void setBusinessUnit(String businessUnit) {
		BusinessUnit = businessUnit;
	}

	public OMSCommand() {
		this.No = "";
		this.CustomerName = "";
		this.CustomerCode = "";
		this.ContactCode = "";
		this.StatusText = "";
		this.Editor = "";
		this.LinkMan = "";
		this.LinkPhone = "";
		this.Creator = "";
		this.Auditor = "";
		this.Factory = "";
		this.BusinessUnit = "";
		this.OrderList = new ArrayList<>();
	}
}
