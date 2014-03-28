package de.oth.app.geekquest.model.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key key;
    private String description;
    private Boolean isAccomplisehd;

    public Mission() {
        this.isAccomplisehd = false;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsAccomplisehd() {
        return isAccomplisehd;
    }

    public void setIsAccomplisehd(Boolean isAccomplisehd) {
        this.isAccomplisehd = isAccomplisehd;
    }

    public void accomplish() {
        setIsAccomplisehd(true);
    }
}
