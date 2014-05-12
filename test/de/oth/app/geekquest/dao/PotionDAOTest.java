package de.oth.app.geekquest.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Key;

import de.oth.app.geekquest.model.CharClass;
import de.oth.app.geekquest.model.Character;
import de.oth.app.geekquest.model.Player;
import de.oth.app.geekquest.model.Potion;

public class PotionDAOTest {
    
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    
    private PlayerDAO pDAO = DAOManager.getPlayerDAO();
    private CharacterDAO cDAO = DAOManager.getCharacterDAO();
    private PotionDAO potionDAO = DAOManager.getPotionDAO();

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testCharacterDrinksPotion() {
        Key<Player> playerKey = pDAO.create("1234");
        
        Key<Character> characterKey = cDAO.create("Frodo", 10l, 
                CharClass.Hobbit, 999l, playerKey);
        
        potionDAO.create(Potion.Color.RED, 3, characterKey);
        
        Character character = cDAO.find(characterKey);
        
        assertNotNull("potions", character.getPotions());
        assertEquals("potion count", 1, character.getPotions().size());
        
        Potion potion = character.getPotions().get(0);
        
        assertEquals("potion enmpty", false, potion.isEmpty());
        assertEquals("health", Long.valueOf(10), character.getHealth());
        
        potion.drink();
        
        assertEquals("potion enmpty", true, potion.isEmpty());
        
        //refetch character 
        character = cDAO.find(characterKey);        
        potion = character.getPotions().get(0);
        
        assertEquals("potion enmpty", true, potion.isEmpty());
        assertEquals("health", Long.valueOf(13), character.getHealth());
    }
    
    @Test
    public void testDeadCharacterDrinksPotion() {
        Key<Player> playerKey = pDAO.create("1234");
        
        Key<Character> characterKey = cDAO.create("Frodo", 0l, 
                CharClass.Hobbit, 999l, playerKey);
        
        potionDAO.create(Potion.Color.RED, 3, characterKey);
        
        Character character = cDAO.find(characterKey);
        
        assertNotNull("potions", character.getPotions());
        assertEquals("potion count", 1, character.getPotions().size());
        
        Potion potion = character.getPotions().get(0);
        
        assertEquals("potion enmpty", false, potion.isEmpty());
        assertEquals("health", Long.valueOf(0), character.getHealth());
        
        potion.drink();
        
        assertEquals("potion enmpty", false, potion.isEmpty());
        
        //refetch character 
        character = cDAO.find(characterKey);        
        potion = character.getPotions().get(0);
        
        assertEquals("potion enmpty", false, potion.isEmpty());
        assertEquals("health", Long.valueOf(0), character.getHealth());
    }
    
    @Test
    public void testAlmostFullHealthCharacterDrinksPotion() {
        Key<Player> playerKey = pDAO.create("1234");
        
        Key<Character> characterKey = cDAO.create("Frodo", 
                Character.MAX_HEALTH - 1, 
                CharClass.Hobbit, 999l, playerKey);
        
        potionDAO.create(Potion.Color.RED, 3, characterKey);
        
        Character character = cDAO.find(characterKey);
        
        assertNotNull("potions", character.getPotions());
        assertEquals("potion count", 1, character.getPotions().size());
        
        Potion potion = character.getPotions().get(0);
        
        assertEquals("potion enmpty", false, potion.isEmpty());
        assertEquals("health", Long.valueOf(Character.MAX_HEALTH - 1), 
                character.getHealth());
        
        potion.drink();
        
        assertEquals("potion enmpty", true, potion.isEmpty());
        
        //refetch character 
        character = cDAO.find(characterKey);        
        potion = character.getPotions().get(0);
        
        assertEquals("potion enmpty", true, potion.isEmpty());
        assertEquals("health", Long.valueOf(Character.MAX_HEALTH), character.getHealth());
    }

}
