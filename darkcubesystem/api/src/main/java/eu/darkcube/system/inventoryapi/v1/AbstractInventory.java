/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.inventoryapi.v1;

import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.Collection;
import java.util.HashSet;

public abstract class AbstractInventory implements IInventory {

	protected final InventoryType inventoryType;
	protected final Collection<HumanEntity> opened = new HashSet<>();
	protected final Inventory handle;
	protected final InventoryListener listener;
	protected final Component title;

	public AbstractInventory(final InventoryType inventoryType, final Component title,
			final int size) {
		this.inventoryType = inventoryType;
		this.title = title;
		this.handle = Bukkit.createInventory(null, size,
				ChatColor.stripColor(LegacyComponentSerializer.legacySection().serialize(title)));
		this.listener = new InventoryListener();
		Bukkit.getPluginManager().registerEvents(this.listener, DarkCubePlugin.systemPlugin());
	}

	public Component getTitle() {
		return title;
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
	public InventoryType getType() {
		return this.inventoryType;
	}

	@Override
	public Inventory getHandle() {
		return this.handle;
	}

	@Override
	public void open(HumanEntity player) {
		player.openInventory(this.handle);
		this.opened.add(player);
	}

	@Override
	public boolean isOpened(HumanEntity player) {
		return this.opened.contains(player);
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