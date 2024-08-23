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
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.woolmania.commands.OpenTeleporterCommand;
import eu.darkcube.system.woolmania.commands.ResetHallsCommand;
import eu.darkcube.system.woolmania.commands.ResetLevelCommand;
import eu.darkcube.system.woolmania.commands.ResetSoundsCommand;
import eu.darkcube.system.woolmania.commands.SetBoosterCommand;
import eu.darkcube.system.woolmania.commands.StatsCommand;
import eu.darkcube.system.woolmania.commands.zenum.ZenumCommand;
import eu.darkcube.system.woolmania.enums.hall.Hall;
import eu.darkcube.system.woolmania.handler.LevelXPHandler;
import eu.darkcube.system.woolmania.inventory.ability.AbilityInventory;
import eu.darkcube.system.woolmania.inventory.ability.AbilityInventoryShop;
import eu.darkcube.system.woolmania.inventory.shop.ShopInventory;
import eu.darkcube.system.woolmania.inventory.shop.ShopInventoryFood;
import eu.darkcube.system.woolmania.inventory.shop.ShopInventoryGadgets;
import eu.darkcube.system.woolmania.inventory.shop.ShopInventorySound;
import eu.darkcube.system.woolmania.inventory.smith.SmithInventory;
import eu.darkcube.system.woolmania.inventory.smith.SmithInventoryRepair;
import eu.darkcube.system.woolmania.inventory.smith.SmithInventoryShopItems;
import eu.darkcube.system.woolmania.inventory.teleporter.TeleportHallsInventory;
import eu.darkcube.system.woolmania.inventory.teleporter.TeleportInventory;
import eu.darkcube.system.woolmania.listener.BlockBreakListener;
import eu.darkcube.system.woolmania.listener.JoinListener;
import eu.darkcube.system.woolmania.listener.LeaveListener;
import eu.darkcube.system.woolmania.listener.NPCListeners;
import eu.darkcube.system.woolmania.listener.PlayerMoveListener;
import eu.darkcube.system.woolmania.listener.PlayerTeleportListener;
import eu.darkcube.system.woolmania.listener.WoolGrenadeListener;
import eu.darkcube.system.woolmania.manager.NPCManager;
import eu.darkcube.system.woolmania.manager.WorldManager;
import eu.darkcube.system.woolmania.npc.NPCCreator;
import eu.darkcube.system.woolmania.npc.NPCRemover;
import eu.darkcube.system.woolmania.registry.WoolRegistry;
import eu.darkcube.system.woolmania.util.GameScoreboard;
import eu.darkcube.system.woolmania.util.Ruler;
import eu.darkcube.system.woolmania.util.WoolManiaPlayer;
import eu.darkcube.system.woolmania.util.WoolRegenerationTimer;
import eu.darkcube.system.woolmania.util.message.LanguageHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WoolMania extends DarkCubePlugin {
    private static WoolMania instance;
    private NPCManager npcManager;
    private NPCCreator npcCreator;
    private ShopInventory shopInventory;
    private ShopInventorySound shopInventorySound;
    private ShopInventoryFood shopInventoryFood;
    private ShopInventoryGadgets shopInventoryGadgets;
    private TeleportInventory teleportInventory;
    public TeleportHallsInventory teleportHallsInventory;
    public SmithInventory smithInventory;
    public SmithInventoryShopItems smithInventoryShopItems;
    public SmithInventoryRepair smithInventoryRepair;
    public AbilityInventory abilityInventory;
    public AbilityInventoryShop abilityInventoryShop;
    private LevelXPHandler levelXPHandler;
    private WoolRegistry woolRegistry;
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
        shopInventorySound = new ShopInventorySound();
        shopInventoryFood = new ShopInventoryFood();
        shopInventoryGadgets = new ShopInventoryGadgets();
        teleportInventory = new TeleportInventory();
        teleportHallsInventory = new TeleportHallsInventory();
        smithInventory = new SmithInventory();
        smithInventoryShopItems = new SmithInventoryShopItems();
        smithInventoryRepair = new SmithInventoryRepair();
        abilityInventory = new AbilityInventory();
        abilityInventoryShop = new AbilityInventoryShop();
        woolRegistry = new WoolRegistry();
        levelXPHandler = new LevelXPHandler();
        NPCListeners.register(npcManager, npcCreator);
        npcCreator.createNPC();

        var teleportListener = new PlayerTeleportListener();
        var joinListener = new JoinListener();
        var leaveListener = new LeaveListener();
        var blockBreakListener = new BlockBreakListener();
        var throwingListener = new WoolGrenadeListener();
        var playerMoveListener = new PlayerMoveListener();
        var ruler = new Ruler();

        instance.getServer().getPluginManager().registerEvents(teleportListener, this);
        instance.getServer().getPluginManager().registerEvents(throwingListener, this);
        instance.getServer().getPluginManager().registerEvents(playerMoveListener, this);
        instance.getServer().getPluginManager().registerEvents(blockBreakListener, this);
        instance.getServer().getPluginManager().registerEvents(joinListener, this);
        instance.getServer().getPluginManager().registerEvents(leaveListener, this);
        instance.getServer().getPluginManager().registerEvents(ruler, this);

        CommandAPI.instance().register(new ResetHallsCommand());
        CommandAPI.instance().register(new ZenumCommand());
        CommandAPI.instance().register(new StatsCommand());
        CommandAPI.instance().register(new ResetLevelCommand());
        CommandAPI.instance().register(new SetBoosterCommand());
        CommandAPI.instance().register(new ResetSoundsCommand());
        CommandAPI.instance().register(new OpenTeleporterCommand());

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            WoolManiaPlayer player = new WoolManiaPlayer(onlinePlayer);
            woolManiaPlayerMap.put(onlinePlayer, player);
            gameScoreboard.createGameScoreboard(onlinePlayer);
            Location location = onlinePlayer.getLocation();
            for (var hall : Hall.values()) {
                if (hall.getHallArea().isWithinBounds(location)) {
                    player.setHall(hall);
                    break;
                }
            }
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

    public WoolManiaPlayer getPlayer(@NotNull Player player) {
        return woolManiaPlayerMap.get(player);
    }

    public static WoolManiaPlayer getStaticPlayer(@NotNull Player player) {
        return WoolMania.getInstance().woolManiaPlayerMap.get(player);
    }

    public ShopInventory getShopInventory() {
        return shopInventory;
    }

    public WoolRegistry getWoolRegistry() {
        return woolRegistry;
    }

    public ShopInventorySound getShopInventorySound() {
        return shopInventorySound;
    }

    public ShopInventoryFood getShopInventoryFood() {
        return shopInventoryFood;
    }

    public ShopInventoryGadgets getShopInventoryGadgets() {
        return shopInventoryGadgets;
    }

    public TeleportInventory getTeleportInventory() {
        return teleportInventory;
    }

    public TeleportHallsInventory getTeleportHallsInventory() {
        return teleportHallsInventory;
    }

    public SmithInventory getSmithInventory() {
        return smithInventory;
    }

    public SmithInventoryShopItems getSmithInventoryShopItems() {
        return smithInventoryShopItems;
    }

    public SmithInventoryRepair getSmithInventoryRepair() {
        return smithInventoryRepair;
    }

    public AbilityInventory getAbilityInventory() {
        return abilityInventory;
    }

    public AbilityInventoryShop getAbilityInventoryShop() {
        return abilityInventoryShop;
    }

    public LevelXPHandler getLevelXPHandler() {
        return levelXPHandler;
    }

    public GameScoreboard getGameScoreboard() {
        return gameScoreboard;
    }
    //</editor-fold>

}
