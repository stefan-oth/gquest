package de.oth.app.geekquest.transactions.migration;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;

import de.oth.app.geekquest.dao.DAOManager;
import de.oth.app.geekquest.dao.MissionDAO;
import de.oth.app.geekquest.model.Mission;

public class MissionMigrationTransaction extends VoidWork {
    
    private MissionDAO mDAO = DAOManager.getMissionDAO();
    
    private Mission mission;
    
    public MissionMigrationTransaction(Mission mission) {
        super();
        this.mission = mission;
    }

    @Override
    public void vrun() {
        //create new mission in own entity group
        mDAO.create(mission.getDescription(), 
                mission.getIsAccomplished(), 
                mission.getParentKey());
        
        //delete old mission
        ObjectifyService.ofy().delete().key(Key.create(mission.getParentKey(), 
                Mission.class, mission.getId()));
    }
}
