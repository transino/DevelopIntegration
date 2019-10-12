package net.zhangqiu.service.database.imps.serial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.interfaces.EntityTransactionHandler;
import net.zhangqiu.service.entity.result.CommonServiceResult;

public abstract class EntityTransactionTemplateService implements EntityTransactionHandler {

	@Autowired
	ApplicationContext applicationContext;
	@Autowired
    EntityContext entityContext;

    private static Logger logger = LoggerFactory.getLogger(EntityTransactionTemplateService.class);

	public abstract Object logicService(String strProjectCode,String datasourceName, Object objectParam)throws Exception;

	public Object transactionService(final String strProjectCode,final String datasourceName, final Object objectParam) throws Exception {
		TransactionTemplate transactionTemplate = applicationContext.getBean(entityContext.getTransactionTemplateName(strProjectCode,datasourceName),TransactionTemplate.class);
		return transactionTemplate.execute(new TransactionCallback<Object>() {
			public Object doInTransaction(TransactionStatus arg0){
				try {
					return logicService(strProjectCode,datasourceName,objectParam);
				} 
				catch (Exception ex) {
                    logger.error("事务回滚，请检查原因",ex);
					arg0.setRollbackOnly();
					CommonServiceResult commonServiceResult = new CommonServiceResult();
					commonServiceResult.setSuccess(false);
					commonServiceResult.setMessage(ex.getMessage());
					return commonServiceResult;
				}
			}
		});
	}

}
