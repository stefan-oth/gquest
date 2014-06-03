package de.oth.app.geekquest.dao.datastore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import de.oth.app.geekquest.dao.CharacterDAO;
import de.oth.app.geekquest.dao.DAOManager;
import de.oth.app.geekquest.dao.MissionDAO;
import de.oth.app.geekquest.dao.PotionDAO;
import de.oth.app.geekquest.model.CharClass;
import de.oth.app.geekquest.model.Character;
import de.oth.app.geekquest.model.Mission;
import de.oth.app.geekquest.model.Player;
import de.oth.app.geekquest.model.Potion;

public class CharacterDAOImplDatastore implements CharacterDAO {

    @Override
    public void delete(Character character) {
        Objectify ofy = ObjectifyService.ofy();
        
        if (character.getId() == null) {
            System.out.println("Delete - Character id is null");
            return;
        }
        
        if (character.getParentKey() == null) {
            System.out.println("Delete - Character parentKey is null");
            return;
        }
        
        Key<Character> key = Key.create(character.getParentKey(), Character.class, 
                character.getId());
        
        ofy.delete().key(key);
    }

    @Override
    public void update(Character character) {
        Objectify ofy = ObjectifyService.ofy();

        ofy.save().entity(character).now();
    }

    @Override
    public Key<Character> create(String name, Long health, CharClass charClass, Long score, 
            Key<Player> parentKey) {
        
        Character character = new Character();
        character.setNickName(name);
        character.setCharClass(charClass);
        character.setScore(score);
        character.setParentKey(parentKey);

        update(character);
        
        character.setHealth(health);
        
        Key<Character> key = Key.create(character.getParentKey(), Character.class, 
                character.getId());
        
        return key;
    }

    @Override
    public Character find(Key<Character> key) {
        MissionDAO missionsDAO = DAOManager.getMissionDAO();
        PotionDAO potionDAO = DAOManager.getPotionDAO();
        Objectify ofy = ObjectifyService.ofy();
        
        Character character = ofy.load().key(key).now();

        List<Mission> missions = missionsDAO.findByParent(key);
        character.setMissions(missions);
        
        List<Potion> potions = potionDAO.findByParent(key);
        character.setPotions(potions);

        return character;
    }

    @Override
    public Character find(Long id, String userId) {
        Key<Player> parentKey = Key.create(Player.class, userId);
        Key<Character> key = Key.create(parentKey, Character.class, id);
        return find(key);
    }
    
    @Override
    public List<Character> findByParent(Key<Player> parentKey) {
        Objectify ofy = ObjectifyService.ofy();
        MissionDAO missionsDAO = DAOManager.getMissionDAO();
        PotionDAO potionDAO = DAOManager.getPotionDAO();
        
        List<Character> characters = ofy.load().type(Character.class).ancestor(
                parentKey).list();
        
        for (Character character : characters) {
            Key<Character> key = Key.create(character.getParentKey(), Character.class, 
                    character.getId());
            List<Mission> missions = missionsDAO.findByParent(key);
            character.setMissions(missions);
            
            List<Potion> potions = potionDAO.findByParent(key);
            character.setPotions(potions);
            
            characters.add(character);
        }
        
        return characters;
    }
    
    @Override
    public List<Character> findByUserId(String userId) {
        Key<Player> parentKey = Key.create(Player.class, userId);
        return findByParent(parentKey);
    }
    
    @Override
    public Character findFirstByUserId(String userId) {
        Objectify ofy = ObjectifyService.ofy();
        MissionDAO missionsDAO = DAOManager.getMissionDAO();
        PotionDAO potionDAO = DAOManager.getPotionDAO();
        
        Key<Player> parentKey = Key.create(Player.class, userId);
        
        Character character = ofy.load().type(Character.class).ancestor(
                parentKey).first().now();

        if ( character != null) {
            Key<Character> key = Key.create(character.getParentKey(), Character.class, 
                    character.getId());
            List<Mission> missions = missionsDAO.findByParent(key);
            character.setMissions(missions);
            
            List<Potion> potions = potionDAO.findByParent(key);
            character.setPotions(potions);
        }
        
        return character;
    }
    
