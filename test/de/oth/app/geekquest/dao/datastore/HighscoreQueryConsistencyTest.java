package de.oth.app.geekquest.dao.datastore;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.dev.HighRepJobPolicy;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Key;

import de.oth.app.geekquest.dao.CharacterDAO;
import de.oth.app.geekquest.dao.DAOManager;
import de.oth.app.geekquest.dao.PlayerDAO;
import de.oth.app.geekquest.model.CharClass;
import de.oth.app.geekquest.model.Character;
import de.oth.app.geekquest.model.Player;

public class HighscoreQueryConsistencyTest {

    private static final class CustomHighRepJobPolicy implements HighRepJobPolicy {
        private static boolean enabled = true;
        
        @Override
        public boolean shouldApplyNewJob(com.google.appengine.api.datastore.Key entityGroup) {
            
            if (!enabled) {
                return true;
            }
            
            // every other new job fails to apply
            return !entityGroup.getName().equals("4567");
        }

        @Override
        public boolean shouldRollForwardExistingJob(com.google.appengine.api.datastore.Key entityGroup) {
            if (!enabled) {
                return true;
            }
            
            // every other exsting job fails to apply
            return !entityGroup.getName().equals("4567");
        }
        
        public static void enable() {
            enabled = true;
        }
        
        public static void disable() {
            enabled = false;
        }
    }
    
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
               .setAlternateHighRepJobPolicyClass(CustomHighRepJobPolicy.class));
    
    private PlayerDAO pDAO = DAOManager.getPlayerDAO();
    private CharacterDAO cDAO = DAOManager.getCharacterDAO();

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testStrongConsistentResult() {
        
        int topX = 10;
        
        String[] expectedName =  {"Istiny", "Ardar", "Frodo", "Etniss", "Undny", 
                "Ashecho", "Sam", "Ghaeni", "Kelbur", "Danmor"};
        
        Key<Player> player1 = pDAO.create("1234");
        Key<Player> player2 = pDAO.create("4567");
        
        cDAO.create("Frodo", 10, CharClass.Hobbit, 999l, player1);
        cDAO.create("Sam", 8, CharClass.Hobbit, 500l, player1);
        cDAO.create("Ghaeni", 15, CharClass.Dwarf, 457l, player1);
        cDAO.create("Etniss", 11, CharClass.Elf, 782l, player1);
        cDAO.create("Kelbur", 3, CharClass.Mage, 300l, player1);
        cDAO.create("Danmor", 20, CharClass.Hobbit, 235l, player1);
        cDAO.create("Ashecho", 9, CharClass.Dwarf, 578l, player1);
        cDAO.create("Neroth", 14, CharClass.Hobbit, 54l, player1);
        cDAO.create("Estost", 6, CharClass.Elf, 100l, player1);
        cDAO.create("Ardar", 8, CharClass.Mage, 10000l, player1);
        
        cDAO.create("Istiny", 14, CharClass.Elf, 10001l, player2);
        cDAO.create("Undny", 9, CharClass.Dwarf, 600l, player2);
        
        List<Character> actualTopX = new CharacterDAOImplDatastore().getTopXCharacters(
                "4567", topX);
        
        assertEquals(topX, actualTopX.size());
        
        for (int i = 0; i < topX; i++) {
            assertEquals(expectedName[i], actualTopX.get(i).getNickName());
        }
    }
    
    @Test
    public void testEventuallyConsistentResult() {
        int topX = 10;
        
        String[] expectedName =  {"Ardar", "Frodo", "Etniss", 
                "Ashecho", "Sam", "Ghaeni", "Kelbur", "Danmor", "Estost", "Neroth"};
        
        Key<Player> player1 = pDAO.create("1234");
        Key<Player> player2 = pDAO.create("4567");
        
        cDAO.create("Frodo", 10, CharClass.Hobbit, 999l, player1);
        cDAO.create("Sam", 8, CharClass.Hobbit, 500l, player1);
        cDAO.create("Ghaeni", 15, CharClass.Dwarf, 457l, player1);
        cDAO.create("Etniss", 11, CharClass.Elf, 782l, player1);
        cDAO.create("Kelbur", 3, CharClass.Mage, 300l, player1);
        cDAO.create("Danmor", 20, CharClass.Hobbit, 235l, player1);
        cDAO.create("Ashecho", 9, CharClass.Dwarf, 578l, player1);
        cDAO.create("Neroth", 14, CharClass.Hobbit, 54l, player1);
        cDAO.create("Estost", 6, CharClass.Elf, 100l, player1);
        cDAO.create("Ardar", 8, CharClass.Mage, 10000l, player1);
        
        cDAO.create("Istiny", 14, CharClass.Elf, 10001l, player2);
        cDAO.create("Undny", 9, CharClass.Dwarf, 600l, player2);
        
        List<Character> actualTopX = new CharacterDAOImplDatastore().getTopXCharacters(
                "4321", topX);
        assertEquals(topX, actualTopX.size());
        
        for (int i = 0; i < topX; i++) {
            assertEquals(expectedName[i], actualTopX.get(i).getNickName());
        }
    }
    
    @Test
    public void testStrongConsistentResultEnteringHighscore() {
        
        CharacterDAOImplDatastore dao = new CharacterDAOImplDatastore();
        
        int topX = 10;
        String[] expectedNameBefore =  {"Ardar", "Frodo", "Etniss", 
                "Ashecho", "Sam", "Ghaeni", "Kelbur", "Danmor", "Estost", "Neroth"};
        String[] expectedNameAfter =  {"Ardar", "Frodo", "Etniss", 
                "Ashecho", "Sam", "Ghaeni", "Kelbur", "Istiny", "Danmor", "Estost"};
        
        Key<Player> player1 = pDAO.create("1234");
        Key<Player> player2 = pDAO.create("4567");
        
        cDAO.create("Frodo", 10, CharClass.Hobbit, 999l, player1);
        cDAO.create("Sam", 8, CharClass.Hobbit, 500l, player1);
        cDAO.create("Ghaeni", 15, CharClass.Dwarf, 457l, player1);
        cDAO.create("Etniss", 11, CharClass.Elf, 782l, player1);
        cDAO.create("Kelbur", 3, CharClass.Mage, 300l, player1);
        cDAO.create("Danmor", 20, CharClass.Hobbit, 235l, player1);
        cDAO.create("Ashecho", 9, CharClass.Dwarf, 578l, player1);
        cDAO.create("Neroth", 14, CharClass.Hobbit, 54l, player1);
        cDAO.create("Estost", 6, CharClass.Elf, 100l, player1);
        cDAO.create("Ardar", 8, CharClass.Mage, 10000l, player1);
        Key<Character> key = cDAO.create("Istiny", 14, CharClass.Elf, 0l, player2);
        
        List<Character> actualTopX = dao.getTopXCharacters("4567", topX);
        
        assertEquals(topX, actualTopX.size());
        
        for (int i = 0; i < topX; i++) {
            assertEquals(expectedNameBefore[i], actualTopX.get(i).getNickName());
        }
        
        Character character = dao.find(key);
        character.setScore(299l);
        dao.update(character);
        
        actualTopX = dao.getTopXCharacters("4567", topX);
        
        assertEquals(topX, actualTopX.size());
        
        for (int i = 0; i < topX; i++) {
            assertEquals(expectedNameAfter[i], actualTopX.get(i).getNickName());
        }
    }
    
    @Test
    public void testEventuallyConsistentResultEnteringHighscore() {
        
        CharacterDAOImplDatastore dao = new CharacterDAOImplDatastore();
        
        int topX = 10;
        String[] expectedName = {"Ardar", "Frodo", "Etniss", 
                "Ashecho", "Sam", "Ghaeni", "Kelbur", "Danmor", "Estost", "Neroth"};
        
        Key<Player> player1 = pDAO.create("1234");
        Key<Player> player2 = pDAO.create("4567");
        
        cDAO.create("Frodo", 10, CharClass.Hobbit, 999l, player1);
        cDAO.create("Sam", 8, CharClass.Hobbit, 500l, player1);
        cDAO.create("Ghaeni", 15, CharClass.Dwarf, 457l, player1);
        cDAO.create("Etniss", 11, CharClass.Elf, 782l, player1);
        cDAO.create("Kelbur", 3, CharClass.Mage, 300l, player1);
        cDAO.create("Danmor", 20, CharClass.Hobbit, 235l, player1);
        cDAO.create("Ashecho", 9, CharClass.Dwarf, 578l, player1);
        cDAO.create("Neroth", 14, CharClass.Hobbit, 54l, player1);
        cDAO.create("Estost", 6, CharClass.Elf, 100l, player1);
        cDAO.create("Ardar", 8, CharClass.Mage, 10000l, player1);
        Key<Character> key = cDAO.create("Istiny", 14, CharClass.Elf, 0l, player2);
        
        List<Character> actualTopX = dao.getTopXCharacters("0000", topX);
        
        assertEquals(topX, actualTopX.size());
        
        for (int i = 0; i < topX; i++) {
            assertEquals(expectedName[i], actualTopX.get(i).getNickName());
        }
        
        Character character = dao.find(key);
        character.setScore(299l);
        dao.update(character);
        
        actualTopX = dao.getTopXCharacters("0000", topX);
        
        assertEquals(topX, actualTopX.size());
        
        for (int i = 0; i < topX; i++) {
            assertEquals(expectedName[i], actualTopX.get(i).getNickName());
        }
    }
    
    @Test
    public void testStrongConsistentResultMovingHighscore() {
        
        CharacterDAOImplDatastore dao = new CharacterDAOImplDatastore();
        
        int topX = 10;
        String[] expectedNameBefore =  {"Ardar", "Frodo", "Etniss", 
                "Ashecho", "Sam", "Ghaeni", "Kelbur", "Istiny", "Danmor", "Estost"};
        String[] expectedNameAfter =  {"Ardar", "Istiny", "Frodo", "Etniss", 
                "Ashecho", "Sam", "Ghaeni", "Kelbur", "Danmor", "Estost"};
        
        Key<Player> player1 = pDAO.create("1234");
        Key<Player> player2 = pDAO.create("4567");
        
        cDAO.create("Frodo", 10, CharClass.Hobbit, 999l, player1);
        cDAO.create("Sam", 8, CharClass.Hobbit, 500l, player1);
        cDAO.create("Ghaeni", 15, CharClass.Dwarf, 457l, player1);
        cDAO.create("Etniss", 11, CharClass.Elf, 782l, player1);
        cDAO.create("Kelbur", 3, CharClass.Mage, 300l, player1);
        cDAO.create("Danmor", 20, CharClass.Hobbit, 235l, player1);
        cDAO.create("Ashecho", 9, CharClass.Dwarf, 578l, player1);
        cDAO.create("Neroth", 14, CharClass.Hobbit, 54l, player1);
        cDAO.create("Estost", 6, CharClass.Elf, 100l, player1);
        cDAO.create("Ardar", 8, CharClass.Mage, 10000l, player1);
        Key<Character> key = cDAO.create("Istiny", 14, CharClass.Elf, 299l, player2);
        
        List<Character> actualTopX = dao.getTopXCharacters("4567", topX);
        
        assertEquals(topX, actualTopX.size());
        
        for (int i = 0; i < topX; i++) {
            assertEquals(expectedNameBefore[i], actualTopX.get(i).getNickName());
        }
        
        Character character = dao.find(key);
        character.setScore(1000l);
        dao.update(character);
        
        actualTopX = dao.getTopXCharacters("4567", topX);
        
        assertEquals(topX, actualTopX.size());
        
        for (int i = 0; i < topX; i++) {
            assertEquals(expectedNameAfter[i], actualTopX.get(i).getNickName());
        }
    }
    
    @Test
    public void testEventuallyConsistentResultMovingHighscore() {
        
        CharacterDAOImplDatastore dao = new CharacterDAOImplDatastore();
        
        int topX = 10;
        String[] expectedName = {"Ardar", "Frodo", "Etniss", 
                "Ashecho", "Sam", "Ghaeni", "Kelbur", "Istiny", "Danmor", "Estost"};
        
        Key<Player> player1 = pDAO.create("1234");
        Key<Player> player2 = pDAO.create("4567");
        
        CustomHighRepJobPolicy.disable();
        
        cDAO.create("Frodo", 10, CharClass.Hobbit, 999l, player1);
        cDAO.create("Sam", 8, CharClass.Hobbit, 500l, player1);
        cDAO.create("Ghaeni", 15, CharClass.Dwarf, 457l, player1);
        cDAO.create("Etniss", 11, CharClass.Elf, 782l, player1);
        cDAO.create("Kelbur", 3, CharClass.Mage, 300l, player1);
        cDAO.create("Danmor", 20, CharClass.Hobbit, 235l, player1);
        cDAO.create("Ashecho", 9, CharClass.Dwarf, 578l, player1);
        cDAO.create("Neroth", 14, CharClass.Hobbit, 54l, player1);
        cDAO.create("Estost", 6, CharClass.Elf, 100l, player1);
        cDAO.create("Ardar", 8, CharClass.Mage, 10000l, player1);
        Key<Character> key = cDAO.create("Istiny", 14, CharClass.Elf, 299l, player2);
        
        List<Character> actualTopX = dao.getTopXCharacters("0000", topX);
        actualTopX = dao.getTopXCharacters("0000", topX);
        
        assertEquals(topX, actualTopX.size());
        
        for (int i = 0; i < topX; i++) {
            assertEquals(expectedName[i], actualTopX.get(i).getNickName());
        }
        
        CustomHighRepJobPolicy.enable();
        
        Character character = dao.find(key);
        character.setScore(1000l);
        dao.update(character);
        
        actualTopX = dao.getTopXCharacters("0000", topX);
        
        assertEquals(topX, actualTopX.size());
        
        for (int i = 0; i < topX; i++) {
            assertEquals(expectedName[i], actualTopX.get(i).getNickName());
        }
    }
    
    @Test
    public void testStrongConsistentResultLeavingHighscore() {
        
        CharacterDAOImplDatastore dao = new CharacterDAOImplDatastore();
        
        int topX = 10;
        String[] expectedNameAfter =  {"Ardar", "Frodo", "Etniss", 
                "Ashecho", "Sam", "Ghaeni", "Kelbur", "Danmor", "Estost", "Neroth"};
        String[] expectedNameBefore =  {"Ardar", "Frodo", "Etniss", 
                "Ashecho", "Sam", "Ghaeni", "Kelbur", "Istiny", "Danmor", "Estost"};
        
        Key<Player> player1 = pDAO.create("1234");
        Key<Player> player2 = pDAO.create("4567");
        
        cDAO.create("Frodo", 10, CharClass.Hobbit, 999l, player1);
        cDAO.create("Sam", 8, CharClass.Hobbit, 500l, player1);
        cDAO.create("Ghaeni", 15, CharClass.Dwarf, 457l, player1);
        cDAO.create("Etniss", 11, CharClass.Elf, 782l, player1);
        cDAO.create("Kelbur", 3, CharClass.Mage, 300l, player1);
        cDAO.create("Danmor", 20, CharClass.Hobbit, 235l, player1);
        cDAO.create("Ashecho", 9, CharClass.Dwarf, 578l, player1);
        cDAO.create("Neroth", 14, CharClass.Hobbit, 54l, player1);
        cDAO.create("Estost", 6, CharClass.Elf, 100l, player1);
        cDAO.create("Ardar", 8, CharClass.Mage, 10000l, player1);
        Key<Character> key = cDAO.create("Istiny", 14, CharClass.Elf, 299l, player2);
        
        List<Character> actualTopX = dao.getTopXCharacters("4567", topX);
        
        assertEquals(topX, actualTopX.size());
        
        for (int i = 0; i < topX; i++) {
            assertEquals(expectedNameBefore[i], actualTopX.get(i).getNickName());
        }
        
        Character character = dao.find(key);
        character.setScore(0l);
        dao.update(character);
        
        actualTopX = dao.getTopXCharacters("4567", topX);
        
        assertEquals(topX, actualTopX.size());
        
        for (int i = 0; i < topX; i++) {
            assertEquals(expectedNameAfter[i], actualTopX.get(i).getNickName());
        }
    }
    
    @Test
    public void testEventuallyConsistentResultLeavingHighscore() {
        
        CharacterDAOImplDatastore dao = new CharacterDAOImplDatastore();
        
        int topX = 10;
        String[] expectedName = {"Ardar", "Frodo", "Etniss", 
                "Ashecho", "Sam", "Ghaeni", "Kelbur", "Istiny", "Danmor", "Estost"};
        
        Key<Player> player1 = pDAO.create("1234");
        Key<Player> player2 = pDAO.create("4567");
        
        CustomHighRepJobPolicy.disable();
        
        cDAO.create("Frodo", 10, CharClass.Hobbit, 999l, player1);
        cDAO.create("Sam", 8, CharClass.Hobbit, 500l, player1);
        cDAO.create("Ghaeni", 15, CharClass.Dwarf, 457l, player1);
        cDAO.create("Etniss", 11, CharClass.Elf, 782l, player1);
        cDAO.create("Kelbur", 3, CharClass.Mage, 300l, player1);
        cDAO.create("Danmor", 20, CharClass.Hobbit, 235l, player1);
        cDAO.create("Ashecho", 9, CharClass.Dwarf, 578l, player1);
        cDAO.create("Neroth", 14, CharClass.Hobbit, 54l, player1);
        cDAO.create("Estost", 6, CharClass.Elf, 100l, player1);
        cDAO.create("Ardar", 8, CharClass.Mage, 10000l, player1);
        Key<Character> key = cDAO.create("Istiny", 14, CharClass.Elf, 299l, player2);
        
        List<Character> actualTopX = dao.getTopXCharacters("0000", topX);
        
        assertEquals(topX, actualTopX.size());
        
        for (int i = 0; i < topX; i++) {
            assertEquals(expectedName[i], actualTopX.get(i).getNickName());
        }
        
        CustomHighRepJobPolicy.enable();
        
        Character character = dao.find(key);
        character.setScore(0l);
        dao.update(character);
        
        actualTopX = dao.getTopXCharacters("0000", topX);
        
        assertEquals(topX, actualTopX.size());
        
        for (int i = 0; i < topX; i++) {
            assertEquals(expectedName[i], actualTopX.get(i).getNickName());
        }
    }
    
    // TODO Testcase for moving and leaving the highscore
}
