package net.zhangqiu.service.check.entity.result;

import java.util.ArrayList;
import java.util.List;

import net.zhangqiu.service.check.entity.CheckRule;


public class CheckRuleResult {
	private boolean check;
	private boolean forceCheck;
	private boolean exception;
	private String exceptionDetail;
	private String ruleExpression;
	private String descriptionExpression;
	private String ifDescription;
	private List<String> descriptionList;

	private CheckRule checkRule;

	public CheckRuleResult(){
		check = true;
		forceCheck = true;
		exceptionDetail = "";
		ruleExpression = "";
		descriptionExpression = "";
		descriptionList = new ArrayList<String>();
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public boolean isForceCheck() {
		return forceCheck;
	}

	public void setForceCheck(boolean forceCheck) {
		this.forceCheck = forceCheck;
	}

	public boolean isException() {
		return exception;
	}

	public void setException(boolean exception) {
		this.exception = exception;
	}

	public String getExceptionDetail() {
		return exceptionDetail;
	}

	public void setExceptionDetail(String exceptionDetail) {
		this.exceptionDetail = exceptionDetail;
	}

	public String getDescriptionExpression() {
		return descriptionExpression;
	}

	public List<String> getDescriptionList() {
		return descriptionList;
	}

	public String getRuleExpression() {
		return ruleExpression;
	}

	public void setRuleExpression(String ruleExpression) {
		this.ruleExpression = ruleExpression;
	}

	public CheckRule getCheckRule() {
		return checkRule;
	}

	public void setCheckRule(CheckRule checkRule) {
		this.checkRule = checkRule;
	}

	public void setIfDescription(String ifDescription) {
		this.ifDescription = ifDescription;
	}

	public String getIfDescription() {
		return ifDescription;
	}

	public void setDescriptionExpression(String descriptionExpression) {
		this.descriptionExpression = descriptionExpression;
	}

	public void setDescriptionList(List<String> descriptionList) {
		this.descriptionList = descriptionList;
	}

	
}
