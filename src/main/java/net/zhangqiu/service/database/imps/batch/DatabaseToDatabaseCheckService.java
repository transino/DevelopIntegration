package net.zhangqiu.service.database.imps.batch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.zhangqiu.service.database.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.service.check.entity.param.DataListResultParam;
import net.zhangqiu.service.check.entity.param.DataResultParam;
import net.zhangqiu.service.check.entity.result.DataListResult;
import net.zhangqiu.service.check.entity.result.DataResult;
import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.interfaces.CheckExpressionList;
import net.zhangqiu.service.database.DataConvertUtils;
import net.zhangqiu.service.database.dao.BatchUpdateSqlDao;
import net.zhangqiu.service.database.entity.param.EntityDatabaseCheckUpdateParam;
import net.zhangqiu.service.entity.result.CommonServiceResult;

import net.zhangqiu.utils.JsonUtils;
import net.zhangqiu.utils.StrUtils;

@Service
public class DatabaseToDatabaseCheckService{

    private static Logger logger = LoggerFactory.getLogger(DatabaseToDatabaseCheckService.class);

	@Autowired
	EnvironmentContext environmentContext;
	@Autowired
	ApplicationContext applicationContext;
	@Autowired
    EntityContext entityContext;
	@Autowired
    BatchUpdateSqlDao batchUpdateSqlDao;
	@Autowired
	@Qualifier("checkExpressionListService")
	CheckExpressionList checkExpressionList;
    @Autowired
    DataConvertUtils dataConvertUtils;
    @Autowired
    SnowFlakeGenerator snowFlakeGenerator;

	private class QueryForRowCallbackHandler implements RowCallbackHandler{
		public QueryForRowCallbackHandler(String strProjectCode, String datasourceName, EntityTable entityTable, String tableName, List<Map<String,Object>> mapList, int cacheLine, int count, DataListResultParam dataListResultParam, DataListResult dataListResult, Map<String,String> updateJsonMap, Map<String,Map<Boolean,String>> ruleMaps){
			this.strProjectCode = strProjectCode;
			this.datasourceName = datasourceName;
			this.entityTable = entityTable;
			this.tableName = tableName;
			this.mapList = mapList;
			this.cacheLine = cacheLine;
			this.count = count;
			this.dataListResultParam = dataListResultParam;
			this.dataListResult = dataListResult;
			this.updateJsonMap = updateJsonMap;
			this.ruleMaps = ruleMaps;
		}
		private String strProjectCode;
		private String datasourceName;
		private EntityTable entityTable;
		private String tableName;
		private List<Map<String,Object>> mapList;
		private int count;
		private int cacheLine;
		private DataListResultParam dataListResultParam;
		private DataListResult dataListResult;
		private Map<String,String> updateJsonMap;
		private Map<String,Map<Boolean,String>> ruleMaps;
		
		private boolean firstRow = true;
		Map<String,String> columnMap = new HashMap<String,String>();
		
		public void processRow(ResultSet arg0) throws SQLException {
			Map<String,Object> map = new HashMap<String,Object>();
			int columnCount = arg0.getMetaData().getColumnCount();
			
			if(firstRow && entityTable != null){
				for(int i=0;i<columnCount;i++){
					for(EntityColumn entityColumn: entityTable.getEntityColumnList()){
						if(arg0.getMetaData().getColumnName(i + 1).toUpperCase().equals(entityColumn.getColumnName().toUpperCase())){
							columnMap.put(arg0.getMetaData().getColumnName(i + 1), entityColumn.getColumnName());
						}
					}
				}
			}
			firstRow = false;
			
			if(entityTable != null){
				for(int i=0;i<columnCount;i++){
					map.put(columnMap.get(arg0.getMetaData().getColumnName(i + 1)), arg0.getObject(i + 1));
				}
			}
			else{
				for(int i=0;i<columnCount;i++){
					map.put(arg0.getMetaData().getColumnName(i + 1), arg0.getObject(i + 1));
				}
			}
			
			mapList.add(map);
			count++;
			try{
				if(count == cacheLine){
					batchUpdateSql(strProjectCode,datasourceName,tableName,mapList,dataListResultParam,dataListResult,updateJsonMap,ruleMaps);
					mapList.clear();
					count=0;
				}
			}
			catch(Exception ex){
                logger.error("",ex);
                throw new SQLException(ex.getMessage());
			}
		}
	}
	
