<%@ page language="java" contentType="text/html; charset=GBK"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<html>
	<head>
		<title>查看所有选手报名信息</title>
	</head>
	<body>
		<f:view>
			<h3>
				已经有这么多人报名了！！！！！
			</h3>
			<h3>
				这里有我吗？没有请单击加入他们
			</h3>
			<h:dataTable value="#{action.select}" var="p" style="text-align:center;width:500px" border="1">
				<h:column>
					<f:facet name="header">
						<h:outputText value="选手编号" />
					</f:facet>
					<h:outputLink value="viewOne.action">
						<f:param name="playerid" value="#{p.id}" />
						<h:outputText value="#{p.id}" />
					</h:outputLink>
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="选手姓名" />
					</f:facet>
					<h:outputText value="#{p.name}" />
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="选手性别" />
					</f:facet>
					<h:outputText value="#{p.sex}" />
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="选手年龄" />
					</f:facet>
					<h:outputText value="#{p.age}" />
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="联系方式" />
					</f:facet>
					<h:outputText value="#{p.tel}" />
				</h:column>
			</h:dataTable>
			<p>
				<h:outputLink value="register.action">
					<h:outputText value="这里还没有我！！" />
				</h:outputLink>
			</p>
		</f:view>
	</body>
</html>
