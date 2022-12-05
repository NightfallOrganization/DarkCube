package eu.darkcube.system.lobbysystem.pserver;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Material;
import org.bukkit.SkullType;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.database.Database;
import eu.darkcube.system.inventory.api.util.ItemBuilder;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.pserver.gameserver.InventoryGameServerSelectionWoolBattle;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager.PServerUserSlots.PServerUserSlotOld;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.GsonSerializer;
import eu.darkcube.system.lobbysystem.util.GsonSerializer.DontSerialize;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.SkullCache;
import eu.darkcube.system.pserver.common.PServer;
import eu.darkcube.system.pserver.common.PServer.State;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.PServerSerializable;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;

public class PServerDataManager {


	public static void beginMigration() {
		if (CloudNetDriver.getInstance().getDatabaseProvider()
				.containsDatabase("pserver_userslots")) {
			Database database = CloudNetDriver.getInstance().getDatabaseProvider()
					.getDatabase("pserver_userslots");
			Type type = new TypeToken<Map<Integer, PServerUserSlotOld>>() {}.getType();
			for (String key : database.keys()) {
				UUID uuid = UUID.fromString(key);
				User user = UserAPI.getInstance().getUser(uuid);
				LobbyUser u = UserWrapper.fromUser(user);
				Map<Integer, PServerUserSlotOld> slots =
						GsonSerializer.gson.fromJson(database.get(key).toJson(), type);
			}
		}
	}

	public static ItemBuilder getDisplayItem(User user, PServerUserSlotOld slot) {
		if (slot.isUsed()) {
			JsonObject data = slot.getData();
			if (data.has("task")) {
				return PServerDataManager.getDisplayItemGamemode(user,
						data.get("task").getAsString());
			}
			ItemBuilder b =
					new ItemBuilder(Material.SKULL_ITEM).durability(SkullType.PLAYER.ordinal());
			b.meta(SkullCache.getCachedItem(user.getUniqueId()).getItemMeta());
			b.unsafeStackSize(true).displayname(Item.WORLD_PSERVER.getDisplayName(user));
			return b;
		}
		return null;
	}

	public static ItemBuilder getDisplayItemGamemode(User user, String task) {
		if (task == null) {
			return null;
		}
		if (Lobby.getInstance().getDataManager().getWoolBattleTasks().contains(task)) {
			return new InventoryGameServerSelectionWoolBattle.Func().apply(user,
					CloudNetDriver.getInstance().getServiceTaskProvider().getServiceTask(task));
		}
		return null;
	}

	public static class PServerUserSlot {
		private UniqueId pserverId = null;
		private JsonDocument data = null;
	}

	public static class PServerUserSlots {

		private Map<Integer, PServerUserSlotOld> slots = null;

		public PServerUserSlots(JsonDocument document) {}

		public PServerUserSlots() {
			if (PServerSupport.isSupported()) {
				long time1 = System.currentTimeMillis(), time2 = System.currentTimeMillis(),
						time3 = System.currentTimeMillis();
				if (PServerDataManager.database.contains(user.getUniqueId().toString())) {
					time2 = System.currentTimeMillis();
					this.slots = GsonSerializer.gson.fromJson(
							PServerDataManager.database.get(user.getUniqueId().toString()).toJson(),
							new TypeToken<Map<Integer, PServerUserSlotOld>>() {}.getType());
					Map<Integer, PServerUserSlotOld> slots = this.slots;
					this.slots = new HashMap<>();
					for (int key : slots.keySet()) {
						PServerUserSlotOld slot = this.new PServerUserSlotOld();
						slot.data = slots.get(key).data;
						slot.pserverId = slots.get(key).pserverId;
						this.slots.put(key, slot);
					}
					time3 = System.currentTimeMillis();
				} else {
					time2 = System.currentTimeMillis();
					this.slots = new HashMap<>();
					PServerDataManager.database.insert(user.getUniqueId().toString(),
							this.createDocument());
					time3 = System.currentTimeMillis();
				}
				if (System.currentTimeMillis() - time1 > 1000) {
					Lobby.getInstance().getLogger()
							.info("PServerUserSlot took too long: "
									+ (System.currentTimeMillis() - time1) + " | "
									+ (System.currentTimeMillis() - time2) + " | "
									+ (System.currentTimeMillis() - time3));
				}
			}
			/*
			 * if (slots == null) { slots = new HashMap<>();
			 * database.insert(user.getUniqueId().toString(), new
			 * JsonDocument(GsonSerializer.gson.toJson(slots))); }
			 */
		}

