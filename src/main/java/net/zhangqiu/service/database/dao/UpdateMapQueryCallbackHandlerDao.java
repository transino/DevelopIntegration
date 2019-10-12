package net.zhangqiu.service.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.zhangqiu.service.database.entity.EntityUtils;
import net.zhangqiu.service.database.interfaces.MapResultSetHandler;
import net.zhangqiu.service.database.interfaces.QueryForMapDataHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

@Repository
public class UpdateMapQueryCallbackHandlerDao extends BaseDao implements QueryForMapDataHandler{

    private static Logger logger = LoggerFactory.getLogger(UpdateMapQueryCallbackHandlerDao.class);

	class MapQueryUpdateCallbackHandler{
		private MapResultSetHandler mapDataHandler;
		public MapResultSetHandler getMapDataHandler() {
			return mapDataHandler;
		}
		public void setMapDataHandler(MapResultSetHandler mapDataHandler) {
			this.mapDataHandler = mapDataHandler;
		}
		
		private class QueryForRowCallbackHandler implements RowCallbackHandler{
			public void processRow(ResultSet arg0) throws SQLException {
				Map<String,Object> map = new HashMap<String,Object>();
				int columnCount = arg0.getMetaData().getColumnCount();
				for(int i=0;i<columnCount;i++){
					map.put(arg0.getMetaData().getColumnName(i + 1), arg0.getObject(i + 1));
				}
				try{
					mapDataHandler.handleData(map,arg0);
				}
				catch (Exception ex) {
                    logger.error("",ex);
				}
			}
		}
		
		private class QueryForPreparedStatementCreator implements PreparedStatementCreator{
			public QueryForPreparedStatementCreator(String sql,Object[] args,String strDatabaseType){
				this.sql = sql;
				//this.args = args;
				this.strDatabaseType = strDatabaseType;
			}
			private String sql;
			//private Object[] args;
			private String strDatabaseType;
			
			public PreparedStatement createPreparedStatement(Connection con)throws SQLException {
				PreparedStatement preparedStatement = con.prepareStatement(sql,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
				if(this.strDatabaseType.equals(EntityUtils.Database_Mysql)){
					preparedStatement.setFetchSize(Integer.MIN_VALUE);
				}
				return preparedStatement;
			}
		}
		
		public void queryForHandler(String strProjectCode,String datasource,String sql,Object[] args) throws Exception{
			JdbcTemplate jdbcTemplate = getJdbcTemplate(strProjectCode,datasource);
			String strDatabaseType = entityContext.getProjectEntityDatasourceMap().get(strProjectCode).get(datasource).getStrDatabaseType();
			QueryForPreparedStatementCreator queryForPreparedStatementCreator = new QueryForPreparedStatementCreator(sql,args,strDatabaseType);
			jdbcTemplate.query(queryForPreparedStatementCreator, new QueryForRowCallbackHandler());
		}
	}
	
	public void queryForHandler(MapResultSetHandler mapResultSetHandler, String strProjectCode, String datasourceName, String sql, Object[] args) throws Exception{
		MapQueryUpdateCallbackHandler mapQueryUpdateCallbackHandler = new MapQueryUpdateCallbackHandler();
		mapQueryUpdateCallbackHandler.setMapDataHandler(mapResultSetHandler);
		mapQueryUpdateCallbackHandler.queryForHandler(strProjectCode,datasourceName, sql, args);
	}
}
