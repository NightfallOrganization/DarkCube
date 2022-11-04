package eu.darkcube.system.inventory.api.v1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.util.concurrent.AtomicDouble;

import eu.darkcube.system.inventory.api.InventoryAPI;
import eu.darkcube.system.inventory.api.util.ItemBuilder;

public abstract class AsyncPagedInventory extends AnimatedInventory {

	protected final AtomicInteger page = new AtomicInteger(1);
	protected final ConcurrentMap<Integer, ItemStack> items = new ConcurrentHashMap<>();
	protected final ConcurrentMap<Integer, ItemStack> pageItems = new ConcurrentHashMap<>();
	protected final ConcurrentMap<Integer, ItemStack> fallbackItems = new ConcurrentHashMap<>();
	protected final ConcurrentMap<Integer, ItemStack> staticItems = new ConcurrentHashMap<>();
	protected final ConcurrentMap<PageArrow, Integer[]> arrowSlots = new ConcurrentHashMap<>();
	protected final ConcurrentMap<PageArrow, ItemStack> arrowItem = new ConcurrentHashMap<>();
	protected final ConcurrentMap<Integer, Double> updateAtSort = new ConcurrentHashMap<>();
	protected final Set<PageArrow> enabledArrows = Collections.synchronizedSet(new HashSet<>());
	protected final Queue<Integer> updateSlots = new ConcurrentLinkedQueue<>();
	protected final AtomicDouble currentSort = new AtomicDouble();
	protected final PagedListener listener;
	protected final double[] SORT;
	protected final double[] PAGE_SORT;
	protected final int[] SLOTS;
	protected final int[] PAGE_SLOTS;
	protected final int pageSize;

	private static final String META_KEY_ARROW_TYPE = "InventoryAPI_PagedInventory_ArrowType";

	public AsyncPagedInventory(InventoryType inventoryType, String title,
					int size, int[] pageSlots, int startSlot) {
		super(inventoryType, title, size);
		SLOTS = new int[size];
		for (int i = 0; i < SLOTS.length; i++) {
			SLOTS[i] = i;
		}

		PAGE_SLOTS = pageSlots;
		PAGE_SORT = new double[PAGE_SLOTS.length];
		for (int i = 0; i < PAGE_SORT.length; i++) {
			PAGE_SORT[i] = IInventory.distance(PAGE_SLOTS[i], startSlot);
		}
		SORT = new double[SLOTS.length];
		for (int i = 0; i < SORT.length; i++) {
			SORT[i] = IInventory.distance(SLOTS[i], startSlot);
		}
		pageSize = PAGE_SLOTS.length;

		for (int i = 0; i < SLOTS.length; i++) {
			int slot = SLOTS[i];
			double sort = SORT[i];
			updateAtSort.put(slot, sort);
		}

		this.insertDefaultItems();
		listener = new PagedListener();
		Bukkit.getPluginManager().registerEvents(listener, InventoryAPI.getInstance());
		startAnimation();
	}

	@Override
	protected void tick() {
		this.currentSort.addAndGet(1);
		List<Integer> updateDone = new ArrayList<>();
		for (int slot : updateAtSort.keySet()) {
			double sort = updateAtSort.get(slot);
			if (shouldDisplaySort(sort)) {
				this.updateSlots.offer(slot);
				updateDone.add(slot);
			}
		}
		updateDone.forEach(updateAtSort::remove);
		Integer slot;
		while ((slot = updateSlots.poll()) != null) {
			ItemStack item = getItem(slot);
			this.informations.add(new AnimationInformation(slot, item));
		}
		postTick(!updateDone.isEmpty());
	}

	protected abstract void postTick(boolean changedInformations);

	protected void calculatePageItems() {
		Set<Integer> update = new HashSet<>();
		update.addAll(this.pageItems.keySet());
		this.pageItems.clear();
		final int page = getPage();
		final int skip = (page - 1) * pageSize;
		for (Entry<Integer, ItemStack> e : items.entrySet()) {
			int slot = e.getKey();
			slot -= skip;
			if (slot >= PAGE_SLOTS.length || slot < 0) {
				// Slot is out of page range
				continue;
			}
			pageItems.put(PAGE_SLOTS[slot], e.getValue());
		}
		update.addAll(this.pageItems.keySet());
		this.updateSlots.addAll(update);
		calculateArrows();
	}

	protected void calculateArrows() {
		for (PageArrow arrow : new HashSet<>(enabledArrows)) {
			hideArrow(arrow);
		}
		final int page = getPage();
		final int maxSlot = this.items.keySet().stream().mapToInt(i -> i).max().orElse(0);
		final int maxPage = maxSlot / pageSize + 1;
		if (maxPage > page) {
			showArrow(PageArrow.NEXT);
		}
		if (page > 1) {
			showArrow(PageArrow.PREVIOUS);
		}
	}

