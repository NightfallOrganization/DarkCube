/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle;

import eu.darkcube.minigame.woolbattle.command.*;
import eu.darkcube.minigame.woolbattle.game.Endgame;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.game.Lobby;
import eu.darkcube.minigame.woolbattle.listener.RegisterNotifyListener;
import eu.darkcube.minigame.woolbattle.map.*;
import eu.darkcube.minigame.woolbattle.mysql.MySQL;
import eu.darkcube.minigame.woolbattle.perk.PerkRegistry;
import eu.darkcube.minigame.woolbattle.team.DefaultTeamManager;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.team.TeamManager;
import eu.darkcube.minigame.woolbattle.translation.LanguageHelper;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.user.WBUserModifier;
import eu.darkcube.minigame.woolbattle.util.*;
import eu.darkcube.minigame.woolbattle.util.DependencyManager.Dependency;
import eu.darkcube.minigame.woolbattle.util.convertingrule.ConvertingRuleHelper;
import eu.darkcube.minigame.woolbattle.util.scheduler.SchedulerTask;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Objective;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Scoreboard;
import eu.darkcube.minigame.woolbattle.util.scoreboard.ScoreboardTeam;
import eu.darkcube.minigame.woolbattle.voidworldplugin.VoidWorldPluginLoader;
import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.commandapi.v3.BukkitCommandExecutor;
import eu.darkcube.system.commandapi.v3.CommandAPI;
import eu.darkcube.system.commandapi.v3.ICommandExecutor;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.loader.PluginClassLoader;
import eu.darkcube.system.loader.ReflectionClassLoader;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.userapi.data.UserModifier;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class WoolBattle extends DarkCubePlugin {

    @Deprecated
    private static WoolBattle instance;
    private final Collection<MapSize> knownMapSizes = new HashSet<>();
    private final PluginClassLoader pluginClassLoader;
    private final WoolBattleListeners listeners = new WoolBattleListeners();
    public String atall;
    public String atteam;
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
    private MySQL mysql;
//    private int maxPlayers;

    public WoolBattle() {
        super("woolbattle");
        Config.load(this);
        this.pluginClassLoader = new ReflectionClassLoader(this);
        WoolBattle.instance = this;
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public static void initScoreboard(Scoreboard sb, WBUser owner) {
        // Spectator is not included in "Team"
        Collection<Team> teams = new HashSet<>(WoolBattle.instance().teamManager().getTeams());
        teams.add(WoolBattle.instance().teamManager().getSpectator());
        for (Team t : teams) {
            ScoreboardTeam team = sb.createTeam(t.getType().getScoreboardTag());
            String s = LegacyComponentSerializer.legacySection().serialize(Component.text("", t.getPrefixStyle()));

            team.setPrefix(s);
        }
        for (ScoreboardObjective obj : ScoreboardObjective.values()) {
            Objective o = sb.createObjective(obj.getKey(), "dummy");
            o.setDisplayName(Message.getMessage(obj.getMessageKey(), owner.getLanguage()));
        }
        for (ObjectiveTeam team : ObjectiveTeam.values()) {
            ScoreboardTeam t = sb.createTeam(team.getKey());
            t.setPrefix(Message.getMessage(team.getMessagePrefix(), owner.getLanguage()));
            t.setSuffix(Message.getMessage(team.getMessageSuffix(), owner.getLanguage()));
        }
    }

    @Deprecated
    public static WoolBattle instance() {
        return WoolBattle.instance;
    }

    public static void registerListeners(Listener... listener) {
        for (Listener l : listener) {
            if (l instanceof RegisterNotifyListener) {
                ((RegisterNotifyListener) l).registered();
            }
            WoolBattle.instance().getServer().getPluginManager()
                    .registerEvents(l, WoolBattle.instance());
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

    @Override
    public void onLoad() {

//        TemplateStorage templateStorage = CloudNetDriver.getInstance().getLocalTemplateStorage();
//        try {
//            for (FileInfo fileInfo : templateStorage.listFiles(
//                    new ServiceTemplate("woolbattle", "2x1", "local"), false)) {
//                System.out.println(fileInfo.getName());
//            }
//            for (FileInfo fileInfo : templateStorage.listFiles(
//                    new ServiceTemplate("woolbattle", "2x1", "local"), "Abstract1-2x1", false)) {
//                System.out.println(fileInfo.getName());
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        new DependencyManager(this).loadDependencies(Dependency.values());

        LanguageHelper.load();

        ConvertingRuleHelper.load();

        // Create configs
        this.saveDefaultConfig("config");
        this.createConfig("config");

        this.saveDefaultConfig("mysql");
        this.createConfig("mysql");

        this.schedulers = new CopyOnWriteArrayList<>();
        this.perkRegistry = new PerkRegistry();

        // Load and connect mysql
        this.mysql = new MySQL();
        this.mysql.connect();

        // Load Name and Tab prefix
        this.atall = this.getConfig("config").getString("at_all");
        this.atteam = this.getConfig("config").getString("at_team");

        // Load teamtypes and teams
        this.teamManager = new DefaultTeamManager(this);

        // Init all GameStates
        this.lobby = new Lobby(this);
        this.ingame = new Ingame(this);
        this.endgame = new Endgame();

        VoidWorldPluginLoader.load();
    }

    @Override
    public void onEnable() {
        mapLoader = new CloudNetMapLoader();
        mapManager = new DefaultMapManager();

        this.userModifier = new WBUserModifier(this);
        UserAPI.getInstance().addModifier(userModifier);

        this.tickTask = new SchedulerTicker(this).runTaskTimer(this, 0, 1);

        listeners.registerAll(this);

        this.lobby.enable();

        loadCommands();

        new CloudNetUpdateScheduler().runTaskTimer(50);
    }

    @Override
    public void onDisable() {
        listeners.unregisterAll();
        UserAPI.getInstance().removeModifier(userModifier);
        this.tickTask.cancel();
        this.mysql.disconnect();
    }

    public GameData gameData() {
        return gameData;
    }

    public void gameData(GameData gameData) {
        this.gameData = gameData;
    }

    public int maxPlayers() {
        return 20;
    }

    public PluginClassLoader pluginClassLoader() {
        return this.pluginClassLoader;
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

    public final void sendMessage(Message msg, Function<ICommandExecutor, Object[]> function) {
        for (WBUser user : WBUser.onlineUsers()) {
            user.user().sendMessage(msg, function.apply(user.user()));
        }
        BukkitCommandExecutor e = new BukkitCommandExecutor(Bukkit.getConsoleSender());
        e.sendMessage(msg, function.apply(e));
    }

    private void loadCommands() {
        CommandAPI.getInstance().register(new CommandDisableStats());
        CommandAPI.getInstance().register(new CommandFix());
        CommandAPI.getInstance().register(new CommandIsStats());
        CommandAPI.getInstance().register(new CommandSetMap(this));
        CommandAPI.getInstance().register(new CommandSettings(this));
        CommandAPI.getInstance().register(new CommandTimer());
        CommandAPI.getInstance().register(new CommandTroll());
        CommandAPI.getInstance().register(new CommandVoteLifes());
        CommandAPI.getInstance().register(new CommandWoolBattle(this));

        CommandAPI.getInstance().register(new CommandSetTeam(this));
        CommandAPI.getInstance().register(new CommandSetLifes(this));
        CommandAPI.getInstance().register(new CommandRevive());
    }

    public PerkRegistry perkRegistry() {
        return perkRegistry;
    }
}
