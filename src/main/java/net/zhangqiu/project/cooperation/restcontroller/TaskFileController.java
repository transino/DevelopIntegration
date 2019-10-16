package net.zhangqiu.project.cooperation.restcontroller;

import java.io.File;

import javax.annotation.PostConstruct;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.project.cooperation.framework.NavigationContext;
import net.zhangqiu.project.cooperation.service.TaskFileSaveService;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.utils.FileUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class TaskFileController {
	
	@Autowired
	EnvironmentContext environmentContext;
	
	@Autowired
	TaskFileSaveService taskFileSaveService;
	
	@Autowired
	NavigationContext navigationContext;
	
	@Autowired
    EntityContext entityContext;
	
	@PostConstruct
	public void init() throws Exception{
		navigationContext.getNavigationMap().put("/cooperation/taskfile/showSave", "任务文件");
	}
	
	@RequestMapping(value = "/cooperation/taskfile/save")
	public String save(@RequestParam(value="indentification",required=false) String indentification,@RequestParam(value="strProjectCode") String strProjectCode,@RequestParam(value="strTaskCode") String strTaskCode,@RequestParam("file") MultipartFile zipFile,@RequestParam(value="encoding",required=false) String encoding) throws Exception {
		return taskFileSaveService.handleService(indentification,strProjectCode,strTaskCode,zipFile.getBytes(),encoding);
    }
	
	@RequestMapping(value = "/cooperation/taskfile/delete")
	public String delete(@RequestParam(value="indentification",required=false) String indentification,@RequestParam(value="strProjectCode") String strProjectCode,@RequestParam(value="strTaskCode") String strTaskCode,@RequestParam(value="encoding",required=false) String encoding) throws Exception {
		return taskFileSaveService.delete(indentification,strProjectCode,strTaskCode,encoding);
    }
	
	@RequestMapping(value = "/cooperation/taskfile/initTable")
	public String initTable(@RequestParam(value="indentification",required=false) String indentification,@RequestParam(value="strProjectCode") String strProjectCode,@RequestParam(value="strTaskCode") String strTaskCode,@RequestParam(value="encoding",required=false) String encoding) throws Exception {
		return taskFileSaveService.initTable(indentification,strProjectCode,strTaskCode,encoding);
    }
	
	@RequestMapping("/cooperation/taskfile/showSave")
	public ModelAndView showSave() {
		return new ModelAndView("/cooperation/taskfile/showsave");
	}
	
	@RequestMapping(value = "/cooperation/taskfile/saveTableFile")
	public String saveTableFile(@RequestParam(value="indentification",required=false) String indentification,@RequestParam(value="strProjectCode") String strProjectCode,@RequestParam(value="strTaskCode") String strTaskCode,@RequestParam("jsonString") String jsonString,@RequestParam(value="encoding",required=false) String encoding) throws Exception {
		return taskFileSaveService.saveTableFile(indentification,strProjectCode,strTaskCode,jsonString,encoding);
    }
	
	@RequestMapping(value = "/cooperation/taskfile/saveDatasourceSqlFile")
	public String saveDatasourceSqlFile(@RequestParam(value="indentification",required=false) String indentification,@RequestParam(value="strProjectCode") String strProjectCode,@RequestParam(value="strTaskCode") String strTaskCode,@RequestParam("jsonString") String jsonString,@RequestParam(value="encoding",required=false) String encoding) throws Exception {
		return taskFileSaveService.saveDatasourceSqlFile(indentification,strProjectCode,strTaskCode,jsonString,encoding);
    }
	
	@RequestMapping(value = "/cooperation/taskfile/saveCheckRuleTableFile")
	public String saveCheckRuleTableFile(@RequestParam(value="indentification",required=false) String indentification,@RequestParam(value="strProjectCode") String strProjectCode,@RequestParam(value="strTaskCode") String strTaskCode,@RequestParam("jsonString") String jsonString,@RequestParam(value="encoding",required=false) String encoding) throws Exception {
		return taskFileSaveService.saveCheckRuleTableFile(indentification,strProjectCode,strTaskCode,jsonString,encoding);
    }
	
	@RequestMapping(value = "/cooperation/taskfile/getTableFile")
	public String getTableFile(@RequestParam(value="indentification",required=false) String indentification,@RequestParam(value="strProjectCode") String strProjectCode,@RequestParam(value="strTaskCode") String strTaskCode,@RequestParam(value="encoding",required=false) String encoding) throws Exception {
		String taskTableFilePath  = entityContext.getTaskTableFilePath(strProjectCode, strTaskCode);
		if(new File(taskTableFilePath).exists()){
			return FileUtils.readFile(taskTableFilePath, environmentContext.getEncoding());
		}
		return "";
    }
	
	@RequestMapping(value = "/cooperation/taskfile/getDatasourceSqlFile")
	public String getDatasourceSqlFile(@RequestParam(value="indentification",required=false) String indentification,@RequestParam(value="strProjectCode") String strProjectCode,@RequestParam(value="strTaskCode") String strTaskCode,@RequestParam(value="encoding",required=false) String encoding) throws Exception {
		String datasourceSqlFilePath  = entityContext.getTaskDatasourceSqlFilePath(strProjectCode, strTaskCode);
		if(new File(datasourceSqlFilePath).exists()){
			return FileUtils.readFile(datasourceSqlFilePath, environmentContext.getEncoding());
		}
		return "";
    }
	
	@RequestMapping(value = "/cooperation/taskfile/getCheckTableFile")
	public String getCheckTableFile(@RequestParam(value="indentification",required=false) String indentification,@RequestParam(value="strProjectCode") String strProjectCode,@RequestParam(value="strTaskCode") String strTaskCode,@RequestParam(value="encoding",required=false) String encoding) throws Exception {
		String checkTableFilePath  = entityContext.getTaskTableCheckFilePath(strProjectCode, strTaskCode);
		if(new File(checkTableFilePath).exists()){
			return FileUtils.readFile(checkTableFilePath, environmentContext.getEncoding());
		}
		return "";
    }
}
