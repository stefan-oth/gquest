package de.oth.app.geekquest.model;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class Mission {
    
    @Id
    private Long id;
    @Parent
    private Key<Character> parent;
    private String description;
    private Boolean isAccomplished;

    public Mission() {
        this.isAccomplished = false;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Key<Character> getParentKey() {
        return parent;
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
