package eu.darkcube.system.lobbysystem.pserver;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.SkullType;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.database.Database;
import eu.darkcube.system.inventory.api.util.ItemBuilder;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.pserver.gameserver.InventoryGameServerSelectionWoolBattle;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager.PServerUserSlots.PServerUserSlot;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.util.GsonSerializer;
import eu.darkcube.system.lobbysystem.util.GsonSerializer.DontSerialize;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.SkullCache;
import eu.darkcube.system.pserver.common.PServer;
import eu.darkcube.system.pserver.common.PServer.State;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packet.PServerSerializable;

public class PServerDataManager {

	private static final Database database = CloudNetDriver.getInstance()
			.getDatabaseProvider()
			.getDatabase("pserver_userslots");

	public static ItemBuilder getDisplayItem(User user, PServerUserSlot slot) {
		if (slot.isUsed()) {
			JsonObject data = slot.getData();
			if (data.has("task")) {
				return PServerDataManager.getDisplayItemGamemode(user, data.get("task").getAsString());
			}
			ItemBuilder b = new ItemBuilder(Material.SKULL_ITEM).durability(SkullType.PLAYER.ordinal());
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

	public static class PServerUserSlots {

		private final User user;
		private Map<Integer, PServerUserSlot> slots = null;

		public PServerUserSlots(User user) {
			this.user = user;
			if (PServerSupport.isSupported()) {
				if (PServerDataManager.database.contains(user.getUniqueId().toString())) {
					this.slots = GsonSerializer.gson.fromJson(PServerDataManager.database.get(user.getUniqueId().toString()).toJson(),
							new TypeToken<Map<Integer, PServerUserSlot>>() {
							}.getType());
					Map<Integer, PServerUserSlot> slots = this.slots;
					this.slots = new HashMap<>();
					for (int key : slots.keySet()) {
						PServerUserSlot slot = this.new PServerUserSlot();
						slot.data = slots.get(key).data;
						slot.pserverId = slots.get(key).pserverId;
						this.slots.put(key, slot);
					}
				} else {
					this.slots = new HashMap<>();
					PServerDataManager.database.insert(user.getUniqueId().toString(), this.createDocument());
				}
			}
			/*
			 * if (slots == null) { slots = new HashMap<>();
			 * database.insert(user.getUniqueId().toString(), new
			 * JsonDocument(GsonSerializer.gson.toJson(slots))); }
			 */
		}

		private JsonDocument createDocument() {
			return JsonDocument.newDocument(GsonSerializer.gson.toJson(this.slots));
		}

		public User getUser() {
			return this.user;
		}

		public PServerUserSlot getSlot(int slot) {
			PServerUserSlot s = this.slots.get(slot);
			if (s == null) {
				s = new PServerUserSlot();
				this.slots.put(slot, s);
			}
			return s;
		}

		public void save() {
			if (PServerSupport.isSupported()) {
				boolean changed = false;
				for (PServerUserSlot slot : this.slots.values()) {
					if (slot.changed) {
						changed = true;
						break;
					}
				}
				if (changed) {
					PServerDataManager.database.update(this.user.getUniqueId().toString(), this.createDocument());
				}
			}
		}

		public class PServerUserSlot {

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

			public void delete() {
				if (this.pserverId != null) {
					PServerProvider.getInstance().removeOwner(this.pserverId, PServerUserSlots.this.getUser().getUniqueId());
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
					boolean privateServer = this.getData().get("private").getAsJsonPrimitive().getAsBoolean();
					boolean temporary = this.getData().has("task");
					long startedAt = System.currentTimeMillis();
					Collection<UUID> owners = PServerProvider.getInstance().getOwners(this.pserverId);
					PServerSerializable ser = new PServerSerializable(this.pserverId, online, state, privateServer,
							temporary, startedAt, owners, null, PServerProvider.getInstance().newName());
					if (this.data.has("task")) {
						ps = PServerProvider.getInstance()
								.createPServer(ser,
										CloudNetDriver.getInstance()
												.getServiceTaskProvider()
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
