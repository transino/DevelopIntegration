package net.zhangqiu.service.check.interfaces;

import java.util.Map;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.entity.result.CommonCheckResult;


public interface Check {
	public CommonCheckResult check(String value, String[] params, Map<String, Object> dataMap, Map<String, String> ruleColumnMap, Map<String, String> columnDescriptionMap, CheckRuleTable checkRuleTable);
}
