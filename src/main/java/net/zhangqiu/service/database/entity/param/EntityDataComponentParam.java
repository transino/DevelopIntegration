package net.zhangqiu.service.database.entity.param;

import java.util.ArrayList;
import java.util.List;

public class EntityDataComponentParam {
	private List<EntityDataComponent> entityDataComponentList;

	public void setEntityDataComponentList(List<EntityDataComponent> entityDataComponentList) {
		this.entityDataComponentList = entityDataComponentList;
	}

	public List<EntityDataComponent> getEntityDataComponentList() {
		return entityDataComponentList;
	}
	
	public EntityDataComponentParam(){
		entityDataComponentList = new ArrayList<EntityDataComponent>();
	}
}
