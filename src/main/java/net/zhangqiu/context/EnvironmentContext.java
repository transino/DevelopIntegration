package net.zhangqiu.context;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import net.zhangqiu.utils.AESKeyUtils;
import net.zhangqiu.utils.FileUtils;
import net.zhangqiu.utils.HardWareUtils;
import net.zhangqiu.utils.StrUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentContext {
	@Value("${environmentContext.encoding}")
	private String encoding;
	@Value("${environmentContext.dateFormt}")
	private String dateFormt;
    @Value("${environmentContext.datetimeFormt}")
    private String datetimeFormt;

    @Value("${environmentContext.defaultProjectName}")
	private String defaultProjectName;
	@Value("${environmentContext.defaultDatasourceName}")
	private String defaultDatasourceName;

    @Value("${environmentContext.ssoUserTimeout}")
	private Integer ssoUserTimeout;

    private Map<String,String> defaultDatasourceNameMap;

    private String runLogPath;
	private String runPath;
	private String tempPath;
	private String sourcePath;
	private String resourcesPath;
	private String lisencePath;
	private String ssoPath;

    private boolean authorization;

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setRunPath(String runPath) {
		this.runPath = runPath;
	}
	public String getRunPath() {
		return runPath;
	}
	public void setTempPath(String tempPath) {
		this.tempPath = tempPath;
	}
	public String getTempPath() {
		return tempPath;
	}
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	public String getSourcePath() {
		return sourcePath;
	}
	public void setResourcesPath(String resourcesPath) {
		this.resourcesPath = resourcesPath;
	}
	public String getResourcesPath() {
		return resourcesPath;
	}
	public void setDateFormt(String dateFormt) {
		this.dateFormt = dateFormt;
	}
	public String getDateFormt() {
		return dateFormt;
	}
	public void setAuthorization(boolean authorization) {
		this.authorization = authorization;
	}
	public boolean isAuthorization() {
		return authorization;
	}	
	public void setLisencePath(String lisencePath) {
		this.lisencePath = lisencePath;
	}
	public String getLisencePath() {
		return lisencePath;
	}
    public String getSsoPath() {
        return ssoPath;
    }
    public void setSsoPath(String ssoPath) {
        this.ssoPath = ssoPath;
    }
	public void setDefaultProjectName(String defaultProjectName) {
		this.defaultProjectName = defaultProjectName;
	}
	public String getDefaultProjectName() {
		return defaultProjectName;
	}
	public void setDefaultDatasourceName(String defaultDatasourceName) {
		this.defaultDatasourceName = defaultDatasourceName;
	}
	public String getDefaultDatasourceName() {
		return defaultDatasourceName;
	}
	public void setDefaultDatasourceNameMap(Map<String,String> defaultDatasourceNameMap) {
		this.defaultDatasourceNameMap = defaultDatasourceNameMap;
	}
	public Map<String,String> getDefaultDatasourceNameMap() {
		return defaultDatasourceNameMap;
	}
    public String getDatetimeFormt() {
        return datetimeFormt;
    }

    public void setDatetimeFormt(String datetimeFormt) {
        this.datetimeFormt = datetimeFormt;
    }

    public String getRunLogPath() {
        return runLogPath;
    }

    public void setRunLogPath(String runLogPath) {
        this.runLogPath = runLogPath;
    }

    public Integer getSsoUserTimeout() {
        return ssoUserTimeout;
    }

    public void setSsoUserTimeout(Integer ssoUserTimeout) {
        this.ssoUserTimeout = ssoUserTimeout;
    }

    @PostConstruct
	public void init() throws Exception{

		runPath = Thread.currentThread().getContextClassLoader().getResource("").getPath().replace("%20", " ").replace("%e5%bc%a0%e7%a7%8b", "张秋");
		sourcePath = System.getProperty("user.dir") +  File.separator;
		resourcesPath = "src" +  File.separator + "main" +  File.separator + "resources" +  File.separator;
		lisencePath = resourcesPath + "lisence";
		tempPath = resourcesPath + "temp" +  File.separator;
        ssoPath = resourcesPath + "sso" +  File.separator;
        runLogPath = "logs";
        if(ssoUserTimeout != null && ssoUserTimeout > 0){
            ssoUserTimeout = ssoUserTimeout*60*1000;
        }

		defaultDatasourceNameMap = new HashMap<String,String>();
		if(!StrUtils.isEmpty(defaultDatasourceName)){
			String[] projectDatasourceNames = defaultDatasourceName.split(",",-1);
			for(String projectDatasourceName : projectDatasourceNames){
				defaultDatasourceNameMap.put(projectDatasourceName.split("\\.",-1)[0], projectDatasourceName.split("\\.",-1)[1]);
			}
		}
		
		String motherboardSN = HardWareUtils.getMotherboardSN();
		if(!StrUtils.isEmpty(motherboardSN)){
			File lisenceFile = new File(lisencePath);
			if(lisenceFile.exists()){
				String lisence = FileUtils.readFile(lisencePath, encoding);
				String decryString = AESKeyUtils.DecryString(lisence, null);
				if(motherboardSN.equals(decryString)){
					this.authorization = true;
				}
			}
		}
		else{
			this.authorization = true;
		}
		
		File tempFile = new File(tempPath);
		if(!tempFile.exists()){
			tempFile.mkdirs();
		}

        File ssoFile = new File(ssoPath);
        if(!ssoFile.exists()){
            ssoFile.mkdirs();
        }
	}
}
