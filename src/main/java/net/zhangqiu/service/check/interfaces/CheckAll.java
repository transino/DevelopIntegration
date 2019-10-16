package net.zhangqiu.service.check.interfaces;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.entity.param.DataListResultParam;
import net.zhangqiu.service.check.entity.result.DataListResult;

public interface CheckAll {
	public void check(DataListResult dataListResult, CheckRuleTable checkRuleTable, DataListResultParam dataListResultParam) throws Exception;
}
