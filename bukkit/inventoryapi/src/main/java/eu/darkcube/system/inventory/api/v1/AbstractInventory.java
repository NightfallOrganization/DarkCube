package eu.darkcube.system.inventory.api.v1;

import java.util.Collection;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import eu.darkcube.system.inventory.api.InventoryAPI;

public abstract class AbstractInventory implements IInventory {

	protected final InventoryType inventoryType;
	protected final Collection<HumanEntity> opened = new HashSet<>();
	protected final Inventory handle;
	protected final InventoryListener listener;

	public AbstractInventory(final InventoryType inventoryType,
					final String title, final int size) {
		this.inventoryType = inventoryType;
		this.handle = Bukkit.createInventory(null, size, ChatColor.stripColor(title));
		this.listener = new InventoryListener();
		Bukkit.getPluginManager().registerEvents(this.listener, InventoryAPI.getInstance());
	}

	@Override
	public void open(HumanEntity player) {
		player.openInventory(this.handle);
		this.opened.add(player);
	}

	protected void handleClose(HumanEntity player) {
		if (this.opened.contains(player)) {
			this.opened.remove(player);
			if (this.opened.isEmpty()) {
				this.destroy();
			}
		}
	}

	protected void destroy() {
		HandlerList.unregisterAll(this.listener);
	}

	@Override
	public boolean isOpened(HumanEntity player) {
		return this.opened.contains(player);
	}

	@Override
	public InventoryType getType() {
		return this.inventoryType;
	}

	@Override
	public Inventory getHandle() {
		return this.handle;
	}

	protected class InventoryListener implements Listener {

		@EventHandler
		public void handle(InventoryCloseEvent event) {
			if (AbstractInventory.this.isOpened(event.getPlayer())) {
				AbstractInventory.this.handleClose(event.getPlayer());
			}
		}
	}
}
