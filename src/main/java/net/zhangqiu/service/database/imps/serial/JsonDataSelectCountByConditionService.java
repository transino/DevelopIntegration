package net.zhangqiu.service.database.imps.serial;

import java.util.Map;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.service.database.dao.SelectByConditionDao;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.param.EntityDataParam;
import net.zhangqiu.service.entity.result.CommonServiceResult;
import net.zhangqiu.utils.StrUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JsonDataSelectCountByConditionService extends EntityDataTemplateService {
	@Autowired
	EnvironmentContext environmentContext;

	@Autowired
    EntityContext entityContext;
	
	@Autowired
    SelectByConditionDao selectByConditionDao;

	@Override
	public CommonServiceResult logicService(String strProjectCode, String datasourceName, EntityDataParam entityData) throws Exception {
		CommonServiceResult commonServiceResult = new CommonServiceResult();
		String sql = "SELECT COUNT(*) FROM " + entityData.getTableName();
		if(!StrUtils.isEmpty(entityData.getStrCondition())){
			sql += " WHERE " + entityData.getStrCondition();
		}

		Map<String,Object> map = entityData.getData();

		int objectsSize = 0;
		if(entityData.getData() != null){
			objectsSize = entityData.getData().size();
		}
		
		Object[] objects = new Object[objectsSize];
		
		if(map != null){
			for(Map.Entry<String, Object> entry : map.entrySet()){
				int indexOf = entry.getKey().indexOf("_");
				int index = Integer.valueOf(entry.getKey().substring(0, indexOf));
				objects[index] = entityContext.convertToType(entry.getValue(),entry.getKey().substring(indexOf+1));
			}
		}

		int count = selectByConditionDao.queryForInt(strProjectCode,datasourceName,entityData.getTableName(), sql,objects);
		commonServiceResult.setData(count);
		commonServiceResult.setSuccess(true);
		commonServiceResult.setMessage("success");
		return commonServiceResult;
	}
}