    @Override
    public List<Character> findByNickName(String nickName) {
        Objectify ofy = ObjectifyService.ofy();
        MissionDAO missionsDAO = DAOManager.getMissionDAO();
        PotionDAO potionDAO = DAOManager.getPotionDAO();
        
        Query<Character> query = ofy.load().type(Character.class);
        
        query = query.filter("nickName =", nickName);
        
        List<Character> characters = query.list();

        for(Character character : characters) {
            Key<Character> key = Key.create(character.getParentKey(), Character.class, 
                    character.getId());
            List<Mission> missions = missionsDAO.findByParent(key);
            character.setMissions(missions);
            
            List<Potion> potions = potionDAO.findByParent(key);
            character.setPotions(potions);
        }
        
        return characters;
    }

    /**
     * The characters are returned without missions and sorted by the score
     */
    @Override
    public List<Character> getCharactersForHighscore(int max, int offset) {
        Objectify ofy = ObjectifyService.ofy();
        Query<Character> query = ofy.load().type(Character.class);
        
        //sort by score descending
        query = query.order("-score");
        
        if (max > 0) {
            query = query.limit(max);
        }
        
        if (offset > 0) {
            query = query.offset(offset);
        }
        
        List<Character> characters = query.list();
        
        return characters;
    }
    
    /**
     * The characters are returned without missions and sorted by the score.
     * The result is strong consistent for the user with the given userId.
     */
    @Override
    public List<Character> getTopXCharacters(String userId, int max) {
        List<Character> characters = new ArrayList<>();   
        List<Character> entitiesUser =  new ArrayList<>();
        
        if (userId != null) {
            entitiesUser = getTopXCharactersUser(userId, max);
        }
        
        QueryResultIterator<Character> globalResult = null;
        Cursor cursor = null;
        int idxUser = 0;
        boolean endReached = false;
        
        do {
            endReached = true;
            globalResult = getTopXCharactersGlobal(max, cursor);
            
            Character characterGlobal = null;
            Character characterUser = null;
            CharacterComparator charCmp = new CharacterComparator();
            
            while (characters.size() < max 
                    && globalResult.hasNext()) {
                
                endReached = false;
                
                if (characterGlobal == null && globalResult.hasNext()) {
                    characterGlobal = globalResult.next();
                    //ignore characters from the current user because of the 
                    //eventual consistency of the global query
                    if (characterGlobal.getParentKey().getName().equals(userId)) {
                        characterGlobal = null;
                        continue;
                    }
                }
                
                if (characterUser == null && idxUser < entitiesUser.size()) {
                    characterUser = entitiesUser.get(idxUser);
                }
                
                //compare characters
                int cmp = charCmp.compare(characterUser, characterGlobal);
                
                if (cmp < 0) {
                    characters.add(characterGlobal);
                    characterGlobal = null;
                } else  {
                    characters.add(characterUser);
                    idxUser++;
                    characterUser = null;
                }
            }
            
            cursor = globalResult.getCursor();
            
            if (endReached && characters.size() < max) {
                while (characters.size() < max 
                        && idxUser < entitiesUser.size()) {
                    characters.add(entitiesUser.get(idxUser));
                    idxUser++;
                }
            }
        
        } while (characters.size() < max && !endReached);
        
        return characters;
    }

    private QueryResultIterator<Character> getTopXCharactersGlobal(int max, 
            Cursor startCursor) {
        
        Objectify ofy = ObjectifyService.ofy();
        
        Query<Character> query = ofy.load().type(Character.class);
        
        //sort by score descending
        query = query.order("-score");
        
        if (max > 0) {
            query = query.limit(max);
        }
        
        if (startCursor != null) {
            query = query.startAt(startCursor);
        }
        
        return query.iterator();
    }

    private List<Character> getTopXCharactersUser(String userId, int max) {
        Objectify ofy = ObjectifyService.ofy();
        
        Key<Player> parentKey = Key.create(Player.class, userId);
        Query<Character> query = ofy.load().type(Character.class).ancestor(parentKey);
        
        //sort by score descending
        query = query.order("-score");
        
        if (max > 0) {
            query = query.limit(max);
        }
        
        List<Character> characters = query.list();
        
        return characters;
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
                cmp = c1.getNickName().compareTo(c2.getNickName());
            }

            return cmp;
        }
        
    }
}
