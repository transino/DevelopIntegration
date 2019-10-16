package net.zhangqiu.service.database.entity;

import java.util.*;

public class EntityTable {
	private String dataSourceName;
	private String tableName;
	private boolean autoGenerateId;
	private Set<String> idColumnSet;
	private Set<String> logicIdColumnSet;
	private List<EntityColumn> entityColumnList;
    private Map<String, EntityColumn> entityColumnMap;
    private List<String> keyNameColumnList;
    private String cacheRule;
    public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getTableName() {
		return tableName;
	}
	public void setIdColumnSet(Set<String> idColumnSet) {
		this.idColumnSet = idColumnSet;
	}
	public Set<String> getIdColumnSet() {
		return idColumnSet;
	}
	public void setAutoGenerateId(boolean autoGenerateId) {
		this.autoGenerateId = autoGenerateId;
	}
	public boolean isAutoGenerateId() {
		return autoGenerateId;
	}
	public void setLogicIdColumnSet(Set<String> logicIdColumnSet) {
		this.logicIdColumnSet = logicIdColumnSet;
	}
	public Set<String> getLogicIdColumnSet() {
		return logicIdColumnSet;
	}
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	public String getDataSourceName() {
		return dataSourceName;
	}
	public void setEntityColumnList(List<EntityColumn> entityColumnList) {
		this.entityColumnList = entityColumnList;
	}
	public List<EntityColumn> getEntityColumnList() {
		return entityColumnList;
	}
    public Map<String, EntityColumn> getEntityColumnMap() {
        return entityColumnMap;
    }

    public void setEntityColumnMap(Map<String, EntityColumn> entityColumnMap) {
        this.entityColumnMap = entityColumnMap;
    }

    public List<String> getKeyNameColumnList() {
        return keyNameColumnList;
    }

    public void setKeyNameColumnList(List<String> keyNameColumnList) {
        this.keyNameColumnList = keyNameColumnList;
    }

    public String getCacheRule() {
        return cacheRule;
    }

    public void setCacheRule(String cacheRule) {
        this.cacheRule = cacheRule;
    }

	public EntityTable(){
		idColumnSet = new LinkedHashSet<String>();
		logicIdColumnSet = new LinkedHashSet<String>();
        keyNameColumnList = new ArrayList<String>();
		entityColumnList = new ArrayList<EntityColumn>();
        entityColumnMap = new HashMap<String, EntityColumn>();
	}
	
}
