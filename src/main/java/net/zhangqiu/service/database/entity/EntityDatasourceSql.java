package net.zhangqiu.service.database.entity;

public class EntityDatasourceSql {
	private String dataSourceName;
	private String initSqlList[];
	private String updateSqlList[];

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	public String getDataSourceName() {
		return dataSourceName;
	}
	public void setInitSqlList(String initSqlList[]) {
		this.initSqlList = initSqlList;
	}
	public String[] getInitSqlList() {
		return initSqlList;
	}
	public void setUpdateSqlList(String updateSqlList[]) {
		this.updateSqlList = updateSqlList;
	}
	public String[] getUpdateSqlList() {
		return updateSqlList;
	}

}
