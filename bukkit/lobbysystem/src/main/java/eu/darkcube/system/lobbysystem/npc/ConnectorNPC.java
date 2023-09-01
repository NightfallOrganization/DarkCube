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
import eu.darkcube.system.bukkit.util.data.BukkitPersistentDataTypes;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.lobbysystem.util.server.DefaultServerInformationV2;
import eu.darkcube.system.lobbysystem.util.server.ServerInformation;
import eu.darkcube.system.lobbysystem.util.server.ServerManager;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class ConnectorNPC {

    private static final Key npcsKey = new Key(Lobby.getInstance(), "npcs");
    private static final Collection<ConnectorNPC> npcs = ConcurrentHashMap.newKeySet();
    private static final NpcFlag<ConnectorNPC> flagConnectorNpc = NpcFlag.flag("connectorNpc", null);
    private static final PersistentDataType<List<String>> strings = PersistentDataTypes.list(PersistentDataTypes.STRING);
    private static final AtomicInteger uidCounter = new AtomicInteger();
    private static final PersistentDataType<List<ConnectorNPC>> type = PersistentDataTypes.list(new PersistentDataType<>() {
        @Override public ConnectorNPC deserialize(Document doc, String key) {
            Document d = doc.readDocument(key);
            int id = d.getInt("id");
            String taskName = d.getString("task");
            String v2Key = d.getString("v2key");
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
            Location loc = BukkitPersistentDataTypes.LOCATION.deserialize(d, "location");
            return new ConnectorNPC(taskName, id, loc, permissions, v2Key);
        }

        @Override public void serialize(Document.Mutable doc, String key, ConnectorNPC data) {
            Document.Mutable d = Document.newJsonDocument();
            d.append("task", data.taskName);
            strings.serialize(d, "permissions", data.permissions);
            d.append("id", data.id);
            if (data.v2Key != null) d.append("v2key", data.v2Key);
            BukkitPersistentDataTypes.LOCATION.serialize(d, "location", data.location);
            doc.append(key, d);
        }

        @Override public ConnectorNPC clone(ConnectorNPC object) {
            return new ConnectorNPC(object.taskName, object.id, object.location, object.permissions, object.v2Key);
        }
    });

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
    private String v2Key = null;

    private ConnectorNPC(String taskName, int id, Location location, List<String> permissions, String v2Key) {
        this.taskName = taskName;
        this.id = id;
        this.location = location;
        this.permissions.addAll(permissions);
        this.v2Key = v2Key;
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
                User user = UserAPI.instance().user(player.getUniqueId());
                ServerInformation c = currentServer.server;
                if (c == null) return;
                c.connectPlayer(user.uniqueId());
            }
        }.runTask(Lobby.getInstance());
    }

    public void show() {
        npcs.add(this);
        if (location.getWorld() == null) location.setWorld(Bukkit.getWorlds().get(0));
        NPCManagement.Builder builder = Lobby.getInstance().npcManagement().builder();
        if (uid == -1) uid = uidCounter.incrementAndGet();
        builder.profileHelper().skin(new DailyRewardNPC.DailyRewardSkin()).name("§8NPC-" + uid);
        builder.trackingRule((ignoredNpc, player) -> {
            if (permissions.isEmpty()) return true;
            for (String permission : permissions) if (player.hasPermission(permission)) return true;
            return false;
        });
        npc = builder.build();
        npc.flagValue(flagConnectorNpc, this);
        npc.location(location);
        hologram = createHologram(location);
        (npcKnockbackThread = new NPCKnockbackThread(npc)).runTaskTimer(Lobby.getInstance(), 5, 5);
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
        CHologram hologram = new CHologram(hologramPlaceholders());
        hologram.location(location);
        return hologram;
    }

    private Placeholders hologramPlaceholders() {
        Placeholders h = new Placeholders(Placeholders.STRING);
        h.add("%%online%%", p -> {
            ServerInformation info = currentServer.server;
            if (info == null) {
                return Message.CONNECTOR_NPC_SERVER_STARTING.getMessageString(UserAPI.instance().user(p.getUniqueId()));
            }
            int online = info.onlinePlayers();
            int maxPlayers = info.maxPlayers();
            return Message.CONNECTOR_NPC_SERVER_ONLINE.getMessageString(UserAPI
                    .instance()
                    .user(p.getUniqueId()), online, maxPlayers == -1 ? 100 : maxPlayers);
        });
        h.add("%%description%%", p -> {
            ServerInformation server = currentServer.server;
            if (server != null) {
                Component displayName = server.displayName();
                return Message.CONNECTOR_NPC_SERVER_DESCRIPTION.getMessageString(UserAPI.instance().user(p.getUniqueId()), Component
                        .empty()
                        .append(displayName == null ? Component.text(server.taskName()) : displayName)
                        .applyFallbackStyle(Style.style(NamedTextColor.LIGHT_PURPLE)));
            }
            return " ";
        });
        return h;
    }

    public CHologram hologram() {
        return hologram;
    }

    public String v2Key() {
        return v2Key;
    }

    public void v2Key(String v2key) {
        this.v2Key = v2key;
    }

    public void update() {
        currentServer.update(Lobby.getInstance().serverManager().informations());
    }

    public static class UpdateListener implements ServerManager.UpdateListener {
        @Override public void update(Collection<? extends ServerInformation> informations) {
            for (ConnectorNPC npc : npcs) {
                npc.currentServer.update(informations);
            }
        }
    }

    public class CurrentServer {

        private volatile ServerInformation server = null;

        private void update(Collection<? extends ServerInformation> informations) {
            Stream<? extends ServerInformation> stream = informations
                    .stream()
                    .filter(ServerInformation.filterByTask(taskName))
                    .filter(ServerInformation.ONLINE_FILTER);
            if (v2Key != null) {
                stream = stream.filter(i -> {
                    if (!(i instanceof DefaultServerInformationV2 v2)) return false;
                    return v2.key().equals(v2Key);
                });
            }
            server = stream.min(ServerInformation.COMPARATOR).orElse(null);
            updateHologram();
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
}
