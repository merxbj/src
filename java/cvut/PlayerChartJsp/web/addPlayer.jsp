<%-- 
    Document   : addPlayer
    Created on : 23.5.2011, 22:51:17
    Author     : eTeR
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <jsp:include page="header.jsp"/>
        <h1>Přidat uživatele</h1>            
        <form action="./start" method="POST">
            <div>
                <label>Jméno</label>
                <input type="text" name="jmeno" value="<%= request.getParameter("jmeno") != null ? request.getParameter("jmeno") : "" %>" />
            </div>
            <div>
                <label>Skóre</label>
                <input type="text" name="skore" value="<%= request.getParameter("skore") != null ? request.getParameter("skore") : "" %>" />
            </div>
            <div>
                <input type="hidden" name="action" value="pridej" />
            </div>
            <div>
                <input type="submit" name="odeslat" value="Odeslat" />
            </div>
        </form>
    </body>
</html>
