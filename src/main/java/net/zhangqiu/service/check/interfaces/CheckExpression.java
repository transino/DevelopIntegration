package net.zhangqiu.service.check.interfaces;

import java.util.Map;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.entity.param.DataResultParam;
import net.zhangqiu.service.check.entity.result.DataResult;

public interface CheckExpression {
	public DataResult check(CheckRuleTable checkRuleTable, Map<String, Object> dataMap, DataResultParam dataResultParam);
}
