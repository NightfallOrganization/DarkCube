/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.inventoryapi.v1;

import com.google.common.util.concurrent.AtomicDouble;
import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

public abstract class AsyncPagedInventory extends AnimatedInventory {

	private static final Key META_KEY_ARROW_TYPE =
			new Key("InventoryAPI", "PagedInventory_ArrowType");
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

	//	protected final double[] PAGE_SORT;
	protected final double[] SORT;
	protected final int[] SLOTS;
	protected final int[] PAGE_SLOTS;
	protected final int pageSize;

	public AsyncPagedInventory(InventoryType inventoryType, Component title, int size,
			BooleanSupplier instant, int[] pageSlots, int startSlot) {
		super(inventoryType, title, size, instant);
		this.SLOTS = new int[size];
		for (int i = 0; i < this.SLOTS.length; i++) {
			this.SLOTS[i] = i;
		}

		this.PAGE_SLOTS = pageSlots;
		//		this.PAGE_SORT = new double[this.PAGE_SLOTS.length];
		//		for (int i = 0; i < this.PAGE_SORT.length; i++) {
		//			this.PAGE_SORT[i] = IInventory.distance(this.PAGE_SLOTS[i], startSlot);
		//		}
		this.SORT = new double[this.SLOTS.length];
		for (int i = 0; i < this.SORT.length; i++) {
			this.SORT[i] = IInventory.distance(this.SLOTS[i], startSlot);
		}
		this.pageSize = this.PAGE_SLOTS.length;

		this.updateSorts();

		this.insertDefaultItems();
		this.listener = new PagedListener();
		Bukkit.getPluginManager().registerEvents(this.listener, DarkCubePlugin.systemPlugin());
		this.startAnimation();
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

	protected void updateSorts() {
		for (int i = 0; i < this.SLOTS.length; i++) {
			int slot = this.SLOTS[i];
			double sort = this.SORT[i];
			this.updateAtSort.put(slot, sort);
		}
	}

	@Override
	protected void tick() {
		this.currentSort.addAndGet(1);
		List<Integer> updateDone = new ArrayList<>();
		for (int slot : this.updateAtSort.keySet()) {
			double sort = this.updateAtSort.get(slot);
			if (this.shouldDisplaySort(sort)) {
				this.updateSlots.offer(slot);
				updateDone.add(slot);
			}
		}
		updateDone.forEach(this.updateAtSort::remove);
		Integer slot;
		while ((slot = this.updateSlots.poll()) != null) {
			ItemStack item = this.getItem(slot);
			this.informations.add(new AnimationInformation(slot, item));
		}
		this.postTick(!updateDone.isEmpty());
	}

	@Override
	protected void asyncOfferAnimations(Collection<AnimationInformation> informations) {
		Map<Integer, ItemStack> items = new HashMap<>();
		this.fillItems(items);
		this.items.putAll(items);
		this.calculatePageItems();
	}

	@Override
	protected void destroy() {
		super.destroy();
		HandlerList.unregisterAll(this.listener);
		this.items.clear();
		this.updateSlots.clear();
	}

	protected abstract void postTick(boolean changedInformations);

	protected void calculatePageItems() {
		Set<Integer> update = new HashSet<>(this.pageItems.keySet());
		this.pageItems.clear();
		final int page = this.getPage();
		final int skip = (page - 1) * this.pageSize;
		for (Entry<Integer, ItemStack> e : this.items.entrySet()) {
			int slot = e.getKey();
			slot -= skip;
			if (slot >= this.PAGE_SLOTS.length || slot < 0) {
				// Slot is out of page range
				continue;
			}
			this.pageItems.put(this.PAGE_SLOTS[slot], e.getValue());
		}
		update.addAll(this.pageItems.keySet());
		this.updateSlots.addAll(update);
		this.calculateArrows();
	}

	protected void calculateArrows() {
		for (PageArrow arrow : new HashSet<>(this.enabledArrows)) {
			this.hideArrow(arrow);
		}
		final int page = this.getPage();
		final int maxSlot = this.items.keySet().stream().mapToInt(i -> i).max().orElse(0);
		final int maxPage = maxSlot / this.pageSize + 1;
		if (maxPage > page) {
			this.showArrow(PageArrow.NEXT);
		}
		if (page > 1) {
			this.showArrow(PageArrow.PREVIOUS);
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

	protected abstract void insertDefaultItems();

	public ItemStack getItem(int slot) {
		double sort = this.SORT[slot];
		if (!this.shouldDisplaySort(sort)) {
			return null;
		}
		if (Arrays.asList(this.arrowSlots.getOrDefault(PageArrow.PREVIOUS, new Integer[0]))
				.contains(slot) && this.isArrowVisible(PageArrow.PREVIOUS)) {
			return this.addArrowMeta(this.arrowItem.get(PageArrow.PREVIOUS), PageArrow.PREVIOUS);
		}
		if (Arrays.asList(this.arrowSlots.getOrDefault(PageArrow.NEXT, new Integer[0]))
				.contains(slot) && this.isArrowVisible(PageArrow.NEXT)) {
			return this.addArrowMeta(this.arrowItem.get(PageArrow.NEXT), PageArrow.NEXT);
		}
		return this.pageItems.getOrDefault(slot,
				this.staticItems.getOrDefault(slot, this.fallbackItems.getOrDefault(slot, null)));
	}

	private ItemStack addArrowMeta(ItemStack arrowItem, PageArrow arrow) {
		if (arrowItem == null || !arrowItem.hasItemMeta()) {
			System.out.println("Broken inventory: No arrow item " + this);
			return arrowItem;
		}
		return ItemBuilder.item(arrowItem).persistentDataStorage()
				.iset(AsyncPagedInventory.META_KEY_ARROW_TYPE, PersistentDataTypes.STRING,
						arrow.name()).builder().build();
	}

	private PageArrow getArrowType(ItemStack arrowItem) {
		Map<String, PageArrow> arrowNames = new HashMap<>();
		Arrays.asList(PageArrow.values()).forEach(a -> arrowNames.put(a.name(), a));
		return arrowNames.get(ItemBuilder.item(arrowItem).persistentDataStorage()
				.get(AsyncPagedInventory.META_KEY_ARROW_TYPE, PersistentDataTypes.STRING));
	}

	private boolean isArrowItem(ItemStack arrowItem) {
		List<String> arrowNames =
				Arrays.stream(PageArrow.values()).map(Enum::name).collect(Collectors.toList());
		ItemBuilder b = ItemBuilder.item(arrowItem);
		return b.persistentDataStorage().has(AsyncPagedInventory.META_KEY_ARROW_TYPE)
				&& arrowNames.contains(b.persistentDataStorage()
				.get(AsyncPagedInventory.META_KEY_ARROW_TYPE, PersistentDataTypes.STRING));
	}

	public boolean shouldDisplaySort(double sort) {
		return this.isInstant() || sort <= this.currentSort.get();
	}

	protected abstract void fillItems(Map<Integer, ItemStack> items);

	public int getPageSize() {
		return this.pageSize;
	}

	public int getPage() {
		return this.page.get();
	}

	public void setPage(int page) {
		this.page.set(page);
		this.calculatePageItems();
	}

	protected void handleClick(InventoryClickEvent event) {
		event.setCancelled(true);
		this.handlePageLogic(event);
	}

	protected void handlePageLogic(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();
		if (this.isArrowItem(item)) {
			PageArrow arrow = this.getArrowType(item);
			if (arrow == PageArrow.PREVIOUS) {
				this.setPage(this.getPage() - 1);
			} else if (arrow == PageArrow.NEXT) {
				this.setPage(this.getPage() + 1);
			}
		}
	}

	protected class PagedListener implements Listener {

		@EventHandler
		public void handle(InventoryClickEvent event) {
			HumanEntity human = event.getWhoClicked();
			if (AsyncPagedInventory.this.isOpened(human)) {
				if (event.getView().getTopInventory().equals(event.getClickedInventory())) {
					AsyncPagedInventory.this.handleClick(event);
				}
			}
		}

		@EventHandler
		public void handle(InventoryDragEvent event) {
			if (AsyncPagedInventory.this.isOpened(event.getWhoClicked())) {
				event.setCancelled(true);
			}
		}

	}

}
