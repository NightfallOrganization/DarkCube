/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.commands;

import eu.darkcube.system.DarkCubeBukkit;
import eu.darkcube.system.DarkCubeSystem;
import eu.darkcube.system.sumo.Sumo;
import eu.darkcube.system.sumo.executions.Ending;
import eu.darkcube.system.sumo.executions.EquipPlayer;
import eu.darkcube.system.sumo.executions.RandomTeam;
import eu.darkcube.system.sumo.executions.Respawn;
import eu.darkcube.system.sumo.other.GameStates;
import eu.darkcube.system.sumo.other.LobbySystemLink;
import eu.darkcube.system.sumo.other.StartingTimer;
import eu.darkcube.system.util.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class SetGameStateCommand implements CommandExecutor {
    private Respawn respawn;
    private EquipPlayer equipPlayer;
    private RandomTeam randomTeam;
    private StartingTimer startingTimer;
    private LobbySystemLink lobbySystemLink;

    public SetGameStateCommand(Respawn respawn, EquipPlayer equipPlayer, RandomTeam randomTeam, StartingTimer startingTimer, LobbySystemLink lobbySystemLink) {
        this.respawn = respawn;
        this.equipPlayer = equipPlayer;
        this.randomTeam = randomTeam;
        this.startingTimer = startingTimer;
        this.lobbySystemLink = lobbySystemLink;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("setgamestate")) {
            if (args.length == 1) {
                try {
                    GameStates newState = GameStates.valueOf(args[0].toUpperCase());
                    GameStates.setState(newState);
                    sender.sendMessage(ChatColor.GREEN + "§7GameState wurde zu §b" + newState + " §7gesetzt.");
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "§7States: §bSTARTING§7, §bPLAYING§7, §bENDING");
                }

                onGameStateChange();
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "§7Usage: /setgamestate <GameState>");
                return true;
            }
        }

        return false;
    }

    private void onGameStateChange () {
        lobbySystemLink.updateLobbyLink();

        if (GameStates.isState(GameStates.STARTING)) {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                Location spawnLocation = new Location(Bukkit.getWorld("world"), 0.5, 101, 0.5);
                player.teleport(spawnLocation);
                startingTimer.startTimer();
            }
        }

        if (GameStates.isState(GameStates.PLAYING)) {
            Set<UUID> playerIDs = Bukkit.getServer().getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet());

            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                randomTeam.balanceTeams(playerIDs);
                equipPlayer.equipPlayerIfInTeam(player);
                respawn.teleportPlayerRandomly(player);
            }
        }

        if (GameStates.isState(GameStates.ENDING)) {
            Ending ending = new Ending(Sumo.getInstance());
            ending.execute();
        }
    }
}