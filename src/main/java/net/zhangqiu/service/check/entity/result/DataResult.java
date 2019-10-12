package net.zhangqiu.service.check.entity.result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataResult {
	private int index;
	private boolean check;
	private boolean forceCheck;
	private boolean exception;
	
	private Map<String,String> data;
	
	private List<CheckRuleResult> lineResultList;
	private Map<String,ColumnResult> columnResultMap;
	private Map<String,List<DataResult>> dataResultListMap;

	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
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
	public void setException(boolean exception) {
		this.exception = exception;
	}
	public boolean isException() {
		return exception;
	}
	public void setData(Map<String,String> data) {
		this.data = data;
	}
	public Map<String,String> getData() {
		return data;
	}
	public void setLineResultList(List<CheckRuleResult> lineResultList) {
		this.lineResultList = lineResultList;
	}
	public List<CheckRuleResult> getLineResultList() {
		return lineResultList;
	}
	public void setColumnResultMap(Map<String,ColumnResult> columnResultMap) {
		this.columnResultMap = columnResultMap;
	}
	public Map<String,ColumnResult> getColumnResultMap() {
		return columnResultMap;
	}
	public void setDataResultListMap(Map<String,List<DataResult>> dataResultListMap) {
		this.dataResultListMap = dataResultListMap;
	}
	public Map<String,List<DataResult>> getDataResultListMap() {
		return dataResultListMap;
	}

	public DataResult(){
		check = true;
		forceCheck = true;
		data = new HashMap<String,String>();
		lineResultList = new ArrayList<CheckRuleResult>();
		columnResultMap = new HashMap<String,ColumnResult>();
		dataResultListMap = new LinkedHashMap<String,List<DataResult>>();
	}
}
