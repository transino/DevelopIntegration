package net.zhangqiu.service.database.entity.param;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntityDataComponent {
	private String operation;
	private String tableName;
	private List<Map<String,Object>> objectMapList;
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public List<Map<String, Object>> getObjectMapList() {
		return objectMapList;
	}
	public void setObjectMapList(List<Map<String, Object>> objectMapList) {
		this.objectMapList = objectMapList;
	}
	
	public EntityDataComponent(){
		objectMapList = new ArrayList<Map<String,Object>>();
	}
}
