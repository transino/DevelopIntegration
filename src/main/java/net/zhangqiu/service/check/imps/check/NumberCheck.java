package net.zhangqiu.service.check.imps.check;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.interfaces.Check;
import net.zhangqiu.service.check.entity.result.CommonCheckResult;

import net.zhangqiu.utils.StrUtils;
import org.springframework.stereotype.Component;

@Component
public class NumberCheck implements Check{
	
	private Pattern pattern1 = Pattern.compile("^-?\\d+$");
	private Pattern pattern2 = Pattern.compile("[a-zA-Z_$][a-zA-Z_0-9$]*");
	public CommonCheckResult check(String value, String[] params, Map<String, Object> dataMap, Map<String,String> ruleColumnMap, Map<String,String> columnDescriptionMap, CheckRuleTable checkRuleTable) {
		CommonCheckResult commonCheckResult = new CommonCheckResult();
        value = StrUtils.getMapInnerValue(ruleColumnMap, dataMap, value);
		if(params == null || params[0].equals("number")){
			Matcher matcher = pattern1.matcher(value);
			if(!matcher.matches()){
				commonCheckResult.setSuccess(false);
				commonCheckResult.setMessage("应为数字");
			}
		}
		else if(params[0].equals("notallnumber")){
			Matcher matcher1 = pattern1.matcher(value);
			if(matcher1.matches()){
				commonCheckResult.setSuccess(false);
				commonCheckResult.setMessage("不能全为数字");
			}
		}
		else if(params[0].equals("notstartnumber")){
			Matcher matcher2 = pattern2.matcher(value);
			if(!matcher2.matches()){
				commonCheckResult.setSuccess(false);
				commonCheckResult.setMessage("不能以数字开头");
			}
		}
		
		return commonCheckResult;
	}
}
