package eu.darkcube.system.lobbysystem.pserver;

import java.lang.reflect.Type;
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
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager.PServerUserSlotsOld.PServerUserSlotOld;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.GsonSerializer;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.SkullCache;
import eu.darkcube.system.pserver.common.UniqueId;
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
				Map<Integer, PServerUserSlotOld> oslots =
						GsonSerializer.gson.fromJson(database.get(key).toJson(), type);
				Map<Integer, PServerUserSlot> slots = new HashMap<>();
				for (Map.Entry<Integer, PServerUserSlotOld> entry : oslots.entrySet()) {
					PServerUserSlot slot =
							new PServerUserSlot(entry.getValue().pserverId, new JsonDocument());
					JsonObject o = entry.getValue().data;
					if (o.has("private"))
						slot.data.append("private", o.get("private").getAsBoolean());
					if (o.has("task"))
						slot.data.append("task", o.get("task").getAsString());
					slots.put(entry.getKey(), slot);
				}
				PServerUserSlots s = new PServerUserSlots();
				s.slots = slots;
				u.setPServerUserSlots(s);
			}
		}
	}

	public static ItemBuilder getDisplayItem(User user, PServerUserSlot slot) {
		if (slot.pserverId != null) {
			if (slot.data.contains("task")) {
				return PServerDataManager.getDisplayItemGamemode(user, slot.data.getString("task"));
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

	public static class PServerUserSlots {
		private Map<Integer, PServerUserSlot> slots;

		public synchronized PServerUserSlot getUserSlot(int slot) {
			if (!slots.containsKey(slot)) {
				PServerUserSlot s = new PServerUserSlot();
				slots.put(slot, s);
				return s;
			}
			return slots.get(slot);
		}

		public synchronized void removeUserSlot(int slot) {
			slots.remove(slot);
		}

		public synchronized JsonDocument createDocument() {
			JsonDocument doc = new JsonDocument();
			for (Map.Entry<Integer, PServerUserSlot> slot : slots.entrySet()) {
				doc.append(Integer.toString(slot.getKey()), slot.getValue().createDocument());
			}
			return doc;
		}

		public static PServerUserSlots fromDocument(JsonDocument doc) {
			PServerUserSlots s = new PServerUserSlots();
			for (String key : doc.keys()) {
				s.slots.put(Integer.parseInt(key),
						PServerUserSlot.fromDocument(doc.getDocument(key)));
			}
			return s;
		}
	}

	public static class PServerUserSlot {
		public UniqueId pserverId = null;
		public JsonDocument data = null;

		public PServerUserSlot() {
			this.data = new JsonDocument();
		}

		public PServerUserSlot(UniqueId pserverId, JsonDocument data) {
			this.pserverId = pserverId;
			this.data = data;
		}

		public boolean isInUse() {
			return pserverId != null;
		}

		public JsonDocument createDocument() {
			JsonDocument doc = new JsonDocument();
			if (pserverId != null)
				doc.append("pserverId", pserverId.toString());
			doc.append("data", data);
			return doc;
		}

		public static PServerUserSlot fromDocument(JsonDocument doc) {
			JsonDocument data = doc.getDocument("data");
			UniqueId pserverId =
					doc.contains("pserverId") ? new UniqueId(doc.getString("pserverId")) : null;
			return new PServerUserSlot(pserverId, data);
		}
	}

	public static class PServerUserSlotsOld {

		// private Map<Integer, PServerUserSlotOld> slots = null;
		//
		// public PServerUserSlotsOld() {
		// if (PServerSupport.isSupported()) {
		// long time1 = System.currentTimeMillis(), time2 = System.currentTimeMillis(),
		// time3 = System.currentTimeMillis();
		// if (PServerDataManager.database.contains(user.getUniqueId().toString())) {
		// time2 = System.currentTimeMillis();
		// this.slots = GsonSerializer.gson.fromJson(
		// PServerDataManager.database.get(user.getUniqueId().toString()).toJson(),
		// new TypeToken<Map<Integer, PServerUserSlotOld>>() {}.getType());
		// Map<Integer, PServerUserSlotOld> slots = this.slots;
		// this.slots = new HashMap<>();
		// for (int key : slots.keySet()) {
		// PServerUserSlotOld slot = this.new PServerUserSlotOld();
		// slot.data = slots.get(key).data;
		// slot.pserverId = slots.get(key).pserverId;
		// this.slots.put(key, slot);
		// }
		// time3 = System.currentTimeMillis();
		// } else {
		// time2 = System.currentTimeMillis();
		// this.slots = new HashMap<>();
		// PServerDataManager.database.insert(user.getUniqueId().toString(),
		// this.createDocument());
		// time3 = System.currentTimeMillis();
		// }
		// if (System.currentTimeMillis() - time1 > 1000) {
		// Lobby.getInstance().getLogger()
		// .info("PServerUserSlot took too long: "
		// + (System.currentTimeMillis() - time1) + " | "
		// + (System.currentTimeMillis() - time2) + " | "
		// + (System.currentTimeMillis() - time3));
		// }
		// }
		// /*
		// * if (slots == null) { slots = new HashMap<>();
		// * database.insert(user.getUniqueId().toString(), new
		// * JsonDocument(GsonSerializer.gson.toJson(slots))); }
		// */
		// }
		//
		// public JsonDocument createDocument() {
		// return JsonDocument.newDocument(GsonSerializer.gson.toJson(this.slots));
		// }
		//
		// public PServerUserSlotOld getSlot(int slot) {
		// PServerUserSlotOld s = this.slots.get(slot);
		// if (s == null) {
		// s = new PServerUserSlotOld();
		// this.slots.put(slot, s);
		// }
		// return s;
		// }
		//
		// public void save() {
		// if (PServerSupport.isSupported()) {
		// boolean changed = false;
		// for (PServerUserSlotOld slot : this.slots.values()) {
		// if (slot.changed) {
		// changed = true;
		// break;
		// }
		// }
		// if (changed) {
		// PServerDataManager.database.update(this.user.getUniqueId().toString(),
		// this.createDocument());
		// }
		// }
		// }

		public class PServerUserSlotOld {

			// @DontSerialize
			// private boolean changed = false;
			private UniqueId pserverId = null;
			private JsonObject data = null;

			public void load(UniqueId pserverId) {
				this.pserverId = pserverId;
				// this.changed = true;
			}

			// public void delete(User owner) {
			// if (this.pserverId != null) {
			// PServerProvider.getInstance().removeOwner(this.pserverId, owner.getUniqueId());
			// if (PServerProvider.getInstance().getOwners(this.pserverId).isEmpty()) {
			// PServerProvider.getInstance().delete(this.pserverId);
			// }
			// }
			// this.pserverId = null;
			// this.data = null;
			// this.changed = true;
			// }
			//
			// public PServer startPServer() {
			// PServer ps = this.createPServer();
			// ps.start();
			// return ps;
			// }
			//
			// public PServer createPServer() {
			// this.checkConfig();
			// PServer ps = this.getPServer();
			// if (ps == null) {
			// int online = 0;
			// PServer.State state = State.OFFLINE;
			// boolean privateServer =
			// this.getData().get("private").getAsJsonPrimitive().getAsBoolean();
			// boolean temporary = this.getData().has("task");
			// long startedAt = System.currentTimeMillis();
			// Collection<UUID> owners =
			// PServerProvider.getInstance().getOwners(this.pserverId);
			// PServerSerializable ser = new PServerSerializable(this.pserverId, online, state,
			// privateServer, temporary, startedAt, owners, null,
			// PServerProvider.getInstance().newName());
			// if (this.data.has("task")) {
			// ps = PServerProvider.getInstance().createPServer(ser,
			// CloudNetDriver.getInstance().getServiceTaskProvider()
			// .getServiceTask(this.getData().get("task").getAsString()));
			// } else {
			// ps = PServerProvider.getInstance().createPServer(ser);
			// }
			// }
			// return ps;
			// }
			//
			// public PServer getPServer() {
			// this.checkConfig();
			// return PServerProvider.getInstance().getPServer(this.pserverId);
			// }
			//
			// private void checkConfig() {
			// if (this.pserverId == null) {
			// throw new InvalidConfigurationException();
			// }
			// }
			//
			// public UniqueId getPServerId() {
			// return this.pserverId;
			// }
			//
			// public boolean isUsed() {
			// return this.pserverId != null;
			// }
		}
	}

	public static class InvalidConfigurationException extends RuntimeException {
		private static final long serialVersionUID = 9158875635797975221L;
	}

}
