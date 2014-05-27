package de.oth.app.geekquest.transactions;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Work;

import de.oth.app.geekquest.dao.DAOManager;
import de.oth.app.geekquest.dao.PotionDAO;
import de.oth.app.geekquest.model.Character;
import de.oth.app.geekquest.model.Potion;

public class SellPotionTransaction implements Work<Void> {
    
    private Potion potion;
    private Character owner;
    private Character recipient;
    private long price;
    
    public SellPotionTransaction(Potion potion, Character owner, Character recipient, 
            long price) {
        this.potion = potion;
        this.recipient = recipient;
        this.owner = owner;
        this.price = price;
    }
    
    public Void run() {
        PotionDAO pDAO = DAOManager.getPotionDAO();
        Objectify ofy = ObjectifyService.ofy();
        
        //check if potion still exists
        Key<Potion> key = Key.create(potion.getOwnerKey(), Potion.class,
                potion.getId());
        Potion p = ofy.load().key(key).now();
        
        if (p == null) {
            return null;
        }
        
        //check if seller and buyer exists and if the buyer has enough gold
        Character seller = ofy.load().key(potion.getOwnerKey()).now();
        Key<Character> buyerKey = Key.create(recipient.getParentKey(), 
                Character.class, recipient.getId());
        Character buyer = ofy.load().key(buyerKey).now();
        
        if (seller == null || buyer == null
                || buyer.getGold() < price) {
            return null;
        }
        
        //sell potion
        buyer.spend(price);
        seller.earn(price);
        ofy.save().entities(buyer, seller);
        
        key = pDAO.create(potion.getColor(), potion.getHealthpoints(), 
                buyerKey);
        pDAO.delete(potion);
        
        owner.getPotions().remove(potion);
        recipient.addPotion(ofy.load().key(key).now());

        return null;
    }
}
