package net.zhangqiu.service.check.calculate.imps;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import net.zhangqiu.service.check.calculate.interfaces.Calculate;
import net.zhangqiu.utils.StrUtils;

@Component
public class SpELCalculate implements Calculate {
	public String calculate(String expression, String resultFormat) {
		if(StrUtils.isEmpty(resultFormat)){
			resultFormat = "#.##";
		}
		DecimalFormat decimalFormat = new DecimalFormat(resultFormat);
		decimalFormat.setRoundingMode(RoundingMode.HALF_UP); 
		
		ExpressionParser expressionParser = new SpelExpressionParser();
		Expression exp = expressionParser.parseExpression(expression);
		return decimalFormat.format(exp.getValue(Double.class));
	}

}
