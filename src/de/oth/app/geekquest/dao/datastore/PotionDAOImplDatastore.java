package de.oth.app.geekquest.dao.datastore;

import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

import de.oth.app.geekquest.dao.PotionDAO;
import de.oth.app.geekquest.model.Character;
import de.oth.app.geekquest.model.Potion;
import de.oth.app.geekquest.model.Potion.Color;

public class PotionDAOImplDatastore implements PotionDAO {

    @Override
    public void delete(Potion potion) {
        Objectify ofy = ObjectifyService.ofy();
        
        if (potion.getId() == null) {
            System.out.println("Delete - Character id is null");
            return;
        }
        
        if (potion.getOwnerKey() == null) {
            System.out.println("Delete - Character parentKey is null");
            return;
        }
        
        Key<Potion> key = Key.create(potion.getOwnerKey(), Potion.class, 
                potion.getId());
        
        ofy.delete().key(key);
    }

    @Override
    public Key<Potion> create(Color color, long healthpoints,
            Key<Character> ownerKey) {
        Potion potion = new Potion(color, healthpoints, ownerKey);
        
        save(potion);

        Key<Potion> key = Key.create(potion.getOwnerKey(), Potion.class, 
                potion.getId());
        
        return key;
    }

    @Override
    public void save(Potion potion) {
        Objectify ofy = ObjectifyService.ofy();

        ofy.save().entity(potion).now();
    }

    @Override
    public void save(List<Potion> potions) {
        Objectify ofy = ObjectifyService.ofy();

        ofy.save().entities(potions).now();
    }

    @Override
    public Potion find(Key<Potion> key) {
        Objectify ofy = ObjectifyService.ofy();
        
        Potion potion = ofy.load().key(key).now();

        return potion;
    }

    @Override
    public Potion find(Long id, Key<Character> parentKey) {
        Key<Potion> key = Key.create(parentKey, Potion.class, id);
        return find(key);
    }

    @Override
    public List<Potion> findByParent(Key<Character> parentKey) {
        Objectify ofy = ObjectifyService.ofy();
        
        List<Potion> potions = ofy.load().type(Potion.class).ancestor(
                parentKey).list();
        
        return potions;
    }

}
