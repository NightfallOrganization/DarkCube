/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.npc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import com.github.juliarn.npclib.api.flag.NpcFlag;
import com.github.unldenis.hologram.line.Line;
import com.github.unldenis.hologram.line.TextLine;
import com.github.unldenis.hologram.placeholder.Placeholders;
import eu.darkcube.system.bukkit.util.data.BukkitPersistentDataTypes;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.lobbysystem.util.server.DefaultServerInformationV2;
import eu.darkcube.system.lobbysystem.util.server.ServerInformation;
import eu.darkcube.system.lobbysystem.util.server.ServerManager;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.scheduler.BukkitRunnable;

public class ConnectorNPC {

    private static final Key npcsKey = Key.key(Lobby.getInstance(), "npcs");
    private static final Collection<ConnectorNPC> npcs = ConcurrentHashMap.newKeySet();
    private static final NpcFlag<ConnectorNPC> flagConnectorNpc = NpcFlag.flag("connectorNpc", null);
    private static final PersistentDataType<List<String>> strings = PersistentDataTypes.list(PersistentDataTypes.STRING);
    private static final AtomicInteger uidCounter = new AtomicInteger();
    private static final PersistentDataType<List<ConnectorNPC>> type = PersistentDataTypes.list(new PersistentDataType<>() {
        @Override
        public ConnectorNPC deserialize(JsonElement json) {
            var d = json.getAsJsonObject();
            var id = d.get("id").getAsInt();
            var taskName = d.get("task").getAsString();
            var v2KeyJson = d.get("v2key");
            var v2Key = v2KeyJson != null ? v2KeyJson.getAsString() : null;
            List<String> permissions;
            if (d.has("permissions")) {
                permissions = strings.deserialize(d.get("permissions"));
                for (var permission : permissions) {
                    if (Bukkit.getPluginManager().getPermission(permission) == null) {
                        Bukkit.getPluginManager().addPermission(new Permission(permission));
                    }
                }
            } else {
                permissions = new ArrayList<>();
            }
            var loc = BukkitPersistentDataTypes.LOCATION.deserialize(d.get("location"));
            return new ConnectorNPC(taskName, id, loc, permissions, v2Key);
        }

        @Override
        public JsonElement serialize(ConnectorNPC data) {
            var d = new JsonObject();
            d.addProperty("task", data.taskName);
            d.add("permissions", strings.serialize(data.permissions));
            d.addProperty("id", data.id);
            if (data.v2Key != null) d.addProperty("v2key", data.v2Key);
            d.add("location", BukkitPersistentDataTypes.LOCATION.serialize(data.location));
            return d;
        }

        @Override
        public ConnectorNPC clone(ConnectorNPC object) {
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
        var id = 1;
        w:
        while (true) {
            for (var npc : npcs) {
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
        for (var p : connectingPlayers) {
            if (p == player) // Cooldown of 1 sec
                return;
        }
        connectingPlayers.add(player);
        new BukkitRunnable() {
            @Override
            public void run() {
                connectingPlayers.remove(player);
            }
        }.runTaskLater(Lobby.getInstance(), 20);
        new BukkitRunnable() {
            @Override
            public void run() {
                var user = UserAPI.instance().user(player.getUniqueId());
                var c = currentServer.server;
                if (c == null) return;
                c.connectPlayer(user.uniqueId());
            }
        }.runTask(Lobby.getInstance());
    }

    public void show() {
        npcs.add(this);
        if (location.getWorld() == null) location.setWorld(Bukkit.getWorlds().get(0));
        var builder = Lobby.getInstance().npcManagement().builder();
        if (uid == -1) uid = uidCounter.incrementAndGet();
        builder.profileHelper().skin(new DailyRewardNPC.DailyRewardSkin()).name("§8NPC-" + uid);
        builder.trackingRule((ignoredNpc, player) -> {
            if (permissions.isEmpty()) return true;
            for (var permission : permissions) if (player.hasPermission(permission)) return true;
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
        var hologram = new CHologram(hologramPlaceholders());
        hologram.location(location);
        return hologram;
    }

    private Placeholders hologramPlaceholders() {
        var h = new Placeholders(Placeholders.STRING);
        h.add("%%online%%", p -> {
            var info = currentServer.server;
            if (info == null) {
                return Message.CONNECTOR_NPC_SERVER_STARTING.getMessageString(UserAPI.instance().user(p.getUniqueId()));
            }
            var online = info.onlinePlayers();
            var maxPlayers = info.maxPlayers();
            return Message.CONNECTOR_NPC_SERVER_ONLINE.getMessageString(UserAPI.instance().user(p.getUniqueId()), online, maxPlayers == -1 ? 100 : maxPlayers);
        });
        h.add("%%description%%", p -> {
            var server = currentServer.server;
            if (server != null) {
                var displayName = server.displayName();
                return Message.CONNECTOR_NPC_SERVER_DESCRIPTION.getMessageString(UserAPI.instance().user(p.getUniqueId()), Component.empty().append(displayName == null ? Component.text(server.taskName()) : displayName).applyFallbackStyle(Style.style(NamedTextColor.LIGHT_PURPLE)));
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
        @Override
        public void update(Collection<? extends ServerInformation> informations) {
            for (var npc : npcs) {
                npc.currentServer.update(informations);
            }
        }
    }

    public class CurrentServer {

        private volatile ServerInformation server = null;

        private void update(Collection<? extends ServerInformation> informations) {
            var stream = informations.stream().filter(ServerInformation.filterByTask(taskName)).filter(ServerInformation.ONLINE_FILTER);
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
            var lobby = Lobby.getInstance();
            this.online = new TextLine(new Line(lobby), "%%online%%", placeholders);
            this.description = new TextLine(new Line(lobby), "%%description%%", placeholders);
        }

        private void location(Location location) {
            location = location.clone().add(0, 0.9, 0);
            online.setLocation(location);
            description.setLocation(location.clone().add(0, 0.28D, 0));
        }

        private void update() {
            var npc = ConnectorNPC.this.npc;
            var newVisible = currentServer.server != null;
            if (newVisible != descriptionVisible) {
                if (npc != null) {
                    if (newVisible) {
                        for (var player : npc.trackedPlayers()) {
                            description.show(player);
                        }
                    } else {
                        for (var player : npc.trackedPlayers()) {
                            description.hide(player);
                        }
                    }
                }
                this.descriptionVisible = newVisible;
            }
            if (npc != null) {
                for (var player : npc.trackedPlayers()) {
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
