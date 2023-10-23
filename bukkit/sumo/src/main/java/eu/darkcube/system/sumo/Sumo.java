/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo;

import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.sumo.commands.MyTeamCommand;
import eu.darkcube.system.sumo.commands.SetTeamCommand;
import eu.darkcube.system.sumo.commands.StartCommand;
import eu.darkcube.system.sumo.commands.TimerCommand;
import eu.darkcube.system.sumo.game.GameDoubleJump;
import eu.darkcube.system.sumo.game.GameItemManager;
import eu.darkcube.system.sumo.game.GameScoreboard;
import eu.darkcube.system.sumo.game.WoolDespawner;
import eu.darkcube.system.sumo.guis.GUIPattern;
import eu.darkcube.system.sumo.guis.TeamGUI;
import eu.darkcube.system.sumo.listener.WoolRegenerationListener;
import eu.darkcube.system.sumo.lobby.LobbyItemManager;
import eu.darkcube.system.sumo.lobby.LobbyScoreboardManager;
import eu.darkcube.system.sumo.lobby.LobbyTimer;
import eu.darkcube.system.sumo.ruler.GameRuler;
import eu.darkcube.system.sumo.ruler.LobbyRuler;
import eu.darkcube.system.sumo.ruler.MainRuler;
import eu.darkcube.system.sumo.team.MapTeamSpawns;
import eu.darkcube.system.sumo.team.TeamColors;
import eu.darkcube.system.sumo.team.TeamManager;

public class Sumo extends DarkCubePlugin {
    private static Sumo instance;
    private LobbyTimer lobbyTimer;
    private GameScoreboard gameScoreboard;
    private GameItemManager gameItemManager;
    private GameDoubleJump gamedoubleJump;

    public Sumo() {
        super("sumo");
    }

    @Override
    public void onEnable() {
        instance = this;

        MapManager mapManager = new MapManager();
        mapManager.loadWorlds();

        MapTeamSpawns mapTeamSpawns = new MapTeamSpawns();
        TeamManager teamManager = new TeamManager(mapTeamSpawns);
        lobbyTimer = new LobbyTimer(this, teamManager);
        lobbyTimer.startTimer();
        gamedoubleJump = new GameDoubleJump(this);
        GameRuler gameRuler = new GameRuler(teamManager, mapTeamSpawns);
        gameScoreboard = new GameScoreboard();
        gameItemManager = new GameItemManager(teamManager);

        getServer().getPluginManager().registerEvents(new WoolDespawner(this), this);
        getServer().getPluginManager().registerEvents(new WoolRegenerationListener(this), this);
        getServer().getPluginManager().registerEvents(new TeamGUI(teamManager), this);
        getServer().getPluginManager().registerEvents(new GUIPattern(), this);
        getServer().getPluginManager().registerEvents(gameScoreboard, this);
        getServer().getPluginManager().registerEvents(new MainRuler(), this);
        getServer().getPluginManager().registerEvents(gameRuler, this);
        getServer().getPluginManager().registerEvents(teamManager, this);
        getServer().getPluginManager().registerEvents(mapManager, this);
        getServer().getPluginManager().registerEvents(new GameDoubleJump(this), this);
        getServer().getPluginManager().registerEvents(new GameItemManager(teamManager), this);
        getServer().getPluginManager().registerEvents(new LobbyScoreboardManager(), this);
        getServer().getPluginManager().registerEvents(new LobbyItemManager(), this);
        getServer().getPluginManager().registerEvents(new LobbyRuler(), this);
        getServer().getPluginManager().registerEvents(new TeamColors(teamManager), this);

        instance.getCommand("myteam").setExecutor(new MyTeamCommand(teamManager));
        instance.getCommand("setteam").setExecutor(new SetTeamCommand(teamManager));
        instance.getCommand("timer").setExecutor(new TimerCommand(this, lobbyTimer));
        instance.getCommand("start").setExecutor(new StartCommand(this, lobbyTimer));

    }

    public static Sumo getInstance() {
        return instance;
    }

    public eu.darkcube.system.sumo.lobby.LobbyTimer getLobbyTimer() {
        return lobbyTimer;
    }

    public TeamManager getTeamManager(MapTeamSpawns mapTeamSpawns) {
        return new TeamManager(mapTeamSpawns);
    }

}