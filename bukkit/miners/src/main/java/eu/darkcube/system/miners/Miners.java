/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners;

import static eu.darkcube.system.miners.enums.TeleportLocations.*;

import java.util.HashMap;
import java.util.Map;

import eu.darkcube.system.bukkit.DarkCubePlugin;
import eu.darkcube.system.bukkit.commandapi.CommandAPI;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.miners.commands.SetTeamCommand;
import eu.darkcube.system.miners.commands.StartCommand;
import eu.darkcube.system.miners.commands.TimerCommand;
import eu.darkcube.system.miners.gamephase.GamePhase;
import eu.darkcube.system.miners.gamephase.Ruler;
import eu.darkcube.system.miners.gamephase.endphase.EndPhase;
import eu.darkcube.system.miners.gamephase.fightphase.FightPhase;
import eu.darkcube.system.miners.gamephase.lobbyphase.LobbyPhase;
import eu.darkcube.system.miners.gamephase.lobbyphase.LobbyScoreboard;
import eu.darkcube.system.miners.gamephase.miningphase.MiningPhase;
import eu.darkcube.system.miners.inventorys.lobby.OwnAbilitiesInventory;
import eu.darkcube.system.miners.inventorys.lobby.TeamInventory;
import eu.darkcube.system.miners.listener.PlayerJoinListener;
import eu.darkcube.system.miners.listener.PlayerLeaveListener;
import eu.darkcube.system.miners.manager.WorldManager;
import eu.darkcube.system.miners.utils.MinersPlayer;
import eu.darkcube.system.miners.utils.Team;
import eu.darkcube.system.miners.utils.message.LanguageHelper;
import eu.darkcube.system.miners.utils.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Miners extends DarkCubePlugin {
    private static Miners instance;
    private LobbyScoreboard lobbyScoreboard;
    public Map<Player, MinersPlayer> minersPlayerMap = new HashMap<>();
    private GamePhase currentPhase;
    private Team teamRed;
    private Team teamBlue;
    private Team teamGreen;
    private Team teamYellow;
    private Team teamWhite;
    private Team teamBlack;
    private Team teamPurple;
    private Team teamOrange;
    private OwnAbilitiesInventory ownAbilitiesInventory;
    private TeamInventory teamInventory;

    public Miners() {
        super("miners");
        instance = this;
    }

    @Override
    public void onEnable() {
        WorldManager.loadWorlds();
        lobbyScoreboard = new LobbyScoreboard();
        LanguageHelper.initialize();

        teamRed = new Team(Message.TEAM_RED, MINE_RED, true);
        teamBlue = new Team(Message.TEAM_BLUE, MINE_BLUE,true);
        teamGreen = new Team(Message.TEAM_GREEN, MINE_GREEN,true);
        teamYellow = new Team(Message.TEAM_YELLOW, MINE_YELLOW,true);
        teamWhite = new Team(Message.TEAM_WHITE, MINE_WHITE,true);
        teamBlack = new Team(Message.TEAM_BLACK, MINE_BLACK,true);
        teamPurple = new Team(Message.TEAM_PURPLE, MINE_PURPLE,true);
        teamOrange = new Team(Message.TEAM_ORANGE, MINE_ORANGE,true);

        var ruler = new Ruler();
        var joinListener = new PlayerJoinListener();
        var leaveListener = new PlayerLeaveListener();
        ownAbilitiesInventory = new OwnAbilitiesInventory();
        teamInventory = new TeamInventory();

        instance.getServer().getPluginManager().registerEvents(ruler, this);
        instance.getServer().getPluginManager().registerEvents(joinListener, this);
        instance.getServer().getPluginManager().registerEvents(leaveListener, this);

        CommandAPI.instance().register(new TimerCommand());
        CommandAPI.instance().register(new SetTeamCommand());
        CommandAPI.instance().register(new StartCommand());

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            lobbyScoreboard.createGameScoreboard(onlinePlayer);
        }

        currentPhase = new LobbyPhase();
        currentPhase.enable();
    }

    @Override
    public void onDisable() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Miners.getInstance().getGameScoreboard().deleteGameScoreboard(onlinePlayer);
        }
    }

    public void enableNextPhase() {
        currentPhase.disable();
        currentPhase = switch (currentPhase) {
            case LobbyPhase _ -> new MiningPhase();
            case MiningPhase _ -> new FightPhase();
            case FightPhase _ -> new EndPhase();
            case null, default -> throw new IllegalStateException("Invalid phase");
        };
        currentPhase.enable();
    }

    //<editor-fold desc="Getter">
    public static Miners getInstance() {
        return instance;
    }

    public LobbyScoreboard getGameScoreboard() {
        return lobbyScoreboard;
    }

    public MinersPlayer getPlayer(@NotNull Player player) {
        return minersPlayerMap.get(player);
    }

    public static MinersPlayer getStaticPlayer(@NotNull Player player) {
        return Miners.getInstance().minersPlayerMap.get(player);
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public OwnAbilitiesInventory getOwnAbilitiesInventory() {
        return ownAbilitiesInventory;
    }

    public TeamInventory getTeamInventory() {
        return teamInventory;
    }

    public Team getTeamOrange() {
        return teamOrange;
    }

    public Team getTeamPurple() {
        return teamPurple;
    }

    public Team getTeamBlack() {
        return teamBlack;
    }

    public Team getTeamWhite() {
        return teamWhite;
    }

    public Team getTeamYellow() {
        return teamYellow;
    }

    public Team getTeamGreen() {
        return teamGreen;
    }

    public Team getTeamBlue() {
        return teamBlue;
    }

    public Team getTeamRed() {
        return teamRed;
    }

    //</editor-fold>

}
