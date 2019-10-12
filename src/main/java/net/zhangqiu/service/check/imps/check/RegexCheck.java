package net.zhangqiu.service.check.imps.check;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.zhangqiu.utils.StrUtils;
import org.springframework.stereotype.Component;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.interfaces.Check;
import net.zhangqiu.service.check.entity.result.CommonCheckResult;

@Component
public class RegexCheck implements Check{

	public CommonCheckResult check(String value, String[] params, Map<String, Object> dataMap, Map<String,String> ruleColumnMap, Map<String,String> columnDescriptionMap, CheckRuleTable checkRuleTable) {
		CommonCheckResult commonCheckResult = new CommonCheckResult();
        value = StrUtils.getMapInnerValue(ruleColumnMap, dataMap, value);
		Pattern pattern = Pattern.compile(params[0]);
		Matcher matcher = pattern.matcher(value);
		if(!matcher.matches()){
			commonCheckResult.setSuccess(false);
			commonCheckResult.setMessage("校验错误");
		}
		
		return commonCheckResult;
	}

}
