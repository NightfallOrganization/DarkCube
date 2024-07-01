/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.inventory;

import java.util.Map;
import java.util.UUID;

import eu.darkcube.system.bukkit.inventoryapi.v1.InventoryType;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.PServerPlugin;
import eu.darkcube.system.pserver.plugin.listener.UserManagmentInventoryListener;
import eu.darkcube.system.pserver.plugin.user.User;
import eu.darkcube.system.pserver.plugin.user.UserCache;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.meta.SkullBuilderMeta;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class UserManagmentInventory extends DefaultPServerSyncPagedInventory {

    public static final InventoryType TYPE = InventoryType.of("PServer_UserManagment");
    public static final Key KEY = Key.key(PServerPlugin.instance(), "key");
    public static final String KEY_VALUE = "UserManagmentInventoryUser";
    public static final Key USER_UUID_KEY = Key.key(PServerPlugin.instance(), "user_uuid");
    public static final Key USER_NAME_KEY = Key.key(PServerPlugin.instance(), "user_name");

    private final UserManagmentInventoryListener listener;

    public UserManagmentInventory(User user) {
        super(user, TYPE, Message.USER_MANAGMENT_INVENTORY_TITLE.getMessage(user.getCommandExecutor()));
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
            ItemBuilder builder = ItemBuilder.item(Material.SKULL_ITEM).damage(3);
            builder.meta(new SkullBuilderMeta(new SkullBuilderMeta.UserProfile(entry.name, entry.uuid)));
            builder.displayname(Component.text(entry.name).color(NamedTextColor.GRAY));
            builder.persistentDataStorage().iset(KEY, PersistentDataTypes.STRING, KEY_VALUE).iset(USER_UUID_KEY, PersistentDataTypes.STRING, entry.uuid.toString()).iset(USER_NAME_KEY, PersistentDataTypes.STRING, entry.name);
            builder.lore(Message.ITEM_LORE_USER_MANAGMENT_INVENTORY_USER.getMessage(user.getCommandExecutor(), entry.name, entry.uuid));
            items.put(index++, builder.build());
        }
    }

    @Override
    protected void destroy() {
        super.destroy();
        this.listener.unregister();
    }
}
