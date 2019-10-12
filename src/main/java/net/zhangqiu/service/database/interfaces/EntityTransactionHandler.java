package net.zhangqiu.service.database.interfaces;

public interface EntityTransactionHandler {
	public Object transactionService(String strPorjectCode, String datasourceName, Object objectParam) throws Exception;
	public Object logicService(String strPorjectCode, String datasourceName, Object objectParam) throws Exception;
}
