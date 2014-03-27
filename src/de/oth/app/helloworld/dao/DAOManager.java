package de.oth.app.helloworld.dao;

import de.oth.app.helloworld.dao.datastore.PlayerDAOImplDatastore;

public class DAOManager {
    
    
    public static PlayerDAO getPlayerDAO() {
        //return new PlayerDAOImplJDO();
        return new PlayerDAOImplDatastore();
    }

}
