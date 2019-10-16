<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>databasecheck</title>
<script type="text/javascript">
function submitclick() {
	document.form.action = "databaseCheck";
    document.form.submit();
}
</script>
</head>
<body>
    <form name="form" method="post">
        <fieldset>
        	<legend>校验数据库</legend>
        	<p id="buttons">
                <a href="/cooperation">首页</a>
            </p>
            <p>
               <label>数据源：</label>
                <select name="datasourceNameKey" id="datasourceNameKey">
	                <c:forEach items="${datasourcelist}" var="datasource">
	  	                <option value="${datasource}" <c:if test="${currentdatasource==datasource}">selected</c:if>>
		                ${datasource}
		                </option>
	                </c:forEach>
                </select>
                <label>表：</label>
                <select name="tableNameKey" id="tableNameKey">
	                <c:forEach items="${tablelist}" var="table">
	  	                <option value="${table}">
		                ${table}
		                </option>
	                </c:forEach>
                </select>
                <label>最大明细条数：</label>
                <select name="maxDataResult" id="maxDataResult">
                    <option value="1" selected >1</option>
                    <option value="2" >2</option>
                    <option value="5" >5</option>
                    <option value="10" >10</option>
                    <option value="20" >20</option>
                    <option value="50"  >50</option>
                    <option value="100" >100</option>
                    <option value="200" >200</option>
                    <option value="500" >500</option>
                    <option value="1000">1000</option>
                </select>
                <label>缓存条数：</label>
                <select name="cacheLine" id="cacheLine">
                    <option value="100" selected >100</option>
                    <option value="200" >200</option>
                    <option value="500" >500</option>
                    <option value="1000" >1000</option>
                    <option value="2000" >2000</option>
                    <option value="5000"  >5000</option>
                    <option value="10000" >10000</option>
                </select>
                <label>线程数：</label>
                <select name="processorCount" id="processorCount">
                    <option value="1" selected >1</option>
                    <option value="2" >2</option>
                    <option value="5" >5</option>
                    <option value="10" >10</option>
                    <option value="20" >20</option>
                </select>
            </p>
            <p>
                <label>显示结果：</label>
                <select name="resultLevel" id="resultLevel">
                    <option value="0" selected >校验错误数据</option>
                    <option value="1" >强制校验错误数据</option>
                    <option value="2" >异常数据</option>
                    <option value="99" >所有校验数据</option>
                </select>
                <label>结果含checkRule：</label>
                <select name="checkRule" id="checkRule">
                    <option value="true" selected >是</option>
                    <option value="false" >否</option>
                </select>
                <label>结果含ruleExpression：</label>
                <select name="ruleExpression" id="ruleExpression">
                    <option value="true" selected >是</option>
                    <option value="false" >否</option>
                </select>
                <label>结果含description：</label>
                <select name="description" id="description">
                    <option value="true" selected >是</option>
                    <option value="false" >否</option>
                </select>
            </p>
            <p>
                <label>结果含descriptionExpression：</label>
                <select name="descriptionExpression" id="descriptionExpression">
                    <option value="true" selected >是</option>
                    <option value="false" >否</option>
                </select>
                <label>校验规则过滤：</label>
                <select name="optional" id="optional">
                    <option value="true" selected >所有检验规则</option>
                    <option value="false" >只强制校验规则</option>
                </select>
                <label>字段规则过滤：</label>
                <select name="columnResultOnlyFalse" id="columnResultOnlyFalse">
                    <option value="true" selected >只显示校验错误字段</option>
                    <option value="false" >显示所有字段数据</option>
                </select>
            </p>
            <p>
                <label>SQL：</label>
            </p>
            <p>
                <textarea id="sql" name="sql" style="width: 800px; height: 300px"></textarea>
            </p>
            <p id="buttons">
                <input id="reset" type="reset" tabindex="4" value="取消">
                <input id="submit" type="submit" tabindex="5" value="校验" onclick="submitclick()">
            </p>
        </fieldset>
    </form>
</body>
</html>