/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo;

import java.io.IOException;

import eu.darkcube.system.bukkit.DarkCubePlugin;
import eu.darkcube.system.server.cloudnet.DarkCubeServerCloudNet;
import eu.darkcube.system.sumo.commands.SetActiveMapCommand;
import eu.darkcube.system.sumo.commands.SetGameStateCommand;
import eu.darkcube.system.sumo.commands.SetLifesCommand;
import eu.darkcube.system.sumo.commands.SetMapCommand;
import eu.darkcube.system.sumo.commands.SetTeamCommand;
import eu.darkcube.system.sumo.commands.ShowActiveMapCommand;
import eu.darkcube.system.sumo.commands.ShowGameStateCommand;
import eu.darkcube.system.sumo.commands.ShowTeamLivesCommand;
import eu.darkcube.system.sumo.commands.StartCommand;
import eu.darkcube.system.sumo.commands.TimerCommand;
import eu.darkcube.system.sumo.commands.TpActiveWorldCommand;
import eu.darkcube.system.sumo.executions.EquipPlayer;
import eu.darkcube.system.sumo.executions.RandomTeam;
import eu.darkcube.system.sumo.executions.Respawn;
import eu.darkcube.system.sumo.executions.Spectator;
import eu.darkcube.system.sumo.guis.TeamGUI;
import eu.darkcube.system.sumo.guis.VotingGUI;
import eu.darkcube.system.sumo.guis.VotingMapGUI;
import eu.darkcube.system.sumo.items.game.ItemWool;
import eu.darkcube.system.sumo.loader.MapLoader;
import eu.darkcube.system.sumo.manager.DamageManager;
import eu.darkcube.system.sumo.manager.LifeManager;
import eu.darkcube.system.sumo.manager.MapManager;
import eu.darkcube.system.sumo.manager.PlayerManager;
import eu.darkcube.system.sumo.manager.TeamManager;
import eu.darkcube.system.sumo.other.GameDoubleJump;
import eu.darkcube.system.sumo.other.LobbySystemLink;
import eu.darkcube.system.sumo.other.Message;
import eu.darkcube.system.sumo.other.StartingTimer;
import eu.darkcube.system.sumo.other.WoolDespawner;
import eu.darkcube.system.sumo.prefix.ChatManager;
import eu.darkcube.system.sumo.prefix.PrefixManager;
import eu.darkcube.system.sumo.ruler.LobbyRuler;
import eu.darkcube.system.sumo.ruler.MapRuler;
import eu.darkcube.system.sumo.scoreboards.GameScoreboard;
import eu.darkcube.system.sumo.scoreboards.LobbyScoreboard;
import eu.darkcube.system.util.Language;

public class Sumo extends DarkCubePlugin {
    private static Sumo instance;
    private LobbySystemLink lobbySystemLink;
    private LifeManager lifeManager;
    private TeamManager teamManager;

    public Sumo() {
        super("sumo");
        instance = this;
    }

    @Override
    public void onEnable() {

        try {
            Language.GERMAN.registerLookup(this.getClassLoader(), "messages_de.properties", s -> Message.KEY_PREFIX + s);
            Language.ENGLISH.registerLookup(this.getClassLoader(), "messages_en.properties", s -> Message.KEY_PREFIX + s);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        var mapLoader = new MapLoader();
        mapLoader.loadWorlds();
        var prefixManager = new PrefixManager(this);
        teamManager = new TeamManager(prefixManager);
        var lobbyScoreboard = new LobbyScoreboard(this, prefixManager);
        var mapManager = new MapManager(this, lobbyScoreboard);
        var woolDespawner = new WoolDespawner(this);
        var gameScoreboard = new GameScoreboard(this, mapManager, prefixManager);
        lifeManager = new LifeManager(teamManager, gameScoreboard);
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
        Spectator.setPrefixManager(prefixManager);
        var chatManager = new ChatManager(prefixManager);
        var itemWool = new ItemWool(teamManager);
        var mapRuler = new MapRuler(mapManager);
        var teamGUI = new TeamGUI(teamManager, prefixManager, equipPlayer);
        var votingMapGUI = new VotingMapGUI(mapManager);
        var votingGUI = new VotingGUI(votingMapGUI);

        DarkCubeServerCloudNet.autoConfigure(false);
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
        instance.getCommand("setgamestate").setExecutor(new SetGameStateCommand(respawn, equipPlayer, randomTeam, startingTimer, lobbySystemLink, prefixManager));
        instance.getCommand("showgamestate").setExecutor(new ShowGameStateCommand());
        instance.getCommand("setmap").setExecutor(new SetMapCommand(mapManager));
        instance.getCommand("showactivemap").setExecutor(new ShowActiveMapCommand(mapManager));
        instance.getCommand("tpactiveworld").setExecutor(new TpActiveWorldCommand(mapManager));
    }

    public static Sumo getInstance() {
        return instance;
    }

    public LifeManager getLifeManager() {
        return lifeManager;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public LobbySystemLink getLobbySystemLink() {
        return lobbySystemLink;
    }

}