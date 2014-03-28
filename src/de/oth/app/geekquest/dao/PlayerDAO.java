package de.oth.app.geekquest.dao;

import de.oth.app.geekquest.model.CharClass;
import de.oth.app.geekquest.model.Mission;
import de.oth.app.geekquest.model.Player;

public interface PlayerDAO {

	public void delete(Player player);
	public void update(Player player);
	public Long create(String name, CharClass charClass, String userId);
	public Mission createMission(String description);
	//public List<Player> findAll();
	public Player find(Long id);
	public Player findByUserId(String userId);

}
