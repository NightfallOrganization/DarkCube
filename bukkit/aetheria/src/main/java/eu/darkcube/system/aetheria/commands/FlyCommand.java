/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class FlyCommand implements CommandExecutor, Listener {

    private final JavaPlugin plugin;

    public FlyCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Nur Spieler können diesen Befehl ausführen.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            player = Bukkit.getPlayerExact(args[0]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "§cSpieler nicht gefunden.");
                return true;
            }
        }

        NamespacedKey key = new NamespacedKey(plugin, "flymode");
        PersistentDataContainer container = player.getPersistentDataContainer();

        if (container.has(key, PersistentDataType.BYTE)) {

            boolean isFlying = container.get(key, PersistentDataType.BYTE) == 1;
            container.set(key, PersistentDataType.BYTE, (byte) (isFlying ? 0 : 1));
            player.setAllowFlight(!isFlying);
            player.setFlying(!isFlying);
            player.sendMessage(ChatColor.YELLOW + (isFlying ? "§7Flugmodus §adeaktiviert" : "§7Flugmodus §aaktiviert"));
        } else {

            container.set(key, PersistentDataType.BYTE, (byte) 1);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.sendMessage(ChatColor.YELLOW + "§7Flugmodus §aaktiviert");
        }

        return true;
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        checkAndApplyFlyMode(event.getPlayer());
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        checkAndApplyFlyMode(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        checkAndApplyFlyMode(event.getPlayer());
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        NamespacedKey key = new NamespacedKey(plugin, "flymode");
        byte flyStatus = player.getPersistentDataContainer().getOrDefault(key, PersistentDataType.BYTE, (byte) 0);

        if (flyStatus == 1) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.setAllowFlight(true);
                player.setFlying(true);
            });
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> checkAndApplyFlyMode(player), 20L); // 20L steht für 20 Ticks, also 1 Sekunde
    }

    private void checkAndApplyFlyMode(Player player) {
        NamespacedKey key = new NamespacedKey(plugin, "flymode");
        byte flyStatus = player.getPersistentDataContainer().getOrDefault(key, PersistentDataType.BYTE, (byte) 0);

        if (flyStatus == 1) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.setAllowFlight(true);
            });
        } else {
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }

}
