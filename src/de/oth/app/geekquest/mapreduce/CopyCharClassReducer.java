package de.oth.app.geekquest.mapreduce;

import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.tools.mapreduce.Reducer;
import com.google.appengine.tools.mapreduce.ReducerInput;

public class CopyCharClassReducer extends Reducer<Key, Key, Void> {
    
    private static final Logger LOG = Logger.getLogger(
            CopyCharClassReducer.class.getName());

    /**
     * 
     */
    private static final long serialVersionUID = -1500306454016431719L;
    
    
    @Override
    public void reduce(Key key, ReducerInput<Key> values) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        if (key == null) {
            LOG.warning("Character key is null!");
            return;
        }
        
        try {
            Entity character = datastore.get(key);
            String charClass = (String) character.getProperty("charClass");
            
            if (charClass != null) {
                while (values.hasNext()) {
                    Key missionKey = values.next();
                    Entity mission;
                    
                    try {
                        mission = datastore.get(missionKey);
                    } catch (EntityNotFoundException e) {
                        LOG.warning("Could not find Mission entity for key: " 
                                + missionKey );
                        continue;
                    }
                    
                    if (!mission.hasProperty("charClass")) {
                        mission.setProperty("charClass", charClass);
                        datastore.put(mission);
                    }
                }
            } else {
                LOG.warning("CharClass of Character key: " + key + " is null");
            }
            
        } catch (EntityNotFoundException e) {
            LOG.warning("Could not find Character entity for key: " + key );
        }
    }
}
