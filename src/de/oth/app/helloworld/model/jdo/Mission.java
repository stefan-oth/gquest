package de.oth.app.helloworld.model.jdo;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(detachable="true")
public class Mission {
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    @Persistent
    private String description;
    @Persistent
    private Boolean isAccomplished;

    public Mission() {
        this.isAccomplished = false;
    }
    
    public Key getKey() {
        return key;
    }
    
    public void setKey(Key key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsAccomplished() {
        return isAccomplished;
    }

    public void setIsAccomplished(Boolean isAccomplished) {
        this.isAccomplished = isAccomplished;
    }

    public void accomplish() {
        setIsAccomplished(true);
    }
}
