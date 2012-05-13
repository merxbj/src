<%-- 
    Document   : scoreBoard
    Created on : 13.5.2012, 1:08:18
    Author     : eTeR
--%>

<%@ taglib prefix="u" tagdir="/WEB-INF/tags/" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>WebPexeso - Score Board</title>
        <script type="text/javascript" src="resources/pageScripts.js"></script>
        <link rel="stylesheet" href="resources/style.css" title="style" />
    </head>
    <body>
        <h1>WebPexeso - Score Board</h1>
        <h2><a href="index.jsp">Go back to menu!</a></h2>
        <u:scoreBoard scoreBoard="${sessionScope.scoreBoard}"/>
    </body>
</html>
