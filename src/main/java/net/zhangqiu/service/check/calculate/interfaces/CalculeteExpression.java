package net.zhangqiu.service.check.calculate.interfaces;

import java.util.Map;

public interface CalculeteExpression {
	public String calculate(String expression, Map<String, Object> dataMap, Map<String, String> ruleColumnMap);
}
