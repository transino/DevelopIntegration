package net.zhangqiu.project.cooperation.restcontroller;

import javax.annotation.PostConstruct;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.project.cooperation.framework.NavigationContext;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.imps.serial.JsonDataUpdateSqlService;
import net.zhangqiu.utils.JsonUtils;
import net.zhangqiu.utils.StrUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

@RestController
public class JsonDataUpdateSqlController {
	
	@Autowired
	EnvironmentContext environmentContext;
	@Autowired
    NavigationContext navigationContext;
	@Autowired
    JsonDataUpdateSqlService jsonDataUpdateSqlService;
	@Autowired
    EntityContext entityContext;
	
	@PostConstruct
	public void init() throws Exception{
		navigationContext.getNavigationMap().put("/cooperation/updatesql/showUpdateSql", "UpdateSql");
	}
	
	@RequestMapping("/cooperation/updatesql/showUpdateSql")
	public ModelAndView showUpdateSql() {
		
		ModelAndView modelAndView = new ModelAndView("/cooperation/updatesql/updatesql");
		modelAndView.addObject("datasourcelist", entityContext.getEntityDatasourceMap().keySet());
		modelAndView.addObject("currentdatasource", entityContext.getEntityDatasourceKey(environmentContext.getDefaultProjectName(), entityContext.getDefaultDatasourceNameMap().get(environmentContext.getDefaultProjectName())));
		return modelAndView;

	}
	
	@RequestMapping("/cooperation/updatesql/updateSql")
	public ModelAndView updateSql(@RequestParam(value="indentification",required = false) String indentification,@RequestParam(value="strProjectCode",required = false) String strProjectCode,@RequestParam(value="datasourceNameKey",required = false) String datasourceNameKey,@RequestParam("jsonData") String jsonData,@RequestParam(value="encoding",required = false) String encoding) throws Exception {
        jsonData = URLEncoder.encode(jsonData, environmentContext.getEncoding());
	    ModelAndView modelAndView = new ModelAndView("/cooperation/message");
		String datasourceName = "";
		if(StrUtils.isEmpty(strProjectCode)){
			if(!StrUtils.isEmpty(datasourceNameKey)){
				strProjectCode = entityContext.getStrProjectCodeFromDatasourceKey(datasourceNameKey);
				datasourceName = entityContext.getDatasourceNameFromDatasourceKey(datasourceNameKey);
			}
		}
		else{
			datasourceName = datasourceNameKey;
		}
		String result = jsonDataUpdateSqlService.handleService(indentification,strProjectCode, datasourceName, jsonData, encoding);
		modelAndView.addObject("message", result);
		return modelAndView;
	}

    @RequestMapping("/cooperation/updatesql/updateSqlService")
    public String updateSqlService(@RequestParam(value="indentification",required = false) String indentification,@RequestParam(value="strProjectCode",required = false) String strProjectCode,@RequestParam(value="datasourceNameKey",required = false) String datasourceNameKey,@RequestParam("jsonData") String jsonData,@RequestParam(value="encoding",required = false) String encoding) throws Exception {
        //jsonData = URLDecoder.decode(jsonData, environmentContext.getEncoding());
        String datasourceName = "";
        if(StrUtils.isEmpty(strProjectCode)){
            if(!StrUtils.isEmpty(datasourceNameKey)){
                strProjectCode = entityContext.getStrProjectCodeFromDatasourceKey(datasourceNameKey);
                datasourceName = entityContext.getDatasourceNameFromDatasourceKey(datasourceNameKey);
            }
        }
        else{
            datasourceName = datasourceNameKey;
        }

        String result = jsonDataUpdateSqlService.handleService(indentification,strProjectCode, datasourceName, jsonData, encoding);

        return result;
    }
}
