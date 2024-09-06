/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners;

import java.util.HashMap;
import java.util.Map;

import eu.darkcube.system.bukkit.DarkCubePlugin;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.miners.gamephase.GamePhase;
import eu.darkcube.system.miners.gamephase.Ruler;
import eu.darkcube.system.miners.gamephase.endphase.EndPhase;
import eu.darkcube.system.miners.gamephase.fightphase.FightPhase;
import eu.darkcube.system.miners.gamephase.lobbyphase.LobbyPhase;
import eu.darkcube.system.miners.gamephase.miningphase.MiningPhase;
import eu.darkcube.system.miners.inventorys.lobby.OwnAbilitiesInventory;
import eu.darkcube.system.miners.inventorys.lobby.TeamInventory;
import eu.darkcube.system.miners.listener.PlayerJoinListener;
import eu.darkcube.system.miners.listener.PlayerLeaveListener;
import eu.darkcube.system.miners.manager.WorldManager;
import eu.darkcube.system.miners.team.Team;
import eu.darkcube.system.miners.utils.GameScoreboard;
import eu.darkcube.system.miners.utils.MinersPlayer;
import eu.darkcube.system.miners.utils.message.LanguageHelper;
import eu.darkcube.system.miners.utils.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Miners extends DarkCubePlugin {
    private static Miners instance;
    private GameScoreboard gameScoreboard;
    public Map<Player, MinersPlayer> minersPlayerMap = new HashMap<>();
    private GamePhase currentPhase;
    private OwnAbilitiesInventory ownAbilitiesInventory;
    private TeamInventory teamInventory;
    private Team teamRed;
    private Team teamBlue;
    private Team teamGreen;
    private Team teamYellow;
    private Team teamWhite;
    private Team teamBlack;
    private Team teamPurple;
    private Team teamOrange;

    public Miners() {
        super("miners");
        instance = this;
    }

    @Override
    public void onEnable() {
        WorldManager.loadWorlds();
        gameScoreboard = new GameScoreboard();
        LanguageHelper.initialize();
        var ruler = new Ruler();
        var joinListener = new PlayerJoinListener();
        var leaveListener = new PlayerLeaveListener();
        ownAbilitiesInventory = new OwnAbilitiesInventory();
        teamInventory = new TeamInventory();

        teamRed = new Team(Message.TEAM_RED, true);
        teamBlue = new Team(Message.TEAM_BLUE, true);
        teamGreen = new Team(Message.TEAM_GREEN, true);
        teamYellow = new Team(Message.TEAM_YELLOW, true);
        teamWhite = new Team(Message.TEAM_WHITE, true);
        teamBlack = new Team(Message.TEAM_BLACK, true);
        teamPurple = new Team(Message.TEAM_PURPLE, true);
        teamOrange = new Team(Message.TEAM_ORANGE, true);

        instance.getServer().getPluginManager().registerEvents(ruler, this);
        instance.getServer().getPluginManager().registerEvents(joinListener, this);
        instance.getServer().getPluginManager().registerEvents(leaveListener, this);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            gameScoreboard.createGameScoreboard(onlinePlayer);
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
            case LobbyPhase lobby -> new MiningPhase(lobby);
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

    public GameScoreboard getGameScoreboard() {
        return gameScoreboard;
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
