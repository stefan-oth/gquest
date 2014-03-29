package de.oth.app.geekquest.model;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Key;

public class Character {
    
    private Key key;
	private String name;
	private CharClass charClass;
	private Integer health;
	private List<Mission> missions = new ArrayList<>();

	public Key getKey() {
	    return key;
	}
	
	public void setKey(Key key) {
	    this.key = key;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CharClass getCharClass() {
		return charClass;
	}

	public void setCharClass(CharClass charClass) {
		this.charClass = charClass;
	}

	public Integer getHealth() {
		return health;
	}

	public void setHealth(Integer health) {
		this.health = health;
	}

	public List<Mission> getMissions() {
		return missions;
	}

	public void setMissions(List<Mission> missions) {
		this.missions = missions;
	}

	public void addMissions(Mission mission) {
		this.missions.add(mission);
	}

	public void heal(int points) {
		setHealth(getHealth() + points);
	}

	public void hurt(int points) {
		setHealth(getHealth() - points);
	}
}
