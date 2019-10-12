package net.zhangqiu.service.check.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CheckRuleTable {
	private String dataSourceName;
	private String tableName;
	private List<CheckRule> checkRuleList;
	private Map<String,String> columnDescriptionMap;
	private Map<String,String> columnTypeMap;
	private Map<String,Map<String,String>> columnValueMap;

    public Map<String, Map<String, String>> getColumnValueMap() {
        return columnValueMap;
    }

    public void setColumnValueMap(Map<String, Map<String, String>> columnValueMap) {
        this.columnValueMap = columnValueMap;
    }

    private Map<String, CheckRuleTable> checkRuleTableMap;

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getTableName() {
		return tableName;
	}
	public void setCheckRuleList(List<CheckRule> checkRuleList) {
		this.checkRuleList = checkRuleList;
	}
	public List<CheckRule> getCheckRuleList() {
		return checkRuleList;
	}
	
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	public String getDataSourceName() {
		return dataSourceName;
	}
	public void setColumnDescriptionMap(Map<String,String> columnDescriptionMap) {
		this.columnDescriptionMap = columnDescriptionMap;
	}
	public Map<String,String> getColumnDescriptionMap() {
		return columnDescriptionMap;
	}
	public void setColumnTypeMap(Map<String,String> columnTypeMap) {
		this.columnTypeMap = columnTypeMap;
	}
	public Map<String,String> getColumnTypeMap() {
		return columnTypeMap;
	}
	public void setCheckRuleTableMap(Map<String, CheckRuleTable> checkRuleTableMap) {
		this.checkRuleTableMap = checkRuleTableMap;
	}
	public Map<String, CheckRuleTable> getCheckRuleTableMap() {
		return checkRuleTableMap;
	}
	public CheckRuleTable(){
		checkRuleList = new ArrayList<CheckRule>();
		columnDescriptionMap = new HashMap<String,String>();
		columnTypeMap = new HashMap<String,String>();
		checkRuleTableMap = new LinkedHashMap<String, CheckRuleTable>();
        columnValueMap = new HashMap<String, Map<String, String>>();
	}
}
