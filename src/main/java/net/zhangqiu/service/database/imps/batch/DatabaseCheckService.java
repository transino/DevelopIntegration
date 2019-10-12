package net.zhangqiu.service.database.imps.batch;

import java.util.List;
import java.util.Map;

import net.zhangqiu.service.check.entity.CheckRule;
import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.entity.param.DataListResultParam;
import net.zhangqiu.service.check.entity.param.DataResultParam;
import net.zhangqiu.service.check.entity.result.DataListResult;
import net.zhangqiu.service.check.interfaces.CheckAll;
import net.zhangqiu.service.check.interfaces.CheckBatch;
import net.zhangqiu.service.check.interfaces.CheckExpressionList;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.param.EntityDatabaseCheckParam;
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


@Service
public class DatabaseCheckService {

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

    private static Logger logger = LoggerFactory.getLogger(DatabaseCheckService.class);
	
	class DataHandler implements ListMapHandler {

		private CheckRuleTable checkRuleTable;
		private DataListResultParam dataListResultParam;
		private DataListResult dataListResult;
		private CheckBatch checkBatch;
		public DataHandler(CheckRuleTable checkRuleTable, DataListResultParam dataListResultParam, DataListResult dataListResult, CheckBatch checkBatch){
			this.checkRuleTable = checkRuleTable;
			this.dataListResultParam = dataListResultParam;
			this.dataListResult = dataListResult;
			this.checkBatch = checkBatch;
		}
		
		public void handleData(List<Map<String, Object>> dataMapList)throws Exception {
			if(checkRuleTable != null){
				if(checkBatch != null){
					checkBatch.check(dataListResult, checkRuleTable, dataMapList, dataListResultParam);
				}
				checkExpressionList.check(dataListResult,checkRuleTable, dataMapList,dataListResultParam);
			}
		}
	}
	
	public String handleService(String indentification,EntityDatabaseCheckParam entityDatabaseCheckParam) throws Exception{
		try{
			DataListResult dataListResult = logicService(entityDatabaseCheckParam);
			return JsonUtils.objectToString(dataListResult);
		}
		catch(Exception ex){
		    logger.error("",ex);
			CommonServiceResult commonServiceResult = new CommonServiceResult();
			commonServiceResult.setSuccess(false);
			commonServiceResult.setMessage(ex.getMessage());
			return JsonUtils.objectToString(commonServiceResult);
		}
	}
	
	public DataListResult logicService(EntityDatabaseCheckParam entityDatabaseCheckParam) throws Exception{
		if(StrUtils.isEmpty(entityDatabaseCheckParam.getDatasourceNameKey())){
			throw new Exception("数据源不能为空");
		}
		if(StrUtils.isEmpty(entityDatabaseCheckParam.getTableNameKey())){
			throw new Exception("表不能为空");
		}
		if(StrUtils.isEmpty(entityDatabaseCheckParam.getSql())){
			throw new Exception("SQL不能为空");
		}
		
		DataListResultParam dataListResultParam = new DataListResultParam();
		dataListResultParam.setMaxDataResult(entityDatabaseCheckParam.getMaxDataResult());
		dataListResultParam.setMaxDataResultBreak(entityDatabaseCheckParam.isMaxDataResultBreak());
		dataListResultParam.setProcessorCount(entityDatabaseCheckParam.getProcessorCount());
		dataListResultParam.setResultLevel(entityDatabaseCheckParam.getResultLevel());
		DataResultParam dataResultParam = new DataResultParam();
		dataResultParam.setCheckRule(entityDatabaseCheckParam.isCheckRule());
		dataResultParam.setColumnResultOnlyFalse(entityDatabaseCheckParam.isColumnResultOnlyFalse());
		dataResultParam.setDescription(entityDatabaseCheckParam.isDescription());
		dataResultParam.setDescriptionExpression(entityDatabaseCheckParam.isDescriptionExpression());
		dataResultParam.setOptional(entityDatabaseCheckParam.isOptional());
		dataResultParam.setRuleExpression(entityDatabaseCheckParam.isRuleExpression());
		dataListResultParam.setDataResultParam(dataResultParam);

		DataListResult dataListResult = new DataListResult();
		CheckRuleTable checkRuleTable  = entityContext.getCheckRuleTableMap().get(entityDatabaseCheckParam.getTableNameKey());

		CheckBatch checkBatch = null;
		if(checkRuleTable != null){
			for(CheckRule checkRule : checkRuleTable.getCheckRuleList()){
				if(checkRule.getLevel() == 4){
					String expressionParam = StrUtils.getExpressionParam(checkRule.getRule(), "@CHECK");
					CheckAll checkAll = applicationContext.getBean(expressionParam, CheckAll.class);
					checkAll.check(dataListResult, checkRuleTable, dataListResultParam);
				}
			}
			for(CheckRule checkRule : checkRuleTable.getCheckRuleList()){
				if(checkRule.getLevel() == 3){
					String expressionParam = StrUtils.getExpressionParam(checkRule.getRule(), "@CHECK");
					checkBatch = applicationContext.getBean(expressionParam, CheckBatch.class);
				}
			}
		}
		
		DataHandler dataHandler = new DataHandler(checkRuleTable,dataListResultParam,dataListResult,checkBatch);
		
		String strProjectCode = entityContext.getStrProjectCodeFromDatasourceKey(entityDatabaseCheckParam.getDatasourceNameKey());
		String datasourceName = entityContext.getDatasourceNameFromDatasourceKey(entityDatabaseCheckParam.getDatasourceNameKey());
		String tableName = entityContext.getTableNameFromTableKey(entityDatabaseCheckParam.getTableNameKey());
		queryForListMapDataHandler.queryForHandler(dataHandler,entityDatabaseCheckParam.getCacheLine(),strProjectCode,datasourceName,tableName,entityDatabaseCheckParam.getSql(), null);
		
		return dataListResult;
	}
}
