package de.oth.app.geekquest.dao.datastore;

import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

import de.oth.app.geekquest.dao.MissionDAO;
import de.oth.app.geekquest.model.Character;
import de.oth.app.geekquest.model.Mission;

public class MissionDAOImplDatastore implements MissionDAO {

    @Override
    public void delete(Mission mission) {
        Objectify ofy = ObjectifyService.ofy();
        
        if (mission.getId() == null) {
            System.out.println("Delete - Mission id is null");
            return;
        }
        
        if (mission.getParentKey() == null) {
            System.out.println("Delete - Mission parentKey is null");
            return;
        }
        
        Key<Mission> key = Key.create(mission.getParentKey(), Mission.class, 
                mission.getId());
        
        ofy.delete().key(key);        
    }

    //TODO rename to save
    @Override
    public void update(Mission mission) {
        Objectify ofy = ObjectifyService.ofy();

        ofy.save().entity(mission).now();
    }

    @Override
    public Key<Mission> create(String description, Boolean accomplished, 
            Key<Character> parentKey) {

        Mission mission = new Mission();
        mission.setDescription(description);
        mission.setIsAccomplished(accomplished);
        mission.setParentKey(parentKey);
        
        update(mission);

        Key<Mission> key = Key.create(mission.getParentKey(), Mission.class, 
                mission.getId());
        
        return key;
    }

    @Override
    public Mission find(Key<Mission> key) {
        Objectify ofy = ObjectifyService.ofy();
        
        Mission mission = ofy.load().key(key).now();

        return mission;
    }

    @Override
    public Mission find(Long id, Key<Character> parentKey) {
        Key<Mission> key = Key.create(parentKey, Mission.class, id);
        return find(key);
    }
    
    @Override
    public List<Mission> findByParent(Key<Character> parentKey) {
        Objectify ofy = ObjectifyService.ofy();
        
        List<Mission> missions = ofy.load().type(Mission.class).ancestor(
                parentKey).list();
        
        return missions;
    }
}
