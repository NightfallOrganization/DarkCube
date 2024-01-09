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
import eu.darkcube.system.sumo.loader.MapLoader;
import eu.darkcube.system.sumo.manager.DamageManager;
import eu.darkcube.system.sumo.manager.LifeManager;
import eu.darkcube.system.sumo.manager.MainManager;
import eu.darkcube.system.sumo.manager.TeamManager;
import eu.darkcube.system.sumo.scoreboards.GameScoreboard;
import eu.darkcube.system.sumo.scoreboards.LobbyScoreboard;
import eu.darkcube.system.sumo.executions.Respawn;
import eu.darkcube.system.sumo.other.StartingTimer;
import eu.darkcube.system.sumo.ruler.LobbyRuler;
import eu.darkcube.system.sumo.ruler.MainRuler;
import eu.darkcube.system.sumo.ruler.MapRuler;

public class Sumo extends DarkCubePlugin {
    private static Sumo instance;
    private LifeManager lifeManager;


    public Sumo() {
        super("sumo");
    }

    @Override
    public void onEnable() {
        instance = this;

        MapLoader mapLoader = new MapLoader();
        mapLoader.loadWorlds();
        LobbyScoreboard lobbyScoreboard = new LobbyScoreboard(this);
        MainRuler mainRuler = new MainRuler(this, lobbyScoreboard);
        GameScoreboard gameScoreboard = new GameScoreboard(mainRuler);
        LobbyRuler lobbyRuler = new LobbyRuler();
        MainManager mainManager = new MainManager(lobbyScoreboard);
        TeamManager teamManager = new TeamManager();
        lifeManager = new LifeManager(teamManager, gameScoreboard, lifeManager);
        Respawn respawn = new Respawn(mainRuler, lifeManager, teamManager);
        EquipPlayer equipPlayer = new EquipPlayer(teamManager);
        RandomTeam randomTeam = new RandomTeam(teamManager);
        StartingTimer startingTimer = new StartingTimer(this, lobbyScoreboard, respawn, equipPlayer, teamManager, randomTeam);
        DamageManager damageManager = new DamageManager(teamManager, this);
        Spectator.setMainRuler(mainRuler);

        getServer().getPluginManager().registerEvents(damageManager, this);
        getServer().getPluginManager().registerEvents(respawn, this);
        getServer().getPluginManager().registerEvents(startingTimer, this);
        getServer().getPluginManager().registerEvents(lobbyScoreboard, this);
        getServer().getPluginManager().registerEvents(gameScoreboard, this);
        getServer().getPluginManager().registerEvents(lobbyRuler, this);
        getServer().getPluginManager().registerEvents(new MapRuler(mainRuler), this);
        getServer().getPluginManager().registerEvents(mainRuler, this);
        getServer().getPluginManager().registerEvents(mainManager, this);
        getServer().getPluginManager().registerEvents(new TeamGUI(teamManager), this);

        instance.getCommand("showteamlifes").setExecutor(new ShowTeamLivesCommand(lifeManager));
        instance.getCommand("setlifes").setExecutor(new SetLifesCommand(lifeManager));
        instance.getCommand("setactivemap").setExecutor(new SetActiveMapCommand(mainRuler, respawn));
        instance.getCommand("start").setExecutor(new StartCommand(startingTimer));
        instance.getCommand("setteam").setExecutor(new SetTeamCommand(teamManager));
        instance.getCommand("timer").setExecutor(new TimerCommand(startingTimer));
        instance.getCommand("setgamestate").setExecutor(new SetGameStateCommand());
        instance.getCommand("showgamestate").setExecutor(new ShowGameStateCommand());
        instance.getCommand("mapvote").setExecutor(new MapVoteCommand(mainRuler));
        instance.getCommand("showactivemap").setExecutor(new ShowActiveMapCommand(mainRuler));
        instance.getCommand("tpactiveworld").setExecutor(new TpActiveWorldCommand(mainRuler));
    }


}