package eu.darkcube.system.pserver.plugin.inventory;

import java.util.Collection;

import eu.darkcube.system.inventory.api.v1.InventoryType;
import eu.darkcube.system.pserver.plugin.user.User;

public class DefaultPServerSyncPagedInventory
				extends DefaultPServerAsyncPagedInventory {

	public DefaultPServerSyncPagedInventory(User user,
					InventoryType inventoryType, String title) {
		super(user, inventoryType, title);
	}
	
	@Override
	protected void offerAnimations(
					Collection<AnimationInformation> informations) {
		if(conditionsApply()) {
			super.asyncOfferAnimations(informations);
		}
	}
}
