package net.zhangqiu.project.cooperation.restcontroller;

import javax.annotation.PostConstruct;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.project.cooperation.framework.NavigationContext;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.param.EntityDatabaseCheckTextParam;
import net.zhangqiu.service.database.imps.batch.FileToDatabaseCheckService;

import net.zhangqiu.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

@RestController
public class FileToDatabaseCheckController {
	@Autowired
	EnvironmentContext environmentContext;
	@Autowired
	NavigationContext navigationContext;
	@Autowired
    FileToDatabaseCheckService fileToDatabaseCheckService;
	@Autowired
    EntityContext entityContext;
	@PostConstruct
	public void init() throws Exception{
		navigationContext.getNavigationMap().put("/cooperation/check/showFileToDatabaseCheck", "校验文件到数据库");
	}
	
	@RequestMapping("/cooperation/check/showFileToDatabaseCheck")
	public ModelAndView showFileToDatabaseCheck() {
		ModelAndView modelAndView = new ModelAndView("/cooperation/check/filetodatabasecheck");
		modelAndView.addObject("datasourcelist", entityContext.getEntityDatasourceMap().keySet());
		modelAndView.addObject("currentdatasource", entityContext.getEntityDatasourceKey(environmentContext.getDefaultProjectName(), entityContext.getDefaultDatasourceNameMap().get(environmentContext.getDefaultProjectName())));
		modelAndView.addObject("tablelist", entityContext.getEntityTableMap().keySet());
		return modelAndView;
	}
	
	@RequestMapping("/cooperation/check/fileToDatabaseCheck")
	public ModelAndView fileToDatabaseCheck(@RequestParam(value="indentification",required=false) String indentification,@ModelAttribute("form") EntityDatabaseCheckTextParam entityDatabaseCheckTextParam) throws Exception {
		ModelAndView modelAndView = new ModelAndView("/cooperation/message");
		String result = fileToDatabaseCheckService.handleService(indentification,entityDatabaseCheckTextParam);
		modelAndView.addObject("message", result);
		return modelAndView;
	}
	
	@RequestMapping("/cooperation/check/fileToDatabaseCheckService")
	public String fileToDatabaseCheckService(@RequestParam(value="indentification",required=false) String indentification,String paramJson) throws Exception {
        paramJson = URLDecoder.decode(paramJson, environmentContext.getEncoding());
        EntityDatabaseCheckTextParam entityDatabaseCheckTextParam = (EntityDatabaseCheckTextParam) JsonUtils.stringToObject(paramJson, EntityDatabaseCheckTextParam.class);
	    return fileToDatabaseCheckService.handleService(indentification,entityDatabaseCheckTextParam);
	}
}
