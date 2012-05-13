<%-- 
    Document   : scoreBoard
    Created on : 13.5.2012, 3:27:12
    Author     : eTeR
--%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@tag description="put the tag description here" pageEncoding="UTF-8"%>

<%-- The list of normal or fragment attributes can be specified here: --%>
<%@attribute name="scoreBoard" type="cz.cvut.fef.ad7b39wpa.model.game.score.ScoreBoard"%>

<table>
    <tr>
        <th>Player Name</th>
        <th>Games</th>
        <th>Wins</th>
    </tr>
    <c:forEach items="${scoreBoard.playerScores}" var="playerScore">
        <tr>
            <td>${playerScore.name}</td>
            <td>${playerScore.games}</td>
            <td>${playerScore.wins}</td>
        </tr>
    </c:forEach>
</table>
