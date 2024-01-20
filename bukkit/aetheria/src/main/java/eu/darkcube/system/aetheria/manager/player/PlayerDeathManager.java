/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.manager.player;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerDeathManager {
    private final CoreManager coreManager;
    private final XPManager xpManager;
    private final HealthManager healthManager;
    private final MaxHealthManager maxHealthManager;

    public PlayerDeathManager(CoreManager coreManager, XPManager xpManager, HealthManager healthManager, MaxHealthManager maxHealthManager) {
        this.coreManager = coreManager;
        this.xpManager = xpManager;
        this.healthManager = healthManager;
        this.maxHealthManager = maxHealthManager;
    }

    public void handlePlayerDeath(Player player) {
        double currentXP = xpManager.getXP(player);
        xpManager.setXP(player, currentXP * 0.25);

        int currentCores = coreManager.getCoreValue(player);
        coreManager.setCoreValue(player, currentCores / 2);

        double maxHealth = maxHealthManager.getMaxHealth(player);
        healthManager.setHealth(player, maxHealth);

        player.sendMessage(" ");
        player.sendMessage("§7-----[§cDu bist gestorben§7]-----");
        player.sendMessage(" ");
        player.sendMessage("§7Du hast §c75% §7von deinen §aXP §7verloren");
        player.sendMessage("§7Du hast §c50% §7deiner §6Cor §7verloren");
        player.sendMessage(" ");
        player.sendMessage("§7--------------------------");
        player.sendMessage(" ");
    }
}
