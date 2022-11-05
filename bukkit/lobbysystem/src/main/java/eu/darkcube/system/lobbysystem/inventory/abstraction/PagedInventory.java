package eu.darkcube.system.lobbysystem.inventory.abstraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.UUIDManager;

public abstract class PagedInventory extends Inventory {

	protected Item mainItem;
	protected int[] TOTAL_SLOTS;
	protected int[] TOTAL_SORT;
	protected int[] SLOTS;
	protected int[] SORT;
	protected int maxSort;
	protected int maxTotalSort;
	protected Set<User> open = new HashSet<>();
	protected Map<User, Integer> page = new HashMap<>();
	protected Map<User, InventoryManager> managers = new HashMap<>();

	public PagedInventory(String title, Item mainItem, int size, InventoryType type, int... pageSlots) {
		super(Bukkit.createInventory(null, size, title), type);
		if (size % 9 != 0) {
			throw new IllegalArgumentException("Illegal size: " + size);
		}
		this.mainItem = mainItem;

		int start = Inventory.s(1, 5);

		this.SLOTS = new int[pageSlots.length];
		this.SORT = new int[this.SLOTS.length];
		for (int i = 0; i < this.SLOTS.length; i++) {
			this.SLOTS[i] = pageSlots[i];
			this.SORT[i] = Inventory.dist(start, this.SLOTS[i]);
		}
		List<Integer> l = new ArrayList<>();
		for (int s : this.SORT)
			l.add(s);
		this.maxSort = l.stream().mapToInt(i -> i).max().orElse(0);

		this.TOTAL_SLOTS = new int[size];
		this.TOTAL_SORT = new int[this.TOTAL_SLOTS.length];
		int[] allslots = PagedInventory.box(1, 1, size / 9, 9);
		for (int i = 0; i < this.TOTAL_SLOTS.length; i++) {
			this.TOTAL_SLOTS[i] = allslots[i];
			this.TOTAL_SORT[i] = Inventory.dist(start, this.TOTAL_SLOTS[i]);
		}
		l = new ArrayList<>();
		for (int s : this.TOTAL_SORT)
			l.add(s);
		this.maxTotalSort = l.stream().mapToInt(i -> i).max().orElse(0);

	}

	public PagedInventory(String title, Item mainItem, int size, InventoryType type) {
		this(title, mainItem, size, type, PagedInventory.box(3, 2, 5, 8));
	}

	public PagedInventory(String title, int size, InventoryType type) {
		this(title, null, size, type);
	}

	public int getPages(User user) {
		Map<Integer, ItemStack> contents = this.getContents(user);
		int max = contents.keySet().stream().mapToInt(i -> i).max().orElse(-1);
		if (max == -1)
			return 1;
		return (max) / this.SLOTS.length + 1;
	}

	public void setPage(User user, int page) {
		if (!this.hasOpened(user)) {
			return;
		}
		if (page < 1) {
			page = 1;
		}
		int pages = this.getPages(user);
		if (page > pages) {
			page = pages;
		}
		this.page.put(user, page);
		this.update(user);
	}

	public boolean hasOpened(User user) {
		return this.open.contains(user);
	}

	public int getPage(User user) {
		return this.page.getOrDefault(user, 1);
	}

	public int getPageSize() {
		return this.SLOTS.length;
	}

	public int getTotalPageSize() {
		return this.TOTAL_SLOTS.length;
	}

	protected void update(User user) {
		this.getManager(user).ifPresent(InventoryManager::update);
	}

	private void load(User user, boolean animation) {
		InventoryManager m = new InventoryManager(user, animation);
		this.getManager(user).ifPresent(InventoryManager::cancel);
		this.managers.put(user, m);
		m.runTaskTimer(Lobby.getInstance(), 1, 1);
	}

	private Optional<InventoryManager> getManager(User user) {
		return Optional.ofNullable(this.managers.get(user));
	}

	@Override
	public void playAnimation(User user) {
		this.load(user, true);
	}

	@Override
	public void skipAnimation(User user) {
		this.load(user, false);
	}

	protected abstract void onOpen(User user);

	protected abstract void onClose(User user);

	protected abstract Map<Integer, ItemStack> getContents(User user);

	protected abstract Map<Integer, ItemStack> getStaticContents(User user);

	protected void insertDefaultItems(InventoryManager manager) {

	}

