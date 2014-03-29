package de.oth.app.geekquest.model;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Key;

public class Player {
    
    private Key key;
    private String userId;
    private List<Character> characters = new ArrayList<>();

    
    public Key getKey() {
        return key;
    }
    
    public void setKey(Key key) {
        this.key = key;
    }
    
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
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
