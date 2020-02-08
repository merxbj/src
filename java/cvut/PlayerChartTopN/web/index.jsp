<%@page contentType="text/html; charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%-- nastartovani session --%>
<%@page session="true" %>

<%-- nacteni souboru - pred kompilaci!!! --%>
<%@include file="/header.jspf" %>

<%-- predani rizeni - za behu!!! --%>
<jsp:include page="${requestScope.showPage}.jsp" />

<%@include file="/footer.jspf" %>
