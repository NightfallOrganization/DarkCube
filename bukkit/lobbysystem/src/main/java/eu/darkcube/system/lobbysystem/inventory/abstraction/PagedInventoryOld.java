package eu.darkcube.system.lobbysystem.inventory.abstraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.UUIDManager;

public abstract class PagedInventoryOld extends Inventory {

	private static final int height = 6;

	protected int[] SLOTS;
	protected int[] SORT;
	private final Item mainItem;

	private Set<User> open = new HashSet<>();
	private Map<User, Integer> PAGE = new HashMap<>();
	private Map<User, Runnable> RUNNABLES = new HashMap<>();

	public PagedInventoryOld(String invTitle, InventoryType type, Item mainItem) {
		super(Bukkit.createInventory(null, 9 * height, invTitle), type);
		this.mainItem = mainItem;
		int center = s(1, 5);
		List<Integer> slots = new ArrayList<>();
		for (int r = 2; r <= height - 1; r++) {
			for (int i = 2; i <= 8; i++) {
				slots.add(s(r, i));
			}
		}
		SLOTS = new int[slots.size()];
		SORT = new int[SLOTS.length];
		for (int i = 0; i < SLOTS.length; i++) {
			SLOTS[i] = slots.get(i);
			SORT[i] = dist(center, SLOTS[i]);
		}
	}
	protected abstract Map<Integer, ItemStack> getContents(User user);

	protected abstract Map<Integer, ItemStack> getStaticContents(User user);

	protected abstract void onOpen(User user);

	protected abstract void onClose(User user);

	public int getPages(User user) {
		Map<Integer, ItemStack> contents = getContents(user);
		int max = contents.keySet().stream().mapToInt(i -> i).max().orElseGet(() -> -1);
		if (max == -1) {
			return 1;
		}
		return (max - 1) / SLOTS.length + 1;
	}

	@Override
	public void playAnimation(User user) {
		animate(user, false);
	}

	@Override
	public void skipAnimation(User user) {
		animate(user, true);
	}

	protected void publishUpdate(User user) {
		Optional.ofNullable(RUNNABLES.get(user)).ifPresent(r -> r.update());
	}

	public boolean fullyOpened(User user) {
		return Optional.ofNullable(RUNNABLES.get(user)).map(r -> r.fullyOpened).orElse(false);
	}

	public void setPage(User user, int page) {
		if (!open.contains(user)) {
			return;
		}
		if (page < 1)
			page = 1;
		if (page > getPages(user))
			page = getPages(user);
		PAGE.put(user, page);
		publishUpdate(user);
	}

	public int getPage(User user) {
		return PAGE.getOrDefault(user, 1);
	}

	public int getPageSize() {
		return SLOTS.length;
	}

	private void animate(User user, boolean instant) {
		Runnable runnable = getRunnable(user, instant, new HashMap<>());
		RUNNABLES.put(user, runnable);
		open.add(user);
		onOpen(user);
		runnable.runTaskTimer(Lobby.getInstance(), 1, 1);
	}

	private Runnable getRunnable(User user, boolean instant, Map<Integer, Map<Integer, ItemStack>> items) {
		if (instant) {
			user.playSound(Sound.NOTE_STICKS, 1, 1);
		}
		return new Runnable(user, instant, items);
	}

	public class Runnable extends BukkitRunnable {

//		private Set<Integer> usedSlots = new HashSet<>();
		private int count = 0;
		private final ItemStack i1;
		private final ItemStack i2;
		private User user;
		private boolean instant;
//		private Map<Integer, ItemStack> oldItems = new HashMap<>();
		public Map<Integer, Map<Integer, ItemStack>> items;
		private Map<Integer, Map<Integer, ItemStack>> newItems = new HashMap<>();
		private boolean isCancelled = false;
		private boolean updateInventory = false;
		private boolean fullyOpened = false;

		@Override
		public synchronized void cancel() throws IllegalStateException {
			open.remove(user);
			RUNNABLES.remove(user);
			PAGE.remove(user);
			isCancelled = true;
			onClose(user);
			this.defaultStatic = null;
			this.items = null;
			this.newItems = null;
			this.oldItems = null;
			this.reserved = null;
			this.staticReserved = null;
			this.updater = null;
			this.usedSlots = null;
			super.cancel();
		}

