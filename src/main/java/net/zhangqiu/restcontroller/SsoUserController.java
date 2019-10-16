package net.zhangqiu.restcontroller;

import net.zhangqiu.service.sso.imp.SsoUserServcie;
import net.zhangqiu.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SsoUserController {

    @Autowired
    SsoUserServcie sssoUserServcie;

    @RequestMapping(value = "/initSsoUser")
    public String initSsoUser(@RequestParam(value="indentification",required = false) String indentification, @RequestParam("strSsoUser") String strSsoUser) throws Exception {
        return sssoUserServcie.handleInitSsoUser(strSsoUser);
    }

    @RequestMapping(value = "/login")
    public String login(@RequestParam(value="indentification",required = false) String indentification, @RequestParam("userCode") String userCode,@RequestParam("password") String password,@RequestParam(value="token",required = false) String token) throws Exception {
        return JsonUtils.objectToString(sssoUserServcie.login(userCode,password,token));
    }

    @RequestMapping(value = "/isLogin")
    public String isLogin(@RequestParam(value="indentification",required = false) String indentification,@RequestParam(value="token",required = false) String token) throws Exception {
        return JsonUtils.objectToString(sssoUserServcie.isLogin(token));
    }

    @RequestMapping(value = "/getSsoUser")
    public String getSsoUser(@RequestParam(value="indentification",required = false) String indentification) throws Exception {
        return sssoUserServcie.getSsoUser();
    }

    @RequestMapping(value = "/addSsoUser")
    public String addSsoUser(@RequestParam(value="indentification",required = false) String indentification, @RequestParam("userCode") String userCode,@RequestParam("userName") String userName,@RequestParam("password") String password) throws Exception {
        return JsonUtils.objectToString(sssoUserServcie.addSsoUser(userCode,userName,password));
    }

    @RequestMapping(value = "/updateSsoUser")
    public String updateSsoUser(@RequestParam(value="indentification",required = false) String indentification, @RequestParam("userCode") String userCode,@RequestParam("userName") String userName,@RequestParam("password") String password) throws Exception {
        return JsonUtils.objectToString(sssoUserServcie.updateSsoUser(userCode,userName,password));
    }

    @RequestMapping(value = "/deleteSsoUser")
    public String deleteSsoUser(@RequestParam(value="indentification",required = false) String indentification, @RequestParam("userCode") String userCode) throws Exception {
        return JsonUtils.objectToString(sssoUserServcie.deleteSsoUser(userCode));
    }
}
