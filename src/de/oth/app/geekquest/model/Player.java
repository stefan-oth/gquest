package de.oth.app.geekquest.model;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;

@Entity
public class Player {
    @Id
    private String name;
    //TODO refactor this!
    private String userId;
    @Ignore
    private List<Character> characters = new ArrayList<>();
    
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.name = userId;
        this.userId = userId;
    }
    
    public List<Character> getCharacters() {
        return characters;
    }
    
    public void setCharacters(List<Character> characters) {
        this.characters = characters;
    }
    
    public void addCharacter(Character character) {
        this.characters.add(character);
    }
}
