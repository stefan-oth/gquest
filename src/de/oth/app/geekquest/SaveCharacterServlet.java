package de.oth.app.geekquest;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import de.oth.app.geekquest.dao.DAOManager;
import de.oth.app.geekquest.dao.PlayerDAO;
import de.oth.app.geekquest.model.CharClass;
import de.oth.app.geekquest.model.jdo.Mission;
import de.oth.app.geekquest.model.jdo.Player;

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
	    
	    //TODO geht das mit dem Player-Objekt bzw. der Id auch anders?
	    String playerId = req.getParameter("playerid");
	    PlayerDAO dao = DAOManager.getPlayerDAO();
	    
	    if (playerId.equals("")) {
	        System.out.println("Creating existing Player");
	        Long id = dao.create(name, charClass, user.getUserId());
	        Player player = dao.find(id);
	        if (player != null) {
	            Mission mission = dao.createMission("Destroy ring");
	            player.addMissions(mission);
	            mission = dao.createMission("Visit Rivendell");
	            mission.accomplish();
	            player.addMissions(mission);
	            dao.update(player);
	        } else {
	            System.out.println("Error missing Player entity with id = " + id);
	        }
	    } else {
	        Long id = Long.valueOf(playerId);
	        System.out.println("Updating existing Player with id = " + id);
	        Player player = dao.find(id);
	        if (player != null) {
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
	        } else {
	            System.out.println("Error missing Player entity with id = " + id);
	        }
	    }

	    resp.sendRedirect("/GeekQuest.jsp");
	}
	
	/*
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
	    
		resp.setContentType("text/plain");
		resp.getWriter().println("GeekQuest");

		PlayerDAO dao = new PlayerDAOImplJDO();
		Long id = dao.create("Frodo", CharClass.Hobbit);

		Player player = dao.find(id);
		Mission mission = dao.createMission("Töte den Oger");
		player.addMissions(mission);
	    mission = dao.createMission("Finde den Ring");
	    player.addMissions(mission);
		dao.update(player);

		resp.getWriter().println("Id:" + player.getId());
		resp.getWriter().println("Name:" + player.getName());
		resp.getWriter().println("CharClass:" + player.getCharClass());
		resp.getWriter().println("Health:" + player.getHealth());
		
		//dao.delete(player);
		
		
		player.setName("Hans");

		dao.update(player);
		player = dao.find(id);

		resp.getWriter().println("Id:" + player.getId());
		resp.getWriter().println("Name:" + player.getName());
		resp.getWriter().println("CharClass:" + player.getCharClass());
		resp.getWriter().println("Health:" + player.getHealth());
		
	}*/
	
    private String checkNull(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
}
