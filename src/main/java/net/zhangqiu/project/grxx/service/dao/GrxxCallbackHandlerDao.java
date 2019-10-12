package net.zhangqiu.project.grxx.service.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.zhangqiu.service.database.DataConvertUtils;
import net.zhangqiu.service.database.dao.BaseDao;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.interfaces.ListMapHandler;
import net.zhangqiu.service.database.interfaces.QueryForListMapDataHandler;
import net.zhangqiu.utils.StrUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

@Repository
public class GrxxCallbackHandlerDao extends BaseDao implements QueryForListMapDataHandler{

    private static Logger logger = LoggerFactory.getLogger(GrxxCallbackHandlerDao.class);
	
	@Autowired
    EntityContext entityContext;
    @Autowired
    DataConvertUtils dataConvertUtils;
	
	class MapQueryCallbackHandler{
		
		private String strProjectCode;
		
		public void setStrProjectCode(String strProjectCode) {
			this.strProjectCode = strProjectCode;
		}
		public String getStrProjectCode() {
			return strProjectCode;
		}
		
		private String datasource;
		public String getDatasource() {
			return datasource;
		}
		public void setDatasource(String datasource) {
			this.datasource = datasource;
		}

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
		private String preAutoId;
		private List<Map<String,Object>> preMapList = new ArrayList<Map<String,Object>>();
		private int totalCount;
		
		private Map<String,Object> getMap(){
			Map<String,Object> result =null;
			if(preMapList.size() == 1){
		    	result = preMapList.get(0);
				if(result.get("DBXX") != null){
					result.put("DBXXListMap", getDbxxListMap());
				}
			}
			else if(preMapList.size() > 1){
				result = preMapList.get(0);
				String GRSFXX = (String) result.get("GRSFXX");
				String ZYXX = (String) result.get("ZYXX");
				String JZDZ = (String) result.get("JZDZ");
				String JYBSBG = (String) result.get("JYBSBG");
				String TSJY = (String) result.get("TSJY");
				boolean isBreak = false;
				for(int i=1;i<preMapList.size();i++){
					String preGRSFXX = (String) preMapList.get(i).get("GRSFXX");
					String preZYXX = (String) preMapList.get(i).get("ZYXX");
					String preJZDZ = (String) preMapList.get(i).get("JZDZ");
					String preJYBSBG = (String) preMapList.get(i).get("JYBSBG");
					String preTSJY = (String) preMapList.get(i).get("TSJY");
					if(!StrUtils.isEqual(GRSFXX, preGRSFXX)){
						result.put("GRSFXX", "*");
						isBreak = true;
						break;
					}
					if(!StrUtils.isEqual(ZYXX, preZYXX)){
						result.put("ZYXX", "*");
						isBreak = true;
						break;
					}
					if(!StrUtils.isEqual(JZDZ, preJZDZ)){
						result.put("JZDZ", "*");
						isBreak = true;
						break;
					}
					if(!StrUtils.isEqual(JYBSBG, preJYBSBG)){
						result.put("JYBSBG", "*");
						isBreak = true;
						break;
					}
					if(!StrUtils.isEqual(TSJY, preTSJY)){
						result.put("TSJY", "*");
						isBreak = true;
						break;
					}
				}
				if(!isBreak){
					if(result.get("DBXX") != null){
						result.put("DBXXListMap", getDbxxListMap());
					}
				}
			}
			preMapList.clear();
			count++;
			totalCount++;
			return result;
		}
		
		private List<Map<String,Object>> getDbxxListMap(){
			String key = entityContext.getEntityTableKey(strProjectCode,datasource, "GR_DBXX");
			List<Map<String,Object>> dbxxListMap = dataConvertUtils.mapListToMapList(preMapList, entityContext.getEntityTableMap().get(key));
			return dbxxListMap;
		}
		
		private class QueryForPreparedStatementCreator implements PreparedStatementCreator{
			public QueryForPreparedStatementCreator(String sql,Object[] args){
				this.sql = sql;
				//this.args = args;
			}
			private String sql;
			//private Object[] args;
			
			public PreparedStatement createPreparedStatement(Connection con)throws SQLException {
				PreparedStatement preparedStatement = con.prepareStatement(sql,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
				preparedStatement.setFetchSize(Integer.MIN_VALUE);
				return preparedStatement;
			}
			
		}
		
		private class QueryForRowCallbackHandler implements RowCallbackHandler{
			public void processRow(ResultSet arg0) throws SQLException {
				Map<String,Object> map = new HashMap<String,Object>();
				int columnCount = arg0.getMetaData().getColumnCount();
				for(int i=0;i<columnCount;i++){
					map.put(arg0.getMetaData().getColumnLabel(i + 1), arg0.getObject(i + 1));
				}
				String currentAutoId = map.get("autoID").toString();
				if(preAutoId == null){
					preAutoId = currentAutoId;
				}
				else{
					if(!preAutoId.equals(currentAutoId)){
						mapList.add(getMap());
						preAutoId = currentAutoId;
					}
				}
				preMapList.add(map);

				try{
					if(count == cacheLine){
						mapDataHandler.handleData(mapList);
						mapList.clear();
						count=0;
						System.out.println(totalCount);
					}
				}
				catch (Exception ex) {
					//异常需统一处理
                    logger.error("",ex);
				}
			}
		}
		
		public void queryForHandler(String strProjectCode,String datasource,String sql,Object[] args) throws Exception{
			mapList = new ArrayList<Map<String,Object>>();
			count = 0;
			JdbcTemplate jdbcTemplate = getJdbcTemplate(strProjectCode,datasource);
			QueryForPreparedStatementCreator queryForPreparedStatementCreator = new QueryForPreparedStatementCreator(sql,args);
			jdbcTemplate.query(queryForPreparedStatementCreator,new QueryForRowCallbackHandler());
			Map<String,Object> result = getMap();
			if(result != null){
				mapList.add(result);
				mapDataHandler.handleData(mapList);
				System.out.println(totalCount);
			}
		}
		
	}
	
	public void queryForHandler(ListMapHandler mapDataHandler, int cacheLine, String strProjectCode, String datasource, String tableName, String sql, Object[] args) throws Exception{
		MapQueryCallbackHandler mapQueryCallbackHandler = new MapQueryCallbackHandler();
		mapQueryCallbackHandler.setCacheLine(cacheLine);
		mapQueryCallbackHandler.setMapDataHandler(mapDataHandler);
		mapQueryCallbackHandler.setStrProjectCode(strProjectCode);
		mapQueryCallbackHandler.setDatasource(datasource);
		mapQueryCallbackHandler.queryForHandler(strProjectCode,datasource, sql, args);
	}
}
