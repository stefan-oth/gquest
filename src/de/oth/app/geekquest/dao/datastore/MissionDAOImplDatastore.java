package de.oth.app.geekquest.dao.datastore;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

import de.oth.app.geekquest.dao.MissionDAO;
import de.oth.app.geekquest.model.Mission;

public class MissionDAOImplDatastore implements MissionDAO {

    @Override
    public void delete(Mission mission) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        if (mission.getKey() == null) {
            System.out.println("Delete - Mission key is null");
            return;
        }
        
        Key key = mission.getKey();
        
        datastore.delete(key);
        
    }

    @Override
    public void update(Mission mission) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        if (mission.getKey() == null) {
            System.out.println("Update - Mission key is null");
            return;
        }
        
        Entity entity = getEntity(mission, mission.getKey().getParent());

        datastore.put(entity);
    }

    @Override
    public Key create(String description, Boolean accomplished, Key parentKey) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        Entity entity = new Entity(Mission.class.getSimpleName(), parentKey);

        entity.setProperty("description", description);
        entity.setProperty("isAccomplished", accomplished);

        Key key = datastore.put(entity);
        
        return key;
    }

    @Override
    public Mission find(Key key) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        try {
            Entity entity = datastore.get(key);
            Mission mission = getMission(entity);
            
            return mission;
        } catch (EntityNotFoundException e) {
            // TODO error log?
            return null;
        }
    }

    @Override
    public Mission find(Long id, Key parentKey) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key key = KeyFactory.createKey(parentKey, Mission.class.getSimpleName(), id);
        try {
            Entity entity = datastore.get(key);
            Mission mission = getMission(entity);
            
            return mission;
        } catch (EntityNotFoundException e) {
            // TODO error log?
            return null;
        }
    }
    
    @Override
    public List<Mission> findByParent(Key parentKey) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        Query query = new Query(Mission.class.getSimpleName()).setAncestor(parentKey);

        List<Entity> entities = datastore.prepare(query).asList(
                FetchOptions.Builder.withDefaults());
        
        List<Mission> missions = new ArrayList<>();
        for (Entity entity : entities) {
            missions.add(getMission(entity));
        }
        
        return missions;
    }
    
    private Entity getEntity(Mission mission, Key parentKey) {
        Entity entity = null;
        if (mission.getKey() == null) {
            entity = new Entity(Mission.class.getSimpleName(), parentKey);
        } else {
            entity = new Entity(mission.getKey());
        }
        entity.setProperty("description", mission.getDescription());
        entity.setProperty("isAccomplished", mission.getIsAccomplished());
        
        return entity;
    }
    
    private Mission getMission(Entity entity) {
        Mission mission = new Mission();
        
        mission.setKey(entity.getKey());
        mission.setDescription((String) entity.getProperty("description"));
        mission.setIsAccomplished((Boolean) entity.getProperty("isAccomplished"));
        
        return mission;
    }

}
