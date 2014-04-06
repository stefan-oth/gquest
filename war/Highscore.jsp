<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="de.oth.app.geekquest.dao.CharacterDAO" %>
<%@ page import="de.oth.app.geekquest.dao.DAOManager" %>
<%@ page import="de.oth.app.geekquest.model.CharClass" %>
<%@ page import="de.oth.app.geekquest.model.Character" %>

<!DOCTYPE html>

<html>
  <head>
    <title>GeekQuest</title>
    <link rel="stylesheet" type="text/css" href="css/main.css"/>
      <meta charset="utf-8"> 
  </head>
  <body>
<%
CharacterDAO dao = DAOManager.getCharacterDAO();

UserService userService = UserServiceFactory.getUserService();
User user = userService.getCurrentUser();

String url = userService.createLoginURL(request.getRequestURI());
String urlLinktext = "Login";

String pageString = request.getParameter("page");
Integer pageIdx = 0;
if (pageString != null) {
    try {
    pageIdx = Integer.valueOf(pageString);
    } catch (NumberFormatException ex) {
        //ignore
    }
}

List<Character> characters = new ArrayList<Character>();
int max_rows = 10;
boolean next = false;

//if (user != null) {
    characters = dao.getCharactersForHighscore(max_rows + 1, pageIdx * max_rows);
    if (characters.size() > max_rows) {
        next = true;
        characters.remove(characters.size() - 1);    
    }
//}

%>
    <div style="width: 100%;">
      <div class="line"></div>
      <div class="topLine">
      
        <div style="float: left;" class="headline">GeekQuest</div>
        <div style="float: right;"><a href="<%=url%>"><%=urlLinktext%></a> <%=(user==null? "" : user.getNickname())%></div>
      </div>
    </div>
    <div class="main">
      <div class="headline">Highscore</div>

<!--% if (user != null){ %--> 
          
      <table>
        <tr>
          <th width="100px">Name</th>
          <th width="100px">Class</th>
          <th width="100px">Score</th>
        </tr>
<% for (Character character : characters) {%>
        <tr> 
          <td><%=character.getName()%></td>
          <td><%=character.getCharClass().toString()%></td>
          <td><%=character.getScore()%></td>
        </tr> 
<%}%>
        <tr>
          <td colspan="3" align="right">
            <% if (pageIdx > 0) { %>
            <a href="/highscore?page=<%=pageIdx - 1%>" >< Prev</a>
            <% } %>
            <%= pageIdx + 1 %>
            <% if (next) { %>
            <a href="/highscore?page=<%=pageIdx + 1%>" >Next ></a>
            <% } %>
          </td>
        </tr>
      </table>
<!--% }else{ %-->

<!--Please login with your Google account-->

<!--% } %-->
    </div>
  </body>
</html> 
