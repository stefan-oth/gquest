package de.oth.app.geekquest.dao.jpa;

import javax.persistence.EntityManager;

import de.oth.app.geekquest.model.CharClass;
import de.oth.app.geekquest.model.jpa.Mission;
import de.oth.app.geekquest.model.jpa.Player;

public class PlayerDAOImplJPA {

	//@Override
	public void delete(Player player) {
	    EntityManager em = EMF.get().createEntityManager();
	    try {
	      Player p = em.find(Player.class, player.getId());
	      em.remove(p);
	    } finally {
	      em.close();
	    }
	}

	//@Override
	public void update(Player player) {
	    EntityManager em = EMF.get().createEntityManager();
	    try {
	      em.merge(player);
	    } finally {
	      em.close();
	    }
	}

	//@Override
	public Long create(String name, CharClass charClass) {
		EntityManager em = EMF.get().createEntityManager();
		Long id = null;
		try {
			Player player =  new Player();
			player.setName(name);
			player.setHealth(10);
			player.setCharClass(charClass);
			em.persist(player);
			em.refresh(player);
			id = player.getId();
		} finally {
			em.close();
		}

		return id;
	}
	
    //TODO auslagern?
    //@Override
    public Mission createMission(String description) {
        Mission mission = new Mission();
        mission.setDescription(description);
        return mission;
    }

	//@Override
	public Player find(Long id) {
		Player p;
		EntityManager em = EMF.get().createEntityManager();
	    try {
	      p = em.find(Player.class, id);
	    } finally {
	      em.close();
	    }

	    return p;
	}
}