	private void batchUpdateSql(String strProjectCode, String datasourceName, String tableName, List<Map<String,Object>> mapList, DataListResultParam dataListResultParam, DataListResult dataListResult, Map<String,String> updateJsonMap, Map<String,Map<Boolean,String>> ruleMaps) throws Exception{
		String key = entityContext.getEntityTableKey(strProjectCode,datasourceName, tableName);

        EntityTable entityTable  = entityContext.getEntityTableMap().get(key);
        if(entityTable.isAutoGenerateId()){
            for(Map<String,Object> map : mapList) {
                for(String string : entityTable.getIdColumnSet()){
                    if(StrUtils.isEmptyObject(map.get(string))){
                        long id = snowFlakeGenerator.nextId();
                        map.put(string,id);
                    }
                }
            }
        }

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

		List<Object[]> objectList = dataConvertUtils.mapListToObjectList(mapList, entityTable);
		batchUpdateSqlDao.batchUpdateSql(strProjectCode,datasourceName,entityContext.getEntityTableInsertMap().get(key), objectList);
        dataListResult.setTransCount(dataListResult.getTransCount() + objectList.size());
	}
	
	private class QueryForPreparedStatementCreator implements PreparedStatementCreator{
		public QueryForPreparedStatementCreator(String sql,Object[] args,String strDatabaseType){
			this.sql = sql;
			this.args = args;
			this.strDatabaseType = strDatabaseType;
		}
		private String sql;
		private Object[] args;
		private String strDatabaseType;
		
		public PreparedStatement createPreparedStatement(Connection con)throws SQLException {
			PreparedStatement preparedStatement = con.prepareStatement(sql,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			if(this.strDatabaseType.equals(EntityUtils.Database_Mysql)){
				preparedStatement.setFetchSize(Integer.MIN_VALUE);
			}
            if(args != null && args.length > 0){
                int i =1;
                for(Object object : args){
                    Object value = object;
                    if(value instanceof java.util.Date){
                        value = new java.sql.Timestamp(((java.util.Date)value).getTime());
                    }
                    preparedStatement.setObject(i,value);
                    i++;
                }
            }
			return preparedStatement;
		}
	}
	
