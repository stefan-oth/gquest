package de.oth.app.geekquest.model;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class Character {

    @Id
    private Long id;
    @Parent
    private Key<Player> parent;
	private String name;
	@Index
	private CharClass charClass;
	@Index
	private Integer health;
	@Index
	private Long score;
	private String imageBlobKey;
	@Ignore
	private List<Mission> missions = new ArrayList<>();
	
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Key<Player> getParentKey() {
        return parent;
    }
    
    public void setParentKey(Key<Player> parent) {
        this.parent = parent;
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
	
	public String getImageBlobKey() {
	    return imageBlobKey;
	}
	
	public void setImageBlobKey(String imageBlobKey) {
	    this.imageBlobKey = imageBlobKey;
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

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }
}
