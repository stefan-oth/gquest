package de.oth.app.geekquest.model;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Work;
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
    
    public boolean isEmpty() {
        return this.healthpoints == null || this.healthpoints <= 0;
    }
    
    public long drink() {
        Objectify ofy = ObjectifyService.ofy();
        
        if (this.owner == null) {
            return 0;
        }
        
        Long healthHealed = ofy.transact(new DrinkPotionWork(this));
        
        return healthHealed;
    }
    
    private class DrinkPotionWork implements Work<Long> {
        
        private Potion potion;
        
        public DrinkPotionWork(Potion potion) {
            this.potion = potion;
        }
        
        public Long run() {
            Objectify ofy = ObjectifyService.ofy();
            Character character = ofy.load().key(potion.owner).now();
            
            if (character.getHealth() <= 0) {
                return 0l;
            }

            long heal = Math.min(Character.MAX_HEALTH - character.getHealth(), 
                    potion.healthpoints);
            
            character.heal(heal);
            potion.healthpoints = 0l;
            
            ofy.save().entities(character, potion);

            return heal;
        }
    }
}
