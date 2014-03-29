package de.oth.app.geekquest.dao.datastore;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import de.oth.app.geekquest.dao.CharacterDAO;
import de.oth.app.geekquest.dao.DAOManager;
import de.oth.app.geekquest.dao.PlayerDAO;
import de.oth.app.geekquest.model.Character;
import de.oth.app.geekquest.model.Player;

public class PlayerDAOImplDatastore implements PlayerDAO {

    @Override
    public void delete(Player player) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        Key key = KeyFactory.createKey(Player.class.getSimpleName(), 
                player.getUserId());
        
        datastore.delete(key);
    }

    @Override
    public void update(Player player) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        Entity entity = getEntity(player);

        datastore.put(entity);
    }

    @Override
    public Key create(String userId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        Entity player = new Entity(Player.class.getSimpleName(), userId);

        player.setProperty("userId", userId);

        Key key = datastore.put(player);
        
        return key;
    }

    @Override
    public Player find(Key key) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        CharacterDAO charDAO = DAOManager.getCharacterDAO();
        
        try {
            Entity entity = datastore.get(key);
            Player player = getPlayer(entity);
            
            List<Character> characters = charDAO.findByParent(key);
            
            player.setCharacters(characters);
            
            return player;
        } catch (EntityNotFoundException e) {
            // TODO error log?
            return null;
        }
    }

    @Override
    public Player findByUserId(String userId) {
        Key key = KeyFactory.createKey(Player.class.getSimpleName(), userId);
        return find(key);
    }
    
    private Player getPlayer(Entity entity) {
        Player player =  new Player();
        player.setUserId((String) entity.getProperty("userId"));
        player.setKey(entity.getKey());
        return player;
    }
    
    private Entity getEntity(Player player) {
        Entity entity = new Entity(Player.class.getSimpleName(), player.getUserId());
        entity.setProperty("userId", player.getUserId());
        
        return entity;
    }
}
