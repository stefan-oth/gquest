package de.oth.app.geekquest.dao;

import de.oth.app.geekquest.dao.datastore.PlayerDAOImplDatastore;

public class DAOManager {
    
    
    public static PlayerDAO getPlayerDAO() {
        //return new PlayerDAOImplJDO();
        return new PlayerDAOImplDatastore();
    }

}
