package net.zhangqiu.project.cooperation.restcontroller;

import javax.annotation.PostConstruct;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.project.cooperation.framework.NavigationContext;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.imps.serial.JsonDataCheckService;
import net.zhangqiu.utils.StrUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class JsonDataCheckController {
	
	@Autowired
	EnvironmentContext environmentContext;
	@Autowired
	NavigationContext navigationContext;
	@Autowired
    JsonDataCheckService jsonDataCheckService;
	@Autowired
    EntityContext entityContext;
	
	@PostConstruct
	public void init() throws Exception{
		navigationContext.getNavigationMap().put("/cooperation/check/showJsonDataCheck", "校验jsonData");
	}
	
	@RequestMapping("/cooperation/check/showJsonDataCheck")
	public ModelAndView showJsonDataCheck() {
		ModelAndView modelAndView = new ModelAndView("/cooperation/check/jsondatacheck");
		modelAndView.addObject("datasourcelist", entityContext.getEntityDatasourceMap().keySet());
		modelAndView.addObject("currentdatasource", entityContext.getEntityDatasourceKey(environmentContext.getDefaultProjectName(), entityContext.getDefaultDatasourceNameMap().get(environmentContext.getDefaultProjectName())));
		return modelAndView;
	}
	
	@RequestMapping("/cooperation/check/jsonDataCheck")
	public ModelAndView jsonDataCheck(@RequestParam(value="indentification",required = false) String indentification,@RequestParam(value="strProjectCode",required = false) String strProjectCode,@RequestParam(value="datasourceNameKey",required = false) String datasourceNameKey,@RequestParam("jsonData") String jsonData,@RequestParam(value="encoding",required = false) String encoding) throws Exception {
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
		String result = jsonDataCheckService.handleService(indentification,strProjectCode,datasourceName, jsonData, encoding);
		modelAndView.addObject("message", result);
		return modelAndView;
	}
}
