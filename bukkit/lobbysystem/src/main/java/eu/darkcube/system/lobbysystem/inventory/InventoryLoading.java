package eu.darkcube.system.lobbysystem.inventory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import eu.darkcube.system.inventory.api.util.ItemBuilder;
import eu.darkcube.system.inventory.api.v1.AsyncPagedInventory;
import eu.darkcube.system.inventory.api.v1.IInventory;
import eu.darkcube.system.inventory.api.v1.InventoryType;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.PositionedIterator;
import eu.darkcube.system.userapi.User;

public class InventoryLoading extends LobbyAsyncPagedInventory {

	private static final InventoryType type_loading = InventoryType.of("loading");

	private final Function<LobbyUser, IInventory> inventoryFunction;

	private final Map<LobbyUser, Map<Integer, ItemStack>> contents = new HashMap<>();

	private final Map<LobbyUser, Integer> counts = new HashMap<>();

	private final Map<LobbyUser, Runner> runnables = new HashMap<>();

	private final PositionedIterator<ItemStack> iterator;

	private static final int[] MAP = new int[] {IInventory.slot(2, 4), IInventory.slot(2, 5),
			IInventory.slot(2, 6), IInventory.slot(3, 6), IInventory.slot(4, 6),
			IInventory.slot(4, 5), IInventory.slot(4, 4), IInventory.slot(3, 4)};

	public InventoryLoading(String title, User user,
			Function<LobbyUser, IInventory> inventoryFunction) {
		super(InventoryLoading.type_loading, title, 5 * 9, AsyncPagedInventory.box(1, 1, 5, 9),
				user);
		Arrays.fill(this.SORT, 0);
		// Arrays.fill(this.TOTAL_SORT, 0);
		this.inventoryFunction = inventoryFunction;

		final ItemStack loading = new ItemBuilder(Material.BARRIER).displayname(" ").build();
		this.iterator = new PositionedIterator<>(
				new ItemStack[] {loading, loading, loading, loading, loading, null, null, null});
	}

	private class Runner {

		private BukkitRunnable run;

		public Runner(LobbyUser user) {
			this.run = new BukkitRunnable() {

				@Override
				public void run() {
					if (InventoryLoading.this.counts.containsKey(user)) {
						final int count = InventoryLoading.this.counts.get(user) + 1;
						InventoryLoading.this.counts.put(user, count);
						InventoryLoading.this.iterator.next();
						ItemStack[] items = InventoryLoading.this.iterator.elements();
						Map<Integer, ItemStack> contents = new HashMap<>();
						for (int i = 0; i < items.length; i++) {
							ItemStack item = items[i];
							if (item != null) {
								contents.put(InventoryLoading.MAP[i], item);
							}
						}
						InventoryLoading.this.contents.put(user, contents);
						InventoryLoading.this.recalculate();
					} else {
						this.cancel();
					}
				}

			};
			this.run.runTaskTimer(Lobby.getInstance(), 2, 2);
		}

		public void stop() {
			this.run.cancel();
		}

	}

	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		if (this.contents.containsKey(this.user)) {
			items.putAll(this.contents.get(this.user));
		}
		super.fillItems(items);
	}

	@Override
	public void open(HumanEntity player) {
		super.open(player);
		if (this.contents.containsKey(this.user)) {
			return;
		}
		this.contents.put(this.user, new HashMap<>());
		this.counts.put(this.user, 0);
		this.runnables.put(this.user, new Runner(this.user));
		new BukkitRunnable() {

			@Override
			public void run() {
				IInventory inv =
						InventoryLoading.this.inventoryFunction.apply(InventoryLoading.this.user);
				InventoryLoading.this.user.setOpenInventory(inv);
			}

		}.runTaskAsynchronously(Lobby.getInstance());
	}

	@Override
	protected void destroy() {
		super.destroy();
		if (!this.contents.containsKey(this.user)) {
			return;
		}
		this.contents.remove(this.user);
		this.counts.remove(this.user);
		this.runnables.remove(this.user).stop();
	}

	@Override
	protected void insertFallbackItems() {
		ItemStack l = Item.LIGHT_GRAY_GLASS_PANE.getItem(this.user.getUser());
		for (int i = 0; i < this.SLOTS.length; i++) {
			int slot = this.SLOTS[i];
			this.fallbackItems.put(slot, l);
		}
		super.insertFallbackItems();
	}

	@Override
	protected void playSound0() {}

}
