package de.oth.app.geekquest.model;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class Potion {
    
    public enum Color {
        RED, BLUE, GREEN;
    }
    
    @Id
    private Long id;
    @Parent
    private Key<Character> owner;
    private Color color;
    private Long healthpoints;
    
    public Potion() {
        healthpoints = 0l;
    }
    
    public Potion(Color color, long healthpoints, Key<Character> owner) {
        this.color = color;
        this.healthpoints = healthpoints;
        this.owner = owner;
    }
    
    public Long getId() {
        return id;
    }
    
    public Key<Character> getOwnerKey() {
        return owner;
    }
    
    public Color getColor() {
        return color;
    }
    
    public Long getHealthpoints() {
        return healthpoints;
    }
    
    public void setEmpty() {
        this.healthpoints = 0l;
    }
    
    public boolean isEmpty() {
        return this.healthpoints == null || this.healthpoints <= 0;
    }
}
