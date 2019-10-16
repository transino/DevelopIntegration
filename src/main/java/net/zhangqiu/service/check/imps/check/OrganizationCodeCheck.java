package net.zhangqiu.service.check.imps.check;

import java.util.Map;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.interfaces.Check;
import net.zhangqiu.service.check.entity.result.CommonCheckResult;

import net.zhangqiu.utils.StrUtils;
import org.springframework.stereotype.Component;

@Component
public class OrganizationCodeCheck implements Check {
	
	private int[] Wi=new int[]{3,7,9,10,5,8,4,2};

	public CommonCheckResult check(String value, String[] params, Map<String, Object> dataMap, Map<String,String> ruleColumnMap, Map<String,String> columnDescriptionMap, CheckRuleTable checkRuleTable) {
		CommonCheckResult commonCheckResult = new CommonCheckResult();
        value = StrUtils.getMapInnerValue(ruleColumnMap, dataMap, value);
		if(value.length()!=10){
			commonCheckResult.setSuccess(false);
			commonCheckResult.setMessage("长度应为10");
		}
		if (value.equals("00000000-0")) {
			commonCheckResult.setSuccess(false);
			commonCheckResult.setMessage("不能为00000000-0");
		   }
		if(!value.substring(8, 9).equals("-")){
			commonCheckResult.setSuccess(false);
			commonCheckResult.setMessage("第9位应为-");
		}
		if(value.matches("[a-z0-9A-Z\\-]")){
			commonCheckResult.setSuccess(false);
			commonCheckResult.setMessage("格式错误");
		}
		boolean f=true;
		for(int index=0;index<8;index++){
			int c =value.charAt(index);
			if(c>=65 && c<=90){
				continue;
			}else if(c>=48 && c<=57){
				continue;
			}else{
				f=false;
				break;
			}
		}
		if(!f){
			commonCheckResult.setSuccess(false);
			commonCheckResult.setMessage("格式错误");
		}
		int sum=0;
		for(int index=0;index<8;index++){
			int c =value.charAt(index);
			if(c>=65 &&c<=90){
				c-=55;
			}else
				c-=48;
			sum+=Wi[index]*c;
		}
		int c9=11-sum%11;
		String s9=String.valueOf(c9);
		if(c9==10){
			s9="X";
		}
		else if(c9==11){
			s9="0";
		}
		if(!value.substring(9, 10).equals(s9)){
			commonCheckResult.setSuccess(false);
			commonCheckResult.setMessage("校验位错误");
		}
		return commonCheckResult;
	}

}
