package eu.darkcube.system.inventory.api.v1;

import java.util.Collection;

public abstract class SyncPagedInventory extends AsyncPagedInventory {
	
	public SyncPagedInventory(InventoryType inventoryType, String title,
					int size, int[] pageSlots, int startSlot) {
		super(inventoryType, title, size, pageSlots, startSlot);
	}

	@Override
	protected void offerAnimations(
					Collection<AnimationInformation> informations) {
		asyncOfferAnimations(informations);
	}
}
