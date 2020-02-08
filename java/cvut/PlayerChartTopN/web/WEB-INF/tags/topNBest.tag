<%-- 
    Document   : topNBest
    Created on : 10.4.2012, 16:30:05
    Author     : eTeR
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@tag description="Prints top N best players" pageEncoding="UTF-8"%>

<%-- The list of normal or fragment attributes can be specified here: --%>
<%@attribute name="players" type="java.util.Collection<cz.cvut.fel.a7b39wpa.c03jsp.model.Player>"%>
<%@attribute name="n" type="java.lang.Integer"%>

<%-- any content can be specified here e.g.: --%>
<table>
    <tr>
        <th>Hráč</th><th>Skóre</th>
    </tr>
    <c:forEach var="player" items="${sessionScope.players}" varStatus="status" end="${n - 1}">
        <c:choose>
            <c:when test="${status.count % 2 == 0}">
                <tr class="even">
                    <td>${player.name}</td><td>${player.score}</td>
                </tr>
            </c:when>
            <c:otherwise>
                <tr class="odd">
                    <td>${player.name}</td><td>${player.score}</td>
                </tr>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</table>
