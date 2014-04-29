package de.oth.app.geekquest.dao;

import com.googlecode.objectify.Key;

import de.oth.app.geekquest.model.Player;

public interface PlayerDAO {

	public void delete(Player player);
	public void update(Player player);
	public Key<Player> create(String userId);
	public Player find(Key<Player> key);
	public Player findByUserId(String userId);

}
