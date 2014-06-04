package de.oth.app.geekquest;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;

import de.oth.app.geekquest.dao.CharacterDAO;
import de.oth.app.geekquest.dao.DAOManager;
import de.oth.app.geekquest.model.Character;
import de.oth.app.geekquest.model.Player;

@SuppressWarnings("serial")
public class AttackCharacterServlet  extends HttpServlet {
    
    /**
     * A logger object.
     */
    private static final Logger LOG = Logger.getLogger(AttackCharacterServlet.class
            .getName());
    
    private static Random rng = new Random();
    private static final int RNG_HEAL_INT_LIMIT = 3;
    private static final int INT_VALUE_FOR_HEAL = 1;
    private static final int MAX_HP_HEAL = 5;
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        
        CharacterDAO charDAO = DAOManager.getCharacterDAO();
        
        User user = (User) req.getAttribute("user");
        if (user == null) {
            UserService userService = UserServiceFactory.getUserService();
            user = userService.getCurrentUser();
        }
        
        Long charId = Long.valueOf(checkNull(req.getParameter("characterId")));
        String playerKeyString = checkNull(req.getParameter("playerKeyString"));
        
        Key<Player> playerKey = Key.create(playerKeyString);
        
        Character character = charDAO.find(charId, playerKey.getName());
        
        if (character != null) {
            //if (character.getHealth() > 0) {
            LOG.info(character.getNickName() + " hit by 1");
            character.hurt(1);

            boolean heal = rng.nextInt(RNG_HEAL_INT_LIMIT) == INT_VALUE_FOR_HEAL;
            if (heal) {
                long hp = rng.nextInt(MAX_HP_HEAL - 1) + 1;
                LOG.info(character.getNickName() + " healed by " + hp);
                character.heal(hp);
            }
            //}
        }

        resp.sendRedirect("/battle");
    }
    
    private String checkNull(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
}
