package de.oth.app.geekquest.dao;

import java.util.List;

import com.googlecode.objectify.Key;

import de.oth.app.geekquest.model.Character;
import de.oth.app.geekquest.model.Mission;


public interface MissionDAO {
    public void delete(Mission mission);
    public void update(Mission mission);
    public Key<Mission> create(String description, Boolean accomplished, Key<Character> parentKey);
    public Mission find(Key<Mission> key);
    public Mission find(Long id, Key<Character> parentKey);
    public List<Mission> findByParent(Key<Character> parentKey);
}