	public void showArrow(PageArrow arrow) {
		this.enabledArrows.add(arrow);
		this.updateSlots.addAll(Arrays.asList(this.arrowSlots.get(arrow)));
	}

	public void hideArrow(PageArrow arrow) {
		this.enabledArrows.remove(arrow);
		this.updateSlots.addAll(Arrays.asList(this.arrowSlots.get(arrow)));
	}

	public boolean isArrowVisible(PageArrow arrow) {
		return this.enabledArrows.contains(arrow);
	}

	public void setPage(int page) {
		this.page.set(page);
		calculatePageItems();
	}

	@Override
	protected void destroy() {
		super.destroy();
		HandlerList.unregisterAll(listener);
		this.items.clear();
		this.updateSlots.clear();
	}

	protected abstract void insertDefaultItems();

	public ItemStack getItem(int slot) {
		double sort = SORT[slot];
		if (!shouldDisplaySort(sort)) {
			return null;
		}
		if (Arrays.asList(arrowSlots.getOrDefault(PageArrow.PREVIOUS, new Integer[0])).contains(slot)
						&& isArrowVisible(PageArrow.PREVIOUS)) {
			return addArrowMeta(arrowItem.get(PageArrow.PREVIOUS), PageArrow.PREVIOUS);
		}
		if (Arrays.asList(arrowSlots.getOrDefault(PageArrow.NEXT, new Integer[0])).contains(slot)
						&& isArrowVisible(PageArrow.NEXT)) {
			return addArrowMeta(arrowItem.get(PageArrow.NEXT), PageArrow.NEXT);
		}
		return pageItems.getOrDefault(slot, staticItems.getOrDefault(slot, fallbackItems.getOrDefault(slot, null)));
	}

	private ItemStack addArrowMeta(ItemStack arrowItem, PageArrow arrow) {
		return new ItemBuilder(
						arrowItem).getUnsafe().setString(META_KEY_ARROW_TYPE, arrow.name()).builder().build();
	}

	private PageArrow getArrowType(ItemStack arrowItem) {
		Map<String, PageArrow> arrowNames = new HashMap<>();
		Arrays.asList(PageArrow.values()).stream().forEach(a -> arrowNames.put(a.name(), a));
		return arrowNames.get(new ItemBuilder(
						arrowItem).getUnsafe().getString(META_KEY_ARROW_TYPE));
	}

	private boolean isArrowItem(ItemStack arrowItem) {
		List<String> arrowNames = Arrays.asList(PageArrow.values()).stream().map(Enum::name).collect(Collectors.toList());
		ItemBuilder b = new ItemBuilder(arrowItem);
		return b.getUnsafe().getString(META_KEY_ARROW_TYPE) != null
						&& arrowNames.contains(b.getUnsafe().getString(META_KEY_ARROW_TYPE));
	}

	public boolean shouldDisplaySort(double sort) {
		return sort <= this.currentSort.get();
	}

	@Override
	public void open(HumanEntity player) {
		super.open(player);
	}

	@Override
	protected void asyncOfferAnimations(
					Collection<AnimationInformation> informations) {
		Map<Integer, ItemStack> items = new HashMap<>();
		fillItems(items);
		this.items.putAll(items);
		this.calculatePageItems();
	}

	protected abstract void fillItems(Map<Integer, ItemStack> items);

	public int getPageSize() {
		return pageSize;
	}

	public int getPage() {
		return this.page.get();
	}

	@Override
	protected void handleClose(HumanEntity player) {
		super.handleClose(player);
	}

	protected void handleClick(InventoryClickEvent event) {
		event.setCancelled(true);
		handlePageLogic(event);
	}

	protected void handlePageLogic(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();
		if (isArrowItem(item)) {
			PageArrow arrow = getArrowType(item);
			if (arrow == PageArrow.PREVIOUS) {
				setPage(getPage() - 1);
			} else if (arrow == PageArrow.NEXT) {
				setPage(getPage() + 1);
			}
		}
	}

	protected class PagedListener implements Listener {

		@EventHandler
		public void handle(InventoryClickEvent event) {
			HumanEntity human = event.getWhoClicked();
			if (isOpened(human)) {
				if (event.getView().getTopInventory().equals(event.getClickedInventory())) {
					handleClick(event);
				}
			}
		}

		@EventHandler
		public void handle(InventoryDragEvent event) {
			if (isOpened(event.getWhoClicked())) {
				event.setCancelled(true);
			}
		}
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
				res[index] = IInventory.slot(r, i);
			}
		}
		return res;
	}
}
