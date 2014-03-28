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

import de.oth.app.geekquest.dao.PlayerDAO;
import de.oth.app.geekquest.model.CharClass;
import de.oth.app.geekquest.model.Mission;
import de.oth.app.geekquest.model.Player;

public class PlayerDAOImplDatastore implements PlayerDAO {

    @Override
    public void delete(Player player) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        Key key = getKey(player);
        
        datastore.delete(key);
    }

    @Override
    public void update(Player player) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        Entity entity = getEntity(player);

        datastore.put(entity);
        
        for (Mission mission : player.getMissions()) {
            Entity e = getEntity(mission, entity.getKey());
            datastore.put(e);
        }
    }

    @Override
    public Key create(String name, CharClass charClass, String userId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        Entity player = new Entity(Player.class.getSimpleName(), userId);
        player.setProperty("name", name);
        player.setProperty("health", 10);
        player.setProperty("charClass", charClass.toString());
        player.setProperty("userId", userId);

        Key key = datastore.put(player);
        
        return key;
    }

    @Override
    public Mission createMission(String description) {
        Mission mission = new Mission();
        mission.setDescription(description);
        return mission;
    }

    @Override
    public Player find(Key key) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        try {
            Entity entity = datastore.get(key);
            Player player = getPlayer(entity);
            
            Query query = new Query(Mission.class.getSimpleName()).setAncestor(entity.getKey());
            
            List<Entity> result = datastore.prepare(query).asList(
                    FetchOptions.Builder.withDefaults());
            
            for (Entity e : result) {
                player.addMissions(getMission(e));
            }
            
            return player;
        } catch (EntityNotFoundException e) {
            // TODO error log?
            return null;
        }
    }

    @Override
    public Player findByUserId(String userId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        Key key = KeyFactory.createKey(Player.class.getSimpleName(), userId);
        try {
            Entity entity = datastore.get(key);
            if (entity != null) {
                Player player = getPlayer(entity);
                
                Query query = new Query(Mission.class.getSimpleName())
                        .setAncestor(entity.getKey());

                List<Entity> missions = datastore.prepare(query).asList(
                        FetchOptions.Builder.withDefaults());

                for (Entity e : missions) {
                    player.addMissions(getMission(e));
                }

                return player;
            }
        } catch (EntityNotFoundException e) {
            //TODO was tun
        }
        return null;
    }
    
    private Player getPlayer(Entity entity) {
        Player player = new Player();
        player.setName((String) entity.getProperty("name"));
        player.setHealth(((Long) entity.getProperty("health")).intValue());
        player.setCharClass(CharClass.valueOf((String) entity.getProperty("charClass")));
        player.setUserId((String) entity.getProperty("userId"));
        player.setMissions(new ArrayList<Mission>());
        
        return player;
    }
    
    private Entity getEntity(Player player) {
        Entity entity = new Entity(getKey(player));
        entity.setProperty("name", player.getName());
        entity.setProperty("health", player.getHealth());
        entity.setProperty("charClass", player.getCharClass().toString());
        entity.setProperty("userId", player.getUserId());
        
        return entity;
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
    
    private Key getKey(Player player) {
        return KeyFactory.createKey(Player.class.getSimpleName(), player.getUserId());
    }

}
