package net.zhangqiu.project.cooperation.restcontroller;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.project.cooperation.framework.NavigationContext;
import net.zhangqiu.service.database.dao.SelectAllDao;
import net.zhangqiu.service.database.dao.UpdateSqlDao;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.utils.StrUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class DatasourceSqlController {
	
	@Autowired
	NavigationContext navigationContext;
	@Autowired
	SelectAllDao selectAllDao;
	@Autowired
    UpdateSqlDao updateSqlDao;
	@Autowired
	EntityContext entityContext;
	@Autowired
	EnvironmentContext environmentContext;

    private static Logger logger = LoggerFactory.getLogger(DatasourceSqlController.class);
	
	@PostConstruct
	public void init() throws Exception{
		navigationContext.getNavigationMap().put("/cooperation/datasourcesql/showList", "数据源SQL日志");
	}
	
	@RequestMapping(value = "/cooperation/datasourcesql/showList")
	public ModelAndView showList(@RequestParam(value="indentification",required=false) String indentification,@RequestParam(value="datasourceNameKey",required=false) String datasourceNameKey,@RequestParam(value="encoding",required=false) String encoding) throws Exception {
		ModelAndView modelAndView = new ModelAndView("/cooperation/datasourcesql/showlist");
		modelAndView.addObject("datasourcelist", entityContext.getEntityDatasourceMap().keySet());

		if(StrUtils.isEmpty(datasourceNameKey)){
			if(entityContext.getEntityDatasourceMap().keySet().size() > 0){
				datasourceNameKey = entityContext.getDefaultDatasourceNameMap().get(environmentContext.getDefaultProjectName());
				if(!StrUtils.isEmpty(datasourceNameKey)){
					datasourceNameKey = entityContext.getEntityDatasourceKey(environmentContext.getDefaultProjectName(), datasourceNameKey);
				}
				else{
					for(String string : entityContext.getEntityDatasourceMap().keySet()){
						datasourceNameKey = string;
						break;
					}
				}
			}
		}
		if(!StrUtils.isEmpty(datasourceNameKey)){
			String strProjectCode = entityContext.getStrProjectCodeFromDatasourceKey(datasourceNameKey);
			String datasourceName = entityContext.getDatasourceNameFromDatasourceKey(datasourceNameKey);

			modelAndView.addObject("currentdatasource", entityContext.getEntityDatasourceKey(strProjectCode, datasourceName));
			
			String[] initSqlList = entityContext.getEntityDatasourceMap().get(datasourceNameKey).getInitSqlList();
			if(initSqlList != null && initSqlList.length > 0){
				try{
					List<Map<String,Object>> list = selectAllDao.query(strProjectCode,datasourceName,"Cooperation_DatasourceSql", "SELECT * FROM Cooperation_DatasourceSql");
					list = StrUtils.listMapDataToUpperCase(list);
					modelAndView.addObject("datasourcesqllist", list);
				}
				catch(Exception ex){
                    logger.error("Cooperation_DatasourceSql，表异常",ex);
				}
			}
		}

		return modelAndView;
    }
	
	@RequestMapping(value = "/cooperation/datasourcesql/delete")
	public ModelAndView delete(@RequestParam(value="indentification",required=false) String indentification,String datasourceNameKey,String strSqlId,@RequestParam(value="encoding",required=false) String encoding) throws Exception {
		String strProjectCode = entityContext.getStrProjectCodeFromDatasourceKey(datasourceNameKey);
		String datasourceName = entityContext.getDatasourceNameFromDatasourceKey(datasourceNameKey);
		String strSql = "DELETE FROM Cooperation_DatasourceSql WHERE strSqlId = ?";
		updateSqlDao.updateSql(strProjectCode,datasourceName, strSql, new Object[]{strSqlId});
		ModelAndView modelAndView = new ModelAndView("redirect:/cooperation/datasourcesql/showList");
		return modelAndView;
    }
}
