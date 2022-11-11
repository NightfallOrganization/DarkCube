package eu.darkcube.system.lobbysystem.inventory;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;

import eu.darkcube.system.inventory.api.v1.IInventory;
import eu.darkcube.system.inventory.api.v1.InventoryType;

public class InventoryPlayer implements IInventory {

	private static final InventoryType player = InventoryType.of("player");

	@Override
	public InventoryType getType() {
		return InventoryPlayer.player;
	}

	@Override
	public Inventory getHandle() {
		return null;
	}

	@Override
	public void open(HumanEntity player) {
	}

	@Override
	public boolean isOpened(HumanEntity player) {
		return true;
	}

}