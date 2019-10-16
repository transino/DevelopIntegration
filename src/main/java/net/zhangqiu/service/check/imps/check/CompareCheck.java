package net.zhangqiu.service.check.imps.check;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.interfaces.Check;
import net.zhangqiu.service.check.entity.result.CommonCheckResult;
import net.zhangqiu.utils.StrUtils;

@Component
public class CompareCheck implements Check {

	@Autowired
    EmptyCheck emptyCheck;
	@Autowired
	EnvironmentContext environmentContext;
	
	public CommonCheckResult check(String value, String[] params, Map<String, Object> dataMap, Map<String,String> ruleColumnMap, Map<String,String> columnDescriptionMap, CheckRuleTable checkRuleTable) {
		CommonCheckResult commonCheckResult = new CommonCheckResult();
		
		value = StrUtils.getMapInnerValue(ruleColumnMap, dataMap, value);
		String param1Value = StrUtils.getMapInnerValue(ruleColumnMap, dataMap, params[1]);
		String param1Description = StrUtils.getDescription(ruleColumnMap, dataMap, columnDescriptionMap, params[1],checkRuleTable.getColumnValueMap());

		if(param1Value.equals("empty")){
			param1Value = "";
		}
		else if(param1Value.equals("systemDate")){
			DateFormat format = new SimpleDateFormat(environmentContext.getDateFormt());
			param1Value = format.format(new Date());
		}
		else if(param1Value.equals("systemYear")){
			DateFormat format = new SimpleDateFormat("yyyy");
			param1Value = format.format(new Date());
		}

		if(params[0].equals(">=")){
			if(value.compareTo(param1Value) < 0){
				commonCheckResult.setSuccess(false);
			}
			commonCheckResult.setMessage("应大于等于" + param1Description);
		}
		else if(params[0].equals(">")){
			if(value.compareTo(param1Value) <= 0){
				commonCheckResult.setSuccess(false);
			}
			commonCheckResult.setMessage("应大于" + param1Description);
		}
		else if(params[0].equals("<=")){
			if(value.compareTo(param1Value) > 0){
				commonCheckResult.setSuccess(false);
			}
			commonCheckResult.setMessage("应小于等于" + param1Description);
		}
		else if(params[0].equals("<")){
			if(value.compareTo(param1Value) >= 0){
				commonCheckResult.setSuccess(false);
			}
			commonCheckResult.setMessage("应小于" + param1Description);
		}
		else if(params[0].equals("=") || params[0].equals("==")){
			if(value.compareTo(param1Value) != 0){
				commonCheckResult.setSuccess(false);
			}
			commonCheckResult.setMessage("应等于" + param1Description);
		}
		else if(params[0].equals("!=")){
			if(value.compareTo(param1Value) == 0){
				commonCheckResult.setSuccess(false);
			}
			commonCheckResult.setMessage("应不等于" + param1Description);
		}
		else if(params[0].equals("isIndexOf")){
			if(param1Value.indexOf(value) == -1){
				commonCheckResult.setSuccess(false);
			}
			commonCheckResult.setMessage("值域应为" + "[" + param1Value + "]");
		}
		else if(params[0].equals("notIsIndexOf")){
			if(param1Value.indexOf(value) > -1){
				commonCheckResult.setSuccess(false);
			}
			commonCheckResult.setMessage("值域不应为" + "[" + param1Value + "]");
		}
		else if(params[0].equals("match")){
			if(!value.matches(param1Value)){
				commonCheckResult.setSuccess(false);
			}
			commonCheckResult.setMessage("校验错误");
		}
		else if(params[0].equals("notMatch")){
			if(value.matches(param1Value)){
				commonCheckResult.setSuccess(false);
			}
			commonCheckResult.setMessage("校验错误");
		}
		else if(params[0].equals("yearMonth")){
			if(!value.substring(0,4).equals(param1Value.substring(0,4))){
				commonCheckResult.setSuccess(false);
			}
			commonCheckResult.setMessage("校验错误");
			/*
			Calendar cal = Calendar.getInstance();
			DateFormat format = new SimpleDateFormat(environmentContext.getDateFormt());
			try{
				Date date1 = format.parse(value);
				cal.setTime(date1);
				int year1 = cal.get(Calendar.YEAR);// 结算应还款年数
				int month1 = cal.get(Calendar.MONTH);// 结算应还款月数
				Date date2 = format.parse(param1Value);
				cal.setTime(date2);
				int year2 = cal.get(Calendar.YEAR);// 开户日期年数
				int month2 = cal.get(Calendar.MONTH);
				if(year1 != year2 || month1 != month2){
					commonCheckResult.setSuccess(false);
					commonCheckResult.setMessage("校验错误");
				}
			}
			catch(Exception ex){
				
			}
			*/
			
		}
		else if(params[0].equals("length")){
			if(value.length() != Integer.parseInt(param1Value)){
				commonCheckResult.setSuccess(false);
			}
			commonCheckResult.setMessage("校验错误");
		}

		return commonCheckResult;
	}

}
