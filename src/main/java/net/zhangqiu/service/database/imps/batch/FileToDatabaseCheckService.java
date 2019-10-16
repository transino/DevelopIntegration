package net.zhangqiu.service.database.imps.batch;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.service.check.entity.param.DataListResultParam;
import net.zhangqiu.service.check.entity.param.DataResultParam;
import net.zhangqiu.service.check.entity.result.DataListResult;
import net.zhangqiu.service.check.entity.result.DataResult;
import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.interfaces.CheckExpressionList;
import net.zhangqiu.service.database.DataConvertUtils;
import net.zhangqiu.service.database.dao.BatchUpdateSqlDao;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.EntityTable;
import net.zhangqiu.service.database.entity.param.EntityDatabaseCheckTextParam;
import net.zhangqiu.service.database.interfaces.ListTextHandler;
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
public class FileToDatabaseCheckService extends EntityDatabaseTextTemplateService {
	
	@Autowired
	EnvironmentContext environmentContext;
	@Autowired
    BatchUpdateSqlDao batchUpdateSqlDao;
	@Autowired
	private EntityContext entityContext;
	@Autowired
	@Qualifier("checkExpressionListService")
	CheckExpressionList checkExpressionList;
	@Autowired
	protected ApplicationContext applicationContext;
    @Autowired
    DataConvertUtils dataConvertUtils;

    private static Logger logger = LoggerFactory.getLogger(FileToDatabaseCheckService.class);

	class TextFileDataHandler implements ListTextHandler {
		private String strProjectCode;
		private String datasourceName;
		private String regex;
		private String key;
		private DataListResultParam dataListResultParam;
		private DataListResult dataListResult;
		private Map<String,String> updateJsonMap;
		private Map<String,Map<Boolean,String>> ruleMaps;
		
		public TextFileDataHandler(String strProjectCode,String datasourceName,String regex,String key,DataListResultParam dataListResultParam,DataListResult dataListResult,Map<String,String> updateJsonMap,Map<String,Map<Boolean,String>> ruleMaps){
			this.strProjectCode = strProjectCode;
			this.datasourceName = datasourceName;
			this.regex = regex;
			this.key = key;
			this.dataListResultParam = dataListResultParam;
			this.dataListResult = dataListResult;
			this.updateJsonMap = updateJsonMap;
			this.ruleMaps = ruleMaps;
		}
		
		public void handleData(List<String> dataList) throws Exception {
			EntityTable entityTable = entityContext.getEntityTableMap().get(key);
			List<Map<String,Object>> mapList = dataConvertUtils.splitDataListToMapList(dataList, regex, entityTable);
			CheckRuleTable checkRuleTable  = entityContext.getCheckRuleTableMap().get(key);
			if(checkRuleTable != null && updateJsonMap.size() > 0){
				
				checkExpressionList.check(dataListResult,checkRuleTable, mapList,dataListResultParam);
				for(int i =0;i<mapList.size();i++){
					Map<String,Object> data = mapList.get(i);
					DataResult dataResult = dataListResult.getDataResultList().get(i);
					for(Map.Entry<String, String> entry : updateJsonMap.entrySet()){
						if(entry.getValue().equals("dataResult")){
							data.put(entry.getKey(), JsonUtils.objectToString(dataResult));
						}
						else if(entry.getValue().startsWith("dataResult.check")){
							data.put(entry.getKey(), ruleMaps.get("dataResult.check").get(dataResult.isCheck()));
						}
						else if(entry.getValue().startsWith("dataResult.forceCheck")){
							data.put(entry.getKey(), ruleMaps.get("dataResult.forceCheck").get(dataResult.isForceCheck()));
						}
						else if(entry.getValue().startsWith("dataResult.exception")){
							data.put(entry.getKey(), ruleMaps.get("dataResult.exception").get(dataResult.isException()));
						}
					}
				}
				dataListResult.getDataResultList().clear();
			}
			
			List<Object[]> objectList = dataConvertUtils.mapListToObjectList(mapList,entityTable);
			batchUpdateSqlDao.batchUpdateSql(strProjectCode,datasourceName,entityContext.getEntityTableInsertMap().get(key), objectList);
            dataListResult.setTransCount(dataListResult.getTransCount() + objectList.size());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object logicService(final EntityDatabaseCheckTextParam entityDatabaseText)throws Exception {
		if(StrUtils.isEmpty(entityDatabaseText.getTableNameKey())){
			throw new Exception("表不能为空");
		}
		if(StrUtils.isEmpty(entityDatabaseText.getResource())){
			throw new Exception("文件路径不能为空");
		}
		if(StrUtils.isEmpty(entityDatabaseText.getUpdateJson())){
			entityDatabaseText.setUpdateJson("{}");
		}

		final DataListResultParam dataListResultParam = new DataListResultParam();
		dataListResultParam.setMaxDataResultBreak(false);
		dataListResultParam.setResultLevel(99);
		dataListResultParam.setProcessorCount(entityDatabaseText.getProcessorCount());
		DataResultParam dataResultParam = new DataResultParam();
		dataListResultParam.setDataResultParam(dataResultParam);
		final DataListResult dataListResult = new DataListResult();
		
		String updateJson = entityDatabaseText.getUpdateJson();
		final Map<String,String> updateJsonMap = (Map<String,String>)JsonUtils.stringToObject(updateJson, Map.class);
		final Map<String,Map<Boolean,String>> ruleMaps = new LinkedHashMap<String,Map<Boolean,String>>();
		if(!StrUtils.isEmpty(updateJson)){
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
		}

		final CommonServiceResult commonServiceResult = new CommonServiceResult();
		
		final String strProjectCode = entityContext.getStrProjectCodeFromTableKey(entityDatabaseText.getTableNameKey());
		final String datasourceName = entityContext.getDatasourceNameFromTableKey(entityDatabaseText.getTableNameKey());
		TransactionTemplate transactionTemplate = applicationContext.getBean(entityContext.getTransactionTemplateName(strProjectCode,datasourceName),TransactionTemplate.class);
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				try {
					String toDatasourceName = entityContext.getDatasourceNameFromTableKey(entityDatabaseText.getTableNameKey());
					ReadTextDataHandler readTextDataHandler = new ReadTextDataHandler();
					readTextDataHandler.setTextDataHandler(new TextFileDataHandler(strProjectCode,toDatasourceName,entityDatabaseText.getRegex(),entityDatabaseText.getTableNameKey(),dataListResultParam,dataListResult,updateJsonMap,ruleMaps));
					readTextDataHandler.setResource(entityDatabaseText.getResource());
					readTextDataHandler.setCacheLine(entityDatabaseText.getCacheLine());
					readTextDataHandler.setEncoding(entityDatabaseText.getEncoding());
					readTextDataHandler.handleData();
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
