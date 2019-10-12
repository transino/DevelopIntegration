package net.zhangqiu.service.check.imps.check;

import java.util.Map;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.interfaces.Check;
import net.zhangqiu.service.check.entity.result.CommonCheckResult;

import net.zhangqiu.utils.StrUtils;
import org.springframework.stereotype.Component;

@Component
public class InstitutionCodeCheck implements Check{

	public CommonCheckResult check(String value, String[] params, Map<String, Object> dataMap, Map<String,String> ruleColumnMap,Map<String,String> columnDescriptionMap,CheckRuleTable checkRuleTable) {
		
		CommonCheckResult commonCheckResult = new CommonCheckResult();
        value = StrUtils.getMapInnerValue(ruleColumnMap, dataMap, value);
		
		byte[] valueByte = value.getBytes();
		if (valueByte.length == 11) {
			int M = 10;
		    int s = M;
		    int k = 9;
		    for (int i = k; i >= 0; i--) {
		    	int temp = valueByte[(k - i)];
		    	if ((temp >= 48) && (temp <= 57)) {
			        temp -= 48;
			    }
		    	else if (((temp >= 65) && (temp <= 90)) || ((temp >= 97) && (temp <= 122))) {
			        temp = 0;
		    	}
		    	else {
			    	commonCheckResult.setSuccess(false);
			    	break;
			    }
			    if ((s + temp) % M == 0){
			        s = 9;
			    }
			    else {
			        s = (s + temp) % M * 2 % (M + 1);
			    }
		    }
		    if(!commonCheckResult.isSuccess()){
		    	s = M + 1 - s;
		 	    if (s == 10) {
		 	        s = 0;
		 	    }
		 	    if (((s == 11) && (valueByte[10] == 88)) || (s == valueByte[10] - 48)) {
		 	    	//commonCheckResult.setSuccess(false);
		 	    }
		    }
		    if(!commonCheckResult.isSuccess()){
	 	    	commonCheckResult.setMessage("校验错误");
	 	    }
		}
		else{
			commonCheckResult.setSuccess(false);
			commonCheckResult.setMessage("字节长度应为11");
		}
		
		return commonCheckResult;
	}

}
