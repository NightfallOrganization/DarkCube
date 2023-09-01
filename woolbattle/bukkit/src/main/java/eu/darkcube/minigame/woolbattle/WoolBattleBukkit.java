/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle;

import eu.darkcube.minigame.woolbattle.api.LobbySystemLinkImpl;
import eu.darkcube.minigame.woolbattle.api.WoolBattleApiImpl;
import eu.darkcube.minigame.woolbattle.game.Endgame;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.game.Lobby;
import eu.darkcube.minigame.woolbattle.listener.RegisterNotifyListener;
import eu.darkcube.minigame.woolbattle.map.*;
import eu.darkcube.minigame.woolbattle.perk.PerkRegistry;
import eu.darkcube.minigame.woolbattle.team.DefaultTeamManager;
import eu.darkcube.minigame.woolbattle.team.TeamManager;
import eu.darkcube.minigame.woolbattle.translation.LanguageHelper;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.user.WBUserModifier;
import eu.darkcube.minigame.woolbattle.util.SchedulerTicker;
import eu.darkcube.minigame.woolbattle.util.convertingrule.ConvertingRuleHelper;
import eu.darkcube.minigame.woolbattle.util.scheduler.SchedulerTask;
import eu.darkcube.minigame.woolbattle.voidworldplugin.VoidWorldPluginLoader;
import eu.darkcube.system.bukkit.DarkCubePlugin;
import eu.darkcube.system.bukkit.commandapi.BukkitCommandExecutor;
import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.userapi.UserModifier;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class WoolBattleBukkit extends DarkCubePlugin {

    @Deprecated
    private static WoolBattleBukkit instance;
    private final Collection<MapSize> knownMapSizes = new HashSet<>();
    private final WoolBattleListeners listeners = new WoolBattleListeners(this);
    private final WoolBattleCommands commands = new WoolBattleCommands(this);
    public String atall;
    public String atteam;
    private LobbySystemLinkImpl lobbySystemLink;
    private PerkRegistry perkRegistry;
    private UserModifier userModifier;
    private Lobby lobby;
    private Ingame ingame;
    private Endgame endgame;
    private TeamManager teamManager;
    private MapManager mapManager;
    private Collection<SchedulerTask> schedulers;
    private GameData gameData;
    private BukkitTask tickTask;
    private MapLoader mapLoader;

    public WoolBattleBukkit() {
        super("woolbattle");
        new WoolBattleApiImpl(this); // This is all we need to do to initialize the Api
        Config.load(this);
        WoolBattleBukkit.instance = this;
    }

    @Deprecated public static WoolBattleBukkit instance() {
        return WoolBattleBukkit.instance;
    }

    public static void registerListeners(Listener... listener) {
        for (Listener l : listener) {
            if (l instanceof RegisterNotifyListener) {
                ((RegisterNotifyListener) l).registered();
            }
            instance.getServer().getPluginManager().registerEvents(l, instance);
        }
    }

    public static void unregisterListeners(Listener... listener) {
        for (Listener l : listener) {
            if (l instanceof RegisterNotifyListener) {
                ((RegisterNotifyListener) l).unregistered();
            }
            HandlerList.unregisterAll(l);
        }
    }

    @Override public void onLoad() {
        this.lobbySystemLink = new LobbySystemLinkImpl(this);
        LanguageHelper.load();

        ConvertingRuleHelper.load();

        // Create configs
        this.saveDefaultConfig("config");
        this.createConfig("config");

        this.saveDefaultConfig("mysql");
        this.createConfig("mysql");

        this.schedulers = new CopyOnWriteArrayList<>();
        this.perkRegistry = new PerkRegistry(this);

        // Load Name and Tab prefix
        this.atall = this.getConfig("config").getString("at_all");
        this.atteam = this.getConfig("config").getString("at_team");

        // Load teamtypes and teams
        this.teamManager = new DefaultTeamManager(this);

        // Init all GameStates
        this.lobby = new Lobby(this);
        this.ingame = new Ingame(this);
        this.endgame = new Endgame(this);

        VoidWorldPluginLoader.load();
    }

    @Override public void onEnable() {
        mapLoader = new CloudNetMapLoader(this);
        mapManager = new DefaultMapManager();

        this.userModifier = new WBUserModifier(this);
        UserAPI.instance().addModifier(userModifier);

        this.tickTask = new SchedulerTicker(this).runTaskTimer(this, 0, 1);

        listeners.registerAll(this);

        lobby.enable();

        commands.enableAll();

        lobbySystemLink.enable();
    }

    @Override public void onDisable() {
        lobbySystemLink.disable();
        commands.disableAll();
        listeners.unregisterAll();
        UserAPI.instance().removeModifier(userModifier);
        this.tickTask.cancel();
    }

    public LobbySystemLinkImpl lobbySystemLink() {
        return lobbySystemLink;
    }

    public GameData gameData() {
        return gameData;
    }

    public void gameData(GameData gameData) {
        this.gameData = gameData;
    }

    public int maxPlayers() {
        return lobby.maxPlayerCount();
    }

    public Lobby lobby() {
        return this.lobby;
    }

    public Ingame ingame() {
        return this.ingame;
    }

    public Endgame endgame() {
        return this.endgame;
    }

    public TeamManager teamManager() {
        return this.teamManager;
    }

    public Collection<SchedulerTask> schedulers() {
        return this.schedulers;
    }

    public MapManager mapManager() {
        return this.mapManager;
    }

    public MapLoader mapLoader() {
        return mapLoader;
    }

    public Collection<MapSize> knownMapSizes() {
        return knownMapSizes;
    }

    public final void sendMessage(Message msg, Object... replacements) {
        this.sendMessage(msg, (u) -> replacements);
    }

    public final void sendMessage(Message msg, Function<CommandExecutor, Object[]> function) {
        for (WBUser user : WBUser.onlineUsers()) {
            user.user().sendMessage(msg, function.apply(user.user()));
        }
        CommandExecutor e = BukkitCommandExecutor.create(Bukkit.getConsoleSender());
        e.sendMessage(msg, function.apply(e));
    }

    public PerkRegistry perkRegistry() {
        return perkRegistry;
    }
}
