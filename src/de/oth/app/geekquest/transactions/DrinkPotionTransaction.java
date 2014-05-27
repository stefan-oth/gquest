package de.oth.app.geekquest.transactions;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Work;

import de.oth.app.geekquest.model.Character;
import de.oth.app.geekquest.model.Potion;

public class DrinkPotionTransaction implements Work<Long> {
    
    private Potion potion;
    
    public DrinkPotionTransaction(Potion potion) {
        this.potion = potion;
    }
    
    public Long run() {
        Objectify ofy = ObjectifyService.ofy();
        Character character = ofy.load().key(potion.getOwnerKey()).now();
        
        if (character.getHealth() <= 0) {
            return 0l;
        }

        long heal = Math.min(Character.MAX_HEALTH - character.getHealth(), 
                potion.getHealthpoints());
        
        character.heal(heal);
        potion.setEmpty();
        
        ofy.save().entities(character, potion);

        return heal;
    }
}