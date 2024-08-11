/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania;

import java.util.HashMap;
import java.util.Map;
import eu.darkcube.system.bukkit.DarkCubePlugin;
import eu.darkcube.system.bukkit.commandapi.CommandAPI;
import eu.darkcube.system.woolmania.commands.ResetHallsCommand;
import eu.darkcube.system.woolmania.commands.ResetLevelCommand;
import eu.darkcube.system.woolmania.commands.SetBoosterCommand;
import eu.darkcube.system.woolmania.commands.StatsCommand;
import eu.darkcube.system.woolmania.handler.LevelXPHandler;
import eu.darkcube.system.woolmania.commands.zenum.ZenumCommand;
import eu.darkcube.system.woolmania.inventory.ShopInventory;
import eu.darkcube.system.woolmania.listener.BlockBreakListener;
import eu.darkcube.system.woolmania.listener.JoinListener;
import eu.darkcube.system.woolmania.listener.LeaveListener;
import eu.darkcube.system.woolmania.listener.NPCListeners;
import eu.darkcube.system.woolmania.manager.NPCManager;
import eu.darkcube.system.woolmania.npc.NPCRemover;
import eu.darkcube.system.woolmania.manager.WorldManager;
import eu.darkcube.system.woolmania.npc.NPCCreator;
import eu.darkcube.system.woolmania.util.GameScoreboard;
import eu.darkcube.system.woolmania.util.Ruler;
import eu.darkcube.system.woolmania.util.WoolManiaPlayer;
import eu.darkcube.system.woolmania.util.WoolRegenerationTimer;
import eu.darkcube.system.woolmania.util.message.LanguageHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class WoolMania extends DarkCubePlugin {
    private static WoolMania instance;
    private NPCManager npcManager;
    private NPCCreator npcCreator;
    private ShopInventory shopInventory;
    private LevelXPHandler levelXPHandler;
    private GameScoreboard gameScoreboard;
    public Map<Player, WoolManiaPlayer> woolManiaPlayerMap = new HashMap<>();

    public WoolMania() {
        super("woolmania");
        instance = this;
    }

    @Override
    public void onEnable() {
        WorldManager.loadWorlds();
        gameScoreboard = new GameScoreboard();
        LanguageHelper.initialize();
        WoolRegenerationTimer.startFirstTimer();
        npcManager = new NPCManager(this);
        npcCreator = new NPCCreator(npcManager);
        shopInventory = new ShopInventory();
        levelXPHandler = new LevelXPHandler();
        NPCListeners.register(npcManager, npcCreator);
        npcCreator.createNPC();

        var joinListener = new JoinListener();
        var leaveListener = new LeaveListener();
        var blockBreakListener = new BlockBreakListener();
        var ruler = new Ruler();

        instance.getServer().getPluginManager().registerEvents(blockBreakListener, this);
        instance.getServer().getPluginManager().registerEvents(joinListener, this);
        instance.getServer().getPluginManager().registerEvents(leaveListener, this);
        instance.getServer().getPluginManager().registerEvents(ruler, this);

        CommandAPI.instance().register(new ResetHallsCommand());
        CommandAPI.instance().register(new ZenumCommand());
        CommandAPI.instance().register(new StatsCommand());
        CommandAPI.instance().register(new ResetLevelCommand());
        CommandAPI.instance().register(new SetBoosterCommand());

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            woolManiaPlayerMap.put(onlinePlayer, new WoolManiaPlayer(onlinePlayer));
            gameScoreboard.createGameScoreboard(onlinePlayer);
        }
    }

    @Override
    public void onDisable() {
        NPCRemover.destoryNPCs(npcCreator);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            WoolMania.getInstance().getGameScoreboard().deleteGameScoreboard(onlinePlayer);
        }
    }

    //<editor-fold desc="Getter">
    public static WoolMania getInstance() {
        return instance;
    }

    public WoolManiaPlayer getPlayer(Player player) {
        return woolManiaPlayerMap.get(player);
    }

    public static WoolManiaPlayer getStaticPlayer(Player player) {
        return WoolMania.getInstance().woolManiaPlayerMap.get(player);
    }

    public ShopInventory getShopInventory() {
        return shopInventory;
    }

    public LevelXPHandler getLevelXPHandler() {
        return levelXPHandler;
    }

    public GameScoreboard getGameScoreboard() {
        return gameScoreboard;
    }
    //</editor-fold>


}
