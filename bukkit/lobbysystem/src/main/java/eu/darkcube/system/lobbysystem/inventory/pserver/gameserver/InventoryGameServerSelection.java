package eu.darkcube.system.lobbysystem.inventory.pserver.gameserver;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonObject;

import de.dytanic.cloudnet.driver.service.ServiceTask;
import eu.darkcube.system.inventory.api.util.ItemBuilder;
import eu.darkcube.system.lobbysystem.inventory.abstraction.DefaultPagedInventory;
import eu.darkcube.system.lobbysystem.inventory.abstraction.Inventory;
import eu.darkcube.system.lobbysystem.inventory.abstraction.InventoryType;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager.PServerUserSlots.PServerUserSlot;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.util.Item;

public abstract class InventoryGameServerSelection extends DefaultPagedInventory {

	private final Item item;
	private final Supplier<Collection<ServiceTask>> supplier;
	private final BiFunction<User, ServiceTask, ItemBuilder> toItemFunction;
	protected final Map<User, Map<Integer, ItemStack>> contents = new HashMap<>();

	public static final String ITEMID = "pserver_gameserver";
	public static final String GAMESERVER_META_KEY = "pserver.gameserver";
	public static final String SLOT = "slot";
	public static final String SERVICETASK = "serviceTask";

	protected final int[] itemSort;
	public final PServerUserSlot psslot;
	public final int slot;

	public InventoryGameServerSelection(User user, Item item, InventoryType type,
			Supplier<Collection<ServiceTask>> supplier, BiFunction<User, ServiceTask, ItemBuilder> toItemFunction,
			PServerUserSlot psslot, int slot) {
		super(item.getDisplayName(user), item, type);
		this.supplier = supplier;
		this.toItemFunction = toItemFunction;
		this.item = item;
		this.psslot = psslot;
		this.slot = slot;

		this.itemSort = new int[] {
				//@formatter:off
				10, 9, 11, 8, 12, 3, 17, 
				16, 18, 2, 4, 7, 13, 15,
				19, 1, 5, 14, 20, 0, 6
				//@formatter:on
		};
	}

	@Override
	protected final Map<Integer, ItemStack> contents(User user) {
		Map<Integer, ItemStack> m = new HashMap<>();
		int slot = 0;
		Collection<ServiceTask> serviceTasks = this.supplier.get();
		for (ServiceTask serviceTask : serviceTasks) {
			ItemBuilder b = this.toItemFunction.apply(user, serviceTask);
			JsonObject data = new JsonObject();
			data.addProperty(InventoryGameServerSelection.SLOT, slot);
			data.addProperty(InventoryGameServerSelection.SERVICETASK, serviceTask.getName());
			b.meta(null);
			b.getUnsafe().setString(InventoryGameServerSelection.GAMESERVER_META_KEY, data.toString());
			Item.setItemId(b, InventoryGameServerSelection.ITEMID);
			m.put(this.itemSort[slot % this.itemSort.length] + this.itemSort.length * (slot / this.itemSort.length), b.build());
			slot++;
		}
		return m;
	}

//	@Override
//	protected void onOpen(User user) {
//		new BukkitRunnable() {
//			@Override
//			public void run() {
//				int slot = 0;
//				Collection<ServiceTask> serviceTasks = supplier.get();
//				for (ServiceTask serviceTask : serviceTasks) {
//					ItemBuilder b = toItemFunction.apply(serviceTask);
//					JsonObject data = new JsonObject();
//					data.addProperty(SLOT, slot);
//					data.addProperty(GAMESERVER_META_KEY, serviceTask.getName());
//					b.getUnsafe().setString(GAMESERVER_META_KEY, data.toString());
//					slot++;
//				}
//			}
//		}.runTaskAsynchronously(Lobby.getInstance());
//	}
//
//	@Override
//	protected void onClose(User user) {
//		contents.remove(user);
//	}
//
//	@Override
//	protected Map<Integer, ItemStack> getContents(User user) {
//		if (contents.containsKey(user)) {
//			return contents.get(user);
//		}
//		Map<Integer, ItemStack> m = new HashMap<>();
//		for (int i = 0; i < getPageSize(); i++) {
//			m.put(i, Item.LOADING.getItem(user));
//		}
//		return m;
//	}

	@Override
	protected void insertDefaultItems(InventoryManager manager) {
		super.insertDefaultItems(manager);
		manager.setFallbackItem(Inventory.s(1, 5), this.item.getItem(manager.user));
	}
}