package de.oth.app.geekquest.dao.datastore;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.dev.HighRepJobPolicy;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import de.oth.app.geekquest.model.CharClass;
import de.oth.app.geekquest.model.Character;

public class HighscoreQueryHRDTest {
    
    private static final class CustomHighRepJobPolicy implements HighRepJobPolicy {
        static int count = 0;
        @Override
        public boolean shouldApplyNewJob(Key entityGroup) {
            // every other new job fails to apply
            return count++ % 2 == 0;
        }

        @Override
        public boolean shouldRollForwardExistingJob(Key entityGroup) {
            // every other exsting job fails to apply
            return count++ % 2 == 0;
        }
    }


    //By setting the unapplied job percentage to 100, we are instructing the 
    //local datastore to operate with the maximum amount of eventual consistency. 
    //Maximum eventual consistency means writes will commit but always fail to 
    //apply, so global (non-ancestor) queries will consistently fail to see changes
    // maximum eventual consistency    
//    private final LocalServiceTestHelper helper =
//        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
//            .setDefaultHighRepJobPolicyUnappliedJobPercentage(100));
    
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
               .setAlternateHighRepJobPolicyClass(CustomHighRepJobPolicy.class));

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }


    @Test
    public void testHighscoreQuery() {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        
        Entity entity = new Entity(Character.class.getSimpleName());

        entity.setProperty("name", "Frodo");
        entity.setProperty("health", 10);
        entity.setProperty("charClass", CharClass.Hobbit.toString());
        entity.setProperty("score", 900);
        
        ds.put(entity);
        
        entity = new Entity(Character.class.getSimpleName());

        entity.setProperty("name", "Gandalf");
        entity.setProperty("health", 100);
        entity.setProperty("charClass", CharClass.Mage.toString());
        entity.setProperty("score", 1000);
        
        ds.put(entity);
        
        List<Character> entities = new CharacterDAOImplDatastore()
                .getCharactersForHighscore(10, 0);
        
        assertEquals(1, entities.size());
        // first global query only sees the first Entity
        entities = new CharacterDAOImplDatastore().getCharactersForHighscore(
                10, 0);
        // second global query sees both Entities because we "groom" (attempt to
        // apply unapplied jobs) after every query
        assertEquals(2, entities.size());
        
    }
}
