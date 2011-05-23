<%-- 
    Document   : listPlayers
    Created on : 23.5.2011, 22:51:58
    Author     : eTeR
--%>

<%@page import="java.io.IOException"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="model.Player"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%!
    void printPlayerList(JspWriter out, HttpSession session) throws IOException {
        List<Player> players = (List<Player>) session.getAttribute("PlayerList");
        if (players != null) {
            out.println("<ul>");
            for (Player p : players) {
                out.print("<li>");
                out.print(String.format("%s | %d", p.getName(), p.getScore()));
                out.println("</li>");
            }
            out.println("</ul>");
        }
    }
%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Player Chart - Player List</title>
    </head>
    <body>
        <jsp:include page="header.jsp"/>
        <% printPlayerList(out, session); %>
    </body>
</html>

