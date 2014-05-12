package de.oth.app.geekquest.dao;

import com.googlecode.objectify.ObjectifyService;

import de.oth.app.geekquest.dao.datastore.CharacterDAOImplDatastore;
import de.oth.app.geekquest.dao.datastore.MissionDAOImplDatastore;
import de.oth.app.geekquest.dao.datastore.PlayerDAOImplDatastore;
import de.oth.app.geekquest.dao.datastore.PotionDAOImplDatastore;
import de.oth.app.geekquest.model.Character;
import de.oth.app.geekquest.model.Mission;
import de.oth.app.geekquest.model.Player;
import de.oth.app.geekquest.model.Potion;

public class DAOManager {
    
    static {
        ObjectifyService.register(Player.class);
        ObjectifyService.register(Character.class);
        ObjectifyService.register(Mission.class);
        ObjectifyService.register(Potion.class);
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
    
    public static PotionDAO getPotionDAO() {
        return new PotionDAOImplDatastore();
    }

}
