package de.oth.app.geekquest.dao;

import java.util.List;

import com.google.appengine.api.datastore.Key;

import de.oth.app.geekquest.model.Mission;

public interface MissionDAO {
    public void delete(Mission mission);
    public void update(Mission mission);
    public Key create(String description, Boolean accomplished, Key parentKey);
    public Mission find(Key key);
    public Mission find(Long id, Key parentKey);
    public List<Mission> findByParent(Key parentKey);
}
