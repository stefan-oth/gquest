package de.oth.app.geekquest.dao;

import com.googlecode.objectify.ObjectifyService;

import de.oth.app.geekquest.dao.datastore.CharacterDAOImplDatastore;
import de.oth.app.geekquest.dao.datastore.MissionDAOImplDatastore;
import de.oth.app.geekquest.dao.datastore.PlayerDAOImplDatastore;
import de.oth.app.geekquest.model.Character;
import de.oth.app.geekquest.model.Mission;
import de.oth.app.geekquest.model.Player;

public class DAOManager {
    
    static {
        ObjectifyService.register(Player.class);
        ObjectifyService.register(Character.class);
        ObjectifyService.register(Mission.class);
    }
    
    
    public static PlayerDAO getPlayerDAO() {
        return new PlayerDAOImplDatastore();
    }
    
    public static MissionDAO getMissionDAO() {
        return new MissionDAOImplDatastore();
    }
    
    public static CharacterDAO getCharacterDAO() {
        return new CharacterDAOImplDatastore();
    }

}
