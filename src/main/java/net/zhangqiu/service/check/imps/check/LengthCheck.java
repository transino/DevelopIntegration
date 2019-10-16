package net.zhangqiu.service.check.imps.check;

import java.util.Map;

import net.zhangqiu.utils.StrUtils;
import org.springframework.stereotype.Component;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.interfaces.Check;
import net.zhangqiu.service.check.entity.result.CommonCheckResult;

@Component
public class LengthCheck implements Check{

	public CommonCheckResult check(String value, String[] params, Map<String, Object> dataMap, Map<String,String> ruleColumnMap, Map<String,String> columnDescriptionMap, CheckRuleTable checkRuleTable) {
		CommonCheckResult commonCheckResult = new CommonCheckResult();
        value = StrUtils.getMapInnerValue(ruleColumnMap, dataMap, value);
		int valueLength = value.length();
		if(params.length == 1){
			String[] orParam = params[0].split("or",-1);
			for(int i=0;i<orParam.length;i++){
				if(valueLength != Integer.valueOf(orParam[i])){
					commonCheckResult.setSuccess(false);
					commonCheckResult.setMessage("长度应为" + params[0].replace("or", "|"));
				}
			}
		}
		else if(params.length == 2){
			int min = Integer.valueOf(params[0]);
			int max = Integer.valueOf(params[1]);
			if(valueLength < min || valueLength > max){
				commonCheckResult.setSuccess(false);
				if(min == max){
					commonCheckResult.setMessage("长度应为" + min);
				}
				else{
					commonCheckResult.setMessage("长度应为" + min + "到" + max);
				}
				
			}
		}
		return commonCheckResult;
	}

}
