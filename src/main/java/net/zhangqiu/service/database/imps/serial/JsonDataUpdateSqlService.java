package net.zhangqiu.service.database.imps.serial;

import java.util.Map;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.service.database.dao.UpdateSqlDao;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.param.EntityDataParam;
import net.zhangqiu.service.entity.result.CommonServiceResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JsonDataUpdateSqlService extends EntityDataTemplateService{
	
	@Autowired
	EnvironmentContext environmentContext;
	@Autowired
	EntityContext entityContext;
	@Autowired
	UpdateSqlDao updateSqlDao;

	@Override
	public Object logicService(String strProjectCode,String datasourceName, EntityDataParam entityData)throws Exception {
		CommonServiceResult commonServiceResult = new CommonServiceResult();
		String sql = entityData.getStrCondition();
		Map<String,Object> map = entityData.getData();
		int count = 0;
		if(map != null && map.size() > 0){
			int objectsSize = entityData.getData().size();
			Object[] objects = new Object[objectsSize];
			for(Map.Entry<String, Object> entry : map.entrySet()){
				int indexOf = entry.getKey().indexOf("_");
				int index = Integer.valueOf(entry.getKey().substring(0, indexOf));
				objects[index] = entityContext.convertToType(entry.getValue(),entry.getKey().substring(indexOf+1));
			}
            count = updateSqlDao.updateSql(strProjectCode,datasourceName, sql, objects);
		}
		else{
            count = updateSqlDao.updateSql(strProjectCode,datasourceName, sql);
		}
		commonServiceResult.setSuccess(true);
		commonServiceResult.setMessage("success");
        commonServiceResult.setData(count);
		return commonServiceResult;
	}
}
