package eu.darkcube.system.pserver.plugin.inventory;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.system.inventory.api.v1.InventoryType;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.user.User;

public class PServerInventory extends DefaultPServerSyncPagedInventory {

	public static final InventoryType TYPE = InventoryType.of("pserver");

	public PServerInventory(User user) {
		super(user, TYPE,
						Message.PSERVER_INVENTORY_TITLE.getMessageString(user));
	}

	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		super.fillItems(items);
		items.put(0, new ItemStack(Material.DIRT));
		items.put(15, new ItemStack(Material.DIRT));
		items.put(10, new ItemStack(Material.DIRT));
		items.put(20, new ItemStack(Material.DIRT));
		items.put(25, new ItemStack(Material.DIRT));
		items.put(30, new ItemStack(Material.DIRT));
		items.put(42, new ItemStack(Material.DIRT));
	}
}