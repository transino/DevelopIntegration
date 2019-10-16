package net.zhangqiu.service.check.imps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.zhangqiu.service.check.calculate.interfaces.CalculeteExpression;
import net.zhangqiu.service.check.entity.param.DataResultParam;
import net.zhangqiu.service.check.entity.result.CheckRuleResult;
import net.zhangqiu.service.check.entity.result.ColumnResult;
import net.zhangqiu.service.check.entity.result.DataResult;

import net.zhangqiu.service.check.entity.CheckRule;
import net.zhangqiu.service.check.entity.CheckRuleTable;
import net.zhangqiu.service.check.imps.check.EmptyCheck;
import net.zhangqiu.service.check.interfaces.Check;
import net.zhangqiu.service.check.interfaces.CheckExpression;
import net.zhangqiu.service.check.interfaces.CheckMap;
import net.zhangqiu.service.check.interfaces.CheckMapList;
import net.zhangqiu.service.check.entity.result.CommonCheckResult;
import net.zhangqiu.service.check.interfaces.Condition;
import net.zhangqiu.utils.StrUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Service;

@Service
public class CheckExpressionService implements CheckExpression{


	@Autowired
	ApplicationContext applicationContext;
	@Autowired
	CalculeteExpression calculeteExpression;
	@Autowired
    EmptyCheck emptyCheck;

    private static Logger logger = LoggerFactory.getLogger(CheckExpressionService.class);


	@SuppressWarnings("unchecked")
	public DataResult check(CheckRuleTable checkRuleTable, Map<String, Object> dataMap, DataResultParam dataResultParam) {
		DataResult dataResult = singleCheck(checkRuleTable,dataMap,null,dataResultParam);
		for(Map.Entry<String,CheckRuleTable> entry : checkRuleTable.getCheckRuleTableMap().entrySet()){
			List<Map<String, Object>> dataMapList = (List<Map<String, Object>>)dataMap.get(entry.getKey());
			if(dataMapList != null){
				List<DataResult> dataResultList = new ArrayList<DataResult>();
				for(Map<String, Object> map : dataMapList){
					DataResult subDataResult = singleCheck(entry.getValue(),map,dataMapList,dataResultParam);
					dataResultList.add(subDataResult);
					if(!subDataResult.isCheck()){
						dataResult.setCheck(false);
					}
					if(!subDataResult.isForceCheck()){
						dataResult.setForceCheck(false);
					}
					if(subDataResult.isException()){
						dataResult.setException(true);
					}
				}
				dataResult.getDataResultListMap().put(entry.getKey(), dataResultList);
			}
		}
		return dataResult;
	}
	
