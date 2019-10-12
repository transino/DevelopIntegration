package net.zhangqiu.service.check.entity;

import java.util.ArrayList;
import java.util.List;

public class CheckRuleTableFile {
	private List<CheckRuleTable> checkRuleTableList;
	public void setCheckRuleTableList(List<CheckRuleTable> checkRuleTableList) {
		this.checkRuleTableList = checkRuleTableList;
	}
	public List<CheckRuleTable> getCheckRuleTableList() {
		return checkRuleTableList;
	}
	public CheckRuleTableFile(){
		checkRuleTableList = new ArrayList<CheckRuleTable>();
	}
}
