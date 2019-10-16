package net.zhangqiu.service.database.interfaces;

import java.sql.ResultSet;
import java.util.Map;

public interface MapResultSetHandler {
	public void handleData(Map<String, Object> dataMap, ResultSet resultSet) throws Exception;
}