	@SuppressWarnings("unchecked")
	public String handleService(String indentification,final EntityDatabaseCheckUpdateParam entityDatabaseCheckUpdateParam) throws Exception {
		try{
			CommonServiceResult commonServiceResult = new CommonServiceResult();
			if(StrUtils.isEmpty(entityDatabaseCheckUpdateParam.getDatasourceNameKey())){
				commonServiceResult.setMessage("数据源不能为空");
				commonServiceResult.setSuccess(false);
				return JsonUtils.objectToString(commonServiceResult);
			}
			if(StrUtils.isEmpty(entityDatabaseCheckUpdateParam.getTableNameKey())){
				commonServiceResult.setMessage("表不能为空");
				commonServiceResult.setSuccess(false);
				return JsonUtils.objectToString(commonServiceResult);
			}
			if(StrUtils.isEmpty(entityDatabaseCheckUpdateParam.getSql())){
				commonServiceResult.setMessage("SQL不能为空");
				commonServiceResult.setSuccess(false);
				return JsonUtils.objectToString(commonServiceResult);
			}
			if(StrUtils.isEmpty(entityDatabaseCheckUpdateParam.getUpdateJson())){
				entityDatabaseCheckUpdateParam.setUpdateJson("{}");
			}
			
			final DataListResultParam dataListResultParam = new DataListResultParam();
			dataListResultParam.setMaxDataResultBreak(false);
			dataListResultParam.setResultLevel(99);
			dataListResultParam.setProcessorCount(entityDatabaseCheckUpdateParam.getProcessorCount());
			DataResultParam dataResultParam = new DataResultParam();
			dataListResultParam.setDataResultParam(dataResultParam);
			final DataListResult dataListResult = new DataListResult();
			
			String updateJson = entityDatabaseCheckUpdateParam.getUpdateJson();
			final Map<String,String> updateJsonMap = (Map<String,String>) JsonUtils.stringToObject(updateJson, Map.class);
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

			final String toStrProjectCode = entityContext.getStrProjectCodeFromTableKey(entityDatabaseCheckUpdateParam.getTableNameKey());
			final String toDatasourceName = entityContext.getDatasourceNameFromTableKey(entityDatabaseCheckUpdateParam.getTableNameKey());
			final String toTableName = entityContext.getTableNameFromTableKey(entityDatabaseCheckUpdateParam.getTableNameKey());
			final String fromStrProjectCode = entityContext.getStrProjectCodeFromDatasourceKey(entityDatabaseCheckUpdateParam.getDatasourceNameKey());
			final String fromDataSourceName = entityContext.getDatasourceNameFromDatasourceKey(entityDatabaseCheckUpdateParam.getDatasourceNameKey());
 
			TransactionTemplate fromTransactionTemplate = applicationContext.getBean(entityContext.getTransactionTemplateName(fromStrProjectCode,fromDataSourceName),TransactionTemplate.class);
			return fromTransactionTemplate.execute(new TransactionCallback<String>() {
				public String doInTransaction(TransactionStatus arg0){
					final CommonServiceResult commonServiceResult = new CommonServiceResult();
					
					TransactionTemplate toTransactionTemplate = applicationContext.getBean(entityContext.getTransactionTemplateName(toStrProjectCode,toDatasourceName),TransactionTemplate.class);
					toTransactionTemplate.execute(new TransactionCallbackWithoutResult() {
						@Override
						protected void doInTransactionWithoutResult(TransactionStatus status) {
							try {
								JdbcTemplate fromJdbcTemplate = applicationContext.getBean(entityContext.getJdbcTemplateName(fromStrProjectCode,fromDataSourceName),JdbcTemplate.class);
								String sql = entityDatabaseCheckUpdateParam.getSql();
								Object[] args = entityDatabaseCheckUpdateParam.getArgs();
								List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
								String strDatabaseType = entityContext.getProjectEntityDatasourceMap().get(fromStrProjectCode).get(fromDataSourceName).getStrDatabaseType();
								QueryForPreparedStatementCreator queryForPreparedStatementCreator = new QueryForPreparedStatementCreator(sql,args,strDatabaseType);
								
								String toEntityTableKey = entityContext.getEntityTableKey(toStrProjectCode,toDatasourceName, toTableName);
								EntityTable toEntityTable = entityContext.getEntityTableMap().get(toEntityTableKey);
								fromJdbcTemplate.query(queryForPreparedStatementCreator, new QueryForRowCallbackHandler(toStrProjectCode,toDatasourceName,toEntityTable,toTableName,mapList,entityDatabaseCheckUpdateParam.getCacheLine(),0,dataListResultParam,dataListResult,updateJsonMap,ruleMaps));
								
								if(mapList.size() > 0){
									batchUpdateSql(toStrProjectCode,toDatasourceName,toTableName,mapList,dataListResultParam,dataListResult,updateJsonMap,ruleMaps);
								}
								
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
						return JsonUtils.objectToString(dataListResult);
					}
					else{
						return JsonUtils.objectToString(commonServiceResult);
					}
				}
			});
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
