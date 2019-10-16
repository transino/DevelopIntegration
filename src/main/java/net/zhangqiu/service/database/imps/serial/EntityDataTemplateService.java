package net.zhangqiu.service.database.imps.serial;

import java.net.URLDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.EntityUtils;
import net.zhangqiu.service.database.entity.param.EntityDataParam;
import net.zhangqiu.service.database.interfaces.JsonDatasourceHandler;
import net.zhangqiu.service.entity.result.CommonServiceResult;

import net.zhangqiu.utils.JsonUtils;
import net.zhangqiu.utils.StrUtils;

public abstract class EntityDataTemplateService extends EntityTransactionTemplateService implements JsonDatasourceHandler{

	@Autowired
	EnvironmentContext environmentContext;
	@Autowired
    EntityContext entityContext;

    private static Logger logger = LoggerFactory.getLogger(EntityDataTemplateService.class);
	
	public Object logicService(String strPorjectCode,String datasourceName, Object entityData) throws Exception{
		return logicService(strPorjectCode,datasourceName,(EntityDataParam)entityData);
	}

	public abstract Object logicService(String strPorjectCode,String datasourceName, EntityDataParam entityData) throws Exception;

	public String handleService(String indentification,String strPorjectCode,String datasourceName,String jsonData,String encoding) throws Exception {
        datasourceName = entityContext.getCurrentDatasourceName(strPorjectCode,datasourceName);
        if(StrUtils.isEmpty(encoding)){
            encoding = environmentContext.getEncoding();
        }
        jsonData = URLDecoder.decode(jsonData, encoding);
        EntityDataParam entityData = EntityUtils.convertToEntityData(jsonData);
        if(StrUtils.isEmpty(entityData.getCheckTableName())){
            entityData.setCheckTableName(entityData.getTableName());
        }
	    try{
			return JsonUtils.objectToString(transactionService(strPorjectCode,datasourceName,entityData));
		}
		catch(Exception ex){
            logger.error(entityData.getTableName()+"，处理异常，请联系任务责任人",ex);
			CommonServiceResult commonServiceResult = new CommonServiceResult();
			commonServiceResult.setSuccess(false);
			commonServiceResult.setMessage(ex.getMessage());
			return JsonUtils.objectToString(commonServiceResult);
		}
	}
}
