/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceTemplate;
import de.dytanic.cloudnet.driver.template.FileInfo;
import de.dytanic.cloudnet.driver.template.TemplateStorage;
import eu.darkcube.minigame.woolbattle.command.*;
import eu.darkcube.minigame.woolbattle.game.Endgame;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.game.Lobby;
import eu.darkcube.minigame.woolbattle.listener.RegisterNotifyListener;
import eu.darkcube.minigame.woolbattle.map.CloudNetMapLoader;
import eu.darkcube.minigame.woolbattle.map.DefaultMapManager;
import eu.darkcube.minigame.woolbattle.map.MapLoader;
import eu.darkcube.minigame.woolbattle.map.MapManager;
import eu.darkcube.minigame.woolbattle.mysql.MySQL;
import eu.darkcube.minigame.woolbattle.perk.PerkRegistry;
import eu.darkcube.minigame.woolbattle.team.DefaultTeamManager;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.team.TeamManager;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.translation.LanguageHelper;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.user.WBUserModifier;
import eu.darkcube.minigame.woolbattle.util.CloudNetLink;
import eu.darkcube.minigame.woolbattle.util.DependencyManager;
import eu.darkcube.minigame.woolbattle.util.DependencyManager.Dependency;
import eu.darkcube.minigame.woolbattle.util.ObjectiveTeam;
import eu.darkcube.minigame.woolbattle.util.ScoreboardObjective;
import eu.darkcube.minigame.woolbattle.util.convertingrule.ConvertingRuleHelper;
import eu.darkcube.minigame.woolbattle.util.scheduler.SchedulerTask;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Objective;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Scoreboard;
import eu.darkcube.minigame.woolbattle.util.scoreboard.ScoreboardTeam;
import eu.darkcube.minigame.woolbattle.voidworldplugin.VoidWorldPluginLoader;
import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.commandapi.v3.BukkitCommandExecutor;
import eu.darkcube.system.commandapi.v3.CommandAPI;
import eu.darkcube.system.commandapi.v3.ILanguagedCommandExecutor;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.loader.PluginClassLoader;
import eu.darkcube.system.loader.ReflectionClassLoader;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.userapi.data.UserModifier;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WoolBattle extends DarkCubePlugin {

    @Deprecated
    private static WoolBattle instance;
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
    private PluginClassLoader pluginClassLoader;
    private BukkitTask tickTask;
    private MapLoader mapLoader;
    private WoolBattleListeners listeners = new WoolBattleListeners();
    private MySQL mysql;
    private int maxPlayers;

    public WoolBattle() {
        super("woolbattle");
        this.pluginClassLoader = new ReflectionClassLoader(this);
        WoolBattle.instance = this;
    }

    public static void initScoreboard(Scoreboard sb, WBUser owner) {
        // Spectator is not included in "Team"
        Collection<Team> teams = new HashSet<>(WoolBattle.instance().teamManager().getTeams());
        teams.add(WoolBattle.instance().teamManager().getSpectator());
        for (Team t : teams) {
            ScoreboardTeam team = sb.createTeam(t.getType().getScoreboardTag());
            String s = LegacyComponentSerializer.legacySection()
                    .serialize(Component.text(" ", t.getPrefixStyle()));
            s = s.replace(" ", "");
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

        TemplateStorage templateStorage = CloudNetDriver.getInstance().getLocalTemplateStorage();
        try {
            for (FileInfo fileInfo : templateStorage.listFiles(
                    new ServiceTemplate("woolbattle", "2x1", "local"), false)) {
                System.out.println(fileInfo.getName());
            }
            for (FileInfo fileInfo : templateStorage.listFiles(
                    new ServiceTemplate("woolbattle", "2x1", "local"), "Abstract1-2x1", false)) {
                System.out.println(fileInfo.getName());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        new DependencyManager(this).loadDependencies(Dependency.values());

        LanguageHelper.load();

        ConvertingRuleHelper.load();

        // Create configs
        this.saveDefaultConfig("teams");
        this.createConfig("teams");

        this.saveDefaultConfig("config");
        this.createConfig("config");

        this.saveDefaultConfig("mysql");
        this.createConfig("mysql");

        this.schedulers = new ArrayList<>();
        this.perkRegistry = new PerkRegistry();

        // Load and connect mysql
        this.mysql = new MySQL();
        this.mysql.connect();

        // Load Name and Tab prefix
        this.atall = this.getConfig("config").getString("at_all");
        this.atteam = this.getConfig("config").getString("at_team");

        // Load teamtypes and teams
        List<String> teams = this.getConfig("teams").getStringList("teams");
        List<TeamType> types =
                teams.stream().map(TeamType::deserialize).collect(Collectors.toList());
        AtomicReference<TeamType> spec = new AtomicReference<>();
        new ArrayList<>(types).forEach(type -> {
            if (type.getDisplayNameKey().equals("SPECTATOR")) {
                types.remove(type);
                spec.set(type);
            }
            if (!type.isEnabled()) {
                types.remove(type);
            }
        });
        this.teamManager = new DefaultTeamManager().loadSpectator(spec.get());
        if (spec.get() == null) {
            TeamType.getTypes().remove(TeamType.SPECTATOR);
            this.teamManager.getSpectator().getType().save();
        }
        types.forEach(type -> this.teamManager.getOrCreateTeam(type));

        this.maxPlayers = 0;
        for (Team team : this.teamManager.getTeams()) {
            this.maxPlayers += team.getType().getMaxPlayers();
        }

        // Init all GameStates
        this.lobby = new Lobby(this);
        this.ingame = new Ingame(this);
        this.endgame = new Endgame();

        VoidWorldPluginLoader.load();
    }

    @Override
    public void onDisable() {
        listeners.unregisterAll();
        UserAPI.getInstance().removeModifier(userModifier);
        this.tickTask.cancel();
        this.mysql.disconnect();
    }

    @Override
    public void onEnable() {
        mapLoader = new CloudNetMapLoader();

        // Load Maps
        mapManager = new DefaultMapManager();

        this.lobby.recalculateMap();
        this.lobby.recalculateEpGlitch();

        // Enable void worlds
        // Bukkit.getWorlds().forEach(world -> {
        // listenerVoidWorld.handle(new WorldInitEvent(world));
        // });

        // load user modifier
        this.userModifier = new WBUserModifier();
        UserAPI.getInstance().addModifier(userModifier);

        // Tick task for custom scheduler
        this.tickTask = new BukkitRunnable() {

            @Override
            public void run() {
                for (SchedulerTask s : new ArrayList<>(schedulers)) {
                    if (s.canExecute()) {
                        s.run();
                    }
                }
            }

        }.runTaskTimer(this, 0, 1);

        // Register default listeners
        listeners.registerAll(this);

        // Load worlds (At serverstart there are no worlds but if the plugin
        // gets
        // reloaded there are)
        // loadWorlds();

        // Enable Lobby
        this.lobby.enable();

        // Enable commands
        CommandAPI.getInstance().register(new CommandDisableStats());
        CommandAPI.getInstance().register(new CommandFix());
        CommandAPI.getInstance().register(new CommandIsStats());
        CommandAPI.getInstance().register(new CommandSetMap());
        CommandAPI.getInstance().register(new CommandSettings());
        CommandAPI.getInstance().register(new CommandTimer());
        CommandAPI.getInstance().register(new CommandTroll());
        CommandAPI.getInstance().register(new CommandVoteLifes());
        CommandAPI.getInstance().register(new CommandWoolBattle());

        CommandAPI.getInstance().register(new CommandSetTeam());
        CommandAPI.getInstance().register(new CommandSetLifes());
        CommandAPI.getInstance().register(new CommandRevive());

        new BukkitRunnable() {
            @Override
            public void run() {
                CloudNetLink.update();
            }
        }.runTaskTimerAsynchronously(WoolBattle.instance(), 10, 10);
    }

    public GameData gameData() {
        return gameData;
    }

    public void gameData(GameData gameData) {
        this.gameData = gameData;
    }

    public int maxPlayers() {
        return this.maxPlayers;
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

    public final void sendMessage(Message msg, Object... replacements) {
        this.sendMessage(msg, (u) -> replacements);
    }

    public final void sendMessage(Message msg,
                                  Function<ILanguagedCommandExecutor, Object[]> function) {
        for (WBUser user : WBUser.onlineUsers()) {
            user.user().sendMessage(msg, function.apply(user.user()));
        }
        BukkitCommandExecutor e = new BukkitCommandExecutor(Bukkit.getConsoleSender());
        e.sendMessage(msg, function.apply(e));
    }

    public PerkRegistry perkRegistry() {
        return perkRegistry;
    }
}
