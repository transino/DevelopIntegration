package net.zhangqiu.service.database.entity;

import java.util.List;

public class EntityDatasourceSqlFile {
	private List<EntityDatasourceSql> entityDatasourceSqlList;

	public void setEntityDatasourceSqlList(List<EntityDatasourceSql> entityDatasourceSqlList) {
		this.entityDatasourceSqlList = entityDatasourceSqlList;
	}

	public List<EntityDatasourceSql> getEntityDatasourceSqlList() {
		return entityDatasourceSqlList;
	}
}
