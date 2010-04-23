<%@ page language="java" import="java.util.*" pageEncoding="GBK"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>My JSP 'welcome.jsp' starting page</title>
	</head>
	<body>
		<f:view>
			<h3>
				奥运啦啦队员海选报名啦！！！
			</h3>
			<h3>
				请选择：
			</h3>
			<h:outputLink value="register.action">
				<h:outputText value="我现在就要报名！！！" />
			</h:outputLink>
			<h:outputLink value="view.action">
				<h:outputText value="我想看看都谁报名了！！！" />
			</h:outputLink>
		</f:view>
	</body>
</html>
