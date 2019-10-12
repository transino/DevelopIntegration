package net.zhangqiu.project.cooperation.restcontroller;


import java.io.File;

import javax.annotation.PostConstruct;

import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.project.cooperation.framework.NavigationContext;
import net.zhangqiu.project.cooperation.service.DatasourceFileSaveService;
import net.zhangqiu.service.database.entity.EntityContext;
import net.zhangqiu.utils.FileUtils;
import net.zhangqiu.utils.StrUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class DatasourceFileController {
	
	@Autowired
	EnvironmentContext environmentContext;
	
	@Autowired
    DatasourceFileSaveService datasourceFileSaveService;
	
	@Autowired
    NavigationContext navigationContext;
	
	@Autowired
    EntityContext entityContext;
	
	@PostConstruct
	public void init() throws Exception{
		navigationContext.getNavigationMap().put("/cooperation/datasourcefile/showSave", "项目文件");
	}
	
	@RequestMapping(value = "/cooperation/datasourcefile/save")
	public String save(@RequestParam(value="indentification",required=false) String indentification,@RequestParam(value="strProjectCode") String strProjectCode,@RequestParam("file") MultipartFile zipFile,@RequestParam(value="encoding",required=false) String encoding) throws Exception {
		return datasourceFileSaveService.handleService(indentification,strProjectCode,zipFile.getBytes(),encoding);
    }
	
	@RequestMapping(value = "/cooperation/datasourcefile/delete")
	public String delete(@RequestParam(value="indentification",required=false) String indentification,@RequestParam(value="strProjectCode") String strProjectCode,@RequestParam(value="encoding",required=false) String encoding) throws Exception {
		return datasourceFileSaveService.delete(indentification,strProjectCode,encoding);
    }
	
	@RequestMapping(value = "/cooperation/datasourcefile/getDatasourceFile")
	public String getDatasourceFile(@RequestParam(value="indentification",required=false) String indentification,@RequestParam(value="strProjectCode") String strProjectCode,@RequestParam(value="encoding",required=false) String encoding) throws Exception {
		String datasourceFilePath  = entityContext.getDatasourceFilePath(strProjectCode);
		if(StrUtils.isEmpty(encoding)){
			encoding = environmentContext.getEncoding();
		}
		if(new File(datasourceFilePath).exists()){
			return FileUtils.readFile(datasourceFilePath, encoding);
		}
		return "";
    }
	
	@RequestMapping(value = "/cooperation/datasourcefile/initTable")
	public String initTable(@RequestParam(value="indentification",required=false) String indentification,@RequestParam(value="strProjectCode") String strProjectCode,@RequestParam(value="encoding",required=false) String encoding) throws Exception {
		return datasourceFileSaveService.initTable(indentification,strProjectCode,encoding);
    }

	@RequestMapping("/cooperation/datasourcefile/showSave")
	public ModelAndView showSave() {
		return new ModelAndView("/cooperation/datasourcefile/showsave");
	}
	
	@RequestMapping(value = "/cooperation/datasourcefile/saveDatasourceFile")
	public String saveDatasourceFile(@RequestParam(value="indentification",required=false) String indentification,@RequestParam(value="strProjectCode") String strProjectCode,@RequestParam("jsonString") String jsonString,@RequestParam(value="encoding",required=false) String encoding) throws Exception {
		return datasourceFileSaveService.saveDatasourceFile(indentification,strProjectCode,jsonString,encoding);
    }

}
