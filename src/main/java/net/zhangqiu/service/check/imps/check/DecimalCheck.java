package net.zhangqiu.service.check.imps.check;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.interfaces.Check;
import net.zhangqiu.service.check.entity.result.CommonCheckResult;

import net.zhangqiu.utils.StrUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DecimalCheck implements Check{
	
	@Autowired
    EmptyCheck emptyCheck;
	
	private static Pattern decimalPattern = Pattern.compile("^(-?\\d+)(\\.\\d+)?$");
	private static Pattern decimalPattern2 = Pattern.compile("^(-?\\d+)(\\.\\d{1,2})?$");
	
	public CommonCheckResult check(String value, String[] params, Map<String, Object> dataMap, Map<String,String> ruleColumnMap, Map<String,String> columnDescriptionMap, CheckRuleTable checkRuleTable) {
		CommonCheckResult commonCheckResult = new CommonCheckResult();
        value = StrUtils.getMapInnerValue(ruleColumnMap, dataMap, value);
		if(params == null || params.length == 0){
			Matcher matcher = decimalPattern.matcher(value);
			if(!matcher.matches()){
				commonCheckResult.setSuccess(false);
				commonCheckResult.setMessage("应为货币类型");
			}
		}
		else if(params.length == 1){
			int length = Integer.parseInt(params[0]);
			if(length == 2){
				Matcher matcher = decimalPattern2.matcher(value);
				if(!matcher.matches()){
					commonCheckResult.setSuccess(false);
					commonCheckResult.setMessage("应为货币类型,2位小数");
				}
			}
			else{
				Pattern decimalPatternN = Pattern.compile("^(-?\\d+)(\\.\\d+{1,"+length+"})?$");
				Matcher matcher = decimalPatternN.matcher(value);
				if(!matcher.matches()){
					commonCheckResult.setSuccess(false);
					commonCheckResult.setMessage(",应为货币类型，"+length+"位小数");
				}
			}	
		}

		return commonCheckResult;
	}
}
