package net.zhangqiu.service.database.imps.serial;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.service.database.dao.SelectByIdDao;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.param.EntityDataParam;
import net.zhangqiu.service.entity.result.CommonServiceResult;
import net.zhangqiu.utils.StrUtils;


@Service
public class JsonDataSelectByIdService extends EntityDataTemplateService {

	@Autowired
	EnvironmentContext environmentContext;
	@Autowired
    EntityContext entityContext;
	@Autowired
    SelectByIdDao selectByIdDao;

	@Override
	public CommonServiceResult logicService(String strProjectCode, String datasourceName, EntityDataParam entityData) throws Exception {
		CommonServiceResult commonServiceResult = new CommonServiceResult();
		entityData.setData(entityContext.getConvertToStandardMap(strProjectCode,datasourceName, entityData));
		String sql = entityContext.getEntityTableSelectByIdSql(strProjectCode,datasourceName,entityData);
		if(StrUtils.isEmpty(sql)){
			String key = entityContext.getEntityTableKey(strProjectCode,datasourceName, entityData.getTableName());
			commonServiceResult.setSuccess(false);
			commonServiceResult.setMessage("表名Key（"+key+"）不存在，请确认是否在*_table.json文件中定义，区分大小写");
			return commonServiceResult;
		}
		Object[] objects = entityContext.getEntityTableIdValue(strProjectCode,datasourceName, entityData);
		Object data = selectByIdDao.query(strProjectCode,datasourceName,entityData.getTableName(), sql, objects);
		commonServiceResult.setData(data);
		commonServiceResult.setSuccess(true);
		commonServiceResult.setMessage("success");
		return commonServiceResult;
	}

}
