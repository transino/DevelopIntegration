package net.zhangqiu.service.database.imps.serial;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.service.database.dao.BatchUpdateSqlDao;
import net.zhangqiu.service.database.entity.EntityTable;
import net.zhangqiu.service.database.entity.EntityUtils;
import net.zhangqiu.service.database.entity.SnowFlakeGenerator;
import net.zhangqiu.service.database.entity.param.EntityDataComponent;
import net.zhangqiu.service.database.entity.param.EntityDataComponentParam;
import net.zhangqiu.service.database.entity.param.EntityDataParam;
import net.zhangqiu.service.database.interfaces.JsonDatasourceHandler;
import net.zhangqiu.service.entity.result.CommonServiceResult;
import net.zhangqiu.utils.JsonUtils;
import net.zhangqiu.utils.StrUtils;

@Service
public class JsonDataComponentService extends EntityTransactionTemplateService implements JsonDatasourceHandler{

	@Autowired
	EnvironmentContext environmentContext;
	
	@Autowired
    BatchUpdateSqlDao batchUpdateSqlDao;
	
	@Autowired
    SnowFlakeGenerator snowFlakeGenerator;

    private static Logger logger = LoggerFactory.getLogger(JsonDataComponentService.class);
	
	@Override
	public Object logicService(String strProjectCode,String datasourceName, Object objectParam) throws Exception {
		CommonServiceResult commonServiceResult = new CommonServiceResult();
		
		EntityDataComponentParam entityDataComponentParam = (EntityDataComponentParam)objectParam;
		for(EntityDataComponent entityDataComponent : entityDataComponentParam.getEntityDataComponentList()){
			if(entityDataComponent.getOperation().toUpperCase().equals("INSERT")){
				String key = entityContext.getEntityTableKey(strProjectCode,datasourceName, entityDataComponent.getTableName());
				EntityTable entityTable  = entityContext.getEntityTableMap().get(key);
				if(entityTable == null){
					throw new Exception("表名Key（"+key+"）不存在，请确认是否在*_table.json文件中定义，区分大小写");
				}
				EntityDataParam entityDataParam = new EntityDataParam();
				entityDataParam.setTableName(entityDataComponent.getTableName());
				String sql = entityContext.getEntityTableInsertSql(strProjectCode,datasourceName, entityDataParam);
				
				if(entityTable.isAutoGenerateId()){
					for(Map<String,Object> mapData : entityDataComponent.getObjectMapList()){
						for(String string : entityTable.getIdColumnSet()){
							mapData.put(string, snowFlakeGenerator.nextId());
						}
					}
				}
				List<Object[]> objectList = new ArrayList<Object[]>();
				for(Map<String,Object> mapData : entityDataComponent.getObjectMapList()){
					entityDataParam.setData(mapData);
					entityDataParam.setData(entityContext.getConvertToStandardMap(strProjectCode,datasourceName, entityDataParam));
					Object[] objects = entityContext.getEntityTableInsertValue(strProjectCode,datasourceName, entityDataParam);
					objectList.add(objects);
				}
				batchUpdateSqlDao.batchUpdateSql(strProjectCode,datasourceName, sql, objectList);
			}
			else if(entityDataComponent.getOperation().toUpperCase().equals("UPDATE")){
				EntityDataParam entityDataParam = new EntityDataParam();
				entityDataParam.setTableName(entityDataComponent.getTableName());
				String sql = entityContext.getEntityTableUpdateByIdSql(strProjectCode,datasourceName, entityDataParam);
				List<Object[]> objectList = new ArrayList<Object[]>();
				for(Map<String,Object> mapData : entityDataComponent.getObjectMapList()){
					entityDataParam.setData(mapData);
					entityDataParam.setData(entityContext.getConvertToStandardMap(strProjectCode,datasourceName, entityDataParam));
					Object[] objects = entityContext.getEntityTableUpdateValue(strProjectCode,datasourceName, entityDataParam);
					objectList.add(objects);
				}
				batchUpdateSqlDao.batchUpdateSql(strProjectCode,datasourceName, sql, objectList);
			}
			if(entityDataComponent.getOperation().toUpperCase().equals("DELETE")){
				EntityDataParam entityDataParam = new EntityDataParam();
				entityDataParam.setTableName(entityDataComponent.getTableName());
				String sql = entityContext.getEntityTableDeleteByIdSql(strProjectCode,datasourceName, entityDataParam);
				List<Object[]> objectList = new ArrayList<Object[]>();
				for(Map<String,Object> mapData : entityDataComponent.getObjectMapList()){
					entityDataParam.setData(mapData);
					Object[] objects = entityContext.getEntityTableIdValue(strProjectCode,datasourceName, entityDataParam);
					objectList.add(objects);
				}
				batchUpdateSqlDao.batchUpdateSql(strProjectCode,datasourceName, sql, objectList);
			}
		}
		commonServiceResult.setMessage("success");
		commonServiceResult.setSuccess(true);
		return commonServiceResult;
	}

	public String handleService(String indentification,String strProjectCode, String datasourceName,String jsonData, String encoding) throws Exception {
		try{
			datasourceName = entityContext.getCurrentDatasourceName(strProjectCode,datasourceName);
			if(StrUtils.isEmpty(encoding)){
				encoding = environmentContext.getEncoding();
			}
			jsonData = URLDecoder.decode(jsonData, encoding);
			EntityDataComponentParam entityDataComponentParam = EntityUtils.convertToEntityDataComponent(jsonData);
			return JsonUtils.objectToString(transactionService(strProjectCode,datasourceName,entityDataComponentParam));
		}
		catch(Exception ex){
            logger.error("JsonDataComponentService服务处理异常，请联系任务责任人",ex);
			CommonServiceResult commonServiceResult = new CommonServiceResult();
			commonServiceResult.setSuccess(false);
			commonServiceResult.setMessage(ex.getMessage());
			return JsonUtils.objectToString(commonServiceResult);
		}
	}
}
