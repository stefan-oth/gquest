package de.oth.app.geekquest.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.AlsoLoad;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

import de.oth.app.geekquest.transactions.DrinkPotionTransaction;
import de.oth.app.geekquest.transactions.HireMercenariesTransaction;
import de.oth.app.geekquest.transactions.SellPotionTransaction;
import de.oth.app.geekquest.util.ShardedCounter;

@Entity
public class Character {
    
    public static final int MAX_MERCENERIES_PER_TRANSACTION = 4;
    public static final long MAX_HEALTH = 100;

    @Id
    private Long id;
    @Parent
    private Key<Player> parent;
    @AlsoLoad("name")
    @Index
	private String nickName;
	@Index
	private CharClass charClass;
	@Ignore
	private ShardedCounter health;
	@Index
	private Long score;
	private Long gold;
	@Index
	private String imageBlobKey;
	
	@Ignore
	private List<Mission> missions = new ArrayList<>();
	@Ignore
	private List<Potion> potions = new ArrayList<>();
	
	public Character() {
	    this.gold = 0l;
	    this.score = 0l;
	}
	
	public void importHealth(@AlsoLoad("health") Long health) {
	    if (health != null) {
	        if (this.health == null) {
	            this.health = new ShardedCounter(getShardedCounterName("health"));
	        }
	        
	        if (!this.health.isShardCreated()) {
	            this.health.increment(health);
	        }
	    }
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
	    if (health == null) {
	        health = new ShardedCounter(getShardedCounterName("health"));
	    }
	    
		return health.getValue();
	}

	public void setHealth(long hp) {
        if (health == null) {
            health = new ShardedCounter(getShardedCounterName("health"));
        }
        if (health.isShardCreated()) {
            health.reset();
        }
       
        health.increment(hp);
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

	public void heal(final long points) {
	    
        if (health == null) {
            health = new ShardedCounter(getShardedCounterName("health"));
        }
        
        long hp = Math.min(MAX_HEALTH - getHealth(), points);
        
        health.increment(hp);
	}

	public void hurt(final long points) {
	    
        if (health == null) {
            health = new ShardedCounter(getShardedCounterName("health"));
        }
        
        long hp = Math.min(getHealth(), points);
        
        health.decrement(hp);
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
    
    public void sell(Potion potion, Character recipient, long price) {
        
        Objectify ofy = ObjectifyService.ofy();
        
        ofy.transact(new SellPotionTransaction(potion, this, recipient, price));
        
    }
    
    public void hire(long pay, Character... characters) {
        if (characters == null || characters.length <= 0) {
            return;
        }
        
        Objectify ofy = ObjectifyService.ofy();
        
        int transactionCnt = (int) Math.ceil((double) characters.length / 
                MAX_MERCENERIES_PER_TRANSACTION);
        
        for (int i = 0; i < transactionCnt; i++) {
            int from = MAX_MERCENERIES_PER_TRANSACTION * i;
            int to = Math.min((MAX_MERCENERIES_PER_TRANSACTION * (i + 1)), 
                    characters.length);
            Character[] merceneries = Arrays.copyOfRange(characters, from, to);
            ofy.transact(new HireMercenariesTransaction(pay, this, merceneries));
        }
    }
    
    public long drink(Potion potion) {
        Objectify ofy = ObjectifyService.ofy();
        
        if (potion.getOwnerKey() == null) {
            return 0;
        }
        
        Long healthHealed = ofy.transact(new DrinkPotionTransaction(potion));
        
        return healthHealed;
    }
    
    private String getShardedCounterName(String property) {
        return Character.class.getSimpleName() + "_" + getId() + "_" + property;
    }
}
