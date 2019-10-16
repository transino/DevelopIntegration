package net.zhangqiu.service.check.imps.check;

import java.util.Map;
import java.util.regex.Pattern;

import net.zhangqiu.utils.StrUtils;
import org.springframework.stereotype.Component;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.interfaces.Check;
import net.zhangqiu.service.check.entity.result.CommonCheckResult;

@Component
public class AccountCodeCheck implements Check{

	private Pattern accountPattern = Pattern.compile("[0-9]*");
	
	public CommonCheckResult check(String value, String[] params, Map<String, Object> dataMap, Map<String,String> ruleColumnMap,Map<String,String> columnDescriptionMap,CheckRuleTable checkRuleTable) {

		CommonCheckResult commonCheckResult = new CommonCheckResult();
        value = StrUtils.getMapInnerValue(ruleColumnMap, dataMap, value);
	    if (value.length() != 14) {
	    	commonCheckResult.setSuccess(false);
	    	commonCheckResult.setMessage("长度应为14位");
	    	return commonCheckResult;
	    }
	    if (!"J".equals(value.substring(0, 1))) {
	    	commonCheckResult.setSuccess(false);
	    	commonCheckResult.setMessage("校验位不对");
	    	return commonCheckResult;
	    }
	    if(!accountPattern.matcher(value.substring(1, 14)).matches()){
	    	commonCheckResult.setSuccess(false);
	    	commonCheckResult.setMessage("校验错误");
	    	return commonCheckResult;
	    }
		return commonCheckResult;
	}

}
