package net.zhangqiu.project.cooperation.restcontroller;

import javax.annotation.PostConstruct;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.project.cooperation.framework.NavigationContext;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.entity.param.EntityDatabaseCheckUpdateParam;
import net.zhangqiu.service.database.imps.batch.DatabaseCheckUpdateListService;

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
public class DatabaseCheckUpdateController {
	
	@Autowired
	NavigationContext navigationContext;
	@Autowired
	DatabaseCheckUpdateListService databaseCheckUpdateListService;
	@Autowired
	EntityContext entityContext;
	@Autowired
	EnvironmentContext environmentContext;
	@PostConstruct
	public void init() throws Exception{
		navigationContext.getNavigationMap().put("/cooperation/check/showDatabaseCheckUpdate", "校验更新数据库");
	}
	
	@RequestMapping("/cooperation/check/showDatabaseCheckUpdate")
	public ModelAndView showDatabaseCheckUpdate() {
		ModelAndView modelAndView = new ModelAndView("/cooperation/check/databasecheckupdate");
		modelAndView.addObject("datasourcelist", entityContext.getEntityDatasourceMap().keySet());
		modelAndView.addObject("currentdatasource", entityContext.getEntityDatasourceKey(environmentContext.getDefaultProjectName(), entityContext.getDefaultDatasourceNameMap().get(environmentContext.getDefaultProjectName())));
		modelAndView.addObject("tablelist", entityContext.getEntityTableMap().keySet());
		return modelAndView;
	}
	
	@RequestMapping("/cooperation/check/databaseCheckUpdate")
	public ModelAndView databaseCheckUpdate(@RequestParam(value="indentification",required=false) String indentification,@ModelAttribute("form") EntityDatabaseCheckUpdateParam entityDatabaseCheckUpdateParam) throws Exception {
		ModelAndView modelAndView = new ModelAndView("/cooperation/message");
		String result = databaseCheckUpdateListService.handleService(indentification,entityDatabaseCheckUpdateParam);
		modelAndView.addObject("message", result);
		return modelAndView;
	}
	
	@RequestMapping("/cooperation/check/databaseCheckUpdateService")
	public String databaseCheckUpdateService(@RequestParam(value="indentification",required=false) String indentification,String paramJson) throws Exception {
        paramJson = URLDecoder.decode(paramJson, environmentContext.getEncoding());
        Object objectParamJson = JsonUtils.stringToJson(paramJson);
        Map<String,Class<?>> map = new HashMap<String,Class<?>>();
        map.put("data", Map.class);
        EntityDatabaseCheckUpdateParam entityDatabaseCheckUpdateParam = (EntityDatabaseCheckUpdateParam) JsonUtils.jsonToObject(objectParamJson,EntityDatabaseCheckUpdateParam.class,map);
        int objectsSize = 0;
        if(entityDatabaseCheckUpdateParam.getData() != null){
            objectsSize = entityDatabaseCheckUpdateParam.getData().size();
        }
        Object[] objects = new Object[objectsSize];
        for(Map.Entry<String, Object> entry : entityDatabaseCheckUpdateParam.getData().entrySet()) {
            int indexOf = entry.getKey().indexOf("_");
            int index = Integer.valueOf(entry.getKey().substring(0, indexOf));
            objects[index] = entityContext.convertToType(entry.getValue(), entry.getKey().substring(indexOf + 1));
        }
        entityDatabaseCheckUpdateParam.setArgs(objects);
        return databaseCheckUpdateListService.handleService(indentification,entityDatabaseCheckUpdateParam);
	}
}
