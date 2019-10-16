package net.zhangqiu.service.database.entity;

public class EntityDatasource {
	private boolean defaultDatasource;
	private String dataSourceName;
	private String url;
	private String userName;
	private String password;
	private String driverClassName;
	private String strDatabaseType;
	private String[] initSqlList;
	public String getDataSourceName() {
		return dataSourceName;
	}
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDriverClassName() {
		return driverClassName;
	}
	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}
	public void setDefaultDatasource(boolean defaultDatasource) {
		this.defaultDatasource = defaultDatasource;
	}
	public boolean isDefaultDatasource() {
		return defaultDatasource;
	}
	public void setStrDatabaseType(String strDatabaseType) {
		this.strDatabaseType = strDatabaseType;
	}
	public String getStrDatabaseType() {
		return strDatabaseType;
	}
	public void setInitSqlList(String[] initSqlList) {
		this.initSqlList = initSqlList;
	}
	public String[] getInitSqlList() {
		return initSqlList;
	}
}
