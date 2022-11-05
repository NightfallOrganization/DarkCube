package eu.darkcube.system.lobbysystem.inventory.pserver;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonObject;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.EventListener;
import eu.darkcube.system.inventory.api.util.ItemBuilder;
import eu.darkcube.system.lobbysystem.inventory.abstraction.DefaultPagedInventory;
import eu.darkcube.system.lobbysystem.inventory.abstraction.Inventory;
import eu.darkcube.system.lobbysystem.inventory.abstraction.InventoryType;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager.PServerUserSlots.PServerUserSlot;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.pserver.common.PServer;
import eu.darkcube.system.pserver.common.PServer.State;
import eu.darkcube.system.pserver.wrapper.event.PServerUpdateEvent;

public class InventoryPServerConfiguration extends DefaultPagedInventory {

	public final PServerUserSlot psslot;

	public InventoryPServerConfiguration(User user, PServerUserSlot psslot) {
		super(InventoryPServerConfiguration.getDisplayName(user, psslot), InventoryType.PSERVER_CONFIGURATION);
		this.psslot = psslot;
		CloudNetDriver.getInstance().getEventManager().registerListener(this);
	}

	private static String getDisplayName(User user, PServerUserSlot psslot) {
		ItemBuilder item = PServerDataManager.getDisplayItem(user, psslot);
		return item == null ? null : item.getDisplayname();
	}

	@Override
	protected Map<Integer, ItemStack> contents(User user) {
		Map<Integer, ItemStack> m = new HashMap<>();
		m.put(8, Item.PSERVER_DELETE.getItem(user));
		State state = this.psslot.getPServer() == null ? State.OFFLINE : this.psslot.getPServer().getState();
		if (state == State.OFFLINE) {
			m.put(12, Item.START_PSERVER.getItem(user));
		} else {
			m.put(12, Item.STOP_PSERVER.getItem(user));
		}
		JsonObject data = this.psslot.getData();
		PServer ps = this.psslot.getPServer();
		if (ps != null && ps.isPrivate() != data.get("private").getAsBoolean()) {
			data.addProperty("private", ps.isPrivate());
			this.psslot.setChanged();
		}
		if (ps == null ? data.get("private").getAsBoolean() : ps.isPrivate()) {
			m.put(10, Item.PSERVER_PRIVATE.getItem(user));
		} else {
			m.put(10, Item.PSERVER_PUBLIC.getItem(user));
		}
		return m;
	}

	@Override
	protected void onClose0(User user) {
		CloudNetDriver.getInstance().getEventManager().unregisterListener(this);
	}

	@Override
	protected void insertDefaultItems(InventoryManager manager) {
		super.insertDefaultItems(manager);
		manager.setFallbackItem(Inventory.s(1, 5), PServerDataManager.getDisplayItem(manager.user, this.psslot).build());
	}

	@EventListener
	public void handle(PServerUpdateEvent event) {
		if (this.psslot.getPServerId() == null) {
			return;
		}
		if (!this.psslot.getPServerId().equals(event.getPServer().getId())) {
			return;
		}
		this.recalculateAll();
	}
}
