package de.oth.app.geekquest.dao.datastore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;

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
    
    /**
     * The characters are returned without missions and sorted by the score.
     * The result is strong consistent for the user with the given userId.
     */
    @Override
    public List<Character> getTopXCharacters(String userId, int max) {
        List<Character> characters = new ArrayList<>();   
        List<Entity> entitiesUser =  new ArrayList<>();
        
        if (userId != null) {
            entitiesUser = getTopXCharactersUser(userId, max);
        }
        
        QueryResultList<Entity> globalResult = null;
        Cursor cursor = null;
        int idxUser = 0;
        
        do {
            globalResult = getTopXCharactersGlobal(max, cursor);
            cursor = globalResult.getCursor();
            
            int idxGlobal = 0;
            Character characterGlobal = null;
            Character characterUser = null;
            CharacterComparator charCmp = new CharacterComparator();
            while (characters.size() < max 
                    && idxGlobal < globalResult.size()) {
                
                if (characterGlobal == null && idxGlobal < globalResult.size()) {
                    characterGlobal = getCharacter(globalResult.get(idxGlobal));
                }
                
                if (characterUser == null && idxUser < entitiesUser.size()) {
                    characterUser = getCharacter(entitiesUser.get(idxUser));
                }
                
                //compare characters
                int cmp = charCmp.compare(characterUser, characterGlobal);
                
                if (cmp < 0) {
                    //ignore characters from the current user because of the 
                    //eventual consistency of the global query
                    if (!characterGlobal.getKey().getParent().getName().equals(
                            userId)) {
                        characters.add(characterGlobal);
                    }
                    idxGlobal++;
                    characterGlobal = null;
                } else  {
                    characters.add(characterUser);
                    idxUser++;
                    characterUser = null;
                }
            }
        
        } while (characters.size() < max && globalResult.size() >= max);
        
        return characters;
    }

    private QueryResultList<Entity> getTopXCharactersGlobal(int max, Cursor startCursor) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        QueryResultList<Entity> globalResult;
        Query query = new Query(Character.class.getSimpleName());
        query.addSort("score", SortDirection.DESCENDING);
        
        FetchOptions options = FetchOptions.Builder.withDefaults();
        if (max > 0) {
            options.limit(max);
        }
        
        if (startCursor != null) {
            options.startCursor(startCursor);
        }
        
        globalResult = datastore.prepare(query).asQueryResultList(options);
        return globalResult;
    }

    private List<Entity> getTopXCharactersUser(String userId, int max) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        List<Entity> entitiesUser;
        Key parentKey = KeyFactory.createKey(Player.class.getSimpleName(), userId);
        Query query = new Query(Character.class.getSimpleName()).setAncestor(parentKey);
        query.addSort("score", SortDirection.DESCENDING);
        
        FetchOptions options = FetchOptions.Builder.withDefaults();
        if (max > 0) {
            options.limit(max);
        }
   
        entitiesUser = datastore.prepare(query).asList(options);
        return entitiesUser;
    }
    
    class CharacterComparator implements Comparator<Character> {

        @Override
        public int compare(Character c1, Character c2) {
            
            if (c1 == null && c2 == null) {
                return 0;
            }
            
            if (c1 == null) {
                return -1;
            }
            
            if (c2 == null) {
                return 1;
            }
            
            int cmp = c1.getScore().compareTo(c2.getScore());
            
            if (cmp == 0) {
                // scores are equal, so compare names
                cmp = c1.getName().compareTo(c2.getName());
            }

            return cmp;
        }
        
    }
}
