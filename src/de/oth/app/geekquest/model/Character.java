package de.oth.app.geekquest.model;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.AlsoLoad;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class Character {
    
    public static final long MAX_HEALTH = 20;

    @Id
    private Long id;
    @Parent
    private Key<Player> parent;
    @AlsoLoad("name")
	private String nickName;
	@Index
	private CharClass charClass;
	@Index
	private Long health;
	@Index
	private Long score;
	private Long gold;
	private String imageBlobKey;
	
	@Ignore
	private List<Mission> missions = new ArrayList<>();
	@Ignore
	private List<Potion> potions = new ArrayList<>();
	
	public Character() {
	    this.gold = 0l;
	    this.score = 0l;
	    this.health = 10l;
	}
	
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
	
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String name) {
		this.nickName = name;
	}

	public CharClass getCharClass() {
		return charClass;
	}

	public void setCharClass(CharClass charClass) {
		this.charClass = charClass;
	}

	public Long getHealth() {
		return health;
	}

	public void setHealth(long health) {
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
	
	public List<Potion> getPotions() {
	    return potions;
	}
	
	public void setPotions(List<Potion> potions) {
	    this.potions = potions;
	}
	
	public void addPotion(Potion potion) {
	    this.potions.add(potion);
	}

	public void heal(long points) {
		setHealth(getHealth() + points);
	}

	public void hurt(long points) {
		setHealth(getHealth() - points);
	}

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }
    
    public Long getGold() {
        return gold;
    }
    
    public void setGold(long gold) {
        this.gold = gold;
    }
    
    public void earn(long gold) {
        this.gold += gold;
    }
    
    public void spend(long gold) {
        this.gold -= gold;
    }
}
