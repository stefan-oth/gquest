package de.oth.app.geekquest.dao;

import java.util.List;

import com.google.appengine.api.datastore.Key;

import de.oth.app.geekquest.model.CharClass;
import de.oth.app.geekquest.model.Character;

public interface CharacterDAO {
    public void delete(Character character);
    public void update(Character character);
    public Key create(String name, Integer health, CharClass charClass, Long score, 
            Key parentKey);
    public Character find(Key key);
    public Character find(Long id, String userId);
    public List<Character> findByUserId(String userId);
    public Character findFirstByUserId(String userId);
    public List<Character> findByParent(Key parentKey);
    public List<Character> getCharactersForHighscore(int max, int offset);
    public List<Character> getTopXCharacters(String userId, int max);
}
