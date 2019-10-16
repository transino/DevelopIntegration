package net.zhangqiu.service.check.calculate.imps;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import net.zhangqiu.service.check.calculate.interfaces.Calculate;
import net.zhangqiu.service.check.calculate.interfaces.ScaleCalculate;
import net.zhangqiu.utils.StrUtils;

@Component
public class SimpleCalculate implements ScaleCalculate, Calculate {

	public String calculate(String expression, String resultFormat,boolean scaleFirst) {
		
		if(StrUtils.isEmpty(resultFormat)){
			resultFormat = "#.##";
		}
		DecimalFormat decimalFormat = new DecimalFormat(resultFormat);
		decimalFormat.setRoundingMode(RoundingMode.HALF_UP);  
		
		List<Integer> symbolIndexList = new ArrayList<Integer>();
		for(int i =0;i<expression.toString().length();i++){
			if(expression.charAt(i) == '+' || expression.charAt(i) == '-'){
				symbolIndexList.add(i);
			}
		}
		
		Double result  = Double.valueOf(expression.substring(0,symbolIndexList.get(0)));
		if(scaleFirst){
			result = Double.valueOf(decimalFormat.format(result));
		}
		
		for(int i=0;i<symbolIndexList.size();i++){
			Double right = 0.0;
			if(i == symbolIndexList.size()-1){
				right = Double.valueOf(expression.substring(symbolIndexList.get(i) + 1));
			}
			else{
				right = Double.valueOf(expression.substring(symbolIndexList.get(i) + 1,symbolIndexList.get(i+1)));
			}
			if(scaleFirst){
				right = Double.valueOf(decimalFormat.format(right));
			}
			char symbol = expression.charAt(symbolIndexList.get(i));
			if(symbol == '+'){
				result =  result + right;
			}
			else if(symbol == '-'){
				result =  result - right;
			}
		}
		return decimalFormat.format(result);
	}

	public String calculate(String expression, String resultFormat) {
		return calculate(expression,resultFormat,false);
	}

}
