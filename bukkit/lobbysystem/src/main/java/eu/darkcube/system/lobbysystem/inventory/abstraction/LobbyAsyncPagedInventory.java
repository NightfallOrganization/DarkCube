package eu.darkcube.system.lobbysystem.inventory.abstraction;

import java.util.Collection;

import eu.darkcube.system.inventory.api.v1.DefaultAsyncPagedInventory;
import eu.darkcube.system.inventory.api.v1.InventoryType;
import eu.darkcube.system.lobbysystem.user.User;

public abstract class LobbyAsyncPagedInventory extends DefaultAsyncPagedInventory {

	protected final User user;

	private boolean done = false;

	public LobbyAsyncPagedInventory(InventoryType inventoryType, String title, User user) {
		super(inventoryType, title, () -> !user.isAnimations());
		this.user = user;
		this.complete();
	}

	public LobbyAsyncPagedInventory(InventoryType inventoryType, String title, int size, int[] box,
					User user) {
		super(inventoryType, title, size, box, () -> !user.isAnimations());
		this.user = user;
		this.complete();
	}

	@Override
	protected final void playSound() {
		if (user.isSounds()) {
			playSound0();
		}
	}

	protected void playSound0() {
		super.playSound();
	}

	protected void complete() {
		this.done = true;
		this.insertDefaultItems();
		this.offerAnimations(this.informations);
	}

	public User getUser() {
		return this.user;
	}

	protected boolean done() {
		return this.done;
	}

	@Override
	protected final void insertDefaultItems() {
		if (this.done()) {
			this.insertDefaultItems0();
		}
	}

	protected void insertDefaultItems0() {
		super.insertDefaultItems();
	}

	protected void asyncOfferAnimations0(Collection<AnimationInformation> informations) {
		super.asyncOfferAnimations(informations);
	}

	protected void offerAnimations0(Collection<AnimationInformation> informations) {
		super.offerAnimations(informations);
	}

	@Override
	protected final void asyncOfferAnimations(Collection<AnimationInformation> informations) {
		if (this.done()) {
			this.asyncOfferAnimations0(informations);
		}
	}

	@Override
	protected final void offerAnimations(Collection<AnimationInformation> informations) {
		if (this.done()) {
			this.offerAnimations0(informations);
		}
	}

}
