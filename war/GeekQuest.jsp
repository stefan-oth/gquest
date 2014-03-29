<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="de.oth.app.geekquest.dao.CharacterDAO" %>
<%@ page import="de.oth.app.geekquest.dao.DAOManager" %>
<%@ page import="de.oth.app.geekquest.model.CharClass" %>
<%@ page import="de.oth.app.geekquest.model.Character" %>
<%@ page import="de.oth.app.geekquest.model.Mission" %>

<!DOCTYPE html>


<%@page import="java.util.ArrayList"%>

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
Character character = null;
String headline = "";
Long characterId = null;
    
if (user != null){
    url = userService.createLogoutURL(request.getRequestURI());
    urlLinktext = "Logout";
    List<Character> characters = dao.findByUserId(user.getUserId());
    if (characters.size() <= 0) {
        character = new Character();
        characterId = -1l;
        headline = "Create your Character";
    } else {
        character = characters.get(0);
        characterId = character.getKey().getId();
        headline = "Edit your Character";
    }
}
%>
  <div style="width: 100%;">
    <div class="line"></div>
    <div class="topLine">
      <!--div style="float: left;"><img src="images/todo.png" /></div-->
      <div style="float: left;" class="headline">GeekQuest</div>
      <div style="float: right;"><a href="<%=url%>"><%=urlLinktext%></a> <%=(user==null? "" : user.getNickname())%></div>
    </div>
  </div>

<div class="main">
<div class="headline"><%=headline %></div>

<% if (user != null){ %> 

<form action="/save" method="post" accept-charset="utf-8">
  <input type="hidden" name="characterId" value="<%=characterId%>"/>
  <table>
    <tr>
      <td><label for="name">Name:</label></td>
      <td><input type="text" name="name" id="name" size="65" value="<%= character.getName() == null ? "" : character.getName() %>"/></td>
    </tr>
    <tr>
      <td><label for="charclass">Character class:</label></td>
      <td>
        <select name="charclass" size="1">
             <option value="">Please choose</option>
             <option value="Hobbit"<%= CharClass.Hobbit.equals(character.getCharClass()) ? " selected" : "" %>>Hobbit</option>
             <option value="Mage"<%= CharClass.Mage.equals(character.getCharClass()) ? " selected" : "" %>>Mage</option>
             <option value="Dwarf"<%= CharClass.Dwarf.equals(character.getCharClass()) ? " selected" : "" %>>Dwarf</option>
             <option value="Elf"<%= CharClass.Elf.equals(character.getCharClass()) ? " selected" : "" %>>Elf</option>
           </select>
      </td>
    </tr>
    <tr>
      <td><label for="Health">Health status:</label></td>
      <td><label for="Healthvalue"><%= character.getHealth() != null ? character.getHealth() : "" %></label></td>
    </tr>
    <tr>
      <td valign="top"><label for="Missions">Missions:</label></td>
      <td>
        <table>
          <tr>
            <th>Description</th>
            <th>Accomplished</th>
          </tr>
<% for (Mission mission : character.getMissions()) {%>
          <tr> 
            <td><%=mission.getDescription()%></td>
            <td><input type="checkbox" name="accomplished" value="accomplished" disabled <%= mission.getIsAccomplished() ? " checked" : "" %>/></td>
          </tr> 
<%}%>
        </table>
      </td>
    </tr>
    <tr>
      <td colspan="2" align="right"><input type="submit" value="Save"/></td>
    </tr>
  </table>
</form>

<% }else{ %>

Please login with your Google account

<% } %>

</div>
</body>
</html> 