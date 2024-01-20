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

    public PlayerDeathManager(JavaPlugin plugin, CoreManager coreManager, XPManager xpManager) {
        this.coreManager = coreManager;
        this.xpManager = xpManager;
    }

    public void handlePlayerDeath(Player player) {
        // Reduziert XP um 75%
        double currentXP = xpManager.getXP(player);
        xpManager.setXP(player, currentXP * 0.25);

        // Reduziert Coins um 50%
        int currentCores = coreManager.getCoreValue(player);
        coreManager.setCoreValue(player, currentCores / 2);

        player.sendMessage(" ");
        player.sendMessage("§7-----[§cDu bist gestorben§7]-----");
        player.sendMessage(" ");
        player.sendMessage("§7Dir wurden §c75% §7von deinen §aXP §7entfernt");
        player.sendMessage("§7Du hast §c50% §7deiner §6Coins §7verloren");
        player.sendMessage(" ");
        player.sendMessage("§7--------------------------");
        player.sendMessage(" ");
    }
}
