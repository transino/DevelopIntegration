package net.zhangqiu.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

public class StrUtilsTest {

    @Test(groups = {"BaseTestNG"})
    public void isEmpty(){
        Assert.assertEquals(StrUtils.isEmpty(""),true);
        Assert.assertEquals(StrUtils.isEmpty(""),true);
        Assert.assertEquals(StrUtils.isEmpty(" "),false);
        Assert.assertEquals(StrUtils.isEmpty("a"),false);
    }
}
