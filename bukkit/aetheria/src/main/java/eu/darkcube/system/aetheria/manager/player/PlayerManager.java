/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.manager.player;

import eu.darkcube.system.aetheria.manager.shared.DamageManager;
import eu.darkcube.system.aetheria.manager.shared.HealthManager;
import eu.darkcube.system.aetheria.manager.shared.LevelManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerManager implements Listener {

    private HealthManager healthManager;
    private CoreManager coreManager;
    private DamageManager damageManager;
    private XPManager xpManager;
    private LevelManager levelManager;
    private MaxHealthManager maxHealthManager;
    private RegenerationManager regenerationManager;
    private final JavaPlugin plugin;

    public PlayerManager(JavaPlugin plugin, HealthManager healthManager, CoreManager coreManager, DamageManager damageManager, XPManager xpManager, LevelManager levelManager, MaxHealthManager maxHealthManager, RegenerationManager regenerationManager) {
        this.healthManager = healthManager;
        this.coreManager = coreManager;
        this.damageManager = damageManager;
        this.xpManager = xpManager;
        this.levelManager = levelManager;
        this.maxHealthManager = maxHealthManager;
        this.regenerationManager = regenerationManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Überprüfen, ob der Spieler das erste Mal beitritt
        if (!player.getPersistentDataContainer().has(new NamespacedKey(plugin, "first_join"), PersistentDataType.BYTE)) {
            // Setzen der Werte
            healthManager.setHealth(player, 20.0);
            coreManager.setCoreValue(player, 0);
            damageManager.setDamage(player, 1.0);
            xpManager.setXP(player, 0.0);
            levelManager.setLevel(player, 1);
            maxHealthManager.setMaxHealth(player, 20.0);
            regenerationManager.setRegenerationRate(player, 1.0);

            // Markieren, dass der Spieler beigetreten ist
            player.getPersistentDataContainer().set(new NamespacedKey(plugin, "first_join"), PersistentDataType.BYTE, (byte) 1);
        }
    }


    public HealthManager getHealthManager() {
        return healthManager;
    }

    public MaxHealthManager getMaxHealthManager() {
        return maxHealthManager;
    }

    public CoreManager getCoreManager() {
        return coreManager;
    }

    public DamageManager getDamageManager() {
        return damageManager;
    }

    public XPManager getXPManager() {
        return xpManager;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public RegenerationManager getRegenerationManager() {
        return regenerationManager;
    }

}
