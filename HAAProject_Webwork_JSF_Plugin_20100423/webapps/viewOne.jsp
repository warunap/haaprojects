<%@ page language="java" contentType="text/html; charset=GBK"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<html>
	<head>
		<title>修改信息</title>
	</head>
	<body>
		<f:view>
			<h3>
				同一个世界，同一个梦想
			</h3>
			<h3>
				感谢您对奥运的支持，请修改下列信息>>>
			</h3>
			<form target="_self" method="post" action="<%=request.getContextPath()%>/modify.action">
			</form>
			<h:form id="player" target="_self">
				<h:inputHidden value="#{action.playerid}" />
				<h:panelGrid columns="3">
					<h:outputText value="选手编号" />
					<h:inputText id="id" value="#{action.player.id}" size="30" required="true" />
					<h:message for="id" />
					<h:outputText value="选手姓名" />
					<h:inputText id="name" size="30" value="#{action.player.name}" required="true">
						<f:validateLength minimum="2" maximum="100" />
					</h:inputText>
					<h:message for="name" />
					<h:outputText value="选手性别" />
					<h:inputText id="sex" size="30" value="#{action.player.sex}" required="true">
						<f:validateLength minimum="2" maximum="6" />
					</h:inputText>
					<h:message for="sex" />
					<h:outputText value="选手年龄" />
					<h:inputText id="age" size="30" value="#{action.player.age}" required="true">
						<f:validateLength minimum="1" maximum="100" />
					</h:inputText>
					<h:message for="age" />
					<h:outputText value="联系方式" />
					<h:inputText id="tel" size="30" value="#{action.player.tel}" required="true">
						<f:validateLength minimum="2" maximum="13" />
					</h:inputText>
					<h:message for="tel" />
				</h:panelGrid>
				<h3>
					感谢您对奥运的支持，提交前请确认您的信息
				</h3>
				<br />
				<h:commandButton action="modify" id="modify" value="modify"></h:commandButton>
			</h:form>

		</f:view>
	</body>
</html>