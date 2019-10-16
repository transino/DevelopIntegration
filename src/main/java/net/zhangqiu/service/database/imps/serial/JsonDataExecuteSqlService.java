package net.zhangqiu.service.database.imps.serial;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.service.database.dao.ExecuteSqlDao;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.param.EntityDataParam;
import net.zhangqiu.service.entity.result.CommonServiceResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JsonDataExecuteSqlService extends EntityDataTemplateService {
	@Autowired
	EnvironmentContext environmentContext;
	@Autowired
    EntityContext entityContext;
	@Autowired
    ExecuteSqlDao executeSqlDao;

	@Override
	public Object logicService(String strProjectCode,String datasourceName, EntityDataParam entityData)throws Exception {
		CommonServiceResult commonServiceResult = new CommonServiceResult();
		String sql = entityData.getStrCondition();
		executeSqlDao.executeSql(strProjectCode,datasourceName, sql);
		commonServiceResult.setSuccess(true);
		commonServiceResult.setMessage("success");
		return commonServiceResult;
	}
}
