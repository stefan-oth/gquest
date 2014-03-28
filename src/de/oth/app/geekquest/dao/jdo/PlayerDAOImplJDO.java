package de.oth.app.geekquest.dao.jdo;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import de.oth.app.geekquest.dao.PlayerDAO;
import de.oth.app.geekquest.model.CharClass;
import de.oth.app.geekquest.model.jdo.Mission;
import de.oth.app.geekquest.model.jdo.Player;

public class PlayerDAOImplJDO implements PlayerDAO {

    @Override
    public void delete(Player player) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            Player p = pm.getObjectById(Player.class, player.getId());
            pm.deletePersistent(p);
        } finally {
            pm.close();
        }
    }

    @Override
    public void update(Player player) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            pm.makePersistent(player);
        } finally {
            pm.close();
        }
    }

    @Override
    public Long create(String name, CharClass charClass, String userId) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Long id = null;
        try {
            Player player = new Player();
            player.setName(name);
            player.setHealth(10);
            player.setCharClass(charClass);
            player.setUserId(userId);
            pm.makePersistent(player);
            id = player.getId();
        } finally {
            pm.close();
        }

        return id;
    }
    
    //TODO auslagern?
    @Override
    public Mission createMission(String description) {
        Mission mission = new Mission();
        mission.setDescription(description);
        return mission;
    }

    @Override
    public Player find(Long id) {
        Player p;
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            p = pm.getObjectById(Player.class, id);
            //detachCopy ist notwendig, damit man nach dem pm.close() das
            //"Entity" noch verändern und dann wieder persistieren kann
            p = pm.detachCopy(p);
        } finally {
            pm.close();
        }

        return p;
    }
    
    @Override
    public Player findByUserId(String userId) {
        Player p = null;
        PersistenceManager pm = PMF.get().getPersistenceManager();
        //pm.getFetchPlan().addGroup("playerGroup");
        //pm.getFetchPlan().setMaxFetchDepth(2);
        try {
            Query q = pm.newQuery(Player.class);
            q.setFilter("userId == userIdParam");
            q.declareParameters("String userIdParam");

            try {
              @SuppressWarnings("unchecked")
              List<Player> results = (List<Player>) q.execute(userId);
              if (results.size() > 0) {
                  //p = results.get(0);
                  //detachCopy ist notwendig, damit man nach dem pm.close() das
                  //"Entity" noch verändern und dann wieder persistieren kann
                  p = pm.detachCopy(results.get(0));
              }
            } finally {
              q.closeAll();
            }
        } finally {
            pm.close();
        }

        return p;
    }
}
