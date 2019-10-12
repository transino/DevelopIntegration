package net.zhangqiu.project.cooperation.framework;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class NavigationContext {
	private Map<String,String> navigationMap;

	public void setNavigationMap(Map<String,String> navigationMap) {
		this.navigationMap = navigationMap;
	}

	public Map<String,String> getNavigationMap() {
		return navigationMap;
	}
	
	@PostConstruct
	public void init(){
		navigationMap = new LinkedHashMap<String,String>();
	}
}
