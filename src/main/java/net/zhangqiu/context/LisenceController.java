package net.zhangqiu.context;

import net.zhangqiu.utils.AESKeyUtils;
import net.zhangqiu.utils.FileUtils;
import net.zhangqiu.utils.HardWareUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class LisenceController {
	
	@Autowired
	EnvironmentContext environmentContext;
	
	@RequestMapping(value = "/lisence/showlisence")
	public ModelAndView showlisence() throws Exception {
		ModelAndView modelAndView = new ModelAndView("lisence");
		modelAndView.addObject("motherboard", HardWareUtils.getMotherboardSN());
		return modelAndView;
    }
	
	@RequestMapping(value = "/lisence/lisence")
	public ModelAndView lisence(@RequestParam("file") MultipartFile file) throws Exception {
		if(file.getBytes() == null || file.getBytes().length == 0){
			ModelAndView errorModelAndView = new ModelAndView("/cooperation/message");
			errorModelAndView.addObject("message", "请选择文件");
			return errorModelAndView;
		}

		String motherboard = HardWareUtils.getMotherboardSN();
		String lisence = new String(file.getBytes());
		String decryString = AESKeyUtils.DecryString(lisence, null);
		if(motherboard.equals(decryString)){
			FileUtils.saveFile(environmentContext.getLisencePath(), lisence, environmentContext.getEncoding());
			environmentContext.init();
			ModelAndView modelAndView = new ModelAndView("redirect:/cooperation");
			return modelAndView;
		}
		else{
			ModelAndView errorModelAndView = new ModelAndView("/cooperation/message");
			errorModelAndView.addObject("message", "文件错误");
			return errorModelAndView;
		}
		
    }
}
