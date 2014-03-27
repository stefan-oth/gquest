package de.oth.app.helloworld.dao;

import de.oth.app.helloworld.model.CharClass;
import de.oth.app.helloworld.model.jdo.Mission;
import de.oth.app.helloworld.model.jdo.Player;

public interface PlayerDAO {

	public void delete(Player player);
	public void update(Player player);
	public Long create(String name, CharClass charClass, String userId);
	public Mission createMission(String description);
	//public List<Player> findAll();
	public Player find(Long id);
	public Player findByUserId(String userId);

}
