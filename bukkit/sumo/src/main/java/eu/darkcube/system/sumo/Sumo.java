/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo;

import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.sumo.commands.*;
import eu.darkcube.system.sumo.executions.EquipPlayer;
import eu.darkcube.system.sumo.executions.RandomTeam;
import eu.darkcube.system.sumo.executions.Spectator;
import eu.darkcube.system.sumo.guis.TeamGUI;
import eu.darkcube.system.sumo.items.game.ItemWool;
import eu.darkcube.system.sumo.loader.MapLoader;
import eu.darkcube.system.sumo.manager.*;
import eu.darkcube.system.sumo.other.GameDoubleJump;
import eu.darkcube.system.sumo.other.WoolDespawner;
import eu.darkcube.system.sumo.prefix.ChatManager;
import eu.darkcube.system.sumo.prefix.PrefixManager;
import eu.darkcube.system.sumo.scoreboards.GameScoreboard;
import eu.darkcube.system.sumo.scoreboards.LobbyScoreboard;
import eu.darkcube.system.sumo.executions.Respawn;
import eu.darkcube.system.sumo.other.StartingTimer;
import eu.darkcube.system.sumo.ruler.LobbyRuler;
import eu.darkcube.system.sumo.ruler.MainRuler;
import eu.darkcube.system.sumo.ruler.MapRuler;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class Sumo extends DarkCubePlugin {
    private static Sumo instance;


    public Sumo() {
        super("sumo");
    }

    @Override
    public void onEnable() {
        instance = this;

        var mapLoader = new MapLoader();
        mapLoader.loadWorlds();
        var lobbyScoreboard = new LobbyScoreboard(this);
        var mainRuler = new MainRuler(this, lobbyScoreboard);
        var teamManager = new TeamManager();
        var woolDespawner = new WoolDespawner(this);
        var gameScoreboard = new GameScoreboard(mainRuler);
        var lifeManager = new LifeManager(teamManager, gameScoreboard);
        var gameDoubleJump = new GameDoubleJump(this, mainRuler);
        var lobbyRuler = new LobbyRuler();
        var prefixManager = new PrefixManager(teamManager);
        var mainManager = new MainManager(lobbyScoreboard, mainRuler, teamManager, prefixManager);
        var respawn = new Respawn(mainRuler, lifeManager, teamManager);
        var equipPlayer = new EquipPlayer(teamManager);
        var randomTeam = new RandomTeam(teamManager, prefixManager);
        var startingTimer = new StartingTimer(this, lobbyScoreboard, respawn, equipPlayer, teamManager, randomTeam);
        var damageManager = new DamageManager(teamManager, this);
        Spectator.setMainRuler(mainRuler);
        var chatManager = new ChatManager();
        var itemWool = new ItemWool(teamManager, this, mainRuler);

        getServer().getPluginManager().registerEvents(gameDoubleJump, this);
        getServer().getPluginManager().registerEvents(itemWool, this);
        getServer().getPluginManager().registerEvents(woolDespawner, this);
        getServer().getPluginManager().registerEvents(damageManager, this);
        getServer().getPluginManager().registerEvents(respawn, this);
        getServer().getPluginManager().registerEvents(startingTimer, this);
        getServer().getPluginManager().registerEvents(lobbyScoreboard, this);
        getServer().getPluginManager().registerEvents(gameScoreboard, this);
        getServer().getPluginManager().registerEvents(lobbyRuler, this);
        getServer().getPluginManager().registerEvents(new MapRuler(mainRuler), this);
        getServer().getPluginManager().registerEvents(mainRuler, this);
        getServer().getPluginManager().registerEvents(mainManager, this);
        getServer().getPluginManager().registerEvents(new TeamGUI(teamManager, prefixManager, equipPlayer), this);
        getServer().getPluginManager().registerEvents(chatManager, this);

        instance.getCommand("showteamlifes").setExecutor(new ShowTeamLivesCommand(lifeManager));
        instance.getCommand("setlifes").setExecutor(new SetLifesCommand(lifeManager, gameScoreboard));
        instance.getCommand("setactivemap").setExecutor(new SetActiveMapCommand(mainRuler, respawn));
        instance.getCommand("start").setExecutor(new StartCommand(startingTimer));
        instance.getCommand("setteam").setExecutor(new SetTeamCommand(teamManager, prefixManager));
        instance.getCommand("timer").setExecutor(new TimerCommand(startingTimer));
        instance.getCommand("setgamestate").setExecutor(new SetGameStateCommand(respawn, equipPlayer, randomTeam, startingTimer));
        instance.getCommand("showgamestate").setExecutor(new ShowGameStateCommand());
        instance.getCommand("mapvote").setExecutor(new MapVoteCommand(mainRuler));
        instance.getCommand("showactivemap").setExecutor(new ShowActiveMapCommand(mainRuler));
        instance.getCommand("tpactiveworld").setExecutor(new TpActiveWorldCommand(mainRuler));
    }

    public static Sumo getInstance() {
        return instance;
    }
}