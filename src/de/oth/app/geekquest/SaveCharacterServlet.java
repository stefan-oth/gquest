package de.oth.app.geekquest;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;

import de.oth.app.geekquest.dao.CharacterDAO;
import de.oth.app.geekquest.dao.DAOManager;
import de.oth.app.geekquest.dao.MissionDAO;
import de.oth.app.geekquest.dao.PlayerDAO;
import de.oth.app.geekquest.model.Character;
import de.oth.app.geekquest.model.CharClass;
import de.oth.app.geekquest.model.Mission;
import de.oth.app.geekquest.model.Player;

@SuppressWarnings("serial")
public class SaveCharacterServlet extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
	    
	    PlayerDAO playerDAO = DAOManager.getPlayerDAO();
        CharacterDAO charDAO = DAOManager.getCharacterDAO();
        MissionDAO missionDAO = DAOManager.getMissionDAO();
	    
	    User user = (User) req.getAttribute("user");
        if (user == null) {
            UserService userService = UserServiceFactory.getUserService();
            user = userService.getCurrentUser();
        }
	    
        Long charId = Long.valueOf(checkNull(req.getParameter("characterId")));
	    String name = checkNull(req.getParameter("name"));
	    CharClass charClass = CharClass.valueOf(checkNull(req.getParameter("charclass")));
	    
	    
	    Player player = playerDAO.findByUserId(user.getUserId());
	    
	    if (player == null) {
            System.out.println("Creating new Player");
            Key<Player> key = playerDAO.create(user.getUserId());
            player = playerDAO.find(key);
	    }
	    
        Character character = charDAO.find(charId, player.getUserId());
	    
	    if (character == null) {
	        System.out.println("Creating new Character");
	        
	        Key<Player> playerKey = Key.create(Player.class, player.getUserId());
	        
	        Key<Character> key = charDAO.create(name, 10, charClass, 0l, playerKey);
	        character = charDAO.find(key);
	        if (character != null) {
	            
	            Key<Character> parentKey = Key.create(character.getParentKey(), 
	                    Character.class, character.getId());
	            
	            Key<Mission> missionKey = missionDAO.create("Destroy ring", false,
	                    parentKey);
	            Mission mission = missionDAO.find(missionKey);
	            character.addMissions(mission);
	            
                missionKey = missionDAO.create("Visit Rivendell", true, parentKey);
                mission = missionDAO.find(missionKey);
                character.addMissions(mission);
	        } else {
	            System.out.println("Error missing Character for current user " + user.toString() 
	                    + " with characterId " + charId);
	        }
	    } else {
            System.out.println("Updating existing Character for current user " + user.toString() 
                    + " with characterId " + charId);
            boolean changed = false;
            if (!name.equals(character.getNickName())) {
                character.setNickName(name);
                changed = true;
            }

            if (charClass != character.getCharClass()) {
                character.setCharClass(charClass);
                changed = true;
            }

            if (changed) {
                charDAO.update(character);
            }
	    }

	    resp.sendRedirect("/GeekQuest.jsp");
	}
	
    private String checkNull(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
}
