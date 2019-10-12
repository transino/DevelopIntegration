package net.zhangqiu.service.database.entity.param;

import java.util.Map;

public class EntityDatabaseCheckUpdateParam {
	private String datasourceNameKey;
	private String tableNameKey;
	private String sql;
	private String updateJson;
    private Map<String,Object> data;
    private Object[] args;
    private int cacheLine;
    private int processorCount;

	public void setDatasourceNameKey(String datasourceNameKey) {
		this.datasourceNameKey = datasourceNameKey;
	}
	public String getDatasourceNameKey() {
		return datasourceNameKey;
	}
	public void setTableNameKey(String tableNameKey) {
		this.tableNameKey = tableNameKey;
	}
	public String getTableNameKey() {
		return tableNameKey;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public void setUpdateJson(String updateJson) {
		this.updateJson = updateJson;
	}
	public String getUpdateJson() {
		return updateJson;
	}
	public int getCacheLine() {
		return cacheLine;
	}
	public void setCacheLine(int cacheLine) {
		this.cacheLine = cacheLine;
	}
	public int getProcessorCount() {
		return processorCount;
	}
	public void setProcessorCount(int processorCount) {
		this.processorCount = processorCount;
	}

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
