package de.oth.app.geekquest.dao;

import java.util.List;

import com.googlecode.objectify.Key;

import de.oth.app.geekquest.model.CharClass;
import de.oth.app.geekquest.model.Character;
import de.oth.app.geekquest.model.Player;

public interface CharacterDAO {
    public void delete(Character character);
    public void update(Character character);
    public Key<Character> create(String name, Long health, CharClass charClass, Long score, 
            Key<Player> parentKey);
    public Character find(Key<Character> key);
    public Character find(Long id, String userId);
    public List<Character> findByUserId(String userId);
    public Character findFirstByUserId(String userId);
    public List<Character> findByParent(Key<Player> parentKey);
    public List<Character> getCharactersForHighscore(int max, int offset);
    public List<Character> getTopXCharacters(String userId, int max);
    public List<Character> findByNickName(String nickName);
}
