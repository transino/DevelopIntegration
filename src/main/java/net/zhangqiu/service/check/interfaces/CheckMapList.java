package net.zhangqiu.service.check.interfaces;

import java.util.List;
import java.util.Map;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.entity.result.CommonCheckResult;


public interface CheckMapList {
	public CommonCheckResult check(String[] params, Map<String, Object> dataMap, List<Map<String, Object>> dataMapList, Map<String, String> ruleColumnMap, Map<String, String> columnDescriptionMap, CheckRuleTable checkRuleTable);
}
