package net.zhangqiu.service.database.interfaces;

public interface QueryForListMapDataHandler {
	public void queryForHandler(ListMapHandler mapDataHandler, int cacheLine, String strProjectCode, String datasourceName, String tableName, String sql, Object[] args) throws Exception;
}
