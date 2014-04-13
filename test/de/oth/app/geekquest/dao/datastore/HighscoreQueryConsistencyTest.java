package de.oth.app.geekquest.dao.datastore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.dev.HighRepJobPolicy;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import de.oth.app.geekquest.dao.CharacterDAO;
import de.oth.app.geekquest.dao.PlayerDAO;
import de.oth.app.geekquest.model.CharClass;
import de.oth.app.geekquest.model.Character;

public class HighscoreQueryConsistencyTest {

    private static final class CustomHighRepJobPolicy implements HighRepJobPolicy {
        @Override
        public boolean shouldApplyNewJob(Key entityGroup) {
            // every other new job fails to apply
            return !entityGroup.getName().equals("4567");
        }

        @Override
        public boolean shouldRollForwardExistingJob(Key entityGroup) {
            // every other exsting job fails to apply
            return !entityGroup.getName().equals("4567");
        }
    }
    
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
               .setAlternateHighRepJobPolicyClass(CustomHighRepJobPolicy.class));
    
    private PlayerDAO pDAO = new PlayerDAOImplDatastore();
    private CharacterDAO cDAO = new CharacterDAOImplDatastore();

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testEventuallyConsistentResult() {
        
        int topX = 10;
        
        String[] expectedName =  {"Istiny", "Ardar", "Frodo", "Etniss", "Undny", 
                "Ashecho", "Sam", "Ghaeni", "Kelbur", "Danmor"};
        
        Key player1 = pDAO.create("1234");
        Key player2 = pDAO.create("4567");
        
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
            assertEquals(expectedName[i], actualTopX.get(i).getName());
        }
    }
    
    @Test
    public void testStrongConsistentResult() {
        int topX = 10;
        
        String[] expectedName =  {"Istiny", "Ardar", "Frodo", "Etniss", "Undny", 
                "Ashecho", "Sam", "Ghaeni", "Kelbur", "Danmor"};
        
        Key player1 = pDAO.create("1234");
        Key player2 = pDAO.create("4321");
        
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
                "1234", topX);
        assertEquals(topX, actualTopX.size());
        
        for (int i = 0; i < topX; i++) {
            assertEquals(expectedName[i], actualTopX.get(i).getName());
        }
    }
}