	public class InventoryManager extends BukkitRunnable {
		public boolean playSound = true;
		public final User user;
		public boolean animate = false;
		public boolean updateRequired = false;
		public int ticksOpen = -2;
		public Set<ArrowType> enabled = new HashSet<>();
		public Map<ArrowType, Integer[]> arrowSlots = new HashMap<>();
		public Map<Integer, ItemStack> fallbackItems = new HashMap<>();
		public Map<Integer, ItemStack> staticItems = new HashMap<>();
		public Map<Integer, ItemStack> pageItems = new HashMap<>();
		public Runnable updater;
		public boolean updaterUpdate = false;
		public boolean updaterUpdated = false;

		private InventoryManager(User user, boolean animate) {
			this.user = user;
			this.animate = animate;

			this.updater = new Runnable() {

				@Override
				public void run() {
					if (InventoryManager.this.updaterUpdate) {
						InventoryManager.this.updaterUpdate = false;
					} else {
						return;
					}

					this.calc();

					InventoryManager.this.updaterUpdated = true;
				}

				private void calc() {
					int pages = PagedInventory.this.getPages(user);
					int page = PagedInventory.this.getPage(user);
					if (page > pages) {
						page = pages;
						PagedInventory.this.setPage(user, page);
					} else if (page < 1) {
						page = 1;
					}
					if (page == 1) {
						InventoryManager.this.hideArrow(ArrowType.PREV);
					} else {
						InventoryManager.this.showArrow(ArrowType.PREV);
					}
					if (page >= pages) {
						InventoryManager.this.hideArrow(ArrowType.NEXT);
					} else {
						InventoryManager.this.showArrow(ArrowType.NEXT);
					}
					Set<Integer> pagedSlots = new HashSet<>();
					for (int slot : PagedInventory.this.SLOTS) {
						pagedSlots.add(slot);
					}

					final int skip = (page - 1) * PagedInventory.this.SLOTS.length;

					Map<Integer, ItemStack> contents = PagedInventory.this.getContents(user);
					Map<Integer, ItemStack> staticContents = PagedInventory.this.getStaticContents(user);

					for (ArrowType type : InventoryManager.this.arrowSlots.keySet()) {
						if (!InventoryManager.this.enabled.contains(type)) {
							String l = Item.getItemId(InventoryManager.this.getLeftArrowItem());
							String r = Item.getItemId(InventoryManager.this.getRightArrowItem());
							for (int slot : InventoryManager.this.arrowSlots.get(type)) {
								if (InventoryManager.this.staticItems.containsKey(slot)) {
									ItemStack i = InventoryManager.this.staticItems.get(slot);
									String itemid = Item.getItemId(i);
									if (itemid.equals(type == ArrowType.PREV ? l : r)) {
										InventoryManager.this.staticItems.remove(slot);
									}
								}
							}
							continue;
						}
						for (int slot : InventoryManager.this.arrowSlots.get(type)) {
							if (!InventoryManager.this.staticItems.containsKey(slot)) {
								InventoryManager.this.staticItems.put(slot,
										type == ArrowType.PREV ? InventoryManager.this.getLeftArrowItem() : InventoryManager.this.getRightArrowItem());
							}
						}
					}

					InventoryManager.this.pageItems.clear();
					for (int slotId : contents.keySet()) {
						ItemStack item = contents.get(slotId);
						slotId -= skip;
						if (slotId >= PagedInventory.this.SLOTS.length || slotId < 0) {
							continue;
						}
						int slot = PagedInventory.this.SLOTS[slotId];
						InventoryManager.this.pageItems.put(slot, item);
					}

					InventoryManager.this.staticItems.clear();
					for (int slotId : staticContents.keySet()) {
						ItemStack item = staticContents.get(slotId);
						slotId -= skip;
						if (slotId >= PagedInventory.this.TOTAL_SLOTS.length || slotId < 0) {
							continue;
						}
						int slot = PagedInventory.this.TOTAL_SLOTS[slotId];
						InventoryManager.this.staticItems.put(slot, item);
					}

				}
			};
//			updater.runTaskTimer(Lobby.getInstance(), 1, 1);
		}

		@Override
		public synchronized void cancel() throws IllegalStateException {
			PagedInventory.this.onClose(this.user);

//			updater.cancel();
			this.updater = null;

			this.enabled.clear();
			this.enabled = null;
			this.arrowSlots.clear();
			this.arrowSlots = null;
			this.fallbackItems.clear();
			this.fallbackItems = null;
			this.staticItems.clear();
			this.staticItems = null;
			this.pageItems.clear();
			this.pageItems = null;

			this.ticksOpen = 0;

			PagedInventory.this.page.remove(this.user);
			PagedInventory.this.managers.remove(this.user);
			PagedInventory.this.open.remove(this.user);
			super.cancel();
		}

