package de.oth.app.geekquest.dao;

import de.oth.app.geekquest.dao.datastore.CharacterDAOImplDatastore;
import de.oth.app.geekquest.dao.datastore.MissionDAOImplDatastore;
import de.oth.app.geekquest.dao.datastore.PlayerDAOImplDatastore;

public class DAOManager {
    
    
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
