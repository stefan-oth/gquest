package de.oth.app.helloworld.dao.datastore;

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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import de.oth.app.helloworld.dao.PlayerDAO;
import de.oth.app.helloworld.model.CharClass;
import de.oth.app.helloworld.model.jdo.Mission;
import de.oth.app.helloworld.model.jdo.Player;

public class PlayerDAOImplDatastore implements PlayerDAO {

    @Override
    public void delete(Player player) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        Key key = KeyFactory.createKey(Player.class.getSimpleName(), player.getId());
        
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
    public Long create(String name, CharClass charClass, String userId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        //Entity player = new Entity(Player.class.getSimpleName());
        Entity player = new Entity(Player.class.getSimpleName(), userId);
        player.setProperty("name", name);
        player.setProperty("health", 10);
        player.setProperty("charClass", charClass.toString());
        player.setProperty("userId", userId);

        Key key = datastore.put(player);
        
        return key.getId();
    }

    @Override
    public Mission createMission(String description) {
        Mission mission = new Mission();
        mission.setDescription(description);
        return mission;
    }

    @Override
    public Player find(Long id) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        Key key = KeyFactory.createKey(Player.class.getSimpleName(), id);
        
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
        
//        Key key = KeyFactory.createKey(Player.class.getSimpleName(), userId);
//        try {
//            Entity entity = datastore.get(key);
//            if (entity != null) {
//                Player player = getPlayer(entity);
//                
//                Query query = new Query(Mission.class.getSimpleName())
//                        .setAncestor(entity.getKey());
//
//                List<Entity> missions = datastore.prepare(query).asList(
//                        FetchOptions.Builder.withDefaults());
//
//                for (Entity e : missions) {
//                    player.addMissions(getMission(e));
//                }
//
//                return player;
//            }
//        } catch (EntityNotFoundException e) {
//            //TODO was tun
//        }
//        return null;
        
        Filter filter = new FilterPredicate("userId", FilterOperator.EQUAL,
                userId);

        Query query = new Query(Player.class.getSimpleName()).setFilter(filter);
        
        List<Entity> result = datastore.prepare(query).asList(
                FetchOptions.Builder.withDefaults());
        
        if (result.size() > 0) {
            Player player = getPlayer(result.get(0));
            
            query = new Query(Mission.class.getSimpleName()).setAncestor(
                    result.get(0).getKey());
            
            List<Entity> missions = datastore.prepare(query).asList(
                    FetchOptions.Builder.withDefaults());
            
            for (Entity e : missions) {
                player.addMissions(getMission(e));
            }
            
            return player;
        }
        return null;
    }
    
    private Player getPlayer(Entity entity) {
        Player player = new Player();
        player.setId(entity.getKey().getId());
        player.setName((String) entity.getProperty("name"));
        player.setHealth(((Long) entity.getProperty("health")).intValue());
        player.setCharClass(CharClass.valueOf((String) entity.getProperty("charClass")));
        player.setUserId((String) entity.getProperty("userId"));
        player.setMissions(new ArrayList<Mission>());
        
        return player;
    }
    
    private Entity getEntity(Player player) {
        Entity entity = new Entity(Player.class.getSimpleName(), player.getId());
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

}
