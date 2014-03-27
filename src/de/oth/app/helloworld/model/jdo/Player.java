package de.oth.app.helloworld.model.jdo;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import de.oth.app.helloworld.model.CharClass;

@PersistenceCapable(detachable="true")
public class Player {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
    @Persistent
    private String userId;
    @Persistent
	private String name;
    @Persistent
	private CharClass charClass;
    @Persistent
	private Integer health;
    @Persistent(defaultFetchGroup = "true")
    @Element(dependent = "true")
	private List<Mission> missions = new ArrayList<>();

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
