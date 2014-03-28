package de.oth.app.geekquest;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import de.oth.app.geekquest.dao.DAOManager;
import de.oth.app.geekquest.dao.PlayerDAO;
import de.oth.app.geekquest.model.CharClass;
import de.oth.app.geekquest.model.Mission;
import de.oth.app.geekquest.model.Player;

@SuppressWarnings("serial")
public class SaveCharacterServlet extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
	    
	    User user = (User) req.getAttribute("user");
	    if (user == null) {
	      UserService userService = UserServiceFactory.getUserService();
	      user = userService.getCurrentUser();
	    }
	    
	    String name = checkNull(req.getParameter("name"));
	    CharClass charClass = CharClass.valueOf(checkNull(req.getParameter("charclass")));
	    
	    PlayerDAO dao = DAOManager.getPlayerDAO();
	    Player player = dao.findByUserId(user.getUserId());
	    
	    if (player == null) {
	        System.out.println("Creating new Player");
	        Key key = dao.create(name, charClass, user.getUserId());
	        player = dao.find(key);
	        if (player != null) {
	            Mission mission = dao.createMission("Destroy ring");
	            player.addMissions(mission);
	            mission = dao.createMission("Visit Rivendell");
	            mission.accomplish();
	            player.addMissions(mission);
	            dao.update(player);
	        } else {
	            System.out.println("Error missing Player for current user " + user.toString());
	        }
	    } else {
	        System.out.println("Updating existing Player with for current user " + user.toString());
            boolean changed = false;
            if (!name.equals(player.getName())) {
                player.setName(name);
                changed = true;
            }

            if (charClass != player.getCharClass()) {
                player.setCharClass(charClass);
                changed = true;
            }

            if (changed) {
                dao.update(player);
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
