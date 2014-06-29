package de.oth.app.geekquest.mapreduce;

import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.tools.mapreduce.Mapper;

public class CopyCharClassMapper extends Mapper<Entity, Key, Key> {
    
    private static final Logger LOG = Logger.getLogger(
            CopyCharClassMapper.class.getName());

    /**
     * 
     */
    private static final long serialVersionUID = 701259116742460518L;

    @Override
    public void map(Entity value) {
        if (!value.hasProperty("charClass")) {
            Key charKey = (Key) value.getProperty("characterKey");
            if (charKey != null) {
                //map the given entity to its parents key
                emit(charKey, value.getKey());
            } else {
                LOG.warning("Character key is null!");
            }
        }
    }
}