	private DataResult singleCheck(CheckRuleTable checkRuleTable, Map<String, Object> dataMap, List<Map<String, Object>> dataMapList, DataResultParam dataResultParam){
		DataResult dataResult = new DataResult();
		Map<String,String> strDataMap = new HashMap<String,String>();
		for(Map.Entry<String, Object> entry : dataMap.entrySet()){
			if(entry.getValue() == null){
				strDataMap.put(entry.getKey(), null);
			}
			else{
				strDataMap.put(entry.getKey(), entry.getValue().toString());
			}
		}
		dataResult.setData(strDataMap);
		int k = 0;
		for(CheckRule checkRule : checkRuleTable.getCheckRuleList()){
			CheckRuleResult checkRuleResult = new CheckRuleResult();
			k++;
			if(checkRule.getLevel() > 2){
				continue;
			}
			if(!dataResultParam.isOptional() && checkRule.isOptional()){
				continue;
			}
			if(dataResultParam.isCheckRule()){
				checkRuleResult.setCheckRule(checkRule);
			}
			String[] params = null;
			try{
				if(dataResultParam.isRuleExpression()){
					String ruleExpression = checkRule.getRule();
					ruleExpression = StrUtils.getReplaceString(ruleExpression, dataMap, checkRule.getRuleColumnMap());
					checkRuleResult.setRuleExpression(ruleExpression);
				}
				if(dataResultParam.isDescriptionExpression()){
					String descriptionExpression = checkRule.getDescription();
					if(!StrUtils.isEmpty(descriptionExpression)){
						Map<String,String> descriptionColumnMap = checkRule.getDescriptionColumnMap();
						for(Map.Entry<String,String> entry: descriptionColumnMap.entrySet()){
							String value = StrUtils.getNullToEmptyStringValue(dataMap.get(entry.getValue()));
							descriptionExpression = descriptionExpression.replace(entry.getKey(), value);
						}
						Map<String,String> descriptionCalculateMap = StrUtils.getParamMap(descriptionExpression, "@CALCULATE");
						for(Map.Entry<String,String> entry: descriptionCalculateMap.entrySet()){
							String calculateResult = calculeteExpression.calculate(entry.getKey(),null,null);
							descriptionExpression = descriptionExpression.replace(entry.getKey(), calculateResult);
						}
						checkRuleResult.setDescriptionExpression(descriptionExpression);
					}
				}
				
				String rule = checkRule.getRule();
				Map<String,String> ruleCalculateMap = StrUtils.getParamMap(rule, "@CALCULATE");
				for(Map.Entry<String,String> entry: ruleCalculateMap.entrySet()){
					String calculateResult = calculeteExpression.calculate(entry.getKey(),dataMap,checkRule.getRuleColumnMap());
					rule = rule.replace(entry.getKey(), calculateResult);
				}
				
				String expressionParam = StrUtils.getExpressionParam(rule, "@CHECK");
				if(checkRule.getLevel() > 0){
					expressionParam = "," + expressionParam;
				}
				params = expressionParam.split(",",-1);

				if(params.length == 1){
					ExpressionParser expressionParser = new SpelExpressionParser();
					Expression exp = expressionParser.parseExpression(params[0]);
					if(!exp.getValue(Boolean.class)){
						checkRuleResult.setCheck(false);
						dataResult.setCheck(false);
						if(!checkRule.isOptional()){
							checkRuleResult.setForceCheck(false);
							dataResult.setForceCheck(false);
						}
					}
				}
				else if(params.length > 1){
					if(params.length == 3){
						String[] conditionParams = params[2].split("~",-1);
						if(conditionParams.length == 1){
							ExpressionParser expressionParser = new SpelExpressionParser();
							Expression exp = expressionParser.parseExpression(conditionParams[0]);
							if(!exp.getValue(Boolean.class)){
								continue;
							}
						}
						else{
							Condition condition = applicationContext.getBean(conditionParams[0], Condition.class);
							String[] paramCondition = new String[conditionParams.length -1];
							for(int i=1;i<conditionParams.length;i++){
								paramCondition[i-1] = conditionParams[i];
							}
							CommonCheckResult conditionResult = condition.check(paramCondition,dataMap,checkRule.getRuleColumnMap(),checkRuleTable.getColumnDescriptionMap(),checkRuleTable);
							if(!conditionResult.isSuccess()){
								continue;
							}
							if(dataResultParam.isDescription()){
								checkRuleResult.setIfDescription(conditionResult.getMessage());
							}
						}
					}

					String value = params[0];
					String[] checkBeanNames = params[1].split("\\|",-1);
					/*
					boolean hasEmptyCheck =false;
					for(int i=0;i<checkBeanNames.length;i++){
						if(checkBeanNames[i].equals("emptyCheck")){
							hasEmptyCheck = true;
							break;
						}
					}
					if(!hasEmptyCheck){
						CommonCheckResult commonCheckResult = emptyCheck.check(value,null,dataMap,checkRule.getRuleColumnMap(),checkRuleTable.getColumnDescriptionMap());
						if(!commonCheckResult.isSuccess()){
							continue;
						}
					}
					*/
					for(int i=0;i<checkBeanNames.length;i++){
						String[] checkBeanParams = checkBeanNames[i].split("~",-1);
						CheckMapList checkMapList = null;
						CheckMap checkMap = null;
						Check check = null;
						
						String[] paramCheck = null;
						if(checkBeanParams.length > 1){
							paramCheck = new String[checkBeanParams.length -1];
							for(int j=1;j<checkBeanParams.length;j++){
								paramCheck[j-1] = checkBeanParams[j];
							}
						}
						CommonCheckResult commonCheckResult = null;
						if(checkRule.getLevel() == 0){
							check = applicationContext.getBean(checkBeanParams[0],Check.class);
							commonCheckResult = check.check(value,paramCheck,dataMap,checkRule.getRuleColumnMap(),checkRuleTable.getColumnDescriptionMap(),checkRuleTable);
						}
						else if(checkRule.getLevel() == 1){
							checkMap = applicationContext.getBean(checkBeanParams[0],CheckMap.class);
							commonCheckResult = checkMap.check(paramCheck,dataMap,checkRule.getRuleColumnMap(),checkRuleTable.getColumnDescriptionMap(),checkRuleTable);
						}
						else if(checkRule.getLevel() == 2){
							checkMapList = applicationContext.getBean(checkBeanParams[0],CheckMapList.class);
							commonCheckResult = checkMapList.check(paramCheck,dataMap,dataMapList,checkRule.getRuleColumnMap(),checkRuleTable.getColumnDescriptionMap(),checkRuleTable);
						}

						if(!commonCheckResult.isSuccess()){
							checkRuleResult.setCheck(false);
							dataResult.setCheck(false);
							if(!checkRule.isOptional()){
								checkRuleResult.setForceCheck(false);
								dataResult.setForceCheck(false);
							}
							if(dataResultParam.isDescription()){
								checkRuleResult.getDescriptionList().add(commonCheckResult.getMessage());
							}
						}
					}		
				}
			}
			catch(Exception ex){
				checkRuleResult.setException(true);
				if(ex instanceof NullPointerException){
                    checkRuleResult.setExceptionDetail("NullPointerException");
                }
                else{
                    checkRuleResult.setExceptionDetail(ex.getMessage());
                }
				checkRuleResult.setCheck(false);
				checkRuleResult.setForceCheck(false);
				dataResult.setException(true);
				dataResult.setCheck(false);
				dataResult.setForceCheck(false);
			}

            if(!StrUtils.isEmpty(checkRuleResult.getDescriptionExpression())){
                checkRuleResult.setIfDescription("");
                checkRuleResult.getDescriptionList().clear();
                checkRuleResult.getDescriptionList().add(checkRuleResult.getDescriptionExpression());
            }
			
			if(checkRuleResult.isCheck()){
                checkRuleResult.setIfDescription("");
				checkRuleResult.getDescriptionList().clear();
			}
			
			if(checkRule.getLevel() == 0){
				ColumnResult columnResult = dataResult.getColumnResultMap().get(checkRule.getMatch());
				if(columnResult == null){
					columnResult = new ColumnResult();
					columnResult.setColumnDescription(checkRuleTable.getColumnDescriptionMap().get(checkRule.getMatch()));
					columnResult.setValue(StrUtils.getNullToEmptyStringValue(dataMap.get(checkRule.getMatch())));
				}
				if(dataResultParam.isDescription()){
					if(!StrUtils.isEmpty(checkRule.getMatch())){
                        if(!StrUtils.isEmpty(checkRuleResult.getIfDescription())){
                        	columnResult.setDescription(checkRuleResult.getIfDescription());
						}
						for(String checkRuleDescription : checkRuleResult.getDescriptionList()){
							if(StrUtils.isEmpty(columnResult.getDescription())){
								columnResult.setDescription(checkRuleDescription);
							}
							else{
								columnResult.setDescription(columnResult.getDescription() + "，" + checkRuleDescription);
							}
						}
					}
				}
				
				if(!checkRuleResult.isCheck()){
					columnResult.setCheck(false);
				}
				if(!checkRuleResult.isForceCheck()){
					columnResult.setForceCheck(false);
				}
				if(checkRuleResult.isException()){
					columnResult.setException(true);
                    columnResult.setDescription(checkRuleResult.getExceptionDetail());
				}

				if(dataResultParam.isColumnResultOnlyFalse()){
					if(!checkRuleResult.isCheck()){
						columnResult.getCheckRuleResult().add(checkRuleResult);
					}
				}
				else{
					columnResult.getCheckRuleResult().add(checkRuleResult);
				}
				
				if(dataResultParam.isColumnResultOnlyFalse()){
					if(!columnResult.isCheck()){
						dataResult.getColumnResultMap().put(checkRule.getMatch(), columnResult);
					}
				}
				else{
					dataResult.getColumnResultMap().put(checkRule.getMatch(), columnResult);
				}
				
			}
			else{
				dataResult.getLineResultList().add(checkRuleResult);
			}
		}

		return dataResult;
	}

}
