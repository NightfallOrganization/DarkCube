package eu.darkcube.system.pserver.plugin.inventory;

import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import eu.darkcube.system.inventory.api.v1.DefaultAsyncPagedInventory;
import eu.darkcube.system.inventory.api.v1.InventoryType;
import eu.darkcube.system.inventory.api.v1.PageArrow;
import eu.darkcube.system.pserver.plugin.Item;
import eu.darkcube.system.pserver.plugin.user.User;

public class DefaultPServerAsyncPagedInventory
				extends DefaultAsyncPagedInventory {

	public final User user;
	protected final Deque<Condition> conditions;
	private boolean apply = false;

	public DefaultPServerAsyncPagedInventory(User user,
					InventoryType inventoryType, String title) {
		super(inventoryType, title);
		this.user = user;
		this.conditions = new ConcurrentLinkedDeque<>();
		addConditions(this.conditions);
		tryFillInv();
	}
	
	protected void tryFillInv() {
		insertArrowItems();
		insertDefaultItems();
		offerAnimations(informations);
	}

	protected void addConditions(Deque<Condition> conditions) {
		this.conditions.offer(() -> this.user != null);
	}

	protected boolean conditionsApply() {
		if (apply) {
			return true;
		}
		if (conditions != null) {
			for (Condition condition : conditions) {
				if (!condition.applies()) {
					return false;
				}
			}
			this.apply = true;
			return true;
		}
		return false;
	}

	public User getUser() {
		return user;
	}

	@Override
	protected void offerAnimations(
					Collection<AnimationInformation> informations) {
		if (conditionsApply()) {
			super.offerAnimations(informations);
		}
	}

	@Override
	protected void insertDefaultItems() {
		if (conditionsApply()) {
			super.insertDefaultItems();
		}
	}

	@Override
	protected void insertArrowItems() {
		if (conditionsApply()) {
			this.arrowItem.put(PageArrow.PREVIOUS, Item.ARROW_PREVIOUS.getItem(user));
			this.arrowItem.put(PageArrow.NEXT, Item.ARROW_NEXT.getItem(user));
		}
	}

	public void open() {
		this.open(user.getOnlinePlayer());
	}

	public static interface Condition {
		boolean applies();
	}
}
