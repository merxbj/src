<%@ taglib prefix="u" tagdir="/WEB-INF/tags/" %>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%-- importy potrebnych trid --%>
<%@page import="java.util.ArrayList" %>
<%@page import="cz.cvut.fel.a7b39wpa.c03jsp.model.Player" %>

<h2>Hráči</h2>
<u:topNBest players="${sessionScope.players}" n="${4}"/>

<%--
zatim neumime JSTL a vlastni tagy, takze vypis pole napiseme kompletne v Jave
  * vsimete si, ze objekty request, response, out atp. jsou implicitne k dispozici
--%>


