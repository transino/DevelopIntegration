package net.zhangqiu.service.check.entity.result;

import java.util.ArrayList;
import java.util.List;

public class DataListResult {
    private int transCount;
    private int checkCount;
	private int checkFailCount;
	private int checkSuccessCount;
	private int forceCheckFailCount;
	private int forceCheckSuccessCount;
	private int exceptionCount;
	private boolean check;
	private boolean forceCheck;
	private boolean exception;
	private List<DataResult> dataResultList;
	public boolean isCheck() {
		return check;
	}
	public void setCheck(boolean check) {
		this.check = check;
	}
	public boolean isForceCheck() {
		return forceCheck;
	}
	public void setForceCheck(boolean forceCheck) {
		this.forceCheck = forceCheck;
	}
	public boolean isException() {
		return exception;
	}
	public void setException(boolean exception) {
		this.exception = exception;
	}
	public List<DataResult> getDataResultList() {
		return dataResultList;
	}
	public int getCheckCount() {
		return checkCount;
	}
	public void setCheckCount(int checkCount) {
		this.checkCount = checkCount;
	}
	public int getCheckFailCount() {
		return checkFailCount;
	}
	public void setCheckFailCount(int checkFailCount) {
		this.checkFailCount = checkFailCount;
	}
	public int getCheckSuccessCount() {
		return checkSuccessCount;
	}
	public void setCheckSuccessCount(int checkSuccessCount) {
		this.checkSuccessCount = checkSuccessCount;
	}
	public int getForceCheckFailCount() {
		return forceCheckFailCount;
	}
	public void setForceCheckFailCount(int forceCheckFailCount) {
		this.forceCheckFailCount = forceCheckFailCount;
	}
	public int getForceCheckSuccessCount() {
		return forceCheckSuccessCount;
	}
	public void setForceCheckSuccessCount(int forceCheckSuccessCount) {
		this.forceCheckSuccessCount = forceCheckSuccessCount;
	}
	public int getExceptionCount() {
		return exceptionCount;
	}
	public void setExceptionCount(int exceptionCount) {
		this.exceptionCount = exceptionCount;
	}
	public void setDataResultList(List<DataResult> dataResultList) {
		this.dataResultList = dataResultList;
	}
    public int getTransCount() {
        return transCount;
    }

    public void setTransCount(int transCount) {
        this.transCount = transCount;
    }
	public DataListResult(){
		check = true;
		forceCheck = true;
		exception = false;
		dataResultList = new ArrayList<DataResult>();
	}
}
