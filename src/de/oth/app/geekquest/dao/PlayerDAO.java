package de.oth.app.geekquest.dao;

import com.google.appengine.api.datastore.Key;

import de.oth.app.geekquest.model.Player;

public interface PlayerDAO {

	public void delete(Player player);
	public void update(Player player);
	public Key create(String userId);
	public Player find(Key key);
	public Player findByUserId(String userId);

}
