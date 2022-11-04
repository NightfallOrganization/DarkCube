package eu.darkcube.system.inventory.api.v1;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class DefaultAsyncPagedInventory extends AsyncPagedInventory {

	public DefaultAsyncPagedInventory(InventoryType inventoryType,
					String title) {
		super(inventoryType, title, 6 * 9, box(3, 2, 5, 8),
						IInventory.slot(1, 5));
	}

	@Override
	protected void postTick(boolean changedInformations) {
		if (changedInformations) {
			playSound();
		}
	}

	protected void playSound() {
		this.opened.stream().filter(p -> p instanceof Player).map(p -> (Player) p).forEach(p -> {
			p.playSound(p.getLocation(), Sound.NOTE_STICKS, 100, 1);
		});
	}

	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {

	}

	protected abstract void insertArrowItems();

	@Override
	protected void insertDefaultItems() {
		this.insertArrowItems();
		ItemStack l = new ItemStack(Material.STAINED_GLASS_PANE);
		l.setDurability((short) 7);
		ItemMeta meta = l.getItemMeta();
		meta.setDisplayName("§6");
		l.setItemMeta(meta);
		ItemStack d = new ItemStack(Material.STAINED_GLASS_PANE);
		d.setDurability((short) 15);
		d.setItemMeta(meta);
		this.fallbackItems.put(IInventory.slot0(1, 1), l);
		this.fallbackItems.put(IInventory.slot0(2, 1), l);
		this.fallbackItems.put(IInventory.slot0(3, 1), l);
		this.fallbackItems.put(IInventory.slot0(4, 1), l);
		this.fallbackItems.put(IInventory.slot0(5, 1), d);
		this.fallbackItems.put(IInventory.slot0(6, 1), l);
		this.fallbackItems.put(IInventory.slot0(7, 1), l);
		this.fallbackItems.put(IInventory.slot0(8, 1), l);
		this.fallbackItems.put(IInventory.slot0(9, 1), l);

		this.fallbackItems.put(IInventory.slot0(1, 2), d);
		this.fallbackItems.put(IInventory.slot0(2, 2), d);
		this.fallbackItems.put(IInventory.slot0(3, 2), d);
		this.fallbackItems.put(IInventory.slot0(4, 2), l);
		this.fallbackItems.put(IInventory.slot0(5, 2), l);
		this.fallbackItems.put(IInventory.slot0(6, 2), l);
		this.fallbackItems.put(IInventory.slot0(7, 2), d);
		this.fallbackItems.put(IInventory.slot0(8, 2), d);
		this.fallbackItems.put(IInventory.slot0(9, 2), d);

		this.fallbackItems.put(IInventory.slot0(1, 3), l);
		this.fallbackItems.put(IInventory.slot0(2, 3), d);
		this.fallbackItems.put(IInventory.slot0(3, 3), d);
		this.fallbackItems.put(IInventory.slot0(4, 3), d);
		this.fallbackItems.put(IInventory.slot0(5, 3), l);
		this.fallbackItems.put(IInventory.slot0(6, 3), d);
		this.fallbackItems.put(IInventory.slot0(7, 3), d);
		this.fallbackItems.put(IInventory.slot0(8, 3), d);
		this.fallbackItems.put(IInventory.slot0(9, 3), l);

		this.fallbackItems.put(IInventory.slot0(1, 4), d);
		this.fallbackItems.put(IInventory.slot0(2, 4), l);
		this.fallbackItems.put(IInventory.slot0(3, 4), d);
		this.fallbackItems.put(IInventory.slot0(4, 4), l);
		this.fallbackItems.put(IInventory.slot0(5, 4), d);
		this.fallbackItems.put(IInventory.slot0(6, 4), l);
		this.fallbackItems.put(IInventory.slot0(7, 4), d);
		this.fallbackItems.put(IInventory.slot0(8, 4), l);
		this.fallbackItems.put(IInventory.slot0(9, 4), d);

		this.fallbackItems.put(IInventory.slot0(1, 5), l);
		this.fallbackItems.put(IInventory.slot0(2, 5), d);
		this.fallbackItems.put(IInventory.slot0(3, 5), l);
		this.fallbackItems.put(IInventory.slot0(4, 5), d);
		this.fallbackItems.put(IInventory.slot0(5, 5), l);
		this.fallbackItems.put(IInventory.slot0(6, 5), d);
		this.fallbackItems.put(IInventory.slot0(7, 5), l);
		this.fallbackItems.put(IInventory.slot0(8, 5), d);
		this.fallbackItems.put(IInventory.slot0(9, 5), l);

		this.fallbackItems.put(IInventory.slot0(1, 6), l);
		this.fallbackItems.put(IInventory.slot0(2, 6), l);
		this.fallbackItems.put(IInventory.slot0(3, 6), d);
		this.fallbackItems.put(IInventory.slot0(4, 6), d);
		this.fallbackItems.put(IInventory.slot0(5, 6), d);
		this.fallbackItems.put(IInventory.slot0(6, 6), d);
		this.fallbackItems.put(IInventory.slot0(7, 6), d);
		this.fallbackItems.put(IInventory.slot0(8, 6), l);
		this.fallbackItems.put(IInventory.slot0(9, 6), l);

		arrowSlots.put(PageArrow.PREVIOUS, new Integer[] {
						IInventory.slot(3, 1), IInventory.slot(4, 1),
						IInventory.slot(5, 1)
		});
		arrowSlots.put(PageArrow.NEXT, new Integer[] {
						IInventory.slot(3, 9), IInventory.slot(4, 9),
						IInventory.slot(5, 9)
		});
	}
}