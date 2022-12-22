/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.inventory;

import eu.darkcube.system.inventoryapi.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.IInventory;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.pserver.plugin.Item;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.user.User;
import eu.darkcube.system.pserver.plugin.user.UserManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Deque;
import java.util.Map;
import java.util.UUID;

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
