<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>


<h2>Přidat uživatele</h2>
<%-- ukazka, jak nebudeme provadet vypis - lepsi je pouzit EL --%>
<form action="<% out.print(response.encodeURL(request.getContextPath() + "/start"));%>" method="POST">
    <div>
        <label>Jméno</label>
        <%-- pristup k parametru poslanemu pres HTTP GET/POST --%>
        <input type="text" name="jmeno" value="${param['jmeno']}" />
    </div>
    <div>
        <label>Skóre</label>
        <input type="text" name="skore" value="${param['skore']}" />
    </div>
    <div>
        <input type="hidden" name="akce" value="pridej" />
    </div>
    <div>
        <input type="submit" name="odeslat" value="Odeslat" />
    </div>
</form>