		private boolean updated = false;
		private int updaterWaiting = 0;
		private BukkitRunnable updater;

		public Runnable(User user, boolean instant, Map<Integer, Map<Integer, ItemStack>> items) {
			this.user = user;
			this.instant = instant;
			this.items = items;
			i1 = Item.LIGHT_GRAY_GLASS_PANE.getItem(user);
			i2 = Item.DARK_GRAY_GLASS_PANE.getItem(user);
			updater = new BukkitRunnable() {
				@Override
				public void run() {
					if (Runnable.this.isCancelled) {
						cancel();
						return;
					}
//					updaterWaiting--;
					if (updaterWaiting <= 0) {
						updaterWaiting = 100;
					} else {
						return;
					}
//					newItems.values().forEach(Map::clear);
					newItems.clear();
					calculateItems(user, newItems);
					PagedInventoryOld.Runnable.this.items = new HashMap<>(newItems);
					updated = true;
				}
			};
			updater.runTaskTimerAsynchronously(Lobby.getInstance(), 0, 1);
		}

		public void update() {
			updaterWaiting = 0;
		}

		private void calculateItems(User user, Map<Integer, Map<Integer, ItemStack>> finalItems) {
			finalItems.clear();

			int pages = getPages(user);
			Map<Integer, ItemStack> m = finalItems.get(10);
			if (m == null) {
				finalItems.put(10, m = new HashMap<>());
			}
			int page = PagedInventoryOld.this.getPage(user);
			if (page > pages) {
				page = pages;
				setPage(user, page);
			}
			int invsize = handle.getSize();
			if (page > 1) {
				m.put(invsize - 8, Item.PREV.getItem(user));
				m.get(invsize - 8).setAmount(page - 1);
			}
			if (page < pages) {
				m.put(invsize - 2, Item.NEXT.getItem(user));
				m.get(invsize - 2).setAmount(pages - page);
			}

			final int skip = (PagedInventoryOld.this.getPage(user) - 1) * SLOTS.length;

			Map<Integer, ItemStack> contents = getContents(user);
			Map<Integer, ItemStack> staticContents = getStaticContents(user);

			for (int slotId : contents.keySet()) {
				ItemStack item = contents.get(slotId);
				slotId -= skip;
				if (slotId >= SLOTS.length || slotId < 0) {
					continue;
				}
				int slot = SLOTS[slotId];
				m = finalItems.get(SORT[slotId]);
				if (m == null) {
					finalItems.put(SORT[slotId], m = new HashMap<>());
				}
				m.put(slot, item);
			}
			int slotCenter = s(1, 5);
			for (int slot : staticContents.keySet()) {
				int dist = dist(slotCenter, slot);
				m = finalItems.get(dist);
				if (m == null) {
					finalItems.put(dist, m = new HashMap<>());
				}
				m.put(slot, staticContents.get(slot));
			}
		}

		private void resetItems() {
			reserved.clear();
			Map<Integer, ItemStack> newItems = new HashMap<>();
			for (int id : items.keySet()) {
				Map<Integer, ItemStack> c = items.get(id);
				for (int slot : c.keySet()) {
					newItems.put(slot, c.get(slot));
				}
			}
			for (int slot : new HashSet<>(usedSlots)) {
				if (!newItems.containsKey(slot)) {
					if (oldItems.containsKey(slot)) {
						handle.setItem(slot, oldItems.get(slot));
					} else {
						handle.clear(slot);
					}
					usedSlots.remove(slot);
				}
			}
			if (fullyOpened) {
				for (int slot : newItems.keySet()) {
					setItem(slot, newItems.get(slot));
				}
			}
		}

		private Map<Integer, ItemStack> oldItems = new HashMap<>();
		private Set<Integer> usedSlots = new HashSet<>();
		private Map<Integer, ItemStack> reserved = new HashMap<>();
		private Map<Integer, ItemStack> staticReserved = new HashMap<>();
		private Map<Integer, ItemStack> defaultStatic = new HashMap<>();
		private final Set<Integer> available = new HashSet<>();

