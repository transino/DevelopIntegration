package net.zhangqiu.service.sso.imp;

import ch.qos.logback.core.util.FileUtil;
import net.zhangqiu.context.EnvironmentContext;
import net.zhangqiu.service.database.entity.SnowFlakeGenerator;
import net.zhangqiu.service.entity.result.CommonServiceResult;
import net.zhangqiu.service.sso.entity.SsoUser;
import net.zhangqiu.service.sso.entity.SsoUserFile;
import net.zhangqiu.utils.FileUtils;
import net.zhangqiu.utils.JsonUtils;
import net.zhangqiu.utils.StrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class SsoUserServcie {

    private static Logger logger = LoggerFactory.getLogger(SsoUserServcie.class);

    @Autowired
    SnowFlakeGenerator snowFlakeGenerator;

    @Autowired
    EnvironmentContext environmentContext;

    private Map<String, Date> loginUserMap = new ConcurrentHashMap<String, Date>();
    private SsoUserFile ssoUserFile;

    @PostConstruct
    private void init() throws  Exception{
        if(environmentContext.getSsoUserTimeout() != null && environmentContext.getSsoUserTimeout() > 0){
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            TimeToIdleThread timeToIdleThread = new TimeToIdleThread();
            executorService.submit(timeToIdleThread);

            File file = new File(environmentContext.getSsoPath() + "ssoUser.json");
            if(!file.exists()){
                String strSsoUser = JsonUtils.objectToString(new SsoUserFile());
                FileUtils.saveFile(environmentContext.getSsoPath() + "ssoUser.json",strSsoUser,environmentContext.getEncoding());
            }
            initSsoUserFile();
        }
    }

    public String handleInitSsoUser(String strSsoUser) throws  Exception{
        strSsoUser = URLDecoder.decode(strSsoUser,environmentContext.getEncoding());
        return JsonUtils.objectToString(initSsoUser(strSsoUser));
    }

    public String getSsoUser() throws  Exception{
        return JsonUtils.objectToString(ssoUserFile);
    }

    public CommonServiceResult initSsoUser(String strSsoUser) throws  Exception{
        FileUtils.saveFile(environmentContext.getSsoPath() + "ssoUser.json",strSsoUser,environmentContext.getEncoding());
        initSsoUserFile();
        CommonServiceResult commonServiceResult = new CommonServiceResult();
        commonServiceResult.setSuccess(true);
        commonServiceResult.setMessage("success");
        return commonServiceResult;
    }

    private void initSsoUserFile() throws  Exception{
        String strSsoUser = FileUtils.readFile(environmentContext.getSsoPath() + "ssoUser.json",environmentContext.getEncoding());
        Object ssoUserObject = JsonUtils.stringToJson(strSsoUser);
        Map<String,Class<?>> map = new HashMap<String,Class<?>>();
        map.put("ssoUserList", SsoUser.class);
        ssoUserFile = (SsoUserFile) JsonUtils.jsonToObject(ssoUserObject, SsoUserFile.class,map);
    }



    public CommonServiceResult addSsoUser(String userCode, String userName, String password) throws  Exception{
        if(StrUtils.isEmpty(userCode) || StrUtils.isEmpty(userName) || StrUtils.isEmpty(password)){
            CommonServiceResult commonServiceResult = new CommonServiceResult();
            commonServiceResult.setSuccess(false);
            commonServiceResult.setMessage("error");
            return commonServiceResult;
        }

        for(SsoUser ssoUser : ssoUserFile.getSsoUserList()){
            if(ssoUser.getUserCode().equals(userCode)){
                CommonServiceResult commonServiceResult = new CommonServiceResult();
                commonServiceResult.setSuccess(false);
                commonServiceResult.setMessage("error");
                return commonServiceResult;
            }
        }

        int i = 0;
        for(SsoUser ssoUser : ssoUserFile.getSsoUserList()){
            if(ssoUser.getUserCode().compareTo(userCode) < 0){
                i++;
            }
            else{
                break;
            }
        }
        SsoUser newUser = new SsoUser();
        newUser.setUserCode(userCode);
        newUser.setUserName(userName);
        newUser.setPassword(password);
        ssoUserFile.getSsoUserList().add(i,newUser);

        String strSsoUser = JsonUtils.objectToString(ssoUserFile);
        FileUtils.saveFile(environmentContext.getSsoPath() + "ssoUser.json",strSsoUser,environmentContext.getEncoding());

        CommonServiceResult commonServiceResult = new CommonServiceResult();
        commonServiceResult.setSuccess(true);
        commonServiceResult.setMessage("success");
        return commonServiceResult;
    }

    public CommonServiceResult updateSsoUser(String userCode, String userName, String password) throws  Exception{

        for(SsoUser ssoUser : ssoUserFile.getSsoUserList()){
            if(ssoUser.getUserCode().equals(userCode)){
                ssoUser.setUserName(userName);
                ssoUser.setPassword(password);
                String strSsoUser = JsonUtils.objectToString(ssoUserFile);
                FileUtils.saveFile(environmentContext.getSsoPath() + "ssoUser.json",strSsoUser,environmentContext.getEncoding());

                CommonServiceResult commonServiceResult = new CommonServiceResult();
                commonServiceResult.setSuccess(true);
                commonServiceResult.setMessage("success");
                return commonServiceResult;
            }
        }

        CommonServiceResult commonServiceResult = new CommonServiceResult();
        commonServiceResult.setSuccess(false);
        commonServiceResult.setMessage("error");
        return commonServiceResult;
    }

    public CommonServiceResult deleteSsoUser(String userCode) throws  Exception{
        for(SsoUser ssoUser : ssoUserFile.getSsoUserList()){
            if(ssoUser.getUserCode().equals(userCode)){
                ssoUserFile.getSsoUserList().remove(ssoUser);
                String strSsoUser = JsonUtils.objectToString(ssoUserFile);
                FileUtils.saveFile(environmentContext.getSsoPath() + "ssoUser.json",strSsoUser,environmentContext.getEncoding());

                CommonServiceResult commonServiceResult = new CommonServiceResult();
                commonServiceResult.setSuccess(true);
                commonServiceResult.setMessage("success");
                return commonServiceResult;
            }
        }

        CommonServiceResult commonServiceResult = new CommonServiceResult();
        commonServiceResult.setSuccess(false);
        commonServiceResult.setMessage("error");
        return commonServiceResult;
    }


    public CommonServiceResult login(String userCode, String password, String token){
        boolean success = false;
        for(SsoUser ssoUser : ssoUserFile.getSsoUserList()){
            if(ssoUser.getUserCode().equals(userCode) && ssoUser.getPassword().equals(password)){
                success = true;
                break;
            }
        }

        if(success){
            if(StrUtils.isEmpty(token)){
                token = String.valueOf(snowFlakeGenerator.nextId());
            }
            loginUserMap.put(token,new Date());
            CommonServiceResult commonServiceResult = new CommonServiceResult();
            commonServiceResult.setSuccess(true);
            commonServiceResult.setMessage("success");
            commonServiceResult.setData(token);
            return commonServiceResult;
        }
        else{
            CommonServiceResult commonServiceResult = new CommonServiceResult();
            commonServiceResult.setSuccess(false);
            commonServiceResult.setMessage("error");
            return commonServiceResult;
        }
    }

    public CommonServiceResult isLogin(String token){
        CommonServiceResult commonServiceResult = new CommonServiceResult();
        if(loginUserMap.containsKey(token)){
            loginUserMap.put(token,new Date());
            commonServiceResult.setSuccess(true);
            commonServiceResult.setMessage("success");
            //logger.error("success:" + token);
        }
        else{
            commonServiceResult.setSuccess(false);
            commonServiceResult.setMessage("error");
            //logger.error("error:" + token);
        }

        return commonServiceResult;
    }

    class TimeToIdleThread implements Runnable{
        @Override
        public void run() {
            while(true) {
                try{
                    Thread.sleep(20000);
                    List<String> tokenList = new ArrayList<String>();
                    Long currentTime = new Date().getTime();
                    for(Map.Entry<String,Date> entry : loginUserMap.entrySet()){
                        if(currentTime - entry.getValue().getTime() > environmentContext.getSsoUserTimeout()){
                            tokenList.add(entry.getKey());
                        }
                    }
                    for(String token : tokenList){
                        loginUserMap.remove(token);
                    }
                }
                catch (Exception ex){
                    logger.error(ex.getMessage(),ex);
                }
            }
        }
    }

}
