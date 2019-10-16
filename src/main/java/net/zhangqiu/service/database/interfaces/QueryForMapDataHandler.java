package net.zhangqiu.service.database.interfaces;

public interface QueryForMapDataHandler {
	public void queryForHandler(MapResultSetHandler mapResultSetHandler, String strProjectCode, String datasourceName, String sql, Object[] args) throws Exception;
}
