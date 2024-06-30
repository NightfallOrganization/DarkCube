/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package building.oneblock.manager.player;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerManager implements Listener {

//    private HealthManager healthManager;
    private CoreManager coreManager;
//    private DamageManager damageManager;
//    private XPManager xpManager;
    private LevelManager levelManager;
//    private MaxHealthManager maxHealthManager;
//    private RegenerationManager regenerationManager;
//    private AttributePointsManager attributePointsManager;
    private final JavaPlugin plugin;
    private ScoreboardManager scoreboardManager;
//
    public PlayerManager(JavaPlugin plugin, CoreManager coreManager, LevelManager levelManager, ScoreboardManager scoreboardManager) {
//        this.healthManager = healthManager;
        this.coreManager = coreManager;
//        this.damageManager = damageManager;
//        this.xpManager = xpManager;
        this.levelManager = levelManager;
//        this.maxHealthManager = maxHealthManager;
//        this.regenerationManager = regenerationManager;
//        this.attributePointsManager = attributePointsManager;
        this.scoreboardManager = scoreboardManager;
        this.plugin = plugin;
    }
//
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        scoreboardManager.setScoreboard(player);
        // Überprüfen, ob der Spieler das erste Mal beitritt
        if (!player.getPersistentDataContainer().has(new NamespacedKey(plugin, "first_join"), PersistentDataType.BYTE)) {
//            // Setzen der Werte
//            healthManager.setHealth(player, 20.0);
            coreManager.setCoreValue(player, 0);
//            damageManager.setDamage(player, 1.0);
//            xpManager.setXP(player, 0.0);
            levelManager.setLevel(player, 1);
//            maxHealthManager.setMaxHealth(player, 20.0);
//            regenerationManager.setRegenerationRate(player, 1.0);
//            attributePointsManager.setAttributePoints(player, 0);

            // Markieren, dass der Spieler beigetreten ist
            player.getPersistentDataContainer().set(new NamespacedKey(plugin, "first_join"), PersistentDataType.BYTE, (byte) 1);
        }
    }

//
//    public HealthManager getHealthManager() {
//        return healthManager;
//    }
//
//    public MaxHealthManager getMaxHealthManager() {
//        return maxHealthManager;
//    }
//
    public CoreManager getCoreManager() {
        return coreManager;
    }
//
//    public AttributePointsManager getAttributePointsManager() {
//        return attributePointsManager;
//    }
//
//    public DamageManager getDamageManager() {
//        return damageManager;
//    }
//
//    public XPManager getXPManager() {
//        return xpManager;
//    }
//
    public LevelManager getLevelManager() {
        return levelManager;
    }

//    public RegenerationManager getRegenerationManager() {
//        return regenerationManager;
//    }

}
