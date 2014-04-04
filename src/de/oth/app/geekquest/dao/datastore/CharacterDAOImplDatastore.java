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
import com.google.appengine.api.datastore.Query.SortDirection;

import de.oth.app.geekquest.dao.CharacterDAO;
import de.oth.app.geekquest.dao.DAOManager;
import de.oth.app.geekquest.dao.MissionDAO;
import de.oth.app.geekquest.model.CharClass;
import de.oth.app.geekquest.model.Character;
import de.oth.app.geekquest.model.Mission;
import de.oth.app.geekquest.model.Player;

public class CharacterDAOImplDatastore implements CharacterDAO {

    @Override
    public void delete(Character character) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        if (character.getKey() == null) {
            System.out.println("Delete - Character key is null");
            return;
        }
        
        Key key = character.getKey();
        
        datastore.delete(key);
    }

    @Override
    public void update(Character character) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        if (character.getKey() == null) {
            System.out.println("Update - Character key is null");
            return;
        }
        
        Entity entity = getEntity(character, character.getKey().getParent());

        datastore.put(entity);
    }

    @Override
    public Key create(String name, Integer health, CharClass charClass, Long score, 
            Key parentKey) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        Entity entity = new Entity(Character.class.getSimpleName(), parentKey);

        entity.setProperty("name", name);
        entity.setProperty("health", health);
        entity.setProperty("charClass", charClass.toString());
        entity.setProperty("score", score);

        Key key = datastore.put(entity);
        
        return key;
    }

    @Override
    public Character find(Key key) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        MissionDAO missionsDAO = DAOManager.getMissionDAO();
        
        try {
            Entity entity = datastore.get(key);
            Character character = getCharacter(entity);
            
            List<Mission> missions = missionsDAO.findByParent(key);
            
            character.setMissions(missions);
            
            return character;
        } catch (EntityNotFoundException e) {
            // TODO error log?
            return null;
        }
    }

    @Override
    public Character find(Long id, String userId) {
        Key parentKey = KeyFactory.createKey(Player.class.getSimpleName(), userId);
        Key key = KeyFactory.createKey(parentKey, Character.class.getSimpleName(), id);
        return find(key);
    }
    
    @Override
    public List<Character> findByParent(Key parentKey) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        MissionDAO missionsDAO = DAOManager.getMissionDAO();
        
        Query query = new Query(Character.class.getSimpleName()).setAncestor(parentKey);

        List<Entity> entities = datastore.prepare(query).asList(
                FetchOptions.Builder.withDefaults());
        
        List<Character> characters = new ArrayList<>();
        for (Entity entity : entities) {
            Character character = getCharacter(entity);
            List<Mission> missions = missionsDAO.findByParent(entity.getKey());
            character.setMissions(missions);
            characters.add(character);
        }
        
        return characters;
    }
    
    @Override
    public List<Character> findByUserId(String userId) {
        Key parentKey = KeyFactory.createKey(Player.class.getSimpleName(), userId);
        return findByParent(parentKey);
    }
    
    @Override
    public Character findFirstByUserId(String userId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        MissionDAO missionsDAO = DAOManager.getMissionDAO();
        
        Key parentKey = KeyFactory.createKey(Player.class.getSimpleName(), userId);
        
        Query query = new Query(Character.class.getSimpleName()).setAncestor(parentKey);

        List<Entity> entities = datastore.prepare(query).asList(
                FetchOptions.Builder.withDefaults().limit(1));
        
        Character character = null;
        if ( entities != null && entities.size() > 0) {
            character = getCharacter(entities.get(0));
            List<Mission> missions = missionsDAO.findByParent(entities.get(0).getKey());
            character.setMissions(missions);
        }
        
        return character;
    }
    
    private Character getCharacter(Entity entity) {
        Character character = new Character();
        character.setKey(entity.getKey());
        character.setName((String) entity.getProperty("name"));
        character.setHealth(((Long) entity.getProperty("health")).intValue());
        character.setCharClass(CharClass.valueOf((String) entity.getProperty("charClass")));
        character.setScore((Long) entity.getProperty("score"));
        character.setImageBlobKey((String) entity.getProperty("imageBlobKey"));
        character.setMissions(new ArrayList<Mission>());
        
        return character;
    }
    
    private Entity getEntity(Character character, Key parentKey) {
        Entity entity = new Entity(Character.class.getSimpleName(), parentKey);
        if (character.getKey() == null) {
            entity = new Entity(Character.class.getSimpleName(), parentKey);
        } else {
            entity = new Entity(character.getKey());
        }
        entity.setProperty("name", character.getName());
        entity.setProperty("health", character.getHealth());
        entity.setProperty("charClass", character.getCharClass().toString());
        entity.setProperty("score", character.getScore());
        entity.setProperty("imageBlobKey", character.getImageBlobKey());
        
        return entity;
    }

    /**
     * The characters are returned without missions and sorted by the score
     */
    @Override
    public List<Character> getCharactersForHighscore(int max, int offset) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        List<Character> characters = new ArrayList<>();
        
        Query query = new Query(Character.class.getSimpleName());
        query.addSort("score", SortDirection.DESCENDING);
        
        FetchOptions options = FetchOptions.Builder.withDefaults();
        if (max > 0) {
            options.limit(max);
        }
        
        if (offset > 0) {
            options.offset(offset);
        }
        
        List<Entity> entities = datastore.prepare(query).asList(options);
        
        for (Entity entity : entities) {
            Character character = getCharacter(entity);
            characters.add(character);
        }
        
        return characters;
    }
}
