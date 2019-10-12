package net.zhangqiu.service.database.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.zhangqiu.service.database.entity.EntityColumn;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.EntityTable;
import net.zhangqiu.utils.BooleanClass;

import net.zhangqiu.utils.StrUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

@Repository
public class SelectByIdDao extends BaseDao{
	
	@Autowired
    EntityContext entityContext;
	
	public Map<String,Object> query(String strProjectCode,String datasourceName,String tableName,String sql,Object[] objects){
		String entityTableKey = entityContext.getEntityTableKey(strProjectCode,datasourceName, tableName);
		final EntityTable entityTable = entityContext.getEntityTableMap().get(entityTableKey);
		final BooleanClass hasEntityTable = new BooleanClass();
		if(entityTable == null){
			hasEntityTable.setBooleanValue(false);
		}
		else{
			hasEntityTable.setBooleanValue(true);
		}
		
		final Map<String,Object> map = new HashMap<String,Object>();
		JdbcTemplate jdbcTemplate = getJdbcTemplate(strProjectCode,datasourceName);
		jdbcTemplate.query(sql, objects, new RowCallbackHandler(){
			public void processRow(ResultSet arg0) throws SQLException {
            int columnCount = arg0.getMetaData().getColumnCount();
            for(int i=0;i<columnCount;i++){
                String strColumnName = null;
                if(hasEntityTable.isBooleanValue()){
                    for(EntityColumn entityColumn: entityTable.getEntityColumnList()){
                        if(arg0.getMetaData().getColumnName(i + 1).toUpperCase().equals(entityColumn.getColumnName().toUpperCase())){
                            strColumnName = entityColumn.getColumnName();
                            break;
                        }
                    }
                }
                else{
                    strColumnName = arg0.getMetaData().getColumnName(i + 1);
                }
                if(!StrUtils.isEmpty(strColumnName)){
                    map.put(strColumnName, arg0.getObject(i + 1));
                }
            }
			}
			
		});
		return map;
	}
}
