/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.provider.CloudServiceProvider;
import eu.cloudnetservice.driver.registry.ServiceRegistry;
import eu.cloudnetservice.modules.bridge.player.PlayerManager;
import eu.darkcube.system.bukkit.Plugin;
import eu.darkcube.system.bukkit.commandapi.CommandAPI;
import eu.darkcube.system.lobbysystem.command.CommandLobbysystem;
import eu.darkcube.system.lobbysystem.command.lobbysystem.CommandBuild;
import eu.darkcube.system.lobbysystem.gadget.listener.ListenerGrapplingHook;
import eu.darkcube.system.lobbysystem.gadget.listener.ListenerHookArrow;
import eu.darkcube.system.lobbysystem.jumpandrun.JaRManager;
import eu.darkcube.system.lobbysystem.listener.ListenerBlock;
import eu.darkcube.system.lobbysystem.listener.ListenerBoostPlate;
import eu.darkcube.system.lobbysystem.listener.ListenerBorder;
import eu.darkcube.system.lobbysystem.listener.ListenerConnectorNPC;
import eu.darkcube.system.lobbysystem.listener.ListenerDailyReward;
import eu.darkcube.system.lobbysystem.listener.ListenerDamage;
import eu.darkcube.system.lobbysystem.listener.ListenerDoublejump;
import eu.darkcube.system.lobbysystem.listener.ListenerFish;
import eu.darkcube.system.lobbysystem.listener.ListenerFisher;
import eu.darkcube.system.lobbysystem.listener.ListenerGadget;
import eu.darkcube.system.lobbysystem.listener.ListenerInteract;
import eu.darkcube.system.lobbysystem.listener.ListenerInventoryClick;
import eu.darkcube.system.lobbysystem.listener.ListenerInventoryClose;
import eu.darkcube.system.lobbysystem.listener.ListenerItemDropPickup;
import eu.darkcube.system.lobbysystem.listener.ListenerJoin;
import eu.darkcube.system.lobbysystem.listener.ListenerLobbyRoundWalk;
import eu.darkcube.system.lobbysystem.listener.ListenerLobbySwitcher;
import eu.darkcube.system.lobbysystem.listener.ListenerMinigameServer;
import eu.darkcube.system.lobbysystem.listener.ListenerPServer;
import eu.darkcube.system.lobbysystem.listener.ListenerPhysics;
import eu.darkcube.system.lobbysystem.listener.ListenerQuit;
import eu.darkcube.system.lobbysystem.listener.ListenerScoreboard;
import eu.darkcube.system.lobbysystem.listener.ListenerSpawnRoundWalk;
import eu.darkcube.system.lobbysystem.listener.ListenerSumoNPC;
import eu.darkcube.system.lobbysystem.listener.ListenerWeather;
import eu.darkcube.system.lobbysystem.listener.ListenerWoolBattleModernNPC;
import eu.darkcube.system.lobbysystem.listener.ListenerWoolBattleNPC;
import eu.darkcube.system.lobbysystem.npc.ConnectorNPC;
import eu.darkcube.system.lobbysystem.npc.DailyRewardNPC;
import eu.darkcube.system.lobbysystem.npc.FisherNPC;
import eu.darkcube.system.lobbysystem.npc.NPCManagement;
import eu.darkcube.system.lobbysystem.npc.SumoNPC;
import eu.darkcube.system.lobbysystem.npc.WoolBattleModernNPC;
import eu.darkcube.system.lobbysystem.npc.WoolBattleNPC;
import eu.darkcube.system.lobbysystem.pserver.PServerSupport;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.DataManager;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.lobbysystem.util.ParticleEffect;
import eu.darkcube.system.lobbysystem.util.SkullCache;
import eu.darkcube.system.lobbysystem.util.gameregistry.GameRegistry;
import eu.darkcube.system.lobbysystem.util.server.ServerManager;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.Language;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

public class Lobby extends Plugin {

    private static Lobby instance;
    private ServiceRegistry serviceRegistry = InjectionLayer.boot().instance(ServiceRegistry.class);
    private CloudServiceProvider cloudServiceProvider = InjectionLayer.boot().instance(CloudServiceProvider.class);
    private DataManager dataManager;
    private NPCManagement npcManagement;
    private NPCManagement.NPC woolbattleNpc;
    private NPCManagement.NPC woolbattleModernNpc;
    private NPCManagement.NPC sumoNpc;
    private NPCManagement.NPC fisherNpc;
    private NPCManagement.NPC dailyRewardNpc;
    private JaRManager jaRManager;
    private GameRegistry gameRegistry;
    private ServerManager serverManager;

