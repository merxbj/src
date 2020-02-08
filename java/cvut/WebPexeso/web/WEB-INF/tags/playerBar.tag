<%-- 
    Document   : score
    Created on : 8.5.2012, 20:31:09
    Author     : eTeR
--%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@tag description="put the tag description here" pageEncoding="UTF-8"%>

<%-- The list of normal or fragment attributes can be specified here: --%>
<%@attribute name="game" type="cz.cvut.fef.ad7b39wpa.model.game.core.Game"%>

<table>
    <tr>
        <th>Player Name</th>
        <th>Attempts</th>
        <th>Score</th>
    </tr>
    <c:forEach items="${game.players}" var="player">
        <c:choose>
            <c:when test="${game.playerOnTurn.name == player.name}">
                <tr class="playerOnTurn">
            </c:when>
            <c:otherwise>
                <tr>
            </c:otherwise>
        </c:choose>

            <td>${player.name}</td>
            <td>${player.attempts}</td>
            <td>${player.score}</td>
        </tr>
    </c:forEach>
</table>
