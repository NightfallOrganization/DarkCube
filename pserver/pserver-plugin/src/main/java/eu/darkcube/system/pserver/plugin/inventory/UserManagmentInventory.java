/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.inventory;

import eu.darkcube.system.inventoryapi.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.listener.UserManagmentInventoryListener;
import eu.darkcube.system.pserver.plugin.user.User;
import eu.darkcube.system.pserver.plugin.user.UserCache;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class UserManagmentInventory extends DefaultPServerSyncPagedInventory {

	public static final InventoryType TYPE = InventoryType.of("PServer_UserManagment");
	public static final String KEY = "KEY";
	public static final String KEY_VALUE = "UserManagmentInventoryUser";
	public static final String USER_UUID_KEY = "user-uuid";
	public static final String USER_NAME_KEY = "user-name";

	private final UserManagmentInventoryListener listener;

	public UserManagmentInventory(User user) {
		super(user, TYPE, Message.USER_MANAGMENT_INVENTORY_TITLE.getMessageString(user));
		this.listener = new UserManagmentInventoryListener(this);
		this.listener.register();
	}

	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		int index = 0;
		for (UUID uuid : UserCache.cache().getKeys()) {
			UserCache.Entry entry = UserCache.cache().getEntry(uuid);
			if (entry == null) {
				// Entry is not valid any more.
				continue;
			}
			ItemBuilder builder = new ItemBuilder(Material.SKULL_ITEM).durability(3);
			builder.owner(entry.name).displayname(ChatColor.GRAY + entry.name);
			builder.getUnsafe().setString(KEY, KEY_VALUE).setString(USER_UUID_KEY, entry.uuid.toString())
					.setString(USER_NAME_KEY, entry.name);
			builder.lore(Message.ITEM_LORE_USER_MANAGMENT_INVENTORY_USER.getMessageString(user, entry.name, entry.uuid)
					.split("(\r\n|\r|\n)"));
			items.put(index++, builder.build());
		}
	}

	@Override
	protected void destroy() {
		super.destroy();
		this.listener.unregister();
	}
}
