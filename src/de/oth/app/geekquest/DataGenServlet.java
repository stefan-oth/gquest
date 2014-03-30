package de.oth.app.geekquest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import de.oth.app.geekquest.dao.CharacterDAO;
import de.oth.app.geekquest.dao.DAOManager;
import de.oth.app.geekquest.dao.MissionDAO;
import de.oth.app.geekquest.dao.PlayerDAO;
import de.oth.app.geekquest.model.CharClass;
import de.oth.app.geekquest.model.Character;
import de.oth.app.geekquest.model.Mission;
import de.oth.app.geekquest.model.Player;

@SuppressWarnings("serial")
public class DataGenServlet extends HttpServlet{
    
    private static final int MIN_HEALTH = 5;
    private static final int MAX_HEALTH = 20;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/plain");
        
        PlayerDAO pDAO = DAOManager.getPlayerDAO();
        CharacterDAO cDAO = DAOManager.getCharacterDAO();
        CharClass[] classes = CharClass.values();
        
        User user = (User) req.getAttribute("user");
        if (user == null) {
            UserService userService = UserServiceFactory.getUserService();
            user = userService.getCurrentUser();
        }
        
        Player player = pDAO.findByUserId(user.getUserId());
        if (player == null) {
            Key key = pDAO.create(user.getUserId());
            player = pDAO.find(key);
        }
        
        String[] names  = getNames();
        
        for (int i = 0; i < names.length; i++) {
            int classId = (int) (Math.random() * classes.length);
            int health = MIN_HEALTH + (int) (Math.random() * (MAX_HEALTH - MIN_HEALTH));
            Key key = cDAO.create(names[i], health, classes[classId], player.getKey());
            Character character = cDAO.find(key);
            addMissions(character);
            player.addCharacter(character);
        }
        
        resp.getWriter().println("data generated");
    }
    
    private void addMissions(Character character) {
        MissionDAO dao = DAOManager.getMissionDAO();
        Key missionKey = dao.create("Destroy ring", false, character.getKey());
        Mission mission = dao.find(missionKey);
        character.addMissions(mission);
        
        missionKey = dao.create("Visit Rivendell", true, character.getKey());
        mission = dao.find(missionKey);
        character.addMissions(mission);
    }
    
    private String[] getNames() {
        //http://www.rinkworks.com/namegen/fnames.cgi?d=1&f=2
        String[] names = { "Emtasu", "Rynir", "Reorm", "Satas", "Regar",
                "Ghaeni", "Etniss", "Kelbur", "Danmor", "Ashecho", "Neroth",
                "Estost", "Ardar", "Delesse", "Osque", "Kybel", "Kalyb",
                "Angann", "Homor", "Ittas", "Imlos", "Cheyh", "Elmerd",
                "Quecrad", "Tornem", "Rynys", "Tinold", "Polesta", "Cerray",
                "Rilwar", "Waril", "Neight", "Polnran", "Iafum", "Hygar",
                "Chever", "Endengy", "Undny", "Istiny", "Oldath", "Tainali",
                "Nysjtur", "Pharoth", "Styad", "Ashpris", "Laelm", "Honena",
                "Erbur", "Awesty", "Emrsam" };
        
        return names;
    }

}
