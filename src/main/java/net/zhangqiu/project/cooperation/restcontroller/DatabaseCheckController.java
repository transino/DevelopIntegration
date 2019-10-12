package net.zhangqiu.project.cooperation.restcontroller;

import javax.annotation.PostConstruct;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.project.cooperation.framework.NavigationContext;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.param.EntityDatabaseCheckParam;
import net.zhangqiu.service.database.imps.batch.DatabaseCheckService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class DatabaseCheckController {
	@Autowired
	EnvironmentContext environmentContext;
	@Autowired
	NavigationContext navigationContext;
	@Autowired
	DatabaseCheckService databaseCheckService;
	@Autowired
    EntityContext entityContext;
	@PostConstruct
	public void init() throws Exception{
		navigationContext.getNavigationMap().put("/cooperation/check/showDatabaseCheck", "校验数据库");
	}
	
	@RequestMapping("/cooperation/check/showDatabaseCheck")
	public ModelAndView showDatabaseCheck() {
		ModelAndView modelAndView = new ModelAndView("/cooperation/check/databasecheck");
		modelAndView.addObject("datasourcelist", entityContext.getEntityDatasourceMap().keySet());
		modelAndView.addObject("currentdatasource", entityContext.getEntityDatasourceKey(environmentContext.getDefaultProjectName(), entityContext.getDefaultDatasourceNameMap().get(environmentContext.getDefaultProjectName())));
		modelAndView.addObject("tablelist", entityContext.getEntityTableMap().keySet());
		return modelAndView;
	}
	
	@RequestMapping("/cooperation/check/databaseCheck")
	public ModelAndView databaseCheck(@RequestParam(value="indentification",required=false) String indentification,@ModelAttribute("form") EntityDatabaseCheckParam entityDatabaseCheckParam) throws Exception {
		ModelAndView modelAndView = new ModelAndView("/cooperation/message");
		String result = databaseCheckService.handleService(indentification,entityDatabaseCheckParam);
		modelAndView.addObject("message", result);
		return modelAndView;
	}
	
	@RequestMapping("/cooperation/check/databaseCheckService")
	public String databaseCheckService(@RequestParam(value="indentification",required=false) String indentification,@ModelAttribute("form") EntityDatabaseCheckParam entityDatabaseCheckParam) throws Exception {
		return databaseCheckService.handleService(indentification,entityDatabaseCheckParam);
	}
}
