package net.zhangqiu.service.database.imps.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.param.EntityDatabaseCheckTextParam;
import net.zhangqiu.service.entity.result.CommonServiceResult;
import net.zhangqiu.utils.JsonUtils;

public abstract class EntityDatabaseTextTemplateService{

	@Autowired
	EnvironmentContext environmentContext;
	@Autowired
	EntityContext entityContext;

    private static Logger logger = LoggerFactory.getLogger(DatabaseCheckService.class);

	public abstract Object logicService(EntityDatabaseCheckTextParam t) throws Exception;

	public String handleService(String indentification,EntityDatabaseCheckTextParam entityDatabaseCheckTextParam) throws Exception {
		try{
			return JsonUtils.objectToString(logicService(entityDatabaseCheckTextParam));
		}
		catch(Exception ex){
            logger.error("",ex);
			CommonServiceResult commonServiceResult = new CommonServiceResult();
			commonServiceResult.setSuccess(false);
			commonServiceResult.setMessage(ex.getMessage());
			return JsonUtils.objectToString(commonServiceResult);
		}
	}
}
