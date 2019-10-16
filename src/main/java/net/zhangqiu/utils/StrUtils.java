package net.zhangqiu.utils;

import java.text.SimpleDateFormat;
import java.util.*;

public class StrUtils {
	
	public static boolean isEmpty(String string){
		if(string == null || string.equals("")){
			return true;
		}
		return false;
	}

    public static boolean isEmptyObject(Object string){
        if(string == null || string.equals("")){
            return true;
        }
        return false;
    }

    public static boolean isStrEqual(Object object1,Object object2){
        if(object1 != null && object2 == null){
            if(object1.toString().equals("")){
                return true;
            }
            else{
                return false;
            }
        }
        else if(object1 == null && object2 != null){
            if(object2.toString().equals("")){
                return true;
            }
            else{
                return false;
            }
        }
        else if(object1 != null && object2 != null){
            return object1.toString().equals(object2.toString());
        }
        else{
            return true;
        }
    }
	
	public static String getExpressionParam(String expression,String paramName){
		return expression.substring(expression.indexOf("(") + 1,expression.lastIndexOf(")"));
	}
	
	public static Map<String,Object> mapDataToUpperCase(Map<String,Object> data){
		Map<String,Object> upperMap = new HashMap<String,Object>();
		for(Map.Entry<String,Object> entry : data.entrySet()){
			upperMap.put(entry.getKey().toUpperCase(), entry.getValue());
		}
		return upperMap;
	}
	
	public static List<Map<String,Object>> listMapDataToUpperCase(List<Map<String,Object>> datalist){
		List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
		for(Map<String,Object> data : datalist){
			mapList.add(mapDataToUpperCase(data));
		}
		return mapList;
	}
	
	public static String paramToUpperCase(String expression,String paramName){
		if(isEmpty(expression)){
			return "";
		}
		else{
			String result = expression;
			while(expression.indexOf(paramName) > -1){
				int startIndex = expression.indexOf(paramName)+paramName.length()+1;
				int endIndex = 0;
				int count = 0;
				for(int i= startIndex;i<expression.length();i++){
					if(expression.charAt(i) == '('){
						count ++;
					}
					else if(expression.charAt(i) == ')'){
						if(count == 0){
							endIndex = i;
							break;
						}
						count--;
					}
				}
				String param = expression.substring(startIndex,endIndex);
				String key = paramName+"("+param+")";
				result = result.replace(key, key.toUpperCase());
				expression = expression.replace(key, "");
			}
			return result;
		}
	}
	
	public static Map<String,String> getParamMap(String expression,String paramName){
		Map<String,String> paramMap = new HashMap<String,String>();
		while(expression.indexOf(paramName) > -1){
			int startIndex = expression.indexOf(paramName)+paramName.length()+1;
			int endIndex = 0;
			int count = 0;
			for(int i= startIndex;i<expression.length();i++){
				if(expression.charAt(i) == '('){
					count ++;
				}
				else if(expression.charAt(i) == ')'){
					if(count == 0){
						endIndex = i;
						break;
					}
					count--;
				}
			}
			String param = expression.substring(startIndex,endIndex);
			String key = paramName+"("+param+")";
			paramMap.put(key, param);
			expression = expression.replace(key, "");
		}
		return paramMap;
	}
	
	public static String getFirstParam(String expression,String paramName){
		String firstParam = "";
		while(expression.indexOf(paramName) > -1){
			int i = expression.indexOf(paramName);
			int j = expression.indexOf(")", i+paramName.length()+1);
			
			firstParam = expression.substring(i+paramName.length()+1,j);
			break;
		}
		return firstParam;
	}
	
	public static String getReplaceString(String expression,Map<String, Object> dataMap,Map<String,String> ruleColumnMap){
		for(Map.Entry<String,String> entry: ruleColumnMap.entrySet()){
			String value = "";
			Object objectValue = dataMap.get(entry.getValue());
			if(objectValue != null){
				value = objectValue.toString();
			}
			expression = expression.replace(entry.getKey(), value);
		}
		return expression;
	}
	
	public static String getNullToEmptyStringValue(Object value){
		if(value != null){
			return  value.toString();
		}
		else{
			return "";
		}
	}

    public static String getNullToEmptyStringValue(Object value,SimpleDateFormat simpleDateFormat){
        if(value != null){
            if(value.getClass().equals(Date.class)){
                String result = simpleDateFormat.format(value);
                if(result.endsWith(" 00:00:00")){
                    result = result.substring(0,result.length() -9);
                }
                return result;
            }
            return  value.toString();
        }
        else{
            return "";
        }
    }
	
	public static Object getNullToEmptyObjectValue(Object value){
		if(value != null){
			return  value;
		}
		else{
			return "";
		}
	}
	
	public static String getMapInnerValue(Map<String,String> ruleColumnMap ,Map<String, Object> dataMap, String param){
		String result = param;
		String key = ruleColumnMap.get(param);
		if(key != null){
			Object data = dataMap.get(key);
			result = getNullToEmptyStringValue(data);
		}
		return result;
	}
	
	public static String getDescription(Map<String,String> ruleColumnMap,Map<String, Object> dataMap,Map<String,String> columnDescriptionMap,String param,Map<String,Map<String,String>> columnValueMap){
	    String valueDescription = "";
		String key = ruleColumnMap.get(param);
		if(key != null){
			Object data = dataMap.get(key);
			String value = getNullToEmptyStringValue(data);
			if(columnValueMap != null && columnValueMap.containsKey(key) && columnValueMap.get(key).containsKey(value)){
			    String[] values = value.split(",",-1);
			    if(values.length > 1){
			        String columnValue = "[";
			        String keyValue = "[";
			        for(String str : values){
                        columnValue += columnValueMap.get(key).get(str) + ",";;
                        keyValue += str + ",";
                    }
                    columnValue = columnValue.substring(0,columnValue.length() -1);
                    keyValue = keyValue.substring(0,keyValue.length()-1);
                    columnValue += "]";
                    keyValue += "]";

                    valueDescription = columnDescriptionMap.get(key) + ":" + columnValue + "(key:" + keyValue + ")";
                }
                else{
                    valueDescription = columnDescriptionMap.get(key) + ":" + columnValueMap.get(key).get(value) + "(key:" + value + ")";
                }
            }
            else{
                valueDescription = columnDescriptionMap.get(key) + ":" + value;
            }
		}
		else{
            valueDescription = param;
		}
		return valueDescription;
	}

	public static boolean isEqual(Object value1,Object value2){
		if(value1 == null && value2 == null){
			return true;
		}
		else if(value1 == null && value2 != null){
			return false;
		}
		else if(value1 != null && value2 == null){
			return false;
		}
		else{
			return value1.equals(value2);
		}
	}
	
}
