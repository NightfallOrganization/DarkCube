/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.lobbysystem.npc;

import com.github.juliarn.npclib.api.flag.NpcFlag;
import com.github.unldenis.hologram.line.Line;
import com.github.unldenis.hologram.line.TextLine;
import com.github.unldenis.hologram.placeholder.Placeholders;
import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.event.events.service.CloudServiceLifecycleChangeEvent;
import eu.cloudnetservice.driver.event.events.service.CloudServiceUpdateEvent;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.provider.CloudServiceProvider;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import eu.cloudnetservice.driver.service.ServiceLifeCycle;
import eu.darkcube.system.DarkCubeServiceProperty;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.npc.ConnectorNPC.CurrentServer.Info;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.lobbysystem.util.MinigameServerSortingInfo;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.AsyncExecutor;
import eu.darkcube.system.util.GameState;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConnectorNPC {

    private static final Key npcsKey = new Key(Lobby.getInstance(), "npcs");
    private static final Collection<ConnectorNPC> npcs = ConcurrentHashMap.newKeySet();
    private static final NpcFlag<ConnectorNPC> flagConnectorNpc = NpcFlag.flag("connectorNpc", null);
    private static final PersistentDataType<List<String>> strings = PersistentDataTypes.list(PersistentDataTypes.STRING);
    private static final AtomicInteger uidCounter = new AtomicInteger();
    private static final PersistentDataType<List<ConnectorNPC>> type = PersistentDataTypes.list(new PersistentDataType<ConnectorNPC>() {
        @Override public ConnectorNPC deserialize(Document doc, String key) {
            Document d = doc.readDocument(key);
            int id = d.getInt("id");
            String taskName = d.getString("task");
            List<String> permissions;
            if (d.contains("permissions")) {
                permissions = strings.deserialize(d, "permissions");
                for (String permission : permissions) {
                    if (Bukkit.getPluginManager().getPermission(permission) == null) {
                        Bukkit.getPluginManager().addPermission(new Permission(permission));
                    }
                }
            } else {
                permissions = new ArrayList<>();
            }
            Location loc = PersistentDataTypes.LOCATION.deserialize(d, "location");
            return new ConnectorNPC(taskName, id, loc, permissions);
        }

        @Override public void serialize(Document.Mutable doc, String key, ConnectorNPC data) {
            Document.Mutable d = Document.newJsonDocument();
            d.append("task", data.taskName);
            strings.serialize(d, "permissions", data.permissions);
            d.append("id", data.id);
            PersistentDataTypes.LOCATION.serialize(d, "location", data.location);
            doc.append(key, d);
        }

        @Override public ConnectorNPC clone(ConnectorNPC object) {
            return new ConnectorNPC(object.taskName, object.id, object.location, object.permissions);
        }
    });

    static {
        InjectionLayer.boot().instance(EventManager.class).registerListener(new Listener());
    }

    private final Location location;
    private final String taskName;
    private final List<String> permissions = new ArrayList<>();
    private final int id;
    private final CurrentServer currentServer = new CurrentServer();
    private final Collection<Player> connectingPlayers = ConcurrentHashMap.newKeySet();
    private int uid = -1;
    private NPCManagement.NPC npc;
    private CHologram hologram;
    private NPCKnockbackThread npcKnockbackThread;

    private ConnectorNPC(String taskName, int id, Location location, List<String> permissions) {
        this.taskName = taskName;
        this.id = id;
        this.location = location;
        this.permissions.addAll(permissions);
    }

    public ConnectorNPC(String taskName, Location location) {
        this.taskName = taskName;
        this.location = location;
        int id = 1;
        w:
        while (true) {
            for (ConnectorNPC npc : npcs) {
                if (npc.taskName.equals(taskName) && npc.id == id) {
                    id++;
                    continue w;
                }
            }
            break;
        }
        this.id = id;
    }

    public static void save() {
        Lobby.getInstance().persistentDataStorage().set(npcsKey, type, new ArrayList<>(npcs));
    }

    public static void clear() {
        npcs.forEach(ConnectorNPC::hide);
    }

    public static void load() {
        Lobby.getInstance().persistentDataStorage().get(npcsKey, type, ArrayList::new).forEach(ConnectorNPC::show);
    }

    public static ConnectorNPC get(String key) {
        return npcs.stream().filter(n -> n.key().equals(key)).findFirst().orElse(null);
    }

    public static ConnectorNPC get(NPCManagement.NPC npc) {
        return npc.flagValue(flagConnectorNpc).orElse(null);
    }

    public static Stream<ConnectorNPC> npcStream() {
        return npcs.stream();
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public String key() {
        return taskName + "-" + id;
    }

    public void connect(Player player) {
        for (Player p : connectingPlayers) {
            if (p == player) // Cooldown of 1 sec
                return;
        }
        connectingPlayers.add(player);
        new BukkitRunnable() {
            @Override public void run() {
                connectingPlayers.remove(player);
            }
        }.runTaskLater(Lobby.getInstance(), 20);
        new BukkitRunnable() {
            @Override public void run() {
                User user = UserAPI.getInstance().getUser(player);
                Info c = currentServer.server;
                if (c == null) return;
                Lobby.getInstance().playerManager().playerExecutor(user.getUniqueId()).connect(c.server.serviceId().name());
            }
        }.runTask(Lobby.getInstance());
    }

    public void show() {
        npcs.add(this);
        if (location.getWorld() == null) location.setWorld(Bukkit.getWorlds().get(0));
        NPCManagement.Builder builder = Lobby.getInstance().npcManagement().builder();
        if (uid == -1) uid = uidCounter.incrementAndGet();
        builder.profileHelper().skin(new DailyRewardNPC.DailyRewardSkin()).name("§8NPC-" + uid);
        builder.trackingRule((n, player) -> {
            if (!permissions.isEmpty()) {
                for (String permission : permissions) {
                    if (player.hasPermission(permission)) return true;
                }
                return false;
            }
            return true;
        });
        npc = builder.build();
        npc.flagValue(flagConnectorNpc, this);
        npc.location(location);
        hologram = createHologram(location);
        (npcKnockbackThread = new NPCKnockbackThread(npc)).runTaskTimer(Lobby.getInstance(), 5, 5);
        AsyncExecutor.service().submit(currentServer::query);
    }

    private void updateHologram() {
        hologram.update();
    }

    public void hide() {
        if (npc == null) return;
        npcs.remove(this);
        if (Lobby.getInstance().isEnabled()) {
            npc.destroy();
        }
        npcKnockbackThread.cancel();
        npc = null;
        hologram = null;
    }

    private CHologram createHologram(Location location) {
//        Hologram hologram = new Hologram(Lobby.getInstance(), location, new TextBlockStandardLoader());
//        Placeholders placeholders = hologramPlaceholders();
//        TextLine line = new TextLine(new Line(Lobby.getInstance()), "test");
//        TextLine line2 = new TextLine(new Line(Lobby.getInstance()), "%%online%%", placeholders);
//        TextLine line3 = new TextLine(new Line(Lobby.getInstance()), "%%description%%", placeholders);
//        hologram.load(line, line2, line3);
        CHologram hologram = new CHologram(hologramPlaceholders());
        hologram.location(location);
        return hologram;
    }

    private Placeholders hologramPlaceholders() {
        Placeholders h = new Placeholders(Placeholders.STRING);
        h.add("%%online%%", p -> {
            int online, maxplayers;
            Info info = currentServer.server;
            if (info != null) {
                online = info.minigame.onPlayers;
                maxplayers = info.minigame.maxPlayers;
            } else {
                return Message.CONNECTOR_NPC_SERVER_STARTING.getMessageString(UserAPI.getInstance().getUser(p));
            }
            return Message.CONNECTOR_NPC_SERVER_ONLINE.getMessageString(UserAPI
                    .getInstance()
                    .getUser(p), online, maxplayers == -1 ? 100 : maxplayers);
        });
        h.add("%%description%%", p -> {
            Info server = currentServer.server;
            if (server != null) {
                return Message.CONNECTOR_NPC_SERVER_DESCRIPTION.getMessageString(UserAPI.getInstance().getUser(p), server.motd);
            }
            return " ";
        });
        return h;
    }

    public CHologram hologram() {
        return hologram;
    }

    public static class Listener {
        private static final Map<UUID, ServiceInfoSnapshot> services = new ConcurrentHashMap<>();

        static {
            for (ServiceInfoSnapshot service : InjectionLayer.boot().instance(CloudServiceProvider.class).services()) {
                services.put(service.serviceId().uniqueId(), service);
            }
        }

        @EventListener public void handle(CloudServiceUpdateEvent event) {
            ServiceInfoSnapshot service = event.serviceInfo();
            services.computeIfPresent(service.serviceId().uniqueId(), (uuid, serviceInfoSnapshot) -> {
                if (!AsyncExecutor.service().isShutdown()) {
                    AsyncExecutor.service().submit(() -> npcs.forEach(n -> n.currentServer.query()));
                }
                return service;
            });
        }

        @EventListener public void handle(CloudServiceLifecycleChangeEvent event) {
            if (event.newLifeCycle() == ServiceLifeCycle.RUNNING) {
                services.put(event.serviceInfo().serviceId().uniqueId(), event.serviceInfo());
                System.out.println("[ConnectorNPC] Server connected: " + event.serviceInfo().serviceId().name());
                if (!AsyncExecutor.service().isShutdown()) {
                    AsyncExecutor.service().submit(() -> npcs.forEach(n -> n.currentServer.query()));
                }
            } else if (event.lastLifeCycle() == ServiceLifeCycle.RUNNING) {
                System.out.println("[ConnectorNPC] Server disconnected: " + event.serviceInfo().serviceId().name());
                ServiceInfoSnapshot snap = services.remove(event.serviceInfo().serviceId().uniqueId());
                if (snap == null) System.err.println("Failed to remove from services!!!");
                if (!AsyncExecutor.service().isShutdown()) {
                    AsyncExecutor.service().submit(() -> npcs.forEach(n -> n.currentServer.query()));
                }
            }
        }
    }

    public class CHologram {
        private final TextLine online;
        private final TextLine description;
        private boolean descriptionVisible = false;

        public CHologram(Placeholders placeholders) {
            Lobby lobby = Lobby.getInstance();
            this.online = new TextLine(new Line(lobby), "%%online%%", placeholders);
            this.description = new TextLine(new Line(lobby), "%%description%%", placeholders);
        }

        private void location(Location location) {
            location = location.clone().add(0, 0.9, 0);
            online.setLocation(location);
            description.setLocation(location.clone().add(0, 0.28D, 0));
        }

        private void update() {
            NPCManagement.NPC npc = ConnectorNPC.this.npc;
            boolean newVisible = currentServer.server != null;
            if (newVisible != descriptionVisible) {
                if (npc != null) {
                    if (newVisible) {
                        for (Player player : npc.trackedPlayers()) {
                            description.show(player);
                        }
                    } else {
                        for (Player player : npc.trackedPlayers()) {
                            description.hide(player);
                        }
                    }
                }
                this.descriptionVisible = newVisible;
            }
            if (npc != null) {
                for (Player player : npc.trackedPlayers()) {
                    online.update(player);
                    if (descriptionVisible) description.update(player);
                }
            }
        }

        public void show(Player player) {
            online.show(player);
            if (descriptionVisible) description.show(player);
        }

        public void hide(Player player) {
            online.hide(player);
            if (descriptionVisible) description.hide(player);
        }
    }

    public class CurrentServer {

        private volatile Info server = null;
        private volatile Info newServer = null;

        private void query() {
            Collection<ServiceInfoSnapshot> servers = Listener.services
                    .values()
                    .stream()
                    .filter(s -> s.serviceId().taskName().equals(taskName))
                    .collect(Collectors.toSet());
            Map<ServiceInfoSnapshot, GameState> states = new HashMap<>();
            for (ServiceInfoSnapshot server : new ArrayList<>(servers)) {
                try {
                    GameState state = server.readProperty(DarkCubeServiceProperty.GAME_STATE);
                    if (state == null) {
                        continue;
                    }
                    states.put(server, state);
                } catch (Exception ex) {
                    servers.remove(server);
                }
            }
            List<Info> sortingInfos = new ArrayList<>();
            for (ServiceInfoSnapshot server : new HashSet<>(servers)) {
                int playingPlayers = server.readProperty(DarkCubeServiceProperty.PLAYING_PLAYERS);
                int maxPlayingPlayers = server.readProperty(DarkCubeServiceProperty.MAX_PLAYING_PLAYERS);

                GameState state = states.get(server);
                String motd = server.readProperty(DarkCubeServiceProperty.DISPLAY_NAME);
                if (motd == null || motd
                        .toLowerCase()
                        .contains("loading") || (state != GameState.INGAME && server.readProperty(DarkCubeServiceProperty.AUTOCONFIGURED))) {
                    servers.remove(server);
                    states.remove(server);
                    continue;
                }
                sortingInfos.add(new Info(new MinigameServerSortingInfo(playingPlayers, maxPlayingPlayers, state), server, "§d" + motd));
            }
            Collections.sort(sortingInfos);
            this.newServer = sortingInfos.stream().findFirst().orElse(null);
            new BukkitRunnable() {
                @Override public void run() {
                    server = newServer;
                    updateHologram();
                }
            }.runTask(Lobby.getInstance());
        }

        class Info implements Comparable<Info> {
            private final MinigameServerSortingInfo minigame;
            private final ServiceInfoSnapshot server;
            private final String motd;

            public Info(MinigameServerSortingInfo minigame, ServiceInfoSnapshot server, String motd) {
                this.minigame = minigame;
                this.server = server;
                this.motd = motd;
            }

            @Override public int compareTo(@NotNull ConnectorNPC.CurrentServer.Info o) {
                return minigame.compareTo(o.minigame);
            }
        }
    }
}
