package net.zhangqiu.service.database.entity.param;

import java.util.Map;

public class EntityDataParam {

	private String tableName;
	private String checkTableName;
	private Map<String,Object> data;
	private String strCondition;
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	public void setStrCondition(String strCondition) {
		this.strCondition = strCondition;
	}
	public String getStrCondition() {
		return strCondition;
	}
	public void setCheckTableName(String checkTableName) {
		this.checkTableName = checkTableName;
	}
	public String getCheckTableName() {
		return checkTableName;
	}
}
