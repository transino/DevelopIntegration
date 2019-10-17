package net.zhangqiu.project.cooperation.restcontroller;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import net.zhangqiu.project.cooperation.framework.NavigationContext;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.service.database.imps.serial.JsonDataSelectByConditionService;
import net.zhangqiu.service.entity.result.CommonServiceResult;
import net.zhangqiu.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


@RestController
public class IndexController {
	
	@Autowired
    NavigationContext navigationContext;
	
	@Autowired
    EntityContext entityContext;
	
	@Autowired
    JsonDataSelectByConditionService jsonDataSelectByConditionService;
	
	@PostConstruct
	public void init() throws Exception{
		navigationContext.getNavigationMap().put("/cooperation/entitycontext/init", "初始化上下文");
	}
	
	@RequestMapping("/cooperation")
	public ModelAndView index() throws Exception {
		ModelAndView modelAndView = new ModelAndView("/cooperation/index");
		modelAndView.addObject("navigationMap", navigationContext.getNavigationMap());
		
		/* 测试数据库类型代码
		EntityDataParam entityDataParam = new EntityDataParam();
		entityDataParam.setTableName("test01");
		entityDataParam.setStrCondition("1=1 order by int01");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("CONST_LIMIT", "4,4");
		entityDataParam.setData(map);
		
		CommonServiceResult commonServiceResult = jsonDataSelectByConditionService.logicService("fxq", "FXQ_ORACLE", entityDataParam);
		Object object = commonServiceResult.getData();
		*/
		
		return modelAndView;
	}
	
	@RequestMapping("/cooperation/entitycontext/init")
	public String entitycontextInit() throws Exception{
		entityContext.init();
		CommonServiceResult commonServiceResult = new CommonServiceResult();
		commonServiceResult.setMessage("success");
		commonServiceResult.setSuccess(true);
		return JsonUtils.objectToString(commonServiceResult);
	}
}
