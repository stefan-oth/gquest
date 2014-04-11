<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<!DOCTYPE html>

<html>
  <head>
    <title>GeekQuest</title>
    <link rel="stylesheet" type="text/css" href="css/main.css"/>
      <meta charset="utf-8"> 
  </head>
  <body>
<%

UserService userService = UserServiceFactory.getUserService();
User user = userService.getCurrentUser();

String url = userService.createLoginURL(request.getRequestURI());
String urlLinktext = "Login";

String error = request.getParameter("error");
String backToUrl = request.getParameter("url");
if (backToUrl == null || backToUrl.equals("")) {
  backToUrl = "/";
}

%>
    <div style="width: 100%;">
      <div class="line"></div>
      <div class="topLine">
      
        <div style="float: left;" class="headline">GeekQuest</div>
        <div style="float: right;"><a href="<%=url%>"><%=urlLinktext%></a> <%=(user==null? "" : user.getNickname())%></div>
      </div>
    </div>
    <div class="main">
      <div class="headline">Error</div>
      <%=error%><br>
      <a href="<%=backToUrl%>" >Back></a>
    </div>
  </body>
</html> 
