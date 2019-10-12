package net.zhangqiu.service.check.interfaces;

import java.util.List;
import java.util.Map;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.entity.param.DataListResultParam;
import net.zhangqiu.service.check.entity.result.DataListResult;

public interface CheckExpressionList {
	public void check(DataListResult dataListResult, CheckRuleTable checkRuleTable, List<Map<String, Object>> dataMapList, DataListResultParam dataListResultParam) throws Exception;
}
