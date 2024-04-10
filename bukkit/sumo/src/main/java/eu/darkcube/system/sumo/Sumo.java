/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.DarkCubeBukkit;
import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.sumo.commands.*;
import eu.darkcube.system.sumo.executions.EquipPlayer;
import eu.darkcube.system.sumo.executions.RandomTeam;
import eu.darkcube.system.sumo.executions.Spectator;
import eu.darkcube.system.sumo.guis.TeamGUI;
import eu.darkcube.system.sumo.guis.VotingGUI;
import eu.darkcube.system.sumo.guis.VotingMapGUI;
import eu.darkcube.system.sumo.items.game.ItemWool;
import eu.darkcube.system.sumo.loader.MapLoader;
import eu.darkcube.system.sumo.manager.*;
import eu.darkcube.system.sumo.other.GameDoubleJump;
import eu.darkcube.system.sumo.other.LobbySystemLink;
import eu.darkcube.system.sumo.other.WoolDespawner;
import eu.darkcube.system.sumo.prefix.ChatManager;
import eu.darkcube.system.sumo.prefix.PrefixManager;
import eu.darkcube.system.sumo.scoreboards.GameScoreboard;
import eu.darkcube.system.sumo.scoreboards.LobbyScoreboard;
import eu.darkcube.system.sumo.executions.Respawn;
import eu.darkcube.system.sumo.other.StartingTimer;
import eu.darkcube.system.sumo.ruler.LobbyRuler;
import eu.darkcube.system.sumo.manager.MapManager;
import eu.darkcube.system.sumo.ruler.MapRuler;

public class Sumo extends DarkCubePlugin {
    private static Sumo instance;
    private LobbySystemLink lobbySystemLink;

    public Sumo() {
        super("sumo");
    }

    @Override
    public void onEnable() {
        instance = this;

        var mapLoader = new MapLoader();
        mapLoader.loadWorlds();
        var lobbyScoreboard = new LobbyScoreboard(this);
        var mapManager = new MapManager(this, lobbyScoreboard);
        var teamManager = new TeamManager();
        var prefixManager = new PrefixManager(teamManager);
        var woolDespawner = new WoolDespawner(this);
        var gameScoreboard = new GameScoreboard(mapManager);
        var lifeManager = new LifeManager(teamManager, gameScoreboard);
        var gameDoubleJump = new GameDoubleJump(this, mapManager);
        var lobbyRuler = new LobbyRuler();
        lobbySystemLink = new LobbySystemLink(mapManager, teamManager);
        var playerManager = new PlayerManager(lobbyScoreboard, mapManager, teamManager, prefixManager, lobbySystemLink);
        var respawn = new Respawn(mapManager, lifeManager, teamManager);
        var equipPlayer = new EquipPlayer(teamManager);
        var randomTeam = new RandomTeam(teamManager, prefixManager);
        var startingTimer = new StartingTimer(this, lobbyScoreboard, respawn, equipPlayer, teamManager, randomTeam, prefixManager);
        var damageManager = new DamageManager(teamManager, this);
        Spectator.setMainRuler(mapManager);
        var chatManager = new ChatManager();
        var itemWool = new ItemWool(teamManager);
        var mapRuler = new MapRuler(mapManager);
        var teamGUI = new TeamGUI(teamManager, prefixManager, equipPlayer);
        var votingMapGUI = new VotingMapGUI(mapManager);
        var votingGUI = new VotingGUI(votingMapGUI);

        DarkCubeBukkit.autoConfigure(false);
        mapManager.setRandomMap();
        lobbySystemLink.updateLobbyLink();

        getServer().getPluginManager().registerEvents(gameDoubleJump, this);
        getServer().getPluginManager().registerEvents(itemWool, this);
        getServer().getPluginManager().registerEvents(woolDespawner, this);
        getServer().getPluginManager().registerEvents(damageManager, this);
        getServer().getPluginManager().registerEvents(respawn, this);
        getServer().getPluginManager().registerEvents(startingTimer, this);
        getServer().getPluginManager().registerEvents(lobbyScoreboard, this);
        getServer().getPluginManager().registerEvents(gameScoreboard, this);
        getServer().getPluginManager().registerEvents(lobbyRuler, this);
        getServer().getPluginManager().registerEvents(mapRuler, this);
        getServer().getPluginManager().registerEvents(mapManager, this);
        getServer().getPluginManager().registerEvents(playerManager, this);
        getServer().getPluginManager().registerEvents(teamGUI, this);
        getServer().getPluginManager().registerEvents(votingMapGUI, this);
        getServer().getPluginManager().registerEvents(votingGUI, this);
        getServer().getPluginManager().registerEvents(chatManager, this);

        instance.getCommand("showteamlifes").setExecutor(new ShowTeamLivesCommand(lifeManager));
        instance.getCommand("setlifes").setExecutor(new SetLifesCommand(lifeManager, gameScoreboard));
        instance.getCommand("setactivemap").setExecutor(new SetActiveMapCommand(mapManager, respawn));
        instance.getCommand("start").setExecutor(new StartCommand(startingTimer));
        instance.getCommand("setteam").setExecutor(new SetTeamCommand(teamManager, prefixManager, equipPlayer));
        instance.getCommand("timer").setExecutor(new TimerCommand(startingTimer));
        instance.getCommand("setgamestate").setExecutor(new SetGameStateCommand(respawn, equipPlayer, randomTeam, startingTimer, lobbySystemLink));
        instance.getCommand("showgamestate").setExecutor(new ShowGameStateCommand());
        instance.getCommand("setmap").setExecutor(new SetMapCommand(mapManager));
        instance.getCommand("showactivemap").setExecutor(new ShowActiveMapCommand(mapManager));
        instance.getCommand("tpactiveworld").setExecutor(new TpActiveWorldCommand(mapManager));
    }

    public static Sumo getInstance() {
        return instance;
    }

    public LobbySystemLink getLobbySystemLink() {
        return lobbySystemLink;
    }

}