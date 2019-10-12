package net.zhangqiu.service.database.entity;

import java.util.ArrayList;
import java.util.List;

public class EntityTableFile {
	private List<EntityTable> entityTableList;
	public void setEntityTableList(List<EntityTable> entityTableList) {
		this.entityTableList = entityTableList;
	}
	public List<EntityTable> getEntityTableList() {
		return entityTableList;
	}
	
	public EntityTableFile(){
		entityTableList = new ArrayList<EntityTable>();
	}
}
