<%-- 
    Document   : index
    Created on : 5.5.2012, 12:21:18
    Author     : eTeR
--%>

<%@ taglib prefix="u" tagdir="/WEB-INF/tags/" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>WebPexeso - Game</title>
        <script type="text/javascript" src="resources/pageScripts.js"></script>
        <link rel="stylesheet" href="resources/style.css" title="style" />
    </head>
    <body onload="initializeGame()">
        <div id="container">
            <div id="field">
                <h1>Pexeso</h1>
                <h2><a href="index.jsp">Go back to menu!</a></h2>

                <u:pexeso field="${sessionScope.game.field}"/>
            </div>

            <div id="playerBar">
                <u:playerBar game="${sessionScope.game}"/>
            </div>

            <div id="control">
                <form action="" method="post" onsubmit="return passTurn()" id="gameControll">
                    <input type="hidden" name="fieldState" id="fieldState" value="${sessionScope.game.field.serialized}" />
                    <input type="submit" name="Submission"  value="Pass Turn" />
                </form>
            </div>
        </div>
    </body>
</html>
