/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons;

import eu.darkcube.system.bukkit.inventoryapi.v1.InventoryType;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserModifier;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.vanillaaddons.inventory.Inventory;

public class AUser {
    private final VanillaAddons addons;
    private final User user;
    private volatile InventoryType openInventory = null;
    private volatile Inventory<?> openInventoryHandle = null;
    private volatile boolean openingInventory = false;

    public AUser(VanillaAddons addons, User user) {
        this.addons = addons;
        this.user = user;
    }

    public static AUser user(User user) {
        return user.metadata().get(new Key(VanillaAddons.getPlugin(VanillaAddons.class).getName(), "user"));
    }

    public InventoryType openInventory() {
        return openInventory;
    }

    public Inventory<?> openInventoryHandle() {
        return openInventoryHandle;
    }

    public <Data> void openInventory(InventoryType openInventory, Data data) {
        if (openingInventory && openInventory == null) return;
        this.openInventory = openInventory;
        if (openInventory != null) {
            openingInventory = true;
            if (this.openInventoryHandle != null) {
                Inventory<?> h = openInventoryHandle;
                this.openInventoryHandle = null;
                h.close(this);
            }
            this.openInventoryHandle = addons.inventoryRegistry().newInventory(openInventory, data);
            this.openInventoryHandle.open(this);
            openingInventory = false;
        } else if (this.openInventoryHandle != null) {
            openingInventory = true;
            Inventory<?> h = openInventoryHandle;
            this.openInventoryHandle = null;
            h.close(this);
            openingInventory = false;
        }
    }

    public User user() {
        return user;
    }

    public VanillaAddons addons() {
        return addons;
    }

    public static class Modifier implements UserModifier {
        private final VanillaAddons addons;
        private final Key auser;

        public Modifier(VanillaAddons addons) {
            this.addons = addons;
            this.auser = new Key(addons.getName(), "user");
        }

        @Override public void onLoad(User user) {
            user.metadata().set(auser, new AUser(addons, user));
        }

        @Override public void onUnload(User user) {
            user.metadata().remove(auser);
        }
    }
}
