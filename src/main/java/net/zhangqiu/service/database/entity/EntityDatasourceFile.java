package net.zhangqiu.service.database.entity;

import java.util.ArrayList;
import java.util.List;

public class EntityDatasourceFile {
	private List<EntityDatasource> entityDatasourceList;
	public void setEntityDatasourceList(List<EntityDatasource> entityDatasourceList) {
		this.entityDatasourceList = entityDatasourceList;
	}
	public List<EntityDatasource> getEntityDatasourceList() {
		return entityDatasourceList;
	}
	public EntityDatasourceFile(){
		entityDatasourceList = new ArrayList<EntityDatasource>();
	}
}
