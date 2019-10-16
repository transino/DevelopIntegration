package net.zhangqiu.service.database.interfaces;

import java.util.List;
import java.util.Map;

public interface ListMapHandler {
	public void handleData(List<Map<String, Object>> dataMapList) throws Exception;
}
