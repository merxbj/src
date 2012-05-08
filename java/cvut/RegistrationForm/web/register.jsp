<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<f:view>
    <html>
        <head>
            <title>Register</title>
        </head>
        <body>
            <h:form>
                <h:outputLabel value="First Name:" style="display: block">
                    <h:inputText id="firstName" value="#{userData.firstName}">
                        <f:validateRegex pattern="^[A-Z][a-zěščřžýáíéúů]+"/>
                    </h:inputText>
                </h:outputLabel>
                <h:message for="firstName" style="color: red"/>

                <h:outputLabel value="Last Name:" style="display: block">
                    <h:inputText id="lastName" value="#{userData.lastName}">
                        <f:validateRegex pattern="^[A-Z][a-zěščřžýáíéúů]+"/>
                    </h:inputText>
                </h:outputLabel>
                <h:message for="lastName" style="color: red"/>

                <h:outputLabel value="Sex:" style="display: block">
                    <h:selectOneListbox id="sex" value="#{userData.sex}">
                        <f:selectItems value="#{userData.sexes}"/>
                    </h:selectOneListbox>
                </h:outputLabel>
                <h:message for="sex" style="color: red"/>

                <h:outputLabel value="Birth Date:" style="display: block">
                    <h:inputText id="birthDate" value="#{userData.birthDate}">
                        <f:convertDateTime dateStyle="short" />
                        <f:validator validatorId="BirthDateValidator" />
                    </h:inputText>
                </h:outputLabel>
                <h:message for="birthDate" style="color: red"/>

                <h:commandButton action="#{pageController.submit}" value="Register!" />
            </h:form>
        </body>
    </html>
</f:view>
