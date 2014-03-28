package de.oth.app.geekquest.model;

import com.google.appengine.api.datastore.Key;

public class Mission {
    
    private Key key;
    private String description;
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
