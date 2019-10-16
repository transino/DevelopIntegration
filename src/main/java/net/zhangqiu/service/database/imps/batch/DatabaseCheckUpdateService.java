package net.zhangqiu.service.database.imps.batch;

import java.sql.ResultSet;
import java.util.Map;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.entity.param.DataResultParam;
import net.zhangqiu.service.check.entity.result.DataListResult;
import net.zhangqiu.service.check.entity.result.DataResult;
import net.zhangqiu.service.check.interfaces.CheckExpression;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.param.EntityDatabaseCheckParam;
import net.zhangqiu.service.database.interfaces.MapResultSetHandler;
import net.zhangqiu.service.database.interfaces.QueryForMapDataHandler;
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
public class DatabaseCheckUpdateService {

    private static Logger logger = LoggerFactory.getLogger(DatabaseCheckUpdateService.class);

	@Autowired
    EntityContext entityContext;
	@Autowired
	@Qualifier("updateMapQueryCallbackHandlerDao")
	QueryForMapDataHandler queryForMapDataHandler;
	@Autowired
	@Qualifier("checkExpressionService")
	CheckExpression checkExpression;
	@Autowired
	protected ApplicationContext applicationContext;
	
	class DataHandler implements MapResultSetHandler {

		private CheckRuleTable checkRuleTable;
		private DataResultParam dataResultParam;

		public DataHandler(CheckRuleTable checkRuleTable, DataResultParam dataResultParam){
			this.checkRuleTable = checkRuleTable;
			this.dataResultParam = dataResultParam;
		}

		public void handleData(Map<String, Object> dataMap, ResultSet resultSet)throws Exception {
			DataResult dataResult = checkExpression.check(checkRuleTable, dataMap, dataResultParam);
			resultSet.updateObject("strPassword", dataResult.isCheck());
			resultSet.rowUpdated();
		}
	}
	
	public String handleService(String indentification,EntityDatabaseCheckParam entityDatabaseCheckParam) throws Exception{
		try{
			return JsonUtils.objectToString(logicService(entityDatabaseCheckParam));
		}
		catch(Exception ex){
            logger.error("",ex);
			CommonServiceResult commonServiceResult = new CommonServiceResult();
			commonServiceResult.setSuccess(false);
			commonServiceResult.setMessage(ex.getMessage());
			return JsonUtils.objectToString(commonServiceResult);
	    }
		
	}
	
	public Object logicService(final EntityDatabaseCheckParam entityDatabaseCheckParam) throws Exception{
		if(StrUtils.isEmpty(entityDatabaseCheckParam.getDatasourceNameKey())){
			throw new Exception("数据源不能为空");
		}
		if(StrUtils.isEmpty(entityDatabaseCheckParam.getTableNameKey())){
			throw new Exception("表不能为空");
		}
		if(StrUtils.isEmpty(entityDatabaseCheckParam.getSql())){
			throw new Exception("SQL不能为空");
		}

		final DataResultParam dataResultParam = new DataResultParam();
		dataResultParam.setCheckRule(entityDatabaseCheckParam.isCheckRule());
		dataResultParam.setColumnResultOnlyFalse(entityDatabaseCheckParam.isColumnResultOnlyFalse());
		dataResultParam.setDescription(entityDatabaseCheckParam.isDescription());
		dataResultParam.setDescriptionExpression(entityDatabaseCheckParam.isDescriptionExpression());
		dataResultParam.setOptional(entityDatabaseCheckParam.isOptional());
		dataResultParam.setRuleExpression(entityDatabaseCheckParam.isRuleExpression());

		final CheckRuleTable checkRuleTable  = entityContext.getCheckRuleTableMap().get(entityDatabaseCheckParam.getTableNameKey());

		final CommonServiceResult commonServiceResult = new CommonServiceResult();
		final String strProjectCode = entityContext.getStrProjectCodeFromTableKey(entityDatabaseCheckParam.getTableNameKey());
		final String datasourceName = entityContext.getDatasourceNameFromTableKey(entityDatabaseCheckParam.getTableNameKey());
		TransactionTemplate transactionTemplate = applicationContext.getBean(entityContext.getTransactionTemplateName(strProjectCode,datasourceName),TransactionTemplate.class);
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				try {
					String strProjectCode = entityContext.getStrProjectCodeFromTableKey(entityDatabaseCheckParam.getTableNameKey());
					String datasourceName = entityContext.getDatasourceNameFromTableKey(entityDatabaseCheckParam.getTableNameKey());
					DataHandler dataHandler = new DataHandler(checkRuleTable,dataResultParam);
					queryForMapDataHandler.queryForHandler(dataHandler,strProjectCode,datasourceName,entityDatabaseCheckParam.getSql(), null);
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
			DataListResult dataListResult = new DataListResult();
			return dataListResult;
		}
		else{
			return commonServiceResult;
		}

	}
}
