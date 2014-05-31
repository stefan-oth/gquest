package de.oth.app.geekquest.model;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Key;

import de.oth.app.geekquest.dao.CharacterDAO;
import de.oth.app.geekquest.dao.DAOManager;
import de.oth.app.geekquest.dao.PlayerDAO;
import de.oth.app.geekquest.dao.PotionDAO;

public class CharacterTest {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(
                    new LocalDatastoreServiceTestConfig().setApplyAllHighRepJobPolicy());
    
    private PlayerDAO pDAO = DAOManager.getPlayerDAO();
    private CharacterDAO cDAO = DAOManager.getCharacterDAO();
    private PotionDAO potionDAO = DAOManager.getPotionDAO();

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }
    
    @Test
    public void testCharacterDrinksPotion() {
        Key<Player> playerKey = pDAO.create("1234");
        
        Key<Character> characterKey = cDAO.create("Frodo", 10l, 
                CharClass.Hobbit, 999l, playerKey);
        
        potionDAO.create(Potion.Color.RED, 3, characterKey);
        
        Character character = cDAO.find(characterKey);
        
        assertNotNull("potions", character.getPotions());
        assertEquals("potion count", 1, character.getPotions().size());
        
        Potion potion = character.getPotions().get(0);
        
        assertEquals("potion enmpty", false, potion.isEmpty());
        assertEquals("health", Long.valueOf(10), character.getHealth());
        
        character.drink(potion);
        
        assertEquals("potion enmpty", true, potion.isEmpty());
        
        //refetch character 
        character = cDAO.find(characterKey);        
        potion = character.getPotions().get(0);
        
        assertEquals("potion enmpty", true, potion.isEmpty());
        assertEquals("health", Long.valueOf(13), character.getHealth());
    }
    
    @Test
    public void testDeadCharacterDrinksPotion() {
        Key<Player> playerKey = pDAO.create("1234");
        
        Key<Character> characterKey = cDAO.create("Frodo", 0l, 
                CharClass.Hobbit, 999l, playerKey);
        
        potionDAO.create(Potion.Color.RED, 3, characterKey);
        
        Character character = cDAO.find(characterKey);
        
        assertNotNull("potions", character.getPotions());
        assertEquals("potion count", 1, character.getPotions().size());
        
        Potion potion = character.getPotions().get(0);
        
        assertEquals("potion enmpty", false, potion.isEmpty());
        assertEquals("health", Long.valueOf(0), character.getHealth());
        
        character.drink(potion);
        
        assertEquals("potion enmpty", false, potion.isEmpty());
        
        //refetch character 
        character = cDAO.find(characterKey);        
        potion = character.getPotions().get(0);
        
        assertEquals("potion enmpty", false, potion.isEmpty());
        assertEquals("health", Long.valueOf(0), character.getHealth());
    }
    
    @Test
    public void testAlmostFullHealthCharacterDrinksPotion() {
        Key<Player> playerKey = pDAO.create("1234");
        
        Key<Character> characterKey = cDAO.create("Frodo", 
                Character.MAX_HEALTH - 1, 
                CharClass.Hobbit, 999l, playerKey);
        
        potionDAO.create(Potion.Color.RED, 3, characterKey);
        
        Character character = cDAO.find(characterKey);
        
        assertNotNull("potions", character.getPotions());
        assertEquals("potion count", 1, character.getPotions().size());
        
        Potion potion = character.getPotions().get(0);
        
        assertEquals("potion enmpty", false, potion.isEmpty());
        assertEquals("health", Long.valueOf(Character.MAX_HEALTH - 1), 
                character.getHealth());
        
        character.drink(potion);
        
        assertEquals("potion enmpty", true, potion.isEmpty());
        
        //refetch character 
        character = cDAO.find(characterKey);        
        potion = character.getPotions().get(0);
        
        assertEquals("potion enmpty", true, potion.isEmpty());
        assertEquals("health", Long.valueOf(Character.MAX_HEALTH), character.getHealth());
    }

    @Test
    public void testCharacterSellsPotion() {
        Key<Player> sellerPKey = pDAO.create("1234");
        Key<Player> buyerPKey = pDAO.create("4321");
        
        Key<Character> sellerCKey = cDAO.create("Frodo", 10l, 
                CharClass.Hobbit, 999l, sellerPKey);
        
        Key<Potion> potionKey = potionDAO.create(Potion.Color.RED, 3, sellerCKey);
        
        Key<Character> buyerCKey = cDAO.create("Sam", 10l, 
                CharClass.Hobbit, 999l, buyerPKey);
        
        Character seller = cDAO.find(sellerCKey);
        Character buyer = cDAO.find(buyerCKey);
        buyer.setGold(15);
        cDAO.update(buyer);
         
        //evaluation before selling the potion
        assertNotNull("potions", seller.getPotions());
        assertEquals("potion count", 1, seller.getPotions().size());
        assertNotNull("gold", seller.getGold());
        assertEquals("gold count", Long.valueOf(0l), seller.getGold());
        assertNotNull("potions", buyer.getPotions());
        assertEquals("potion count", 0, buyer.getPotions().size());
        assertNotNull("gold", buyer.getGold());
        assertEquals("gold count", Long.valueOf(15l), buyer.getGold());
        
        Potion potion = seller.getPotions().get(0);
        seller.sell(potion, buyer, 10);
        
        //refetch characters and potion
        seller = cDAO.find(sellerCKey);
        buyer = cDAO.find(buyerCKey);
        potion = potionDAO.find(potionKey);
        
        //evaluation after selling the potion
        assertNull("old potion", potion);
        assertNotNull("potions", seller.getPotions());
        assertEquals("potion count", 0, seller.getPotions().size());
        assertNotNull("gold", seller.getGold());
        assertEquals("gold count", Long.valueOf(10l), seller.getGold());
        assertNotNull("potions", buyer.getPotions());
        assertEquals("potion count", 1, buyer.getPotions().size());
        assertNotNull("gold", buyer.getGold());
        assertEquals("gold count", Long.valueOf(5l), buyer.getGold());
    }
    
    @Test
    public void testCharacterSellsPotionWithoutGold() {
        Key<Player> sellerPKey = pDAO.create("1234");
        Key<Player> buyerPKey = pDAO.create("4321");
        
        Key<Character> sellerCKey = cDAO.create("Frodo", 10l, 
                CharClass.Hobbit, 999l, sellerPKey);
        
        Key<Potion> potionKey = potionDAO.create(Potion.Color.RED, 3, sellerCKey);
        
        Key<Character> buyerCKey = cDAO.create("Sam", 10l, 
                CharClass.Hobbit, 999l, buyerPKey);
        
        Character seller = cDAO.find(sellerCKey);
        Character buyer = cDAO.find(buyerCKey);
         
        //evaluation before selling the potion
        assertNotNull("potions", seller.getPotions());
        assertEquals("potion count", 1, seller.getPotions().size());
        assertNotNull("gold", seller.getGold());
        assertEquals("gold count", Long.valueOf(0l), seller.getGold());
        assertNotNull("potions", buyer.getPotions());
        assertEquals("potion count", 0, buyer.getPotions().size());
        assertNotNull("gold", buyer.getGold());
        assertEquals("gold count", Long.valueOf(0l), buyer.getGold());
        
        Potion potion = seller.getPotions().get(0);
        seller.sell(potion, buyer, 10);
        
        //refetch characters and potion
        seller = cDAO.find(sellerCKey);
        buyer = cDAO.find(buyerCKey);
        potion = potionDAO.find(potionKey);
        
        //evaluation after selling the potion
        assertNotNull("old potion", potion);
        assertNotNull("potions", seller.getPotions());
        assertEquals("potion count", 1, seller.getPotions().size());
        assertNotNull("gold", seller.getGold());
        assertEquals("gold count", Long.valueOf(0l), seller.getGold());
        assertNotNull("potions", buyer.getPotions());
        assertEquals("potion count", 0, buyer.getPotions().size());
        assertNotNull("gold", buyer.getGold());
        assertEquals("gold count", Long.valueOf(0l), buyer.getGold());
    }
    
    @Test
    public void testCharacterSellsSamePotionTwice() {
        Key<Player> sellerPKey = pDAO.create("1234");
        Key<Player> buyerPKey = pDAO.create("4321");
        
        Key<Character> sellerCKey = cDAO.create("Frodo", 10l, 
                CharClass.Hobbit, 999l, sellerPKey);
        
        Key<Potion> potionKey = potionDAO.create(Potion.Color.RED, 3, sellerCKey);
        
        Key<Character> buyerCKey = cDAO.create("Sam", 10l, 
                CharClass.Hobbit, 999l, buyerPKey);
        
        Character seller = cDAO.find(sellerCKey);
        Character buyer = cDAO.find(buyerCKey);
        buyer.setGold(15);
        cDAO.update(buyer);
         
        //evaluation before selling the potion
        assertNotNull("potions", seller.getPotions());
        assertEquals("potion count", 1, seller.getPotions().size());
        assertNotNull("gold", seller.getGold());
        assertEquals("gold count", Long.valueOf(0l), seller.getGold());
        assertNotNull("potions", buyer.getPotions());
        assertEquals("potion count", 0, buyer.getPotions().size());
        assertNotNull("gold", buyer.getGold());
        assertEquals("gold count", Long.valueOf(15l), buyer.getGold());
        
        Potion potion = seller.getPotions().get(0);
        seller.sell(potion, buyer, 10);
        
        //refetch characters and potion
        seller = cDAO.find(sellerCKey);
        buyer = cDAO.find(buyerCKey);
        Potion tmpPotion = potionDAO.find(potionKey);
        
        //evaluation after selling the potion
        assertNull("old potion", tmpPotion);
        assertNotNull("potions", seller.getPotions());
        assertEquals("potion count", 0, seller.getPotions().size());
        assertNotNull("gold", seller.getGold());
        assertEquals("gold count", Long.valueOf(10l), seller.getGold());
        assertNotNull("potions", buyer.getPotions());
        assertEquals("potion count", 1, buyer.getPotions().size());
        assertNotNull("gold", buyer.getGold());
        assertEquals("gold count", Long.valueOf(5l), buyer.getGold());
        
        seller.sell(potion, buyer, 10);
        
        //evaluation after selling the same potion again
        assertNull("old potion", tmpPotion);
        assertNotNull("potions", seller.getPotions());
        assertEquals("potion count", 0, seller.getPotions().size());
        assertNotNull("gold", seller.getGold());
        assertEquals("gold count", Long.valueOf(10l), seller.getGold());
        assertNotNull("potions", buyer.getPotions());
        assertEquals("potion count", 1, buyer.getPotions().size());
        assertNotNull("gold", buyer.getGold());
        assertEquals("gold count", Long.valueOf(5l), buyer.getGold());
    }
    
    @Test
    public void testCharacterHireAMercenary() {
        Key<Player> player1Key = pDAO.create("1234");
        Key<Player> player2Key = pDAO.create("1235");
        
        Key<Character> p1CKey = cDAO.create("Frodo", 20l, 
                CharClass.Hobbit, 999l, player1Key);
        
        Key<Character> p2CKey = cDAO.create("Sam", 5l, 
                CharClass.Hobbit, 999l, player2Key);
        
        Character char1 = cDAO.find(p1CKey);
        Character merc1 = cDAO.find(p2CKey);
        char1.setGold(10);
        cDAO.update(char1);
        merc1.setGold(5);
        cDAO.update(merc1);
         
        //evaluation before hiring
        assertNotNull("gold char", char1.getGold());
        assertEquals("gold count char", Long.valueOf(10l), char1.getGold());
        assertNotNull("gold merc1", merc1.getGold());
        assertEquals("gold count merc1", Long.valueOf(5l), merc1.getGold());
        
        char1.hire(5, merc1);
        
        //refetch characters
        char1 = cDAO.find(p1CKey);
        merc1 = cDAO.find(p2CKey);
        
        //evaluation after hiring
        assertNotNull("gold char", char1.getGold());
        assertEquals("gold count char", Long.valueOf(5l), char1.getGold());
        assertNotNull("gold merc1", merc1.getGold());
        assertEquals("gold count merc1", Long.valueOf(10l), merc1.getGold());
    }
    
    @Test
    public void testCharacterHireMercenaries() {
        Key<Player> player1Key = pDAO.create("1234");
        Key<Player> player2Key = pDAO.create("1235");
        Key<Player> player3Key = pDAO.create("1236");
        Key<Player> player4Key = pDAO.create("1237");
        
        Key<Character> p1CKey = cDAO.create("Frodo", 20l, 
                CharClass.Hobbit, 999l, player1Key);
        
        Key<Character> p2CKey = cDAO.create("Sam", 5l, 
                CharClass.Hobbit, 999l, player2Key);
        
        Key<Character> p3CKey = cDAO.create("Legolas", 5l, 
                CharClass.Elf, 999l, player3Key);
        
        Key<Character> p4CKey = cDAO.create("Gimli", 5l, 
                CharClass.Dwarf, 999l, player4Key);
        
        Character char1 = cDAO.find(p1CKey);
        Character merc1 = cDAO.find(p2CKey);
        Character merc2 = cDAO.find(p3CKey);
        Character merc3 = cDAO.find(p4CKey);
        
        char1.setGold(15);
        cDAO.update(char1);
         
        //evaluation before hiring
        assertNotNull("gold char", char1.getGold());
        assertEquals("gold count char", Long.valueOf(15l), char1.getGold());
        assertNotNull("gold merc1", merc1.getGold());
        assertEquals("gold count merc1", Long.valueOf(0l), merc1.getGold());
        assertNotNull("gold merc2", merc2.getGold());
        assertEquals("gold count merc2", Long.valueOf(0l), merc2.getGold());
        assertNotNull("gold merc3", merc3.getGold());
        assertEquals("gold count merc3", Long.valueOf(0l), merc3.getGold());
        
        char1.hire(5, merc1, merc2, merc3);
        
        //refetch characters
        char1 = cDAO.find(p1CKey);
        merc1 = cDAO.find(p2CKey);
        merc2 = cDAO.find(p3CKey);
        merc3 = cDAO.find(p4CKey);
        
        //evaluation after hiring
        assertNotNull("gold char", char1.getGold());
        assertEquals("gold count char", Long.valueOf(0l), char1.getGold());
        assertNotNull("gold merc1", merc1.getGold());
        assertEquals("gold count merc1", Long.valueOf(5l), merc1.getGold());
        assertNotNull("gold merc2", merc2.getGold());
        assertEquals("gold count merc2", Long.valueOf(5l), merc2.getGold());
        assertNotNull("gold merc3", merc3.getGold());
        assertEquals("gold count merc3", Long.valueOf(5l), merc3.getGold());
    }
    
    @Test
    public void testCharacterHireMercenariesWithoutEnoughGold() {
        Key<Player> player1Key = pDAO.create("1234");
        Key<Player> player2Key = pDAO.create("1235");
        Key<Player> player3Key = pDAO.create("1236");
        Key<Player> player4Key = pDAO.create("1237");
        
        Key<Character> p1CKey = cDAO.create("Frodo", 20l, 
                CharClass.Hobbit, 999l, player1Key);
        
        Key<Character> p2CKey = cDAO.create("Sam", 5l, 
                CharClass.Hobbit, 999l, player2Key);
        
        Key<Character> p3CKey = cDAO.create("Legolas", 5l, 
                CharClass.Elf, 999l, player3Key);
        
        Key<Character> p4CKey = cDAO.create("Gimli", 5l, 
                CharClass.Dwarf, 999l, player4Key);
        
        Character char1 = cDAO.find(p1CKey);
        Character merc1 = cDAO.find(p2CKey);
        Character merc2 = cDAO.find(p3CKey);
        Character merc3 = cDAO.find(p4CKey);
        
        char1.setGold(10);
        cDAO.update(char1);
         
        //evaluation before hiring
        assertNotNull("gold char", char1.getGold());
        assertEquals("gold count char", Long.valueOf(10l), char1.getGold());
        assertNotNull("gold merc1", merc1.getGold());
        assertEquals("gold count merc1", Long.valueOf(0l), merc1.getGold());
        assertNotNull("gold merc2", merc2.getGold());
        assertEquals("gold count merc2", Long.valueOf(0l), merc2.getGold());
        assertNotNull("gold merc3", merc3.getGold());
        assertEquals("gold count merc3", Long.valueOf(0l), merc3.getGold());
        
        char1.hire(5, merc1, merc2, merc3);
        
        //refetch characters
        char1 = cDAO.find(p1CKey);
        merc1 = cDAO.find(p2CKey);
        merc2 = cDAO.find(p3CKey);
        merc3 = cDAO.find(p4CKey);
        
        //evaluation after hiring
        assertNotNull("gold char", char1.getGold());
        assertEquals("gold count char", Long.valueOf(10l), char1.getGold());
        assertNotNull("gold merc1", merc1.getGold());
        assertEquals("gold count merc1", Long.valueOf(0l), merc1.getGold());
        assertNotNull("gold merc2", merc2.getGold());
        assertEquals("gold count merc2", Long.valueOf(0l), merc2.getGold());
        assertNotNull("gold merc3", merc3.getGold());
        assertEquals("gold count merc3", Long.valueOf(0l), merc3.getGold());
    }
    
    @Test//(expected = IllegalArgumentException.class)
    public void testCharacterHireMoreThanFiveMercenaries() {
        Key<Player> player1Key = pDAO.create("1234");
        Key<Player> player2Key = pDAO.create("1235");
        Key<Player> player3Key = pDAO.create("1236");
        Key<Player> player4Key = pDAO.create("1237");
        Key<Player> player5Key = pDAO.create("1238");
        Key<Player> player6Key = pDAO.create("1239");
        Key<Player> player7Key = pDAO.create("1240");
        
        Key<Character> p1CKey = cDAO.create("Frodo", 20l, 
                CharClass.Hobbit, 999l, player1Key);
        
        Key<Character> p2CKey = cDAO.create("Sam", 5l, 
                CharClass.Hobbit, 999l, player2Key);
        
        Key<Character> p3CKey = cDAO.create("Legolas", 5l, 
                CharClass.Elf, 999l, player3Key);
        
        Key<Character> p4CKey = cDAO.create("Gimli", 5l, 
                CharClass.Dwarf, 999l, player4Key);
        
        Key<Character> p5CKey = cDAO.create("Gandalf", 5l, 
                CharClass.Mage, 999l, player5Key);
        
        Key<Character> p6CKey = cDAO.create("Golum", 5l, 
                CharClass.Hobbit, 999l, player6Key);
        
        Key<Character> p7CKey = cDAO.create("Bilbo", 5l, 
                CharClass.Hobbit, 999l, player7Key);
        
        Character char1 = cDAO.find(p1CKey);
        Character merc1 = cDAO.find(p2CKey);
        Character merc2 = cDAO.find(p3CKey);
        Character merc3 = cDAO.find(p4CKey);
        Character merc4 = cDAO.find(p5CKey);
        Character merc5 = cDAO.find(p6CKey);
        Character merc6 = cDAO.find(p7CKey);
        
        char1.setGold(30);
        cDAO.update(char1);
        
        assertNotNull("gold char", char1.getGold());
        assertEquals("gold count char", Long.valueOf(30l), char1.getGold());
        assertNotNull("gold merc1", merc1.getGold());
        assertEquals("gold count merc1", Long.valueOf(0l), merc1.getGold());
        assertNotNull("gold merc2", merc2.getGold());
        assertEquals("gold count merc2", Long.valueOf(0l), merc2.getGold());
        assertNotNull("gold merc3", merc3.getGold());
        assertEquals("gold count merc3", Long.valueOf(0l), merc3.getGold());
        assertNotNull("gold merc4", merc4.getGold());
        assertEquals("gold count merc4", Long.valueOf(0l), merc4.getGold());
        assertNotNull("gold merc5", merc5.getGold());
        assertEquals("gold count merc53", Long.valueOf(0l), merc5.getGold());
        assertNotNull("gold merc6", merc6.getGold());
        assertEquals("gold count merc6", Long.valueOf(0l), merc6.getGold());
        
        char1.hire(5, merc1, merc2, merc3, merc4, merc5, merc6);
        
        //refetch characters
        char1 = cDAO.find(p1CKey);
        merc1 = cDAO.find(p2CKey);
        merc2 = cDAO.find(p3CKey);
        merc3 = cDAO.find(p4CKey);
        merc4 = cDAO.find(p5CKey);
        merc5 = cDAO.find(p6CKey);
        merc6 = cDAO.find(p7CKey);
        
        assertNotNull("gold char", char1.getGold());
        assertEquals("gold count char", Long.valueOf(0l), char1.getGold());
        assertNotNull("gold merc1", merc1.getGold());
        assertEquals("gold count merc1", Long.valueOf(5l), merc1.getGold());
        assertNotNull("gold merc2", merc2.getGold());
        assertEquals("gold count merc2", Long.valueOf(5l), merc2.getGold());
        assertNotNull("gold merc3", merc3.getGold());
        assertEquals("gold count merc3", Long.valueOf(5l), merc3.getGold());
        assertNotNull("gold merc4", merc4.getGold());
        assertEquals("gold count merc4", Long.valueOf(5l), merc4.getGold());
        assertNotNull("gold merc5", merc5.getGold());
        assertEquals("gold count merc5", Long.valueOf(5l), merc5.getGold());
        assertNotNull("gold merc6", merc6.getGold());
        assertEquals("gold count merc6", Long.valueOf(5l), merc6.getGold());
        
    }
    
    @Test
    public void testHeal() {
        
        Key<Player> player1Key = pDAO.create("1234");
        Key<Character> p1CKey = cDAO.create("Frodo", 10l, 
                CharClass.Hobbit, 999l, player1Key);
        
        Character character = cDAO.find(p1CKey);
        character.heal(5);
        assertEquals("health character", Long.valueOf(15l), character.getHealth());
        character = cDAO.find(p1CKey);
        assertEquals("health character", Long.valueOf(15l), character.getHealth());
    }
    
    @Test
    public void testHealMaximumHealth() {
        
        Key<Player> player1Key = pDAO.create("1234");
        Key<Character> p1CKey = cDAO.create("Frodo", Character.MAX_HEALTH - 2, 
                CharClass.Hobbit, 999l, player1Key);
        
        Character character = cDAO.find(p1CKey);
        character.heal(5);
        assertEquals("health character", Long.valueOf(Character.MAX_HEALTH), character.getHealth());
        character = cDAO.find(p1CKey);
        assertEquals("health character", Long.valueOf(Character.MAX_HEALTH), character.getHealth());
    }
    
    @Test
    public void testHurt() {
        
        Key<Player> player1Key = pDAO.create("1234");
        Key<Character> p1CKey = cDAO.create("Frodo", 10l, 
                CharClass.Hobbit, 999l, player1Key);
        
        Character character = cDAO.find(p1CKey);
        character.hurt(5);
        assertEquals("health character", Long.valueOf(5l), character.getHealth());
        character = cDAO.find(p1CKey);
        assertEquals("health character", Long.valueOf(5l), character.getHealth());
    }
    
    @Test
    public void testOverkill() {
        
        Key<Player> player1Key = pDAO.create("1234");
        Key<Character> p1CKey = cDAO.create("Frodo", 4l, 
                CharClass.Hobbit, 999l, player1Key);
        
        Character character = cDAO.find(p1CKey);
        character.hurt(5);
        assertEquals("healt character", Long.valueOf(0), character.getHealth());
        character = cDAO.find(p1CKey);
        assertEquals("healt character", Long.valueOf(0), character.getHealth());
    }
}
