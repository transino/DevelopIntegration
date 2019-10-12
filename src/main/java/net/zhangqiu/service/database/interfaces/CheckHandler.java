package net.zhangqiu.service.database.interfaces;

import java.util.Map;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.entity.result.DataResult;

public interface CheckHandler {
	public DataResult check(CheckRuleTable checkRuleTable, Map<String, Object> dataMap);
}
