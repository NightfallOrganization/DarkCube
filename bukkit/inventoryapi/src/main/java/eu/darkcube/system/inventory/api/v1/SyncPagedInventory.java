package eu.darkcube.system.inventory.api.v1;

import java.util.Collection;
import java.util.function.BooleanSupplier;

public abstract class SyncPagedInventory extends AsyncPagedInventory {

	public SyncPagedInventory(InventoryType inventoryType, String title, int size, BooleanSupplier instant,
			int[] pageSlots, int startSlot) {
		super(inventoryType, title, size, instant, pageSlots, startSlot);
	}

	@Override
	protected void offerAnimations(Collection<AnimationInformation> informations) {
		this.asyncOfferAnimations(informations);
	}

}
