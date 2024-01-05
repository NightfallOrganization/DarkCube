/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo;

import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.sumo.commands.*;
import eu.darkcube.system.sumo.guis.TeamGUI;
import eu.darkcube.system.sumo.loader.MapLoader;
import eu.darkcube.system.sumo.manager.MainManager;
import eu.darkcube.system.sumo.other.LobbyScoreboard;
import eu.darkcube.system.sumo.other.Respawn;
import eu.darkcube.system.sumo.other.StartingTimer;
import eu.darkcube.system.sumo.ruler.LobbyRuler;
import eu.darkcube.system.sumo.ruler.MainRuler;
import eu.darkcube.system.sumo.ruler.MapRuler;

public class Sumo extends DarkCubePlugin {
    private static Sumo instance;


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
        LobbyRuler lobbyRuler = new LobbyRuler();
        MainManager mainManager = new MainManager(lobbyScoreboard);
        Respawn respawn = new Respawn(mainRuler);
        StartingTimer startingTimer = new StartingTimer(this, lobbyScoreboard, respawn);

        getServer().getPluginManager().registerEvents(respawn, this);
        getServer().getPluginManager().registerEvents(startingTimer, this);
        getServer().getPluginManager().registerEvents(lobbyScoreboard, this);
        getServer().getPluginManager().registerEvents(lobbyRuler, this);
        getServer().getPluginManager().registerEvents(new MapRuler(mainRuler), this);
        getServer().getPluginManager().registerEvents(mainRuler, this);
        getServer().getPluginManager().registerEvents(mainManager, this);
        getServer().getPluginManager().registerEvents(new TeamGUI(), this);

        instance.getCommand("timer").setExecutor(new TimerCommand(startingTimer));
        instance.getCommand("setgamestate").setExecutor(new SetGameStateCommand());
        instance.getCommand("showgamestate").setExecutor(new ShowGameStateCommand());
        instance.getCommand("mapvote").setExecutor(new MapVoteCommand(mainRuler));
        instance.getCommand("showactivemap").setExecutor(new ShowActiveMapCommand(mainRuler));
        instance.getCommand("tpactiveworld").setExecutor(new TpActiveWorldCommand(mainRuler));
    }


}