package net.zhangqiu.service.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.zhangqiu.service.database.entity.EntityColumn;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.EntityTable;
import net.zhangqiu.service.database.entity.SnowFlakeGenerator;
import net.zhangqiu.utils.StrUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataConvertUtils {

    @Autowired
    SnowFlakeGenerator snowFlakeGenerator;

    @Autowired
    EntityContext entityContext;

	public List<Object[]> splitDataListToObjectList(List<String> dataList,String regex){
		List<Object[]> objectList = new ArrayList<Object[]>();
		for(String string : dataList){
			Object[] objects = string.split(regex,-1);
			objectList.add(objects);
		}
		return objectList;
	}
	
	public List<Map<String,Object>> splitDataListToMapList(List<String> dataList,String regex,EntityTable entityTable) throws Exception{
		List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
        int line =0;
		for(String string : dataList){
            Map<String,Object> map = new HashMap<String,Object>();
            String[] objects = string.split(regex,-1);
            for(int i=0;i<objects.length;i++){
                if(entityTable.isAutoGenerateId()){
                    if(entityTable.getIdColumnSet().contains(entityTable.getEntityColumnList().get(i).getColumnName())){
                        if(StrUtils.isEmpty(objects[i])){
                            objects[i] = String.valueOf(snowFlakeGenerator.nextId());
                        }
                    }
                }
                try{
                    map.put(entityTable.getEntityColumnList().get(i).getColumnName(),  entityContext.convertToType(objects[i],entityTable.getEntityColumnList().get(i).getColumnType()));
                }
                catch (Exception ex){
                    int column = i + 1;
                    throw new Exception("行:" + String.valueOf(line+1) + ",列:" + column +  ",字段名:" + entityTable.getEntityColumnList().get(i).getColumnName() + "," + ex.getMessage());
                }

            }
            mapList.add(map);

            line++;
		}
		return mapList;
	}
	
	public List<Object[]> mapListToObjectList(List<Map<String,Object>> mapList,EntityTable entityTable){
		List<Object[]> objectList = new ArrayList<Object[]>();
		for(Map<String,Object> map : mapList){
			List<Object> objects = new ArrayList<Object>();
			for(int i=0;i<entityTable.getEntityColumnList().size();i++){
				objects.add(map.get(entityTable.getEntityColumnList().get(i).getColumnName()));
			}
			objectList.add(objects.toArray());
		}
		return objectList;
	}
	
	public List<Map<String,Object>> mapListToMapList(List<Map<String,Object>> sourceList,EntityTable entityTable){
		List<Map<String,Object>> resultMapList = new ArrayList<Map<String,Object>>();
		for(Map<String,Object> map : sourceList){
			Map<String,Object> resultMap = new HashMap<String,Object>();
			for(EntityColumn entityColumn: entityTable.getEntityColumnList()){
				String columnName = entityColumn.getColumnName();
				resultMap.put(columnName, map.get(columnName));
			}
			resultMapList.add(resultMap);
		}
		return resultMapList;
	}
}
