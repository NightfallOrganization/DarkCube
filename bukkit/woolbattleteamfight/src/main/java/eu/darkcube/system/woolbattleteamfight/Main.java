/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattleteamfight;


import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.woolbattleteamfight.commands.SetTeamCommand;
import eu.darkcube.system.woolbattleteamfight.commands.MyTeamCommand;
import eu.darkcube.system.woolbattleteamfight.commands.StartCommand;
import eu.darkcube.system.woolbattleteamfight.commands.TimerCommand;
import eu.darkcube.system.woolbattleteamfight.game.CoreManager;
import eu.darkcube.system.woolbattleteamfight.game.DeathMessage;
import eu.darkcube.system.woolbattleteamfight.items.other.Bow;
import eu.darkcube.system.woolbattleteamfight.items.other.DoubleJump;
import eu.darkcube.system.woolbattleteamfight.game.GameScoreboard;
import eu.darkcube.system.woolbattleteamfight.items.other.EnderPearl;
import eu.darkcube.system.woolbattleteamfight.items.perks.Rettungskapsel;
import eu.darkcube.system.woolbattleteamfight.items.perks.Switcher;
import eu.darkcube.system.woolbattleteamfight.ruler.GameRuler;
import eu.darkcube.system.woolbattleteamfight.game.GameItemManager;
import eu.darkcube.system.woolbattleteamfight.guis.TeamGUI;
import eu.darkcube.system.woolbattleteamfight.lobby.*;
import eu.darkcube.system.woolbattleteamfight.ruler.LobbyRuler;
import eu.darkcube.system.woolbattleteamfight.ruler.MainRuler;
import eu.darkcube.system.woolbattleteamfight.team.MapTeamSpawns;
import eu.darkcube.system.woolbattleteamfight.team.TeamColors;
import eu.darkcube.system.woolbattleteamfight.team.TeamManager;
import eu.darkcube.system.woolbattleteamfight.wool.WoolBreaker;
import eu.darkcube.system.woolbattleteamfight.wool.WoolManager;

public class Main extends DarkCubePlugin {
    private static Main instance;
    private LobbyTimer lobbyTimer;
    private WoolManager woolManager;
    private GameScoreboard gameScoreboard;
    private DoubleJump doubleJump;
    private GameItemManager gameItemManager;


    public Main() {
        super("woolbattleteamfight");
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
        GameRuler gameRuler = new GameRuler(teamManager, mapTeamSpawns);
        woolManager = new WoolManager(teamManager);
        gameScoreboard = new GameScoreboard();
        doubleJump = new DoubleJump(this);
        WoolBreaker woolBreaker = new WoolBreaker(woolManager);
        gameItemManager = new GameItemManager();
        EnderPearl enderPearl = new EnderPearl(gameItemManager);
        Switcher switcher = new Switcher(gameItemManager);
        Rettungskapsel rettungskapsel = new Rettungskapsel(gameItemManager);
        DeathMessage deathMessage = new DeathMessage(teamManager);
        CoreManager coreManager = new CoreManager(teamManager);
        Bow bow = new Bow();

        getServer().getPluginManager().registerEvents(bow, this);
        getServer().getPluginManager().registerEvents(coreManager, this);
        getServer().getPluginManager().registerEvents(deathMessage, this);
        getServer().getPluginManager().registerEvents(rettungskapsel, this);
        getServer().getPluginManager().registerEvents(switcher, this);
        getServer().getPluginManager().registerEvents(enderPearl, this);
        getServer().getPluginManager().registerEvents(woolBreaker, this);
        getServer().getPluginManager().registerEvents(gameScoreboard, this);
        getServer().getPluginManager().registerEvents(new DoubleJump(this), this);
        getServer().getPluginManager().registerEvents(new MainRuler(), this);
        getServer().getPluginManager().registerEvents(woolManager, this);
        getServer().getPluginManager().registerEvents(gameRuler, this);
        getServer().getPluginManager().registerEvents(teamManager, this);
        getServer().getPluginManager().registerEvents(mapManager, this);
        getServer().getPluginManager().registerEvents(new GameItemManager(), this);
        getServer().getPluginManager().registerEvents(new LobbyScoreboardManager(), this);
        getServer().getPluginManager().registerEvents(new LobbyItemManager(), this);
        getServer().getPluginManager().registerEvents(new LobbyRuler(), this);
        getServer().getPluginManager().registerEvents(new TeamGUI(teamManager, mapTeamSpawns), this);
        getServer().getPluginManager().registerEvents(new TeamColors(teamManager), this);

        instance.getCommand("myteam").setExecutor(new MyTeamCommand(teamManager));
        instance.getCommand("setteam").setExecutor(new SetTeamCommand(teamManager));
        instance.getCommand("timer").setExecutor(new TimerCommand(this, lobbyTimer));
        instance.getCommand("start").setExecutor(new StartCommand(this, lobbyTimer));
    }

    public static Main getInstance() {
        return instance;
    }

    public LobbyTimer getLobbyTimer() {
        return lobbyTimer;
    }

    public DoubleJump getDoubleJump() {
        return doubleJump;
    }

    public WoolManager getWoolManager() {
        return woolManager;
    }

    public TeamManager getTeamManager(MapTeamSpawns mapTeamSpawns) {
        return new TeamManager(mapTeamSpawns);
    }
}
