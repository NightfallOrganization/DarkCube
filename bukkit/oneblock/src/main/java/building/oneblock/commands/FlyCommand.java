/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package building.oneblock.commands;

import building.oneblock.util.Message;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Bukkit;
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
            Player player = (Player) sender;
            User user = UserAPI.instance().user(player.getUniqueId());
            user.sendMessage(Message.ONLY_PLAYERS_CAN_USE);
            return true;
        }

        Player player = (Player) sender;
        User user = UserAPI.instance().user(player.getUniqueId());

        if (args.length == 1) {
            player = Bukkit.getPlayerExact(args[0]);
            if (player == null) {
                user.sendMessage(Message.PLAYER_NOT_FOUND);
                return true;
            }
        }

        if (args.length == 0) {
            flyCheck(sender);

        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                user.sendMessage(Message.PLAYER_NOT_FOUND);
                return true;
            }

            flyCheck(target);

            NamespacedKey key = new NamespacedKey(plugin, "flymode");
            PersistentDataContainer container = target.getPersistentDataContainer();
            boolean isFlying = container.get(key, PersistentDataType.BYTE) == 1;

            if (isFlying) {
                user.sendMessage(Message.COMMAND_HAVE_FLY_ON, target.getName());
            } else {
                user.sendMessage(Message.COMMAND_HAVE_FLY_OFF, target.getName());
            }

            return true;
        }

        return true;
    }

    private void flyCheck(CommandSender sender) {
        Player player = (Player) sender;
        User user = UserAPI.instance().user(player.getUniqueId());
        NamespacedKey key = new NamespacedKey(plugin, "flymode");
        PersistentDataContainer container = player.getPersistentDataContainer();

        if (container.has(key, PersistentDataType.BYTE)) {

            boolean isFlying = container.get(key, PersistentDataType.BYTE) == 1;
            container.set(key, PersistentDataType.BYTE, (byte) (isFlying ? 0 : 1));
            player.setAllowFlight(!isFlying);
            player.setFlying(!isFlying);

            if (isFlying) {
                user.sendMessage(Message.COMMAND_FLY_OFF);
            } else {
                user.sendMessage(Message.COMMAND_FLY_ON);
            }

        } else {

            container.set(key, PersistentDataType.BYTE, (byte) 1);
            player.setAllowFlight(true);
            player.setFlying(true);
            user.sendMessage(Message.COMMAND_FLY_ON);
        }

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
        checkAndApplyFlyMode(event.getPlayer());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        checkAndApplyFlyMode(event.getPlayer());
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
