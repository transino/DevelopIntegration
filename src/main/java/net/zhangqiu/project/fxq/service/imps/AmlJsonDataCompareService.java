package net.zhangqiu.project.fxq.service.imps;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.EntityTable;
import net.zhangqiu.service.database.entity.EntityUtils;
import net.zhangqiu.service.database.entity.param.EntityDataParam;
import net.zhangqiu.service.entity.result.CommonServiceResult;
import net.zhangqiu.utils.JsonUtils;
import net.zhangqiu.utils.StrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Map;

@Service
public class AmlJsonDataCompareService {

    private static Logger logger = LoggerFactory.getLogger(AmlJsonDataCompareService.class);

    @Autowired
    EnvironmentContext environmentContext;
    @Autowired
    EntityContext entityContext;

    public String handleService(String indentification, String strProjectCode,String datasourceName,String preJsonString,String afterJsonString, String encoding) throws Exception {
        try{
            if(StrUtils.isEmpty(encoding)){
                encoding = environmentContext.getEncoding();
            }
            preJsonString = URLDecoder.decode(preJsonString, encoding);
            EntityDataParam preEntityData = EntityUtils.convertToEntityData(preJsonString);
            preEntityData.setData(entityContext.getConvertToStandardMap(strProjectCode,datasourceName, preEntityData));
            String entityTableKey = entityContext.getEntityTableKey(strProjectCode,datasourceName, preEntityData.getTableName());
            EntityTable entityTable  = entityContext.getEntityTableMap().get(entityTableKey);
            if(entityTable == null){
                CommonServiceResult commonServiceResult = new CommonServiceResult();
                commonServiceResult.setSuccess(false);
                commonServiceResult.setMessage("表名Key（"+entityTableKey+"）不存在，请确认是否在*_table.json文件中定义，区分大小写");
                return JsonUtils.objectToString(commonServiceResult);
            }

            String result = "";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(environmentContext.getDatetimeFormt());
            if(entityTable.getKeyNameColumnList().size() > 0){
                for(String keyName : entityTable.getKeyNameColumnList()){
                    try{

                        if(StrUtils.isEmpty(entityTable.getEntityColumnMap().get(keyName).getShowColumn())){
                            result += "，" + entityTable.getEntityColumnMap().get(keyName).getColumnDescription() + "：" + StrUtils.getNullToEmptyStringValue(preEntityData.getData().get(keyName),simpleDateFormat);
                        }
                        else{
                            if(entityTable.getEntityColumnMap().get(keyName).getShowColumn().startsWith("const:")){
                                String[] fields = entityTable.getEntityColumnMap().get(keyName).getShowColumn().split(":");
                                String showField = fields[1];
                                result += "，" + entityTable.getEntityColumnMap().get(keyName).getColumnDescription() + "：" + entityContext.getConstMap().get(strProjectCode).get(showField).get(preEntityData.getData().get(keyName));
                            }
                            else if(entityTable.getEntityColumnMap().get(keyName).getShowColumn().startsWith("table:")){
                                String[] fields = entityTable.getEntityColumnMap().get(keyName).getShowColumn().split(":");
                                String[] showFields = fields[1].split("\\.",-1);
                                String defalustDatasourceName = entityContext.getDefaultDatasourceNameMap().get(strProjectCode);
                                String key = entityContext.getEntityTableKey(strProjectCode,defalustDatasourceName,showFields[0]);
                                result += "，" + entityTable.getEntityColumnMap().get(keyName).getColumnDescription() + "：" +  entityContext.getCachedataKeyMap().get(key).get(preEntityData.getData().get(keyName)).get(showFields[1]);
                            }
                        }
                    }
                    catch (Exception ex){
                        logger.error("",ex);
                        result += "，"+keyName+"字段获取异常";
                    }
                }
                result = result.substring(1);
                result = "（" + result + "）";
            }
            String fieldResult = "";
            if(!StrUtils.isEmpty(afterJsonString)){
                afterJsonString = URLDecoder.decode(afterJsonString, encoding);
                EntityDataParam afterEntityData = EntityUtils.convertToEntityData(afterJsonString);
                afterEntityData.setData(entityContext.getConvertToStandardMap(strProjectCode,datasourceName, afterEntityData));
                for(Map.Entry<String,Object> preEntry: preEntityData.getData().entrySet()){
                    if(!StrUtils.isStrEqual(preEntry.getValue(),afterEntityData.getData().get(preEntry.getKey()))){
                        try{
                            if(StrUtils.isEmpty(entityTable.getEntityColumnMap().get(preEntry.getKey()).getShowColumn())){
                                fieldResult += "，" + entityTable.getEntityColumnMap().get(preEntry.getKey()).getColumnDescription() +  "：" + StrUtils.getNullToEmptyStringValue(preEntry.getValue(),simpleDateFormat) + "-->"  + StrUtils.getNullToEmptyStringValue(afterEntityData.getData().get(preEntry.getKey()),simpleDateFormat);
                            }
                            else{
                                if(entityTable.getEntityColumnMap().get(preEntry.getKey()).getShowColumn().startsWith("const:")){
                                    String[] fields = entityTable.getEntityColumnMap().get(preEntry.getKey()).getShowColumn().split(":");
                                    String showField = fields[1];
                                    String preShowValue = "";
                                    if(!StrUtils.isEmptyObject(preEntry.getValue())){
                                        String[] values = preEntry.getValue().toString().split(",",-1);
                                        if(values.length > 1){
                                            preShowValue = "[";
                                            for (String value : values){
                                                preShowValue += entityContext.getConstMap().get(strProjectCode).get(showField).get(value) + ",";
                                            }
                                            preShowValue = preShowValue.substring(0,preShowValue.length() - 1);
                                            preShowValue += "]";
                                        }
                                        else{
                                            preShowValue = entityContext.getConstMap().get(strProjectCode).get(showField).get(preEntry.getValue());
                                        }
                                    }
                                    String afterShowValue = "";
                                    if(!StrUtils.isEmptyObject(afterEntityData.getData().get(preEntry.getKey()))){
                                        String[] values = afterEntityData.getData().get(preEntry.getKey()).toString().split(",",-1);
                                        if(values.length > 1){
                                            afterShowValue = "[";
                                            for (String value : values){
                                                afterShowValue += entityContext.getConstMap().get(strProjectCode).get(showField).get(value) + ",";
                                            }
                                            afterShowValue = afterShowValue.substring(0,afterShowValue.length() - 1);
                                            afterShowValue += "]";
                                        }
                                        else{
                                            afterShowValue = entityContext.getConstMap().get(strProjectCode).get(showField).get(afterEntityData.getData().get(preEntry.getKey()));
                                        }
                                    }

                                    fieldResult += "，" + entityTable.getEntityColumnMap().get(preEntry.getKey()).getColumnDescription() +  "：" + preShowValue + "-->"  + afterShowValue;
                                }
                                else if(entityTable.getEntityColumnMap().get(preEntry.getKey()).getShowColumn().startsWith("table:")){
                                    String[] fields = entityTable.getEntityColumnMap().get(preEntry.getKey()).getShowColumn().split(":");
                                    String[] showFields = fields[1].split("\\.",-1);
                                    String defalustDatasourceName = entityContext.getDefaultDatasourceNameMap().get(strProjectCode);
                                    String key = entityContext.getEntityTableKey(strProjectCode,defalustDatasourceName,showFields[0]);
                                    String preShowValue = "";
                                    if(!StrUtils.isEmptyObject(preEntry.getValue())){
                                        String[] values = preEntry.getValue().toString().split(",",-1);
                                        if(values.length > 1){
                                            preShowValue = "[";
                                            for (String value : values){
                                                preShowValue += entityContext.getCachedataKeyMap().get(key).get(value).get(showFields[1]).toString() + ",";
                                            }
                                            preShowValue = preShowValue.substring(0,preShowValue.length() - 1);
                                            preShowValue += "]";
                                        }
                                        else{
                                            preShowValue = entityContext.getCachedataKeyMap().get(key).get(preEntry.getValue()).get(showFields[1]).toString();
                                        }
                                    }
                                    String afterShowValue = "";
                                    if(!StrUtils.isEmptyObject(afterEntityData.getData().get(preEntry.getKey()))){
                                        String[] values = afterEntityData.getData().get(preEntry.getKey()).toString().split(",",-1);
                                        if(values.length > 1){
                                            afterShowValue = "[";
                                            for (String value : values){
                                                afterShowValue += entityContext.getCachedataKeyMap().get(key).get(value).get(showFields[1]) + ",";
                                            }
                                            afterShowValue = afterShowValue.substring(0,afterShowValue.length() - 1);
                                            afterShowValue += "]";
                                        }
                                        else{
                                            afterShowValue = entityContext.getCachedataKeyMap().get(key).get(afterEntityData.getData().get(preEntry.getKey())).get(showFields[1]).toString();
                                        }
                                    }
                                    fieldResult += "，" + entityTable.getEntityColumnMap().get(preEntry.getKey()).getColumnDescription() +  "：" + preShowValue + "-->"  + afterShowValue;
                                }
                            }
                        }
                        catch (Exception ex){
                            //logger.error("",ex);
                            result += "，"+preEntry.getKey()+"字段获取异常";
                        }
                    }
                }
                if(!StrUtils.isEmpty(fieldResult)){
                    fieldResult = fieldResult.substring(1);
                }
            }
            if(!StrUtils.isEmpty(fieldResult)){
                if(!StrUtils.isEmpty(result)){
                    result = result + "：";
                }
                result = result + fieldResult;
            }
            return result;
        }
        catch(Exception ex){
            logger.error("",ex);
            CommonServiceResult commonServiceResult = new CommonServiceResult();
            commonServiceResult.setSuccess(false);
            commonServiceResult.setMessage(ex.getMessage());
            return JsonUtils.objectToString(commonServiceResult);
        }
    }

}
