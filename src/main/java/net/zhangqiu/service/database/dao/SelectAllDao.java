package net.zhangqiu.service.database.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.zhangqiu.service.database.entity.EntityColumn;
import net.zhangqiu.service.database.entity.EntityTable;
import net.zhangqiu.utils.BooleanClass;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class SelectAllDao extends BaseDao{

	public List<Map<String,Object>> query(String strProjectCode,String datasourceName,String tableName,String sql){
		String entityTableKey = entityContext.getEntityTableKey(strProjectCode,datasourceName, tableName);
		final EntityTable entityTable = entityContext.getEntityTableMap().get(entityTableKey);
		final Map<String,String> columnMap = new HashMap<String,String>();
		final BooleanClass fisrtRow = new BooleanClass();
		fisrtRow.setBooleanValue(true);
		JdbcTemplate jdbcTemplate = getJdbcTemplate(strProjectCode,datasourceName);
		return jdbcTemplate.query(sql, new RowMapper<Map<String,Object>>(){
			public Map<String, Object> mapRow(ResultSet arg0, int arg1) throws SQLException {
				int columnCount = arg0.getMetaData().getColumnCount();
				Map<String, Object> map = new HashMap<String,Object>();
				if(fisrtRow.isBooleanValue() && entityTable != null){
					for(int i=0;i<columnCount;i++){
						for(EntityColumn entityColumn: entityTable.getEntityColumnList()){
							if(arg0.getMetaData().getColumnName(i + 1).toUpperCase().equals(entityColumn.getColumnName().toUpperCase())){
								columnMap.put(arg0.getMetaData().getColumnName(i + 1), entityColumn.getColumnName());
							}
						}
					}
				}
				
				fisrtRow.setBooleanValue(false);

				if(entityTable != null){
					for(int i=0;i<columnCount;i++){
					    if(columnMap.get(arg0.getMetaData().getColumnName(i + 1)) != null){
                            map.put(columnMap.get(arg0.getMetaData().getColumnName(i + 1)), arg0.getObject(i + 1));
                        }
					}
				}
				else{
					for(int i=0;i<columnCount;i++){
						map.put(arg0.getMetaData().getColumnName(i + 1), arg0.getObject(i + 1));
					}
				}
				
				return map;
			}
		});
	}
	
	public int queryForInt(String strProjectCode,String datasourceName,String tableName,String sql){
		JdbcTemplate jdbcTemplate = getJdbcTemplate(strProjectCode,datasourceName);
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}
}
