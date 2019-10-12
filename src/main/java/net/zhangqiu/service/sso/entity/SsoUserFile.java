package net.zhangqiu.service.sso.entity;

import java.util.ArrayList;
import java.util.List;

public class SsoUserFile {
    private List<SsoUser> ssoUserList;

    public List<SsoUser> getSsoUserList() {
        return ssoUserList;
    }

    public void setSsoUserList(List<SsoUser> ssoUserList) {
        this.ssoUserList = ssoUserList;
    }

    public SsoUserFile(){
        ssoUserList = new ArrayList<SsoUser>();
    }

}
