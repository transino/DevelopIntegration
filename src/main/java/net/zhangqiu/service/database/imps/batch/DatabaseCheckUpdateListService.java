package net.zhangqiu.service.database.imps.batch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.entity.param.DataListResultParam;
import net.zhangqiu.service.check.entity.param.DataResultParam;
import net.zhangqiu.service.check.entity.result.DataListResult;
import net.zhangqiu.service.check.entity.result.DataResult;
import net.zhangqiu.service.check.interfaces.CheckExpressionList;
import net.zhangqiu.service.database.dao.BatchUpdateSqlDao;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.EntityTable;
import net.zhangqiu.service.database.entity.param.EntityDatabaseCheckUpdateParam;
import net.zhangqiu.service.database.interfaces.ListMapHandler;
import net.zhangqiu.service.database.interfaces.QueryForListMapDataHandler;
import net.zhangqiu.service.entity.result.CommonServiceResult;
import net.zhangqiu.utils.JsonUtils;
import net.zhangqiu.utils.StrUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class DatabaseCheckUpdateListService {

    private static Logger logger = LoggerFactory.getLogger(DatabaseCheckUpdateListService.class);

	@Autowired
    EntityContext entityContext;
	@Autowired
	@Qualifier("listMapQueryCallbackHandlerDao")
	QueryForListMapDataHandler queryForListMapDataHandler;
	@Autowired
	@Qualifier("checkExpressionListService")
	CheckExpressionList checkExpressionList;
	@Autowired
	protected ApplicationContext applicationContext;
	@Autowired
    BatchUpdateSqlDao batchUpdateSqlDao;
	
	class DataHandler implements ListMapHandler {
		private String strProjectCode;
		private String datasourceName;
		private String updateByIdSql;
		private Map<String,Map<Boolean,String>> ruleMaps;
		private EntityTable entityTable;
		private CheckRuleTable checkRuleTable;
		private DataListResultParam dataListResultParam;
		private DataListResult dataListResult;
		public DataHandler(String strProjectCode, String datasourceName, String updateByIdSql, Map<String,Map<Boolean,String>> ruleMaps, EntityTable entityTable, CheckRuleTable checkRuleTable, DataListResultParam dataListResultParam, DataListResult dataListResult){
			this.strProjectCode = strProjectCode;
			this.datasourceName = datasourceName;
			this.updateByIdSql = updateByIdSql;
			this.ruleMaps = ruleMaps;
			this.entityTable = entityTable;
			this.checkRuleTable = checkRuleTable;
			this.dataListResultParam = dataListResultParam;
			this.dataListResult = dataListResult;
		}
		
		public void handleData(List<Map<String, Object>> dataMapList)throws Exception {
			if(checkRuleTable != null){
				checkExpressionList.check(dataListResult,checkRuleTable, dataMapList,dataListResultParam);
				
				List<Object[]> objectList = new ArrayList<Object[]>();
				for(int i=0;i<dataMapList.size();i++){
					DataResult dataResult = dataListResult.getDataResultList().get(i);
					
					Map<String, Object> dataMap = dataMapList.get(i);
					List<Object> objList = new ArrayList<Object>();
					for(Map.Entry<String,Map<Boolean,String>> entry : ruleMaps.entrySet()){
						if(entry.getKey().equals("dataResult")){
							objList.add(JsonUtils.objectToString(dataResult));
						}
						else if(entry.getKey().equals("dataResult.check")){
							objList.add(entry.getValue().get(dataResult.isCheck()));
						}
						if(entry.getKey().equals("dataResult.forceCheck")){
							objList.add(entry.getValue().get(dataResult.isForceCheck()));
						}
						if(entry.getKey().equals("dataResult.exception")){
							objList.add(entry.getValue().get(dataResult.isException()));
						}
					}
					for(String strIdColumn : entityTable.getIdColumnSet()){
						objList.add(dataMap.get(strIdColumn));
					}
					
					objectList.add(objList.toArray());
				}

				batchUpdateSqlDao.batchUpdateSql(strProjectCode,datasourceName,updateByIdSql, objectList);
				dataListResult.getDataResultList().clear();
			}
		}
	}
	
	public String handleService(String indentification, EntityDatabaseCheckUpdateParam entityDatabaseCheckUpdateParam) throws Exception{
		try{
			return JsonUtils.objectToString(logicService(entityDatabaseCheckUpdateParam));
		}
		catch(Exception ex){
            logger.error("",ex);
			CommonServiceResult commonServiceResult = new CommonServiceResult();
			commonServiceResult.setSuccess(false);
			commonServiceResult.setMessage(ex.getMessage());
			return JsonUtils.objectToString(commonServiceResult);
	    }
	}
	
	@SuppressWarnings("unchecked")
	public Object logicService(final EntityDatabaseCheckUpdateParam entityDatabaseCheckUpdateParam) throws Exception{
		if(StrUtils.isEmpty(entityDatabaseCheckUpdateParam.getTableNameKey())){
			throw new Exception("表不能为空");
		}
		if(StrUtils.isEmpty(entityDatabaseCheckUpdateParam.getSql())){
			throw new Exception("SQL不能为空");
		}
		if(StrUtils.isEmpty(entityDatabaseCheckUpdateParam.getUpdateJson())){
			throw new Exception("UpdateJson不能为空");
		}
		
		final DataListResultParam dataListResultParam = new DataListResultParam();
		dataListResultParam.setMaxDataResultBreak(false);
		dataListResultParam.setResultLevel(99);
		dataListResultParam.setProcessorCount(entityDatabaseCheckUpdateParam.getProcessorCount());
		DataResultParam dataResultParam = new DataResultParam();
		dataListResultParam.setDataResultParam(dataResultParam);

		final DataListResult dataListResult = new DataListResult();
		String key = entityDatabaseCheckUpdateParam.getTableNameKey();
		final CheckRuleTable checkRuleTable  = entityContext.getCheckRuleTableMap().get(key);

		final EntityTable entityTable = entityContext.getEntityTableMap().get(key);
		String idConditionSql = "";
		for(String strIdColumn : entityTable.getIdColumnSet()){
			idConditionSql = strIdColumn + "=?" + " AND ";
		}
		idConditionSql = idConditionSql.substring(0,idConditionSql.length() - 5);
		
		String updateJson = entityDatabaseCheckUpdateParam.getUpdateJson();
		String updateColumnSql = "";
		Map<String,String> updateJsonMap = (Map<String,String>) JsonUtils.stringToObject(updateJson, Map.class);
		for(String updateKey : updateJsonMap.keySet()){
			updateColumnSql += updateKey + "=?,";
		}

		updateColumnSql = updateColumnSql.substring(0,updateColumnSql.length() - 1);
		final String updateByIdSql = "UPDATE " + entityTable.getTableName() + " SET " + updateColumnSql + " WHERE " + idConditionSql;

		final Map<String,Map<Boolean,String>> ruleMaps = new LinkedHashMap<String,Map<Boolean,String>>();
		for(String value : updateJsonMap.values()){
			Map<Boolean,String> ruleMap = new HashMap<Boolean,String>();
			if(value.equals("dataResult")){
				ruleMaps.put("dataResult", null);
			}
			else if(value.startsWith("dataResult.check") || value.startsWith("dataResult.forceCheck") || value.startsWith("dataResult.exception")){
				if(value.contains(",")){
					String[] rules = value.split(",");
					ruleMap.put(true, rules[1]);
					ruleMap.put(false, rules[2]);
				}
				else{
					ruleMap.put(true, "1");
					ruleMap.put(false, "0");
				}
				ruleMaps.put(value.split(",")[0], ruleMap);
			}
		}
		
		
		final CommonServiceResult commonServiceResult = new CommonServiceResult();
		final String strProjectCode = entityContext.getStrProjectCodeFromTableKey(entityDatabaseCheckUpdateParam.getTableNameKey());
		final String datasourceName = entityContext.getDatasourceNameFromTableKey(entityDatabaseCheckUpdateParam.getTableNameKey());
		
		TransactionTemplate transactionTemplate = applicationContext.getBean(entityContext.getTransactionTemplateName(strProjectCode,datasourceName),TransactionTemplate.class);
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				try {
					DataHandler dataHandler = new DataHandler(strProjectCode,datasourceName,updateByIdSql,ruleMaps,entityTable,checkRuleTable,dataListResultParam,dataListResult);
					queryForListMapDataHandler.queryForHandler(dataHandler,entityDatabaseCheckUpdateParam.getCacheLine(),strProjectCode,datasourceName,entityTable.getTableName(),entityDatabaseCheckUpdateParam.getSql(), entityDatabaseCheckUpdateParam.getArgs());
					commonServiceResult.setMessage("sucess");
					commonServiceResult.setSuccess(true);
				} 
				catch (Exception ex) {
                    logger.error("",ex);
					status.setRollbackOnly();
					commonServiceResult.setMessage(ex.getMessage());
					commonServiceResult.setSuccess(false);
				}
			}
		});
		if(commonServiceResult.isSuccess()){
			return dataListResult;
		}
		else{
			return commonServiceResult;
		}
	}
}
