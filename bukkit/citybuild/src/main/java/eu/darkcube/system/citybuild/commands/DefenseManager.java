//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package eu.darkcube.system.citybuild.commands;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class DefenseManager {
    private Citybuild plugin;
    private static final NamespacedKey DEFENSE_KEY = new NamespacedKey(Citybuild.getInstance(), "defense");

    public DefenseManager(Citybuild plugin) {
        this.plugin = plugin;// 20
    }// 21

    public void addDefense(Player player, int defenseToAdd) {
        PersistentDataContainer data = player.getPersistentDataContainer();// 24
        int currentDefense = (Integer)data.getOrDefault(DEFENSE_KEY, PersistentDataType.INTEGER, 0);// 25
        data.set(DEFENSE_KEY, PersistentDataType.INTEGER, currentDefense + defenseToAdd);// 26
    }// 27

    public void resetDefense(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();// 30
        data.set(DEFENSE_KEY, PersistentDataType.INTEGER, 0);// 31
    }// 32

    public int getDefense(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();// 35
        return (Integer)data.getOrDefault(DEFENSE_KEY, PersistentDataType.INTEGER, 0);// 36
    }
}
