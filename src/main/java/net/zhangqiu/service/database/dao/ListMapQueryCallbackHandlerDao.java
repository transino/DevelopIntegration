package net.zhangqiu.service.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import net.zhangqiu.service.database.entity.EntityColumn;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.EntityTable;
import net.zhangqiu.service.database.entity.EntityUtils;
import net.zhangqiu.service.database.interfaces.ListMapHandler;
import net.zhangqiu.service.database.interfaces.QueryForListMapDataHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

@Repository
public class ListMapQueryCallbackHandlerDao extends BaseDao implements QueryForListMapDataHandler {

    private static Logger logger = LoggerFactory.getLogger(ListMapQueryCallbackHandlerDao.class);

	@Autowired
    EntityContext entityContext;
	
	class MapQueryCallbackHandler{
		private ListMapHandler mapDataHandler;
		public ListMapHandler getMapDataHandler() {
			return mapDataHandler;
		}
		public void setMapDataHandler(ListMapHandler mapDataHandler) {
			this.mapDataHandler = mapDataHandler;
		}

		private int cacheLine;
		public void setCacheLine(int cacheLine) {
			this.cacheLine = cacheLine;
		}
		public int getCacheLine() {
			return cacheLine;
		}
		
		private List<Map<String,Object>> mapList;
		private int count;
		

		private class QueryForRowCallbackHandler implements RowCallbackHandler{
			private EntityTable entityTable;
			QueryForRowCallbackHandler(EntityTable entityTable){
				this.entityTable = entityTable;
			}
			
			private boolean firstRow = true;
			Map<String,String> columnMap = new HashMap<String,String>();
			
			public void processRow(ResultSet arg0) throws SQLException {
				Map<String,Object> map = new HashMap<String,Object>();
				int columnCount = arg0.getMetaData().getColumnCount();
				
				if(firstRow && entityTable != null){
					for(int i=0;i<columnCount;i++){
						for(EntityColumn entityColumn: entityTable.getEntityColumnList()){
							if(arg0.getMetaData().getColumnName(i + 1).toUpperCase().equals(entityColumn.getColumnName().toUpperCase())){
								columnMap.put(arg0.getMetaData().getColumnName(i + 1), entityColumn.getColumnName());
							}
						}
					}
				}
				firstRow = false;

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
				
				mapList.add(map);
				count++;
				try{
					if(count == cacheLine){
						mapDataHandler.handleData(mapList);
						mapList.clear();
						count=0;
					}
				}
				catch (Exception ex) {
					//异常需统一处理
                    logger.error("",ex);
                    throw new SQLException(ex.getMessage());
				}
			}
		}
		
		private class QueryForPreparedStatementCreator implements PreparedStatementCreator{
			public QueryForPreparedStatementCreator(String sql,Object[] args,String strDatabaseType){
				this.sql = sql;
				this.args = args;
				this.strDatabaseType = strDatabaseType;
			}
			private String sql;
			private Object[] args;
			private String strDatabaseType;
			
			public PreparedStatement createPreparedStatement(Connection con)throws SQLException {
			    /*
				PreparedStatement preparedStatement = con.prepareStatement(sql,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
				if(this.strDatabaseType.equals(EntityUtils.Database_Mysql)){
					preparedStatement.setFetchSize(Integer.MIN_VALUE);
				}
				*/
                PreparedStatement preparedStatement = con.prepareStatement(sql);
				if(args != null && args.length > 0){
				    int i =1;
				    for(Object object : args){
                        Object value = object;
                        if(value instanceof java.util.Date){
                            value = new java.sql.Timestamp(((java.util.Date)value).getTime());
                        }
                        preparedStatement.setObject(i,value);
                        i++;
                    }
                }
				return preparedStatement;
			}
		}
		
		public void queryForHandler(String strProjectCode,String datasource,String tableName,String sql,Object[] args) throws Exception{
			
			String entityTableKey = entityContext.getEntityTableKey(strProjectCode,datasource, tableName);
			EntityTable entityTable = entityContext.getEntityTableMap().get(entityTableKey);
			mapList = new ArrayList<Map<String,Object>>();
			count = 0;
			JdbcTemplate jdbcTemplate = getJdbcTemplate(strProjectCode,datasource);
			String strDatabaseType = entityContext.getProjectEntityDatasourceMap().get(strProjectCode).get(datasource).getStrDatabaseType();
			QueryForPreparedStatementCreator queryForPreparedStatementCreator = new QueryForPreparedStatementCreator(sql,args,strDatabaseType);
			jdbcTemplate.query(queryForPreparedStatementCreator, new QueryForRowCallbackHandler(entityTable));
			if(mapList.size() > 0){
				mapDataHandler.handleData(mapList);
			}
		}
	}
	
	public void queryForHandler(ListMapHandler mapDataHandler, int cacheLine, String strProjectCode, String datasource, String tableName, String sql, Object[] args) throws Exception{
		MapQueryCallbackHandler mapQueryCallbackHandler = new MapQueryCallbackHandler();
		mapQueryCallbackHandler.setCacheLine(cacheLine);
		mapQueryCallbackHandler.setMapDataHandler(mapDataHandler);
		mapQueryCallbackHandler.queryForHandler(strProjectCode,datasource, tableName,sql, args);
	}
}
