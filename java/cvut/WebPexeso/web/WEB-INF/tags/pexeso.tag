<%-- 
    Document   : pexeso
    Created on : 8.5.2012, 1:55:46
    Author     : eTeR
--%>

<%@tag import="cz.cvut.fef.ad7b39wpa.model.game.core.Card"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@tag description="Draws the pexeso field." pageEncoding="UTF-8"%>

<%-- The list of normal or fragment attributes can be specified here: --%>
<%@attribute name="field" type="cz.cvut.fef.ad7b39wpa.model.game.core.Field"%>

<table>
    <c:forEach items="${field.xyfield}" var="row">
        <tr>
            <c:forEach items="${row}" var="card">
                <td>
                    <img src="resources/img/card_bottom.jpg" height="${(768 - 100)*0.95 / field.size}" width="auto" onclick="onCardClick(${card.x},${card.y})" id="(${card.x},${card.y})" />
                </td>
            </c:forEach>
        </tr>
    </c:forEach>
</table>
