/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo;

import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.sumo.commands.*;
import eu.darkcube.system.sumo.game.*;
import eu.darkcube.system.sumo.game.items.LifeManager;
import eu.darkcube.system.sumo.guis.GUIPattern;
import eu.darkcube.system.sumo.guis.TeamGUI;
import eu.darkcube.system.sumo.listener.WoolRegenerationListener;
import eu.darkcube.system.sumo.lobby.LobbyItemManager;
import eu.darkcube.system.sumo.lobby.LobbyScoreboard;
import eu.darkcube.system.sumo.lobby.LobbyTimer;
import eu.darkcube.system.sumo.other.MapManager;
import eu.darkcube.system.sumo.ruler.GameRuler;
import eu.darkcube.system.sumo.ruler.LobbyRuler;
import eu.darkcube.system.sumo.ruler.MainRuler;
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
        ArmorManager armorManager = new ArmorManager();
        TeamManager teamManager = new TeamManager(armorManager);
        LifeManager lifeManager = new LifeManager(gameScoreboard);
        lobbyTimer = new LobbyTimer(this, teamManager, armorManager);
        lobbyTimer.startTimer();
        gamedoubleJump = new GameDoubleJump(this);
        gameScoreboard = new GameScoreboard(lifeManager, teamManager);
        GameRespawn gameRespawn = new GameRespawn(gameScoreboard, lifeManager, teamManager);
        GameRuler gameRuler = new GameRuler(teamManager, gameRespawn, lifeManager, lobbyTimer);
        gameItemManager = new GameItemManager(teamManager);

        getServer().getPluginManager().registerEvents(lifeManager, this);
        getServer().getPluginManager().registerEvents(new WoolDespawner(this), this);
        getServer().getPluginManager().registerEvents(new WoolRegenerationListener(this), this);
        getServer().getPluginManager().registerEvents(new TeamGUI(teamManager), this);
        getServer().getPluginManager().registerEvents(new GUIPattern(), this);
        getServer().getPluginManager().registerEvents(gameScoreboard, this);
        getServer().getPluginManager().registerEvents(new MainRuler(lobbyTimer), this);
        getServer().getPluginManager().registerEvents(gameRuler, this);
        getServer().getPluginManager().registerEvents(teamManager, this);
        getServer().getPluginManager().registerEvents(mapManager, this);
        getServer().getPluginManager().registerEvents(new GameDoubleJump(this), this);
        getServer().getPluginManager().registerEvents(new GameItemManager(teamManager), this);
        getServer().getPluginManager().registerEvents(new LobbyScoreboard(), this);
        getServer().getPluginManager().registerEvents(new LobbyItemManager(), this);
        getServer().getPluginManager().registerEvents(new LobbyRuler(), this);
        getServer().getPluginManager().registerEvents(new TeamColors(teamManager), this);

        instance.getCommand("setlifes").setExecutor(new SetLifesCommand(teamManager, lifeManager, gameScoreboard));
        instance.getCommand("mylifes").setExecutor(new MyLifesCommand(lifeManager, teamManager));
        instance.getCommand("myteam").setExecutor(new MyTeamCommand(teamManager));
        instance.getCommand("setteam").setExecutor(new SetTeamCommand(teamManager));
        instance.getCommand("timer").setExecutor(new TimerCommand(this, lobbyTimer));
        instance.getCommand("start").setExecutor(new StartCommand(this, lobbyTimer));

    }

    public static Sumo getInstance() {
        return instance;
    }

    public LobbyTimer getLobbyTimer() {
        return lobbyTimer;
    }

    public TeamManager getTeamManager(ArmorManager armorManager) {
        return new TeamManager(armorManager);
    }

}