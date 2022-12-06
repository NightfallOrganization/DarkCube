package eu.darkcube.system.lobbysystem.inventory.pserver.gameserver;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import com.google.gson.JsonObject;
import org.bukkit.inventory.ItemStack;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import eu.darkcube.system.inventory.api.util.ItemBuilder;
import eu.darkcube.system.inventory.api.v1.IInventory;
import eu.darkcube.system.inventory.api.v1.InventoryType;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.userapi.User;

public abstract class InventoryGameServerSelection extends LobbyAsyncPagedInventory {

	private final Item item;

	private final Supplier<Collection<ServiceTask>> supplier;

	private final BiFunction<User, ServiceTask, ItemBuilder> toItemFunction;

	protected final Map<User, Map<Integer, ItemStack>> contents = new HashMap<>();

	public static final String ITEMID = "pserver_gameserver";

	public static final String GAMESERVER_META_KEY = "pserver.gameserver";

	public static final String SLOT = "slot";

	public static final String SERVICETASK = "serviceTask";

	protected final int[] itemSort;

	public final int psslot;

	private boolean done = false;

	private final User auser;

	public InventoryGameServerSelection(User user, Item item, InventoryType type,
			Supplier<Collection<ServiceTask>> supplier,
			BiFunction<User, ServiceTask, ItemBuilder> toItemFunction, int psslot) {
		super(type, item.getDisplayName(user), UserWrapper.fromUser(user));
		auser = user;
		this.supplier = supplier;
		this.toItemFunction = toItemFunction;
		this.item = item;
		this.psslot = psslot;

		this.itemSort = new int[] {
				//@formatter:off
				10, 9, 11, 8, 12, 3, 17, 
				16, 18, 2, 4, 7, 13, 15,
				19, 1, 5, 14, 20, 0, 6
				//@formatter:on
		};
		this.done = true;
		this.complete();
	}

	@Override
	protected boolean done() {
		return super.done() && this.done;
	}

	@Override
	protected void insertFallbackItems() {
		this.fallbackItems.put(IInventory.slot(1, 5), this.item.getItem(this.auser));
		super.insertFallbackItems();
	}

	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		int slot = 0;
		Collection<ServiceTask> serviceTasks = this.supplier.get();
		for (ServiceTask serviceTask : serviceTasks) {
			ItemBuilder b = this.toItemFunction.apply(this.auser, serviceTask);
			JsonObject data = new JsonObject();
			data.addProperty(InventoryGameServerSelection.SLOT, slot);
			data.addProperty(InventoryGameServerSelection.SERVICETASK, serviceTask.getName());
			b.getUnsafe().setString(InventoryGameServerSelection.GAMESERVER_META_KEY,
					data.toString());
			Item.setItemId(b, InventoryGameServerSelection.ITEMID);
			items.put(this.itemSort[slot % this.itemSort.length]
					+ this.itemSort.length * (slot / this.itemSort.length), b.build());
			slot++;
		}
	}

	// @Override
	// protected void onOpen(User user) {
	// new BukkitRunnable() {
	// @Override
	// public void run() {
	// int slot = 0;
	// Collection<ServiceTask> serviceTasks = supplier.get();
	// for (ServiceTask serviceTask : serviceTasks) {
	// ItemBuilder b = toItemFunction.apply(serviceTask);
	// JsonObject data = new JsonObject();
	// data.addProperty(SLOT, slot);
	// data.addProperty(GAMESERVER_META_KEY, serviceTask.getName());
	// b.getUnsafe().setString(GAMESERVER_META_KEY, data.toString());
	// slot++;
	// }
	// }
	// }.runTaskAsynchronously(Lobby.getInstance());
	// }
	//
	// @Override
	// protected void onClose(User user) {
	// contents.remove(user);
	// }
	//
	// @Override
	// protected Map<Integer, ItemStack> getContents(User user) {
	// if (contents.containsKey(user)) {
	// return contents.get(user);
	// }
	// Map<Integer, ItemStack> m = new HashMap<>();
	// for (int i = 0; i < getPageSize(); i++) {
	// m.put(i, Item.LOADING.getItem(user));
	// }
	// return m;
	// }
}
