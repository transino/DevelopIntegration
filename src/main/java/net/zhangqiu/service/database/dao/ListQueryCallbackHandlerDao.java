package net.zhangqiu.service.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.zhangqiu.service.database.entity.EntityUtils;
import net.zhangqiu.service.database.interfaces.ListDataHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

@Repository
public class ListQueryCallbackHandlerDao extends BaseDao {

    private static Logger logger = LoggerFactory.getLogger(ListQueryCallbackHandlerDao.class);

	class ListQueryCallbackHandler{
		private ListDataHandler listDataHandler;
		public void setListDataHandler(ListDataHandler listDataHandler) {
			this.listDataHandler = listDataHandler;
		}
		public ListDataHandler getListDataHandler() {
			return listDataHandler;
		}
		private int cacheLine;
		public void setCacheLine(int cacheLine) {
			this.cacheLine = cacheLine;
		}
		public int getCacheLine() {
			return cacheLine;
		}
		
		private List<List<Object>> dataList;
		private int count;
		
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
				PreparedStatement preparedStatement = con.prepareStatement(sql,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
				if(this.strDatabaseType.equals(EntityUtils.Database_Mysql)){
					preparedStatement.setFetchSize(Integer.MIN_VALUE);
				}
				return preparedStatement;
			}
		}

		
		public void queryForHandler(String strProjectCode,String datasource,String sql,Object[] args) throws Exception{
			dataList = new ArrayList<List<Object>>();
			count = 0;
			JdbcTemplate jdbcTemplate = getJdbcTemplate(strProjectCode,datasource);
			String strDatabaseType = entityContext.getProjectEntityDatasourceMap().get(strProjectCode).get(datasource).getStrDatabaseType();
			QueryForPreparedStatementCreator queryForPreparedStatementCreator = new QueryForPreparedStatementCreator(sql,args,strDatabaseType);
			jdbcTemplate.query(queryForPreparedStatementCreator, new QueryForRowCallbackHandler());
			if(dataList.size()>0){
				listDataHandler.handleData(dataList);
			}
		}

		private class QueryForRowCallbackHandler implements RowCallbackHandler{
			public void processRow(ResultSet arg0) throws SQLException {
				List<Object> list = new ArrayList<Object>();
				int columnCount = arg0.getMetaData().getColumnCount();
				for(int i=0;i<columnCount;i++){
					list.add(arg0.getObject(i + 1));
				}
				dataList.add(list);
				count++;
				try{
					if(count == cacheLine){
						listDataHandler.handleData(dataList);
						dataList.clear();
						count=0;
					}
				}
				catch(Exception ex){
                    logger.error("",ex);
				}
			}
		}
	}
	
	public void queryForHandler(ListDataHandler listDataHandler,int cacheLine,String strProjectCode,String datasource,String sql,Object[] args) throws Exception{
		ListQueryCallbackHandler listQueryCallbackHandler = new ListQueryCallbackHandler();
		listQueryCallbackHandler.setListDataHandler(listDataHandler);
		listQueryCallbackHandler.setCacheLine(cacheLine);
		listQueryCallbackHandler.queryForHandler(strProjectCode,datasource, sql, args);
	}
}
