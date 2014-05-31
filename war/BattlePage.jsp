<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="de.oth.app.geekquest.dao.CharacterDAO" %>
<%@ page import="de.oth.app.geekquest.dao.DAOManager" %>
<%@ page import="de.oth.app.geekquest.model.Character" %>
<%@ page import="com.google.appengine.api.blobstore.BlobKey" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.images.ImagesService" %>
<%@ page import="com.google.appengine.api.images.ImagesServiceFactory" %>
<%@ page import="com.google.appengine.api.images.ServingUrlOptions" %>

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
long MAX_HP = 100;

BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
CharacterDAO dao = DAOManager.getCharacterDAO();

UserService userService = UserServiceFactory.getUserService();
User user = userService.getCurrentUser();

String url = userService.createLoginURL(request.getRequestURI());
String urlLinktext = "Login";
Character character = null;
Long characterId = null;
String playerKeyString = null;

ImagesService imagesService = ImagesServiceFactory.getImagesService();
String imageServingUrl = null;
    
if (user != null){
    url = userService.createLogoutURL(request.getRequestURI());
    urlLinktext = "Logout";
}

List<Character> characters = dao.findByNickName("Ragnaros");    
    
if (characters.size() > 0) {
    character = characters.get(0);
    characterId = character.getId();
    playerKeyString = character.getParentKey().getString();
    if (character.getImageBlobKey() != null) {
        BlobKey blobKey = new BlobKey(character.getImageBlobKey());
        imageServingUrl = imagesService.getServingUrl(
            ServingUrlOptions.Builder.withBlobKey(blobKey));
    }
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
<div class="headline">Battle</div>

<% if (character != null) { %> 
<div class="stats">
<form action="/attack" method="post" accept-charset="utf-8">
  <input type="hidden" name="characterId" value="<%=characterId%>"/>
  <input type="hidden" name="playerKeyString" value="<%=playerKeyString%>"/>
  <table>
  <tr>
  <td>
<% if (imageServingUrl != null) { %>
    <div><img src="<%= imageServingUrl %>" /></div>
<%}%>
  </td>
  <td>
  <table cellpadding="5">
    <tr>
      <td><label for="name">Name:</label></td>
      <td><label for="namevalue"><%= character.getNickName() == null ? "" : character.getNickName() %></label></td>
    </tr>
    <tr>
      <td><label for="Healthpoints">Health points:</label></td>
      <td><progress value="<%= character.getHealth() != null ? character.getHealth() : 0 %>" max="<%= MAX_HP%>">
          </progress>
      </td>
    </tr>
    <tr>
      <td><label for="State">State:</label></td>
      <td><label for="Statevalue"><%= character.getHealth() != null && character.getHealth() > 0 ? "alive & kicking" : "defeated" %></label></td>
    </tr>
    <tr>
      <td colspan="2" align="center"><input type="submit" value="Attack"/></td>
    </tr>
  </table>
  </td>
  </tr>
  </table>
</form>
</div>

<% }else{ %>

No one is here to battle

<% } %>

</div>
</body>
</html> 