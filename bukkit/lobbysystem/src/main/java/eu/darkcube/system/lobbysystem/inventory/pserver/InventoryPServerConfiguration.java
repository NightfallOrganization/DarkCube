package eu.darkcube.system.lobbysystem.inventory.pserver;

import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.EventListener;
import eu.darkcube.system.inventory.api.util.ItemBuilder;
import eu.darkcube.system.inventory.api.v1.InventoryType;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager.PServerUserSlot;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.pserver.bukkit.event.PServerUpdateEvent;
import eu.darkcube.system.pserver.common.PServer.State;
import eu.darkcube.system.userapi.User;

public class InventoryPServerConfiguration extends LobbyAsyncPagedInventory {

	private static final InventoryType type_pserver_configuration =
			InventoryType.of("type_pserver_configuration");

	public final int psslot;

	private boolean done = false;

	public InventoryPServerConfiguration(User user, int psslot) {
		super(type_pserver_configuration,
				getDisplayName(user,
						UserWrapper.fromUser(user).getPServerUserSlots().getUserSlot(psslot)),
				user);
		this.psslot = psslot;
		CloudNetDriver.getInstance().getEventManager().registerListener(this);
		this.done = true;
		this.complete();
	}

	private static String getDisplayName(User user, PServerUserSlot psslot) {
		ItemBuilder item = PServerDataManager.getDisplayItem(user, psslot);
		return item == null ? null : item.getDisplayname();
	}

	@Override
	protected boolean done() {
		return super.done() && this.done;
	}

	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		super.fillItems(items);
		items.put(8, Item.PSERVER_DELETE.getItem(this.user.getUser()));
		// State state = this.psslot.getPServer() == null ? State.OFFLINE
		// : this.psslot.getPServer().getState();
		State state = State.OFFLINE;
		if (state == State.OFFLINE) {
			items.put(12, Item.START_PSERVER.getItem(this.user.getUser()));
		} else {
			items.put(12, Item.STOP_PSERVER.getItem(this.user.getUser()));
		}
		// if (ps != null && ps.isPrivate() != data.get("private").getAsBoolean()) {
		// data.addProperty("private", ps.isPrivate());
		// this.psslot.setChanged();
		// }
		// if (ps == null ? data.get("private").getAsBoolean() : ps.isPrivate()) {
		// items.put(10, Item.PSERVER_PRIVATE.getItem(this.user.getUser()));
		// } else {
		// items.put(10, Item.PSERVER_PUBLIC.getItem(this.user.getUser()));
		// }
	}

	@Override
	protected void destroy() {
		CloudNetDriver.getInstance().getEventManager().unregisterListener(this);
	}

	@Override
	protected void insertFallbackItems() {
		// this.fallbackItems.put(IInventory.slot(1, 5),
		// PServerDataManager.getDisplayItem(this.user.getUser(), this.psslot).build());
		super.insertFallbackItems();
	}

	@EventListener
	public void handle(PServerUpdateEvent event) {
		// if (this.psslot.getPServerId() == null) {
		// return;
		// }
		// if (!this.psslot.getPServerId().equals(event.getPServer().getId())) {
		// return;
		// }
		Bukkit.broadcastMessage("update");
		this.recalculate();
	}

}