		@Override
		public void run() {
			if (this.user.getOpenInventory() != PagedInventory.this && this.ticksOpen > 0) {
				this.cancel();
				return;
			}
			if (this.ticksOpen == 0) {
				PagedInventory.this.open.add(this.user);
				PagedInventory.this.onOpen(this.user);
				PagedInventory.this.insertDefaultItems(this);
				this.updateInSync();
				if (!this.animate && this.playSound) {
					this.user.playSound(Sound.NOTE_STICKS, 1, 1);
				}
			}
			this.ticksOpen++;
			if (this.updateRequired) {
				this.updateRequired = false;
				this.updaterUpdated = false;
				this.updaterUpdate = true;
				this.updater.run();
//				updateInSync();
			}
			if (this.updaterUpdated) {
				this.updaterUpdated = false;
				this.updateInSync();
			} else if (this.ticksOpen <= PagedInventory.this.maxTotalSort) {
				this.updateInSync();
				if (this.animate && this.playSound) {
					this.user.playSound(Sound.NOTE_STICKS, 1, 1);
				}
			}
		}

		public void update() {
			this.updateRequired = true;
		}

		public void updateInSync() {
			Player p = UUIDManager.getPlayerByUUID(this.user.getUniqueId());
			for (int slot = 0; slot < PagedInventory.this.handle.getSize(); slot++) {
				PagedInventory.this.handle.setItem(slot, this.getItem(slot));
			}
			p.updateInventory();
		}

		public void setStaticItem(int slot, ItemStack item) {
			if (item != null) {
				this.staticItems.put(slot, item);
			} else if (this.staticItems.containsKey(slot)) {
				this.staticItems.remove(slot);
			} else {
				return;
			}
			this.update();
		}

		public void setPageItem(int slot, ItemStack item) {
			if (item != null) {
				this.pageItems.put(slot, item);
			} else if (this.pageItems.containsKey(slot)) {
				this.pageItems.remove(slot);
			} else {
				return;
			}
			this.update();
		}

		public void setFallbackItem(int slot, ItemStack item) {
			if (item != null) {
				this.fallbackItems.put(slot, item);
			} else if (this.fallbackItems.containsKey(slot)) {
				this.fallbackItems.remove(slot);
			} else {
				return;
			}
			this.update();
		}

		public void showArrow(ArrowType type) {
			this.enabled.add(type);
		}

		public void hideArrow(ArrowType type) {
			this.enabled.remove(type);
		}

		public ItemStack getLeftArrowItem() {
			return Item.PREV.getItem(this.user);
		}

		public ItemStack getRightArrowItem() {
			return Item.NEXT.getItem(this.user);
		}

		public ItemStack getItem(int slot) {
			int sort = PagedInventory.this.TOTAL_SORT[slot];
			if (this.animate && sort > this.ticksOpen) {
				return null;
			}
			return this.getItemUnsafe(slot);
		}

		public ItemStack getItemUnsafe(int slot) {
			if (Arrays.asList(this.arrowSlots.getOrDefault(ArrowType.PREV, new Integer[0])).contains(slot)
					&& this.enabled.contains(ArrowType.PREV)) {
				return this.getLeftArrowItem();
			}
			if (Arrays.asList(this.arrowSlots.getOrDefault(ArrowType.NEXT, new Integer[0])).contains(slot)
					&& this.enabled.contains(ArrowType.NEXT)) {
				return this.getRightArrowItem();
			}
			ItemStack i = null;
			if (this.fallbackItems.containsKey(slot)) {
				i = this.fallbackItems.get(slot);
			}
			if (this.staticItems.containsKey(slot)) {
				i = this.staticItems.get(slot);
			}
			if (this.pageItems.containsKey(slot)) {
				i = this.pageItems.get(slot);
			}
			return i;
		}
	}

	public static enum ArrowType {
		PREV, NEXT;
	}

	protected static Map<Integer, ItemStack> convert(Map<Integer, Item> map, User user) {
		Map<Integer, ItemStack> m = new HashMap<>();
		for (int k : map.keySet()) {
			m.put(k, map.get(k).getItem(user));
		}
		return m;
	}

	protected static int[] box(int r1, int i1, int r2, int i2) {
		final int fr1 = Math.min(r1, r2);
		final int fr2 = Math.max(r1, r2);
		final int fi1 = Math.min(i1, i2);
		final int fi2 = Math.max(i1, i2);
		int[] res = new int[(fr2 - fr1 + 1) * (fi2 - fi1 + 1)];
		int index = 0;
		for (int r = fr1; r <= fr2; r++) {
			for (int i = fi1; i <= fi2; i++, index++) {
				res[index] = Inventory.s(r, i);
			}
		}
		return res;
	}
}
