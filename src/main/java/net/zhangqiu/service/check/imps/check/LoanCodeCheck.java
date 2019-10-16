package net.zhangqiu.service.check.imps.check;

import java.util.Map;

import net.zhangqiu.utils.StrUtils;
import org.springframework.stereotype.Component;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.interfaces.Check;
import net.zhangqiu.service.check.entity.result.CommonCheckResult;

@Component
public class LoanCodeCheck implements Check{

	private int[] weightValue = new int[]{1,3,5,7,11,2,13,1,1,17,19,97,23,29};
	private int[] checkValue = new int[14];
	
	public CommonCheckResult check(String value, String[] params, Map<String, Object> dataMap, Map<String,String> ruleColumnMap,Map<String,String> columnDescriptionMap,CheckRuleTable checkRuleTable) {
		
		CommonCheckResult commonCheckResult = new CommonCheckResult();
        value = StrUtils.getMapInnerValue(ruleColumnMap, dataMap, value);
		if(value.getBytes().length==16){
			for(int index=0;index<value.length()-1;index++){
				char c=value.charAt(index);
				if(index<3){
					if((c>=48&&c<=57) || (c>=65&&c<=90)){
						continue;
					}
					else{
						commonCheckResult.setSuccess(false);
						break;
					}
				}
				else{
					if(c>=48&&c<=57){
						continue;
					}
					else{
						commonCheckResult.setSuccess(false);
						break;
					}
				}
			}
			int sum=0;
		    for(int index=0;index<14;index++){
		    	char c=value.charAt(index);
		    	if ((c >= 65) && (c <= 90)) {
		    		checkValue[index]=c-55;
		    	}
		    	else{
		    		checkValue[index]=c-48;
		    	}
		    	sum+=weightValue[index]*checkValue[index];
		    }
		    int m=sum%97+1;
		    String ss=""+m;
		    if(m<10){
		    	ss="0"+m;
		    }
		    if(!ss.equals(value.substring(14))){
		    	commonCheckResult.setSuccess(false);
		    }
		    if(!commonCheckResult.isSuccess()){
		    	commonCheckResult.setMessage("校验错误");
		    }
		}
		else{
			commonCheckResult.setSuccess(false);
			commonCheckResult.setMessage("字节长度应为16");
		}

		return commonCheckResult;
	}
}
