package de.oth.app.helloworld.model.jpa;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import de.oth.app.helloworld.model.CharClass;
import de.oth.app.helloworld.model.jpa.Mission;
import de.oth.app.helloworld.model.jpa.Player;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    private String userId;
	private String name;
	@Enumerated(EnumType.STRING)
	private CharClass charClass;
	private Integer health;
	@Basic
	private List<Mission> missions;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
