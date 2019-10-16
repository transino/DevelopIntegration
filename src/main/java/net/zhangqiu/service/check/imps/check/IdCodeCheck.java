package net.zhangqiu.service.check.imps.check;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import net.zhangqiu.utils.StrUtils;
import org.springframework.stereotype.Component;

import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.interfaces.Check;
import net.zhangqiu.service.check.entity.result.CommonCheckResult;

@Component
public class IdCodeCheck implements Check{
	
	private int[] WEIGHT=new int[]{7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2};
	private String[] FACTOR=new String[]{"1","0","X","9","8","7","6","5","4","3","2"};
	
	public CommonCheckResult check(String value, String[] params, Map<String, Object> dataMap, Map<String,String> ruleColumnMap,Map<String,String> columnDescriptionMap,CheckRuleTable checkRuleTable) {
		CommonCheckResult commonCheckResult = new CommonCheckResult();
        value = StrUtils.getMapInnerValue(ruleColumnMap, dataMap, value);
		if(value.length()==18){
			int sum=0;
			for(int index=0;index<value.length()-1;index++){
				char c = value.charAt(index);
				if(c>=48 &&c<=57){
					sum+=(c-48)*WEIGHT[index];
				}else{
					commonCheckResult.setSuccess(false);
					commonCheckResult.setMessage("本体码应为数字");
					break;
				}
			}
			if(commonCheckResult.isSuccess()){
				int y=sum%11;
				if(!FACTOR[y].equals(value.substring(17))){
					commonCheckResult.setSuccess(false);
					commonCheckResult.setMessage("校验码不对");
				}
			}
		}
		else if(value.length()==15){
			for(int index=0;index<value.length();index++){
				char c = value.charAt(index);
				if(c<48 || c>58){
					commonCheckResult.setSuccess(false);
					commonCheckResult.setMessage("应为数字");
					break;
				}
			}
			if(commonCheckResult.isSuccess()){
				Format sf = new SimpleDateFormat("yyMMdd");
				Date a;
				try {
					a = (Date)sf.parseObject((value.substring(6,12)));
					String t =sf.format(a);
					if(!t.equals(value.substring(6,12))){
						commonCheckResult.setSuccess(false);
						commonCheckResult.setMessage("出生日期无效");
					}
				} 
				catch (ParseException e) {
					commonCheckResult.setSuccess(false);
					commonCheckResult.setMessage("出生日期无效");
				}
			}
		}
		else{
			commonCheckResult.setSuccess(false);
			commonCheckResult.setMessage("长度应为15|18");
		}
		return commonCheckResult;
	}

}
