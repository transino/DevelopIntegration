package net.zhangqiu.utils;

import java.util.Date;
import java.util.Map;

import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

public class JsonUtils {

	public static Date MorphDynaBeanToDate(Object objectMorphDynaBean){
		if(objectMorphDynaBean instanceof JSONNull){
			return null;
		}
		MorphDynaBean morphDynaBean = (MorphDynaBean)objectMorphDynaBean;
		Date date= new Date((Long)morphDynaBean.get("time"));
		return date;
	}
	
	public static Object jsonToObject(Object json,Class<?> objectClass,Map<String,Class<?>> map){
		return JSONObject.toBean((JSONObject)json,objectClass,map);
	}
	
	public static Object jsonToObject(Object json,Class<?> objectClass){
		return JSONObject.toBean((JSONObject)json,objectClass);
	}
	
	public static String jsonToString(Object json){
		return ((JSONObject)json).toString();
	}
	
	public static Object stringToJson(String string){
		return JSONObject.fromObject(string);
	}
	
	public static Object stringToObject(String string,Class<?> objectClass){
		JSONObject jsonObject = JSONObject.fromObject(string);
		return JSONObject.toBean(jsonObject,objectClass);
	}

	public static String objectToString(Object object){
		return JSONObject.fromObject(object).toString();
	}
	
	public static Object objectToJson(Object object){
		return JSONObject.fromObject(object);
	}
	
	public static Object mapToJson(Map<String,Object> map){
		return JSONObject.fromObject(map);
	}
	
	public static String mapToString(Map<String,Object> map){
		return JSONObject.fromObject(map).toString();
	} 
}
