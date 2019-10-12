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
public class CreditCodeCheck implements Check {

	private Pattern creditPattern = Pattern.compile("[A-Z]{1}[0-9]{16}[0-9A-Z\\*]{1}");
	
	public CommonCheckResult check(String value, String[] params, Map<String, Object> dataMap, Map<String,String> ruleColumnMap, Map<String,String> columnDescriptionMap, CheckRuleTable checkRuleTable) {
		CommonCheckResult commonCheckResult = new CommonCheckResult();
        value = StrUtils.getMapInnerValue(ruleColumnMap, dataMap, value);
		byte[] valueByte = value.getBytes();
		if (valueByte.length == 18) {
			int m = 36;
			int s = m;
			for (int i = 0; i <= valueByte.length - 2; i++) {
			    s = (s + char2num(valueByte[i])) % m;
			    if (s == 0) s = m;
			    s = s * 2 % (m + 1);
			}
			if(!((s + char2num(valueByte[(valueByte.length - 1)])) % m == 1)){
			    commonCheckResult.setSuccess(false);
			}
			if(commonCheckResult.isSuccess()){
				Matcher matcher = creditPattern.matcher(value);
			    if (!matcher.matches()){
			    	commonCheckResult.setSuccess(false);
			    }
			}
		    if(!commonCheckResult.isSuccess()){
		    	commonCheckResult.setMessage("校验错误");
		    }
		}
		else{
			commonCheckResult.setSuccess(false);
			commonCheckResult.setMessage("字节长度应为18");
		}
		return commonCheckResult;
	}
	
	private int char2num(byte a) {
	    if (a == 42){
	    	return 36;
	    }
	    if ((a >= 48) && (a <= 57)) {
	        return a - 48;
	    }
	    return a - 55;
	}
}
