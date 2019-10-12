package net.zhangqiu.service.database.interfaces;

public interface JsonDatasourceHandler {
	public String handleService(String indentification, String strProjectCode, String datasourceName, String jsonString, String encoding) throws Exception;
}
