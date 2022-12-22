/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.inventoryapi.v1;

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
