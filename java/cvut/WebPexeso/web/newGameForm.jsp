<%-- 
    Document   : newGameForm
    Created on : 13.5.2012, 3:53:15
    Author     : eTeR
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script type="text/javascript" src="resources/pageScripts.js"></script>
        <link rel="stylesheet" href="resources/style.css" title="style" />
        <title>WebPexeso - New Game</title>
    </head>
    <body onload="onNewGameLoad()">
        <h1>WebPexeso - Create New Game!</h1>
        <h2><a href="index.jsp">Go back to menu!</a></h2>
        <form action="" method="post" id="newGame">
            <table>
                <c:forTokens items="1,2,3,4" delims="," var="playerNumber">
                    <tr>
                        <td><input type="checkbox" name="PlayerEnabled" checked="checked" id="PlayerEnabled${playerNumber}" onclick="updatePlayerInputEnableness(${playerNumber})"/></td>
                        <td><input type="text" name="PlayerName" value="Player ${playerNumber}" id="PlayerName${playerNumber}"/></td>
                        <td>
                            <select name="PlayerType" id="PlayerType${playerNumber}">
                                <option value="-1">Human</option>
                                <option value="0">PC - Low</option>
                                <option value="1">PC - Medium</option>
                                <option value="2">PC - High</option>
                                <option value="3">PC - Godlike</option>
                            </select>
                        </td>
                    </tr>
                </c:forTokens>
            </table>
            <input type="submit" name="NewGame" value="New Game" />
        </form>
    </body>
</html>
