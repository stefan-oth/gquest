package de.oth.app.geekquest.model;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

import de.oth.app.geekquest.dao.CharacterDAO;
import de.oth.app.geekquest.dao.DAOManager;
import de.oth.app.geekquest.dao.MissionDAO;
import de.oth.app.geekquest.dao.PlayerDAO;

public class MissionMigrationTest {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(
                    new LocalDatastoreServiceTestConfig().setApplyAllHighRepJobPolicy());
    
    private PlayerDAO pDAO = DAOManager.getPlayerDAO();
    private CharacterDAO cDAO = DAOManager.getCharacterDAO();
    private MissionDAO mDAO = DAOManager.getMissionDAO();
    
    private Key<Player> playerKey;
    private Key<Character> characterKey;

    @Before
    public void setUp() {
        helper.setUp();
        
        playerKey = pDAO.create("1234");
        
        characterKey = cDAO.create("Frodo", 10l, 
                CharClass.Hobbit, 999l, playerKey);
        
        //initial missions
        Objectify ofy = ObjectifyService.ofy();
        
        Mission mission = new Mission();
        mission.setDescription("Find something");
        mission.setIsAccomplished(false);
        mission.setParentKey(characterKey);
        
        ofy.save().entity(mission).now();
        
        mission = new Mission();
        mission.setDescription("Kill somebody");
        mission.setIsAccomplished(true);
        mission.setParentKey(characterKey);
        
        ofy.save().entity(mission).now();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testMigration() {
        Objectify ofy = ObjectifyService.ofy();
        
        List<Mission> missions = ofy.load().type(Mission.class).ancestor(
                characterKey).list();
        
        assertEquals("old mission count", 2, missions.size());
        
        missions = ofy.load().type(Mission.class).filter(
                "characterKey", characterKey).list();
        
        assertEquals("migrated mission count", 0, missions.size());
        
        //load character and migrate missions
        Character character = ofy.load().key(characterKey).now();
        
        assertNotNull("charcter", character);
        
        //TODO why is objectify not calling this function within a junit test?
        character.onLoad();
        
        missions = ofy.load().type(Mission.class).ancestor(
                characterKey).list();
        
        assertEquals("old mission count", 0, missions.size());
        
        missions = ofy.load().type(Mission.class).filter(
                "characterKey", characterKey).list();
        
        assertEquals("migrated mission count", 2, missions.size());
    }
    
    @Test
    public void testPartialMigration() {
        Objectify ofy = ObjectifyService.ofy();
        
        List<Mission> missions = ofy.load().type(Mission.class).ancestor(
                characterKey).list();
        
        assertEquals("old mission count", 2, missions.size());
        
        missions = ofy.load().type(Mission.class).filter(
                "characterKey", characterKey).list();
        
        assertEquals("migrated mission count", 0, missions.size());
        
        missions = ofy.load().type(Mission.class).list();
        
        //load character and migrate missions
        Character character = ofy.load().key(characterKey).now();
        
        assertNotNull("charcter", character);
        
        //TODO why is objectify not calling this function within a junit test?
        character.onLoad();
        
        Mission mission = new Mission();
        mission.setDescription("Catch something");
        mission.setIsAccomplished(false);
        mission.setParentKey(characterKey);
        
        ofy.save().entity(mission).now();
        
        missions = ofy.load().type(Mission.class).ancestor(
                characterKey).list();
        
        assertEquals("old mission count", 1, missions.size());
        
        missions = ofy.load().type(Mission.class).filter(
                "characterKey", characterKey).list();
        
        assertEquals("migrated mission count", 2, missions.size());
        
        //TODO why is objectify not calling this function within a junit test?
        character.onLoad();
        
        missions = ofy.load().type(Mission.class).ancestor(
                characterKey).list();
        
        assertEquals("old mission count", 0, missions.size());
        
        missions = ofy.load().type(Mission.class).filter(
                "characterKey", characterKey).list();
        
        assertEquals("migrated mission count", 3, missions.size());
    }
    
    @Test
    public void testFetchOnlyOldMissionsByParent() {
        List<Mission> missions = DAOManager.getMissionDAO().findByCharacter(
                characterKey);
        assertEquals("mission count", 2, missions.size());
    }
    
    @Test
    public void testFetchOnlyNewMissionsByParent() {
        Objectify ofy = ObjectifyService.ofy();
        
        //load character and migrate missions
        Character character = ofy.load().key(characterKey).now();
        
        assertNotNull("charcter", character);
        
        //migrate missions
        character.onLoad();
        
        List<Mission> missions = DAOManager.getMissionDAO().findByCharacter(
                characterKey);
        
        assertEquals("mission count", 2, missions.size());
    }
    
    @Test
    public void testFetchOldAndNewMissionsByParent() {
        Objectify ofy = ObjectifyService.ofy();
        
        //load character and migrate missions
        Character character = ofy.load().key(characterKey).now();
        
        assertNotNull("charcter", character);
        
        //migrate missions
        character.onLoad();
        
        Mission mission = new Mission();
        mission.setDescription("Catch something");
        mission.setIsAccomplished(false);
        mission.setParentKey(characterKey);
        
        ofy.save().entity(mission).now();
        
        List<Mission> missions = DAOManager.getMissionDAO().findByCharacter(
                characterKey);
        
        assertEquals("mission count", 3, missions.size());
    }
}
