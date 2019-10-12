package net.zhangqiu.project.fxq.restcontroller;

import net.zhangqiu.project.fxq.service.imps.AmlJsonDataCompareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AmlJsonDataRestController {

    @Autowired
    @Qualifier("amlJsonDataCompareService")
    AmlJsonDataCompareService amlJsonDataCompareService;

    @RequestMapping(value = "/jsonDataCompare")
    public String jsonDataCompare(@RequestParam(value="indentification",required = false) String indentification,@RequestParam("strProjectCode") String strProjectCode,@RequestParam(value="datasourceName",required = false) String datasourceName,@RequestParam("preJsonString") String preJsonString,@RequestParam(value="afterJsonString",required = false) String afterJsonString,@RequestParam(value="encoding",required = false) String encoding) throws Exception {
        return amlJsonDataCompareService.handleService(indentification,strProjectCode, datasourceName,preJsonString,afterJsonString,encoding);
    }
}
