<%-- 
    Document   : index
    Created on : 13.5.2012, 1:33:26
    Author     : eTeR
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>WebPexeso</title>
        <script type="text/javascript" src="resources/pageScripts.js"></script>
        <link rel="stylesheet" href="resources/style.css" title="style" />
    </head>
    <body>
        <h1>Welcome in WebPexeso!</h1>
        <h2>Where do you want to go?</h2>
        <ul>
            <%
                if (session.getAttribute("game") != null) {
                    out.write("<li><a href=\"Game\">Continue an existing game!</a></li>");
                }
            %>
            <li><a href="NewGame">Play a new game!</a></li>
            <li><a href="ScoreBoard">See the scoreboard!</a></li>
        </ul>
    </body>
</html>
