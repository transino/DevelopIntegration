package net.zhangqiu.service.database.entity.param;

public class EntityDatabaseCheckTextParam {

	private String encoding;
	private String resource;
	private int cacheLine;
	private String regex;
	private String tableNameKey;
	private String sql;
	
	private String updateJson;
	private int processorCount;
	
	public EntityDatabaseCheckTextParam(){
		cacheLine = 1000;
		regex = ",";
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public int getCacheLine() {
		return cacheLine;
	}

	public void setCacheLine(int cacheLine) {
		this.cacheLine = cacheLine;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public void setTableNameKey(String tableNameKey) {
		this.tableNameKey = tableNameKey;
	}

	public String getTableNameKey() {
		return tableNameKey;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getSql() {
		return sql;
	}

	public void setUpdateJson(String updateJson) {
		this.updateJson = updateJson;
	}

	public String getUpdateJson() {
		return updateJson;
	}

	public void setProcessorCount(int processorCount) {
		this.processorCount = processorCount;
	}

	public int getProcessorCount() {
		return processorCount;
	}
}
