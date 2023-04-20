/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.lobbysystem.npc;

import com.github.unldenis.hologram.AbstractLine;
import com.github.unldenis.hologram.Hologram;
import com.github.unldenis.hologram.HologramPool;
import com.github.unldenis.hologram.placeholder.Placeholders;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.service.CloudServiceConnectNetworkEvent;
import de.dytanic.cloudnet.driver.event.events.service.CloudServiceDisconnectNetworkEvent;
import de.dytanic.cloudnet.driver.event.events.service.CloudServiceInfoUpdateEvent;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import eu.darkcube.system.DarkCubeServiceProperty;
import eu.darkcube.system.libs.com.github.juliarn.npc.NPC;
import eu.darkcube.system.libs.com.github.juliarn.npc.modifier.MetadataModifier;
import eu.darkcube.system.libs.com.github.juliarn.npc.profile.Profile;
import eu.darkcube.system.libs.com.github.juliarn.npc.profile.ProfileUtils;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.npc.ConnectorNPC.CurrentServer.Info;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.lobbysystem.util.MinigameServerSortingInfo;
import eu.darkcube.system.lobbysystem.util.UUIDManager;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.AsyncExecutor;
import eu.darkcube.system.util.GameState;
import eu.darkcube.system.util.ReflectionUtils;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConnectorNPC {

	private static final Key npcsKey = new Key(Lobby.getInstance(), "npcs");
	private static final Collection<ConnectorNPC> npcs = ConcurrentHashMap.newKeySet();
	private static final PersistentDataType<List<String>> strings =
			PersistentDataTypes.list(PersistentDataTypes.STRING);
	private static final PersistentDataType<List<ConnectorNPC>> type =
			PersistentDataTypes.list(new PersistentDataType<ConnectorNPC>() {
				@Override
				public ConnectorNPC deserialize(JsonDocument doc, String key) {
					JsonDocument d = doc.getDocument(key);
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

				@Override
				public void serialize(JsonDocument doc, String key, ConnectorNPC data) {
					JsonDocument d = new JsonDocument();
					d.append("task", data.taskName);
					strings.serialize(d, "permissions", data.permissions);
					d.append("id", data.id);
					PersistentDataTypes.LOCATION.serialize(d, "location", data.location);
					doc.append(key, d);
				}

				@Override
				public ConnectorNPC clone(ConnectorNPC object) {
					return new ConnectorNPC(object.taskName, object.id, object.location,
							object.permissions);
				}
			});

	private static final Collection<ServiceInfoSnapshot> services = ConcurrentHashMap.newKeySet();

	static {
		CloudNetDriver.getInstance().getEventManager().registerListener(new Listener());
		services.addAll(CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices());
	}

	private final Location location;
	private final String taskName;
	private final List<String> permissions = new ArrayList<>();
	private final int id;
	private final CurrentServer currentServer = new CurrentServer();
	private final Collection<Player> connectingPlayers = ConcurrentHashMap.newKeySet();
	private NPC npc;
	private ConnectorHologram hologram;
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
		Lobby.getInstance().persistentDataStorage().get(npcsKey, type, ArrayList::new)
				.forEach(ConnectorNPC::show);
	}

	public static ConnectorNPC get(String key) {
		return npcs.stream().filter(n -> n.key().equals(key)).findFirst().orElse(null);
	}

	public static ConnectorNPC get(NPC npc) {
		return npcs.stream().filter(n -> n.npc == npc).findFirst().orElse(null);
	}

	public static Stream<ConnectorNPC> npcStream() {
		return npcs.stream();
	}

	private Placeholders hologramPlaceholders() {
		Placeholders h = new Placeholders();
		h.add("%%online%%", p -> {
			int online, maxplayers;
			Info info = currentServer.server;
			if (info != null) {
				online = info.minigame.onPlayers;
				maxplayers = info.minigame.maxPlayers;
			} else {
				return Message.CONNECTOR_NPC_SERVER_STARTING.getMessageString(
						UserAPI.getInstance().getUser(p));
			}
			return Message.CONNECTOR_NPC_SERVER_ONLINE.getMessageString(
					UserAPI.getInstance().getUser(p), online, maxplayers == -1 ? 100 : maxplayers);
		});
		h.add("%%description%%", p -> {
			Info server = currentServer.server;
			if (server != null) {
				return Message.CONNECTOR_NPC_SERVER_DESCRIPTION.getMessageString(
						UserAPI.getInstance().getUser(p), server.motd);
			}
			return "";
		});
		return h;
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
			@Override
			public void run() {
				connectingPlayers.remove(player);
			}
		}.runTaskLater(Lobby.getInstance(), 20);
		new BukkitRunnable() {
			@Override
			public void run() {
				User user = UserAPI.getInstance().getUser(player);
				if (currentServer.server != null) {
					connect0(user);
				}
			}
		}.runTask(Lobby.getInstance());
	}

	private void connect0(User player) {
		UUIDManager.getManager().getPlayerExecutor(player.getUniqueId())
				.connect(currentServer.server.server.getServiceId().getName());
	}

	public void show() {
		npcs.add(this);
		if (location.getWorld() == null)
			location.setWorld(Bukkit.getWorlds().get(0));
		Profile profile =
				new Profile(new UUID(new Random().nextLong(), 0), ProfileUtils.randomName(),
						Collections.singleton(new DailyRewardNPC.DailyRewardSkin()));
		hologram = new ConnectorHologram(location);
		try {
			HologramPool hpool = Lobby.getInstance().getHologramPool();
			Method m = hpool.getClass().getDeclaredMethod("takeCareOf", Hologram.class);
			m.setAccessible(true);
			ReflectionUtils.invokeMethod(hpool, m, hologram);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		npc = NPC.builder().profile(profile).location(location).imitatePlayer(false)
				.lookAtPlayer(true).spawnCustomizer((npc1, player) -> {
					if (!permissions.isEmpty()) {
						boolean hasAny = false;
						for (String permission : permissions) {
							if (player.hasPermission(permission)) {
								hasAny = true;
							}
						}
						if (!hasAny) {
							npc1.addExcludedPlayer(player);
							hologram.addExcludedPlayer(player);
							hologram.hide(player);
						}
					}
					Scoreboard sb = player.getScoreboard();
					Team team = sb.getTeam("npc-lib-lobby");
					if (team == null) {
						team = sb.registerNewTeam("npc-lib-lobby");
					}
					team.setNameTagVisibility(NameTagVisibility.NEVER);
					team.addEntry(profile.getName());
					npc1.metadata().queue(MetadataModifier.EntityMetadata.SKIN_LAYERS, true).send();
				}).usePlayerProfiles(false).build(Lobby.getInstance().getNpcPool());
		(npcKnockbackThread = new NPCKnockbackThread(npc)).runTaskTimer(Lobby.getInstance(), 5, 5);
		AsyncExecutor.service().submit(currentServer::query);
	}

	public void hide() {
		if (npc == null)
			return;
		npcs.remove(this);
		if (Lobby.getInstance().isEnabled())
			Lobby.getInstance().getNpcPool().removeNPC(npc.getEntityId());
		npcKnockbackThread.cancel();
		npc = null;
		Lobby.getInstance().getHologramPool().remove(hologram);
		hologram = null;
	}

	public static class Listener {
		@EventListener
		public void handle(CloudServiceInfoUpdateEvent event) {
			add(event.getServiceInfo());
			if (!AsyncExecutor.service().isShutdown()) {
				AsyncExecutor.service().submit(() -> npcs.forEach(n -> n.currentServer.query()));
			}
		}

		@EventListener
		public void handle(CloudServiceConnectNetworkEvent event) {
			add(event.getServiceInfo());
			if (!AsyncExecutor.service().isShutdown()) {
				AsyncExecutor.service().submit(() -> npcs.forEach(n -> n.currentServer.query()));
			}
		}

		private void add(ServiceInfoSnapshot server) {
			services.forEach(s -> {
				if (s.getServiceId().equals(server.getServiceId())) {
					services.remove(s);
				}
			});
			services.add(server);
		}

		@EventListener
		public void handle(CloudServiceDisconnectNetworkEvent event) {
			services.remove(event.getServiceInfo());
			if (!AsyncExecutor.service().isShutdown()) {
				AsyncExecutor.service().submit(() -> npcs.forEach(n -> n.currentServer.query()));
			}
		}
	}

	private class ConnectorHologram extends Hologram {

		private final AbstractLine<?> l1;
		private final AbstractLine<?> l2;
		private final Method show, hide;
		private final Set<Player> l2visible = new CopyOnWriteArraySet<>();

		@SuppressWarnings("deprecation")
		public ConnectorHologram(Location location) {
			//noinspection UnstableApiUsage
			super(Lobby.getInstance(), location, hologramPlaceholders(),
					new Object[] {"%%online%%", false}, new Object[] {"%%description%%", false});
			l1 = lines.get(1);
			l2 = lines.get(0);
			try {
				show = AbstractLine.class.getDeclaredMethod("show", Player.class);
				show.setAccessible(true);
				hide = AbstractLine.class.getDeclaredMethod("hide", Player.class);
				hide.setAccessible(true);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}

		private synchronized void update() {
			currentServer.server = currentServer.newServer;
			l1.update();
			if (currentServer.server == null) {
				l2visible.forEach(p -> {
					hide(l2, p);
					l2visible.remove(p);
				});
			} else {
				l2.update();
				for (Player p : seeingPlayers) {
					if (!l2visible.contains(p)) {
						show(l2, p);
						l2visible.add(p);
					}
				}
			}
		}

		@Override
		protected void show(@NotNull Player player) {
			seeingPlayers.add(player);
			show(l1, player);
			boolean l2 = currentServer.server != null;
			if (l2) {
				l2visible.add(player);
				show(this.l2, player);
			}
		}

		@Override
		protected void hide(@NotNull Player player) {
			hide(l1, player);
			if (l2visible.remove(player)) {
				hide(l2, player);
			}
			seeingPlayers.remove(player);
		}

		private void show(AbstractLine<?> line, Player player) {
			ReflectionUtils.invokeMethod(line, show, player);
		}

		private void hide(AbstractLine<?> line, Player player) {
			ReflectionUtils.invokeMethod(line, hide, player);
		}
	}

	public class CurrentServer {

		private volatile Info server = null;
		private volatile Info newServer = null;

		private void query() {
			Collection<ServiceInfoSnapshot> servers =
					services.stream().filter(s -> s.getServiceId().getTaskName().equals(taskName))
							.collect(Collectors.toSet());
			Map<ServiceInfoSnapshot, GameState> states = new HashMap<>();
			for (ServiceInfoSnapshot server : new ArrayList<>(servers)) {
				try {
					GameState state =
							server.getProperty(DarkCubeServiceProperty.GAME_STATE).orElse(null);
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
				int playingPlayers =
						server.getProperty(DarkCubeServiceProperty.PLAYING_PLAYERS).orElse(-1);
				int maxPlayingPlayers =
						server.getProperty(DarkCubeServiceProperty.MAX_PLAYING_PLAYERS).orElse(-1);

				GameState state = states.get(server);
				String motd =
						server.getProperty(DarkCubeServiceProperty.DISPLAY_NAME).orElse(null);
				if (motd == null || motd.toLowerCase().contains("loading") || (
						state != GameState.INGAME && server.getProperty(
								DarkCubeServiceProperty.AUTOCONFIGURED).orElse(false))) {
					servers.remove(server);
					states.remove(server);
					continue;
				}
				sortingInfos.add(new Info(
						new MinigameServerSortingInfo(playingPlayers, maxPlayingPlayers, state),
						server, "§d" + motd));
			}
			Collections.sort(sortingInfos);
			this.newServer = sortingInfos.stream().findFirst().orElse(null);
			hologram.update();
		}

		class Info implements Comparable<Info> {
			private MinigameServerSortingInfo minigame;
			private ServiceInfoSnapshot server;
			private String motd;

			public Info(MinigameServerSortingInfo minigame, ServiceInfoSnapshot server,
					String motd) {
				this.minigame = minigame;
				this.server = server;
				this.motd = motd;
			}

			@Override
			public int compareTo(@NotNull ConnectorNPC.CurrentServer.Info o) {
				return minigame.compareTo(o.minigame);
			}
		}
	}
}