		public JsonDocument createDocument() {
			return JsonDocument.newDocument(GsonSerializer.gson.toJson(this.slots));
		}

		public PServerUserSlotOld getSlot(int slot) {
			PServerUserSlotOld s = this.slots.get(slot);
			if (s == null) {
				s = new PServerUserSlotOld();
				this.slots.put(slot, s);
			}
			return s;
		}

		public void save() {
			if (PServerSupport.isSupported()) {
				boolean changed = false;
				for (PServerUserSlotOld slot : this.slots.values()) {
					if (slot.changed) {
						changed = true;
						break;
					}
				}
				if (changed) {
					PServerDataManager.database.update(this.user.getUniqueId().toString(),
							this.createDocument());
				}
			}
		}

		public class PServerUserSlotOld {

			@DontSerialize
			private boolean changed = false;
			private UniqueId pserverId = null;
			private JsonObject data = null;

			public void load(UniqueId pserverId) {
				this.pserverId = pserverId;
				this.changed = true;
			}

			private JsonObject newData() {
				this.data = new JsonObject();
				this.data.addProperty("private", true);
				this.changed = true;
				return this.data;
			}

			public void delete(User owner) {
				if (this.pserverId != null) {
					PServerProvider.getInstance().removeOwner(this.pserverId, owner.getUniqueId());
					if (PServerProvider.getInstance().getOwners(this.pserverId).isEmpty()) {
						PServerProvider.getInstance().delete(this.pserverId);
					}
				}
				this.pserverId = null;
				this.data = null;
				this.changed = true;
			}

			public PServer startPServer() {
				PServer ps = this.createPServer();
				ps.start();
				return ps;
			}

			public PServer createPServer() {
				this.checkConfig();
				PServer ps = this.getPServer();
				if (ps == null) {
					int online = 0;
					PServer.State state = State.OFFLINE;
					boolean privateServer =
							this.getData().get("private").getAsJsonPrimitive().getAsBoolean();
					boolean temporary = this.getData().has("task");
					long startedAt = System.currentTimeMillis();
					Collection<UUID> owners =
							PServerProvider.getInstance().getOwners(this.pserverId);
					PServerSerializable ser = new PServerSerializable(this.pserverId, online, state,
							privateServer, temporary, startedAt, owners, null,
							PServerProvider.getInstance().newName());
					if (this.data.has("task")) {
						ps = PServerProvider.getInstance().createPServer(ser,
								CloudNetDriver.getInstance().getServiceTaskProvider()
										.getServiceTask(this.getData().get("task").getAsString()));
					} else {
						ps = PServerProvider.getInstance().createPServer(ser);
					}
				}
				return ps;
			}

			public PServer getPServer() {
				this.checkConfig();
				return PServerProvider.getInstance().getPServer(this.pserverId);
			}

			private void checkConfig() {
				if (this.pserverId == null) {
					throw new InvalidConfigurationException();
				}
			}

			public JsonObject getData() {
				return this.data == null ? this.newData() : this.data;
			}

			public UniqueId getPServerId() {
				return this.pserverId;
			}

			public boolean isUsed() {
				return this.pserverId != null;
			}

			public void setChanged() {
				this.changed = true;
			}

		}
	}

	public static class InvalidConfigurationException extends RuntimeException {
		private static final long serialVersionUID = 9158875635797975221L;
	}

}
