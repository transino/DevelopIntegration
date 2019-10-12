package net.zhangqiu.service.database.interfaces;

import java.util.List;

public interface ListDataHandler {
	public void handleData(List<List<Object>> dataList) throws Exception;
}
