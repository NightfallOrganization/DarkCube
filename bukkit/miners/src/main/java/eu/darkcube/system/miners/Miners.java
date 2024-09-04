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
import eu.darkcube.system.miners.listener.PlayerJoinListener;
import eu.darkcube.system.miners.listener.PlayerLeaveListener;
import eu.darkcube.system.miners.manager.WorldManager;
import eu.darkcube.system.miners.utils.GameScoreboard;
import eu.darkcube.system.miners.utils.MinersPlayer;
import eu.darkcube.system.miners.utils.Ruler;
import eu.darkcube.system.miners.utils.message.LanguageHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Miners extends DarkCubePlugin {
    private static Miners instance;
    private GameScoreboard gameScoreboard;
    public Map<Player, MinersPlayer> minersPlayerMap = new HashMap<>();

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

        instance.getServer().getPluginManager().registerEvents(ruler, this);
        instance.getServer().getPluginManager().registerEvents(joinListener, this);
        instance.getServer().getPluginManager().registerEvents(leaveListener, this);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            gameScoreboard.createGameScoreboard(onlinePlayer);
        }
    }

    @Override
    public void onDisable() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Miners.getInstance().getGameScoreboard().deleteGameScoreboard(onlinePlayer);
        }
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

    //</editor-fold>

}