    public Lobby() {
        super("LobbySystem");
        Lobby.instance = this;
    }

    public static Lobby getInstance() {
        return Lobby.instance;
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).map(UserAPI.instance()::user).map(UserWrapper::fromUser).forEach(LobbyUser::stopJaR);
        if (PServerSupport.isSupported()) {
            SkullCache.unregister();
        }
        ConnectorNPC.clear();

        var eventManager = InjectionLayer.boot().instance(EventManager.class);
        var ext = InjectionLayer.ext();
        if (PServerSupport.isSupported()) {
            eventManager.unregisterListener(ext.instance(ListenerPServer.class));
        }
    }

    @Override
    public void onEnable() {
        this.npcManagement = new NPCManagement(this);
        this.serverManager = new ServerManager(this);
        this.gameRegistry = new GameRegistry(this);
        this.serverManager.registerListener(new ConnectorNPC.UpdateListener());

        UserWrapper userWrapper = new UserWrapper();
        UserAPI.instance().addModifier(userWrapper);
        userWrapper.beginMigration();

        PServerSupport.init();
        // Load all messages
        try {
            Language.GERMAN.registerLookup(this.getClassLoader(), "messages_de.properties", s -> Message.KEY_PREFIX + s);
            Language.ENGLISH.registerLookup(this.getClassLoader(), "messages_en.properties", s -> Message.KEY_PREFIX + s);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        List<String> languageEntries = new ArrayList<>();
        languageEntries.addAll(Arrays.stream(Message.values()).map(Message::key).toList());
        languageEntries.addAll(Arrays.stream(Item.values()).map(i -> Message.PREFIX_ITEM + i.getKey()).toList());
        languageEntries.addAll(Arrays.stream(Item.values()).filter(i -> !i.getBuilder().lore().isEmpty()).map(i -> Message.PREFIX_ITEM + Message.PREFIX_LORE + i.getKey()).toList());
        Language.validateEntries(languageEntries.toArray(new String[0]), s -> Message.KEY_PREFIX + s);

        this.dataManager = new DataManager(this);
        this.jaRManager = new JaRManager();
        this.woolbattleNpc = WoolBattleNPC.create();
        this.woolbattleModernNpc = WoolBattleModernNPC.create();
        this.dailyRewardNpc = DailyRewardNPC.create();
        this.fisherNpc = FisherNPC.create();
        this.sumoNpc = SumoNPC.create();

        CommandAPI.instance().register(new CommandBuild());
        CommandAPI.instance().register(new CommandLobbysystem(this));

        for (World world : Bukkit.getWorlds()) {
            world.setGameRuleValue("randomTickSpeed", "0");
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setFullTime(6000);
        }

        new ListenerSpawnRoundWalk(this);
        new ListenerLobbyRoundWalk(this);
        new ListenerJoin();
        new ListenerScoreboard();
        new ListenerQuit();
        new ListenerBlock();
        new ListenerDoublejump();
        new ListenerDamage();
        new ListenerInventoryClick();
        new ListenerInventoryClose();
        new ListenerInteract();
        new ListenerLobbySwitcher(this);
        new ListenerWoolBattleNPC();
        new ListenerWoolBattleModernNPC();
        new ListenerSumoNPC();
        new ListenerConnectorNPC(this);
        new ListenerMinigameServer(this);
        new ListenerItemDropPickup();
        new ListenerFish();
        new ListenerGadget();
        new ListenerDailyReward();
        new ListenerFisher();
        new ListenerHookArrow();
        new ListenerGrapplingHook();
        new ListenerBoostPlate();
        new ListenerWeather();
        new ListenerPhysics();
        new ListenerBorder();

        if (PServerSupport.isSupported()) {
            SkullCache.register();
            var eventManager = InjectionLayer.boot().instance(EventManager.class);
            eventManager.registerListener(ListenerPServer.class);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!dataManager.isWinter()) {
                    return;
                }
                for (Player p : Bukkit.getOnlinePlayers()) {
                    ParticleEffect.FIREWORKS_SPARK.display(20, 20, 20, 0F, 200, p.getLocation(), p);
                    ParticleEffect.SNOW_SHOVEL.display(20, 20, 20, 0F, 100, p.getLocation(), p);
                }
            }
        }.runTaskTimer(this, 1, 1);
        for (World world : Bukkit.getWorlds()) {
            world.setStorm(getDataManager().isWinter());
            world.setThundering(false);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                ConnectorNPC.load();
            }
        }.runTask(this);
    }

    public void savePlayer(LobbyUser user) {
        user.player().ifPresent(p -> {
            user.setLastPosition(p.getLocation());
            user.setSelectedSlot(p.getInventory().getHeldItemSlot());
        });
    }

    public void setupPlayer(LobbyUser user) {
        Player p = user.asPlayer();
        if (p == null) return;
        setItems(user);

        DataManager dataManager = getDataManager();
        Location spawn = dataManager.getSpawn();
        p.setGameMode(GameMode.SURVIVAL);
        p.setAllowFlight(user.getCurrentJaR() == null);
        p.setCompassTarget(spawn.getBlock().getLocation());
        p.setExhaustion(0);
        p.setSaturation(0);
        p.setFireTicks(0);
        p.setMaxHealth(20);
        p.setHealth(20);
        p.setLevel(0);
        p.setPlayerWeather(WeatherType.CLEAR);
        p.setFoodLevel(20);
        p.setExp(1);
        PlayerInventory inv = p.getInventory();
        inv.setHeldItemSlot(Math.max(Math.min(user.getSelectedSlot(), 8), 0));

        Location l = user.getLastPosition();
        if (!dataManager.getBorder().isInside(l)) {
            l = dataManager.getSpawn();
        } else if (l.add(0, 1, 0).getBlock().getType().isSolid()) {
            l = dataManager.getSpawn();
        }
        p.teleport(l);
    }

    public void setItems(LobbyUser user) {
        Player p = user.asPlayer();
        if (p == null) return;
        User u = user.user();
        PlayerInventory inv = p.getInventory();
        inv.clear();
        if (user.getCurrentJaR() == null) {
            inv.setItem(0, Item.INVENTORY_COMPASS.getItem(u));
            inv.setItem(1, Item.INVENTORY_SETTINGS.getItem(u));
            inv.setItem(4, Item.byGadget(user.getGadget()).getItem(u));
            user.getGadget().fillExtraItems(user);
            try {
                if (PServerSupport.isSupported()) {
                    inv.setItem(6, Item.PSERVER_MAIN_ITEM.getItem(u));
                }
            } catch (Exception ignored) {
            }

            inv.setItem(7, Item.INVENTORY_GADGET.getItem(u));
            inv.setItem(8, Item.INVENTORY_LOBBY_SWITCHER.getItem(u));
        } else {
            inv.setItem(8, Item.JUMPANDRUN_STOP.getItem(u));
        }
    }

    public GameRegistry gameRegistry() {
        return gameRegistry;
    }

    public NPCManagement npcManagement() {
        return npcManagement;
    }

    public NPCManagement.NPC getWoolBattleNPC() {
        return this.woolbattleNpc;
    }

    public NPCManagement.NPC getWoolBattleModernNPC() {
        return this.woolbattleModernNpc;
    }

    public NPCManagement.NPC getSumoNPC() {
        return this.sumoNpc;
    }

    public NPCManagement.NPC getDailyRewardNpc() {
        return this.dailyRewardNpc;
    }

    public NPCManagement.NPC getFisherNpc() {
        return this.fisherNpc;
    }

    public DataManager getDataManager() {
        return this.dataManager;
    }

    public JaRManager getJaRManager() {
        return this.jaRManager;
    }

    public ServerManager serverManager() {
        return serverManager;
    }

    public ServiceRegistry serviceRegistry() {
        return serviceRegistry;
    }

    public PlayerManager playerManager() {
        return serviceRegistry.firstProvider(PlayerManager.class);
    }

    public CloudServiceProvider cloudServiceProvider() {
        return cloudServiceProvider;
    }

    @Override
    public String getCommandPrefix() {
        return "§aLobbySystem";
    }
}
