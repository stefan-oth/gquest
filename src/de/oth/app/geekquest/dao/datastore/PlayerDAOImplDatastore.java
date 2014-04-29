package de.oth.app.geekquest.dao.datastore;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

import de.oth.app.geekquest.dao.PlayerDAO;
import de.oth.app.geekquest.model.Player;

public class PlayerDAOImplDatastore implements PlayerDAO {

    @Override
    public void delete(Player player) {
        Objectify ofy = ObjectifyService.ofy();
        
        if (player.getUserId() == null) {
            System.out.println("Delete - Player userId is null");
            return;
        }
        
        Key<Player> key = Key.create(Player.class, player.getUserId());
        
        ofy.delete().key(key);  
    }

    //TODO rename to save
    @Override
    public void update(Player player) {
        Objectify ofy = ObjectifyService.ofy();

        ofy.save().entity(player).now();
    }

    @Override
    public Key<Player> create(String userId) {
        
        Player player = new Player();
        player.setUserId(userId);

        Key<Player> key = Key.create(Player.class, userId);
        
        return key;
    }

    @Override
    public Player find(Key<Player> key) {
        Objectify ofy = ObjectifyService.ofy();
        
        Player player = ofy.load().key(key).now();

        return player;
    }

    @Override
    public Player findByUserId(String userId) {
        Key<Player> key = Key.create(Player.class, userId);
        return find(key);
    }
}