		private void setItems() {
			Map<Integer, ItemStack> current = items.get(count);
			if (current == null)
				return;
			for (int slot : current.keySet()) {
				setItem(slot, current.get(slot));
			}
		}

		private void setStaticItem(int slot, ItemStack i) {
			if (!available.contains(slot)) {
				staticReserved.put(slot, i);
				return;
			}
			handle.setItem(slot, i);
		}

		private void setStaticItemDefault(int slot, ItemStack ordefault) {
			defaultStatic.put(slot, ordefault);
			if (!staticReserved.containsKey(slot) && handle.getItem(slot) == null) {
				handle.setItem(slot, ordefault);
			}
		}

		private void setItem(int slot, ItemStack i) {
			if (!available.contains(slot)) {
				reserved.put(slot, i);
				return;
			}
			ItemStack old = handle.getItem(slot);
			if (old != null && !oldItems.containsKey(slot) && !usedSlots.contains(slot)) {
				oldItems.put(slot, old);
			}
			usedSlots.add(slot);
			handle.setItem(slot, i);

			updateInventory = true;
		}

		private void makeAvailable(int slot) {
			available.add(slot);
			if (reserved.containsKey(slot)) {
				setItem(slot, reserved.remove(slot));
			}
			if (staticReserved.containsKey(slot)) {
				setStaticItem(slot, staticReserved.remove(slot));
			}
		}

		@Override
		public void run() {
			if (user.getOpenInventory() != PagedInventoryOld.this) {
				this.cancel();
				return;
			}
			if (updated) {
				updated = false;
				if (count <= 13) {
					final int c = count;
					for (int i = 0; i < c; i++) {
						count = i;
						setItems();
					}
				}
				if (count > 0) {
					resetItems();
				}
			}
			for (int r = 1; r <= handle.getSize() / 9; r++) {
				for (int i = 1; i <= 9; i++) {
					int s = s(r, i);
					int d = dist(s(1, 5), s);
					if (d < count && !available.contains(s)) {
						makeAvailable(s);
					}
				}
			}
//			for (int slot : defaultStatic.keySet()) {
//
//			}

			switch (count) {
			case 0:
				setStaticItemDefault(4, mainItem.getItem(user));
				break;
			case 1:
				setStaticItemDefault(3, i1);
				setStaticItemDefault(5, i1);
				break;
			case 2:
				setStaticItemDefault(2, i1);
				setStaticItemDefault(6, i1);
				break;
			case 3:
				setStaticItemDefault(1, i1);
				setStaticItemDefault(7, i1);
				break;
			case 4:
				setStaticItemDefault(0, i1);
				setStaticItemDefault(8, i1);
				break;
			case 5:
				setStaticItemDefault(9, i2);
				setStaticItemDefault(17, i2);
				break;
			case 6:
				setStaticItemDefault(18, i1);
				setStaticItemDefault(26, i1);
				break;
			case 7:
				setStaticItemDefault(27, i2);
				setStaticItemDefault(35, i2);
				break;
			case 8:
				setStaticItemDefault(36, i1);
				setStaticItemDefault(44, i1);
				break;
			case 9:
				setStaticItemDefault(45, i1);
				setStaticItemDefault(53, i1);
				break;
			case 10:
				setStaticItemDefault(46, i1);
				setStaticItemDefault(52, i1);
				break;
			case 11:
				setStaticItemDefault(47, i2);
				setStaticItemDefault(51, i2);
				break;
			case 12:
				setStaticItemDefault(48, i2);
				setStaticItemDefault(50, i2);
				break;
			case 13:
				setStaticItemDefault(49, i2);
				fullyOpened = true;
				break;
			default:
				break;
			}
			if (count <= 13) {
				setItems();
				if (!instant) {
					user.playSound(Sound.NOTE_STICKS, 1, 1);
				}
			}

			count++;
			if (instant && count <= 14) {
				run();
			}
			if (updateInventory) {
				UUIDManager.getPlayerByUUID(user.getUniqueId()).updateInventory();
				updateInventory = false;
			}
		}
	}
}
