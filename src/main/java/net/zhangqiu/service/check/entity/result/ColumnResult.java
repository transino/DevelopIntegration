package net.zhangqiu.service.check.entity.result;

import java.util.ArrayList;
import java.util.List;

public class ColumnResult {
	
	private String columnDescription;
	private String value;
	private boolean check;
	private boolean forceCheck;
	private boolean exception;
	private String description;
	private List<CheckRuleResult> checkRuleResult;
	
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<CheckRuleResult> getCheckRuleResult() {
		return checkRuleResult;
	}
	public void setCheckRuleResult(List<CheckRuleResult> checkRuleResult) {
		this.checkRuleResult = checkRuleResult;
	}
	
	public void setColumnDescription(String columnDescription) {
		this.columnDescription = columnDescription;
	}
	public String getColumnDescription() {
		return columnDescription;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	public ColumnResult(){
		check = true;
		forceCheck = true;
		checkRuleResult = new ArrayList<CheckRuleResult>();
		description = "";
		value = "";
	}
}
