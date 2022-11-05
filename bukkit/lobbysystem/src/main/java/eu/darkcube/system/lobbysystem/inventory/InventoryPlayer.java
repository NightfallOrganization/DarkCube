package eu.darkcube.system.lobbysystem.inventory;

import eu.darkcube.system.lobbysystem.inventory.abstraction.Inventory;
import eu.darkcube.system.lobbysystem.inventory.abstraction.InventoryType;
import eu.darkcube.system.lobbysystem.user.User;

public class InventoryPlayer extends Inventory {

	public InventoryPlayer() {
		super(null, InventoryType.PLAYER);
	}

	@Override
	public void playAnimation(User user) {
		
	}

	@Override
	public void skipAnimation(User user) {
		
	}
}