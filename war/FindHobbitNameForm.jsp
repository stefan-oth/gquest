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
%>
    <div style="width: 100%;">
      <div class="line"></div>
      <div class="topLine">
      
        <div style="float: left;" class="headline">GeekQuest</div>
        <div style="float: right;"><a href="<%=url%>"><%=urlLinktext%></a> <%=(user==null? "" : user.getNickname())%></div>
      </div>
    </div>
    <div class="main">
      <p>Please enter your gender, firstname and lastname.</p>
      <form action="/gethobbitname" method="post" accept-charset="utf-8">
        <table>
          <tr>
            <td><label for="gender">Gender:</label></td>
            <td>
              <input type="radio" name="gender" value="male">Male
              <input type="radio" name="gender" value="female">Female
            </td>
          </tr>
          <tr>
            <td><label for="name">Firstname:</label></td>
            <td><input type="text" name="firstname" id="firstname" size="65" value=""/></td>
          </tr>
          <tr>
            <td><label for="name">Lastname:</label></td>
            <td><input type="text" name="lastname" id="lastname" size="65" value=""/></td>
          </tr>
          <tr>
            <td colspan="2" align="right"><input type="button" value="Back" onclick="location.href='/geekquest'"/><input type="submit" value="Generate"/></td>
          </tr>
        </table>
      </form>
    </div>
  </body>
</html> 
