package eu.darkcube.system.citybuild.commands;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class LevelXPManager implements Listener {

    private JavaPlugin plugin;

    public LevelXPManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public int getXPForLevel(int level) {
        level = Math.max(1, level);
        int xp = (int)Math.pow(level, 2);  // Die benötigten XP steigen exponentiell mit dem Level an.
        return Math.max(1, xp);
    }

    public void addXP(Player player, double amount) {
        NamespacedKey xpKey = new NamespacedKey(plugin, "xp");
        NamespacedKey levelKey = new NamespacedKey(plugin, "level");
        NamespacedKey apKey = new NamespacedKey(plugin, "ap"); // Neuer AP Schlüssel
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        double currentXP = pdc.getOrDefault(xpKey, PersistentDataType.DOUBLE, 0.0);
        int currentLevel = pdc.getOrDefault(levelKey, PersistentDataType.INTEGER, 0);
        int currentAP = pdc.getOrDefault(apKey, PersistentDataType.INTEGER, 0); // Aktuelle AP erhalten
        double newXP = currentXP + amount;

        while (newXP >= getXPForLevel(currentLevel + 1)) {
            newXP -= getXPForLevel(currentLevel + 1);
            currentLevel++;
            currentAP += 2; // AP um 2 erhöhen, wenn Level erhöht wird
        }

        pdc.set(xpKey, PersistentDataType.DOUBLE, newXP);
        pdc.set(levelKey, PersistentDataType.INTEGER, currentLevel);
        pdc.set(apKey, PersistentDataType.INTEGER, currentAP); // Neue AP setzen
        player.setLevel(0);
        double progress = newXP / getXPForLevel(currentLevel + 1);
        player.setExp((float) progress);
    }



    public void resetXP(Player player) {
        NamespacedKey xpKey = new NamespacedKey(plugin, "xp");
        NamespacedKey levelKey = new NamespacedKey(plugin, "level");
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(xpKey, PersistentDataType.DOUBLE, 0.0);
        pdc.set(levelKey, PersistentDataType.INTEGER, 0);
        player.setExp(0);
        player.setLevel(0);
    }

    public void resetAP(Player player) {
        NamespacedKey apKey = new NamespacedKey(plugin, "ap");
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(apKey, PersistentDataType.INTEGER, 0);
    }

    public double getXP(Player player) {
        NamespacedKey xpKey = new NamespacedKey(plugin, "xp");
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        return pdc.getOrDefault(xpKey, PersistentDataType.DOUBLE, 0.0);
    }

    public int getAP(Player player) {
        NamespacedKey apKey = new NamespacedKey(plugin, "ap");
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        return pdc.getOrDefault(apKey, PersistentDataType.INTEGER, 0);
    }

    public void addAP(Player player, int amount) {
        NamespacedKey apKey = new NamespacedKey(plugin, "ap");
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        int currentAP = pdc.getOrDefault(apKey, PersistentDataType.INTEGER, 0);
        int newAP = currentAP + amount;

        // Sicherstellen, dass AP nicht unter 0 fallen
        newAP = Math.max(0, newAP);

        pdc.set(apKey, PersistentDataType.INTEGER, newAP);
    }

    public void resetAttribute(Player player, String attributeKey) {
        NamespacedKey key = new NamespacedKey(plugin, attributeKey);
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(key, PersistentDataType.DOUBLE, 0.0);
    }

    public double getAttribute(Player player, String attributeKey) {
        NamespacedKey key = new NamespacedKey(plugin, attributeKey);
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        return pdc.getOrDefault(key, PersistentDataType.DOUBLE, 0.0);
    }

    public void setAttribute(Player player, String attributeKey, double value) {
        NamespacedKey key = new NamespacedKey(plugin, attributeKey);
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(key, PersistentDataType.DOUBLE, value);
    }

    public int getLevel(Player player) {
        NamespacedKey levelKey = new NamespacedKey(plugin, "level");
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        return pdc.getOrDefault(levelKey, PersistentDataType.INTEGER, 0);
    }

}
