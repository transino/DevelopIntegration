package net.zhangqiu.service.check.calculate.imps;

import org.springframework.stereotype.Component;

import net.zhangqiu.service.check.calculate.interfaces.Calculate;

@Component
public class StringCalculate implements Calculate {

	public String calculate(String expression, String resultFormat) {
		return expression + resultFormat;
	}

}
