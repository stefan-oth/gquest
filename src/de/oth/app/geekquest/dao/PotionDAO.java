package de.oth.app.geekquest.dao;

import java.util.List;

import com.googlecode.objectify.Key;

import de.oth.app.geekquest.model.Character;
import de.oth.app.geekquest.model.Potion;
import de.oth.app.geekquest.model.Potion.Color;

public interface PotionDAO {
    public void delete(Potion potion);
    public Key<Potion> create(Color color, long healthpoints, Key<Character> ownerKey);
    public void save(Potion potion);
    public void save(List<Potion> potions);
    public Potion find(Key<Potion> key);
    public Potion find(Long id, Key<Character> parentKey);
    public List<Potion> findByParent(Key<Character> parentKey);
}
