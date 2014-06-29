package de.oth.app.geekquest.mapreduce;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.tools.mapreduce.MapOnlyMapper;

import de.oth.app.geekquest.dao.DAOManager;
import de.oth.app.geekquest.model.Character;

public class MissionMigrationMapJob extends MapOnlyMapper<Key, Void> {

    /**
     * 
     */
    private static final long serialVersionUID = 801352994780838466L;

    @Override
    public void map(Key key) {    
        //load character to invoke the onLoad() method which migrates the missions
        //of the character
        Character character = DAOManager.getCharacterDAO().find(key.getId(), 
                key.getParent().getName());
    }
}
