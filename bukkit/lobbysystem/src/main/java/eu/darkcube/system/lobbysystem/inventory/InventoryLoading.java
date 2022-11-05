package eu.darkcube.system.lobbysystem.inventory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import eu.darkcube.system.inventory.api.util.ItemBuilder;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.abstraction.Inventory;
import eu.darkcube.system.lobbysystem.inventory.abstraction.InventoryType;
import eu.darkcube.system.lobbysystem.inventory.abstraction.PagedInventory;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.PositionedIterator;

public class InventoryLoading extends PagedInventory {

	private final Function<User, Inventory> inventoryFunction;
	private final Map<User, Map<Integer, ItemStack>> contents = new HashMap<>();
	private final Map<User, Integer> counts = new HashMap<>();
	private final Map<User, Runner> runnables = new HashMap<>();

	private final PositionedIterator<ItemStack> iterator;
	private static final int[] MAP = new int[] {
			Inventory.s(2, 4), Inventory.s(2, 5), Inventory.s(2, 6), Inventory.s(3, 6), Inventory.s(4, 6), Inventory.s(4, 5), Inventory.s(4, 4), Inventory.s(3, 4)
	};

	public InventoryLoading(String title, Function<User, Inventory> inventoryFunction) {
		super(title, null, 5 * 9, InventoryType.LOADING, PagedInventory.box(1, 1, 5, 9));
		Arrays.fill(this.SORT, 0);
		Arrays.fill(this.TOTAL_SORT, 0);
		this.inventoryFunction = inventoryFunction;

		final ItemStack loading = new ItemBuilder(Material.BARRIER).displayname(" ").build();
		this.iterator = new PositionedIterator<>(new ItemStack[] {
				loading, loading, loading, loading, loading, null, null, null
		});
	}

	private class Runner {

		private BukkitRunnable run;

		public Runner(User user) {
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
						InventoryLoading.this.update(user);
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
	protected Map<Integer, ItemStack> getContents(User user) {
		if (this.contents.containsKey(user)) {
			return this.contents.get(user);
		}
		return new HashMap<>();
	}

	@Override
	protected void onOpen(User user) {
		if (this.contents.containsKey(user)) {
			return;
		}
		this.contents.put(user, new HashMap<>());
		this.counts.put(user, 0);
		this.runnables.put(user, new Runner(user));
		new BukkitRunnable() {
			@Override
			public void run() {
				Inventory inv = InventoryLoading.this.inventoryFunction.apply(user);
				user.setOpenInventory(inv);
			}
		}.runTaskAsynchronously(Lobby.getInstance());
	}

	@Override
	protected void onClose(User user) {
		if (!this.contents.containsKey(user)) {
			return;
		}
		this.contents.remove(user);
		this.counts.remove(user);
		this.runnables.remove(user).stop();
	}

	@Override
	protected Map<Integer, ItemStack> getStaticContents(User user) {
		return new HashMap<>();
	}

	@Override
	protected void insertDefaultItems(InventoryManager manager) {
		ItemStack l = Item.LIGHT_GRAY_GLASS_PANE.getItem(manager.user);
		for (int i = 0; i < this.TOTAL_SLOTS.length; i++) {
			int slot = this.TOTAL_SLOTS[i];
			manager.setFallbackItem(slot, l);
		}
		manager.playSound = false;
		super.insertDefaultItems(manager);
	}
}
