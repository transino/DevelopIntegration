package net.zhangqiu.service.database.imps.serial;

import java.net.URLDecoder;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.service.check.entity.param.DataResultParam;
import net.zhangqiu.service.check.entity.result.DataResult;
import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.interfaces.CheckExpression;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.EntityUtils;
import net.zhangqiu.service.database.entity.param.EntityDataParam;
import net.zhangqiu.service.database.interfaces.CheckHandler;
import net.zhangqiu.service.database.interfaces.JsonDatasourceHandler;
import net.zhangqiu.service.entity.result.CommonServiceResult;
import net.zhangqiu.utils.JsonUtils;
import net.zhangqiu.utils.StrUtils;

@Service
public class JsonDataCheckService implements JsonDatasourceHandler,CheckHandler{
	
	@Autowired
	EnvironmentContext environmentContext;
	@Autowired
	EntityContext entityContext;
	@Autowired
	CheckExpression checkExpression;

    private static Logger logger = LoggerFactory.getLogger(JsonDataCheckService.class);


	public String handleService(String indentification, String strProjectCode,String datasourceName,String jsonString, String encoding) throws Exception {
        datasourceName = entityContext.getCurrentDatasourceName(strProjectCode,datasourceName);
        if(StrUtils.isEmpty(encoding)){
            encoding = environmentContext.getEncoding();
        }
        jsonString = URLDecoder.decode(jsonString, encoding);
        EntityDataParam entityData = EntityUtils.convertToEntityData(jsonString);
        if(StrUtils.isEmpty(entityData.getCheckTableName())){
            entityData.setCheckTableName(entityData.getTableName());
        }
        entityData.setData(entityContext.getConvertToStandardMap(strProjectCode,datasourceName, entityData));
        String entityTableKey = entityContext.getEntityTableKey(strProjectCode,datasourceName, entityData.getCheckTableName());
        CheckRuleTable checkRuleTable  = entityContext.getCheckRuleTableMap().get(entityTableKey);
	    try{
			if(checkRuleTable == null){
				CommonServiceResult commonServiceResult = new CommonServiceResult();
				commonServiceResult.setSuccess(false);
				commonServiceResult.setMessage("表名Key（"+entityTableKey+"）不存在，请确认是否在*_tablecheck.json文件中定义，区分大小写");
				return JsonUtils.objectToString(commonServiceResult);
			}
			return JsonUtils.objectToString(check(checkRuleTable,entityData.getData()));
		}
		catch(Exception ex){
            logger.error(entityTableKey+"，校验异常，请联系任务责任人",ex);
			CommonServiceResult commonServiceResult = new CommonServiceResult();
			commonServiceResult.setSuccess(false);
			commonServiceResult.setMessage(ex.getMessage());
			return JsonUtils.objectToString(commonServiceResult);
		}
	}

	public DataResult check(CheckRuleTable checkRuleTable,Map<String, Object> dataMap) {
		DataResultParam dataResultParam = new DataResultParam();
		//dataResultParam.setColumnResultOnlyFalse(false);
		return checkExpression.check(checkRuleTable, dataMap, dataResultParam);
	}

}
