<%-- 
    Document   : index
    Created on : 5.5.2012, 12:21:18
    Author     : eTeR
--%>

<%@page import="cz.cvut.fef.ad7b39wpa.model.Game"%>
<%@ taglib prefix="u" tagdir="/WEB-INF/tags/" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <script type="text/javascript" src="resources/pageScripts.js"></script>
        <link rel="stylesheet" href="resources/style.css" title="style" />
    </head>
    <body onload="initializeGame()">
        <h1>Pexeso</h1>

        <u:pexeso game="${sessionScope.game}"/>

        <form action="" method="post" onsubmit="return passTurn()" id="gameControll">
            <input type="hidden" name="fieldState" id="fieldState" value="${sessionScope.game.serialized}" />
            <input type="hidden" name="aiState" id="aiState" value=""/>
            <input type="submit" name="Submission"  value="Pass Turn" />
        </form>
    </body>
</html>
