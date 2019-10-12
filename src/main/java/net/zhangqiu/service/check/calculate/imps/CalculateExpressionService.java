package net.zhangqiu.service.check.calculate.imps;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import net.zhangqiu.service.check.calculate.interfaces.Calculate;
import net.zhangqiu.service.check.calculate.interfaces.CalculeteExpression;
import net.zhangqiu.service.check.calculate.interfaces.ScaleCalculate;
import net.zhangqiu.utils.StrUtils;

@Service
public class CalculateExpressionService implements CalculeteExpression{

	@Autowired
	ApplicationContext applicationContext;
	
	@Autowired
	@Qualifier("simpleCalculate")
    Calculate calculate;
	
	@Autowired
	@Qualifier("simpleCalculate")
	ScaleCalculate scaleCalculate;
	
	public String calculate(String expression,Map<String, Object> dataMap,Map<String,String> ruleColumnMap) {
		String expressionParam = StrUtils.getExpressionParam(expression, "@CALCULATE");
		if(dataMap != null && ruleColumnMap != null){
			expressionParam = StrUtils.getReplaceString(expressionParam, dataMap, ruleColumnMap);
		}

		String[] params = expressionParam.split(",",-1);
		if(params.length == 1){
			return calculate.calculate(params[0], null);
		}
		else if(params.length == 2){
			return calculate.calculate(params[0], params[1]);
		} 
		else if(params.length == 3){
			if(params[2].equals("true") || params[2].equals("false")){
				return scaleCalculate.calculate(params[0], params[1], Boolean.valueOf(params[2]));
			}
			else{
				Calculate calculate = applicationContext.getBean(params[2], Calculate.class);
				return calculate.calculate(params[0], params[1]);
			}
		}
		return null;
	}
}
