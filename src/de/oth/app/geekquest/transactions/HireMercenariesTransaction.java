package de.oth.app.geekquest.transactions;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Work;

import de.oth.app.geekquest.model.Character;

public class HireMercenariesTransaction implements Work<Void> {
    
    private Character[] characters;
    private Character hirer;
    private long pay;
    
    public HireMercenariesTransaction(long pay, Character hirer, Character... characters) {
        this.characters = characters;
        this.pay = pay;
        this.hirer = hirer;
    }
    
    public Void run() {
        Objectify ofy = ObjectifyService.ofy();
        
        Key<Character> key = Key.create(hirer.getParentKey(), Character.class,
                hirer.getId());
        hirer = ofy.load().key(key).now();
        
        if (hirer == null || hirer.getGold() < pay * characters.length) {
            return null;
        }
        
        //hire characters
        for (Character mercenary : characters) {
            mercenary.earn(pay);
            hirer.spend(pay);
        }
        ofy.save().entities(characters);
        ofy.save().entity(hirer);

        return null;
    }
}
