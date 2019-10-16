package net.zhangqiu.service.database.imps.serial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.service.database.dao.UpdateSqlDao;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.param.EntityDataParam;
import net.zhangqiu.service.database.entity.result.SaveOrUpdateResult;
import net.zhangqiu.utils.StrUtils;

@Service
public class JsonDataUpdateByIdService extends JsonDataSaveOrUpdateTemplateService{

	@Autowired
	EnvironmentContext environmentContext;
	@Autowired
    UpdateSqlDao updateSqlDao;
	@Autowired
    EntityContext entityContext;

	@Override
	public SaveOrUpdateResult logicService(String strProjectCode,String datasourceName,EntityDataParam entityData) throws Exception {

		SaveOrUpdateResult saveOrUpdateResult = super.getCheckResult(strProjectCode,datasourceName, entityData);
		if(!saveOrUpdateResult.isSuccess()){
			return saveOrUpdateResult;
		}
		String sql = entityContext.getEntityTableUpdateByIdSql(strProjectCode,datasourceName,entityData);
		if(StrUtils.isEmpty(sql)){
			String key = entityContext.getEntityTableKey(strProjectCode,datasourceName, entityData.getTableName());
			saveOrUpdateResult.setSuccess(false);
			saveOrUpdateResult.setMessage("表名Key（"+key+"）不存在，请确认是否在*_table.json文件中定义，区分大小写");
			return saveOrUpdateResult;
		}
		Object[] objects = entityContext.getEntityTableUpdateValue(strProjectCode,datasourceName, entityData);
		updateSqlDao.updateSql(strProjectCode,datasourceName, sql, objects);
		saveOrUpdateResult.setSuccess(true);
		saveOrUpdateResult.setMessage("success");
        entityContext.addOrUpdateCachedata(strProjectCode,datasourceName,entityData);
		return saveOrUpdateResult;
	}

}
