package eu.darkcube.system.pserver.plugin.inventory;

import java.util.*;

import org.bukkit.*;
import org.bukkit.inventory.*;

import eu.darkcube.system.inventory.api.util.*;
import eu.darkcube.system.inventory.api.v1.*;
import eu.darkcube.system.pserver.plugin.*;
import eu.darkcube.system.pserver.plugin.user.*;

public class UserManagmentUserInventory extends DefaultPServerSyncPagedInventory {

	public static final InventoryType TYPE = InventoryType.of("UserManagmentUserInventory");
	private final UUID targetUUID;
	private final User target;

	@SuppressWarnings("deprecation")
	public UserManagmentUserInventory(User user, UUID targetUUID, String targetName) {
		super(user, TYPE, Message.USER_MANAGMENT_USER_INVENTORY_TITLE.getMessageString(user, targetName));
		this.targetUUID = targetUUID;
		this.target = UserManager.getInstance().getUser(this.targetUUID);
		this.staticItems.put(IInventory.slot0(5, 1),
				new ItemBuilder(Material.SKULL_ITEM).durability(3).displayname(ChatColor.GRAY + targetName)
						.owner(targetName).lore(Message.ITEM_LORE_USER_MANAGMENT_INVENTORY_USER
								.getMessageString(user, targetName, targetUUID).split("\n"))
						.build());
		tryFillInv();
	}

	@Override
	protected void addConditions(Deque<Condition> conditions) {
		super.addConditions(conditions);
		conditions.add(() -> target != null);
	}

	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		items.put(3, Item.USER_MANAGMENT_PERMISSIONS.getItem(user));
	}
}
