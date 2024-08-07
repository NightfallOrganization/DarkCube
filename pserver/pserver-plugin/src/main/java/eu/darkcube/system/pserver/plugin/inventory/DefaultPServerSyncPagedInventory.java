/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.inventory;

import java.util.Collection;

import eu.darkcube.system.bukkit.inventoryapi.v1.InventoryType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.pserver.plugin.user.User;

public class DefaultPServerSyncPagedInventory extends DefaultPServerAsyncPagedInventory {

    public DefaultPServerSyncPagedInventory(User user, InventoryType inventoryType, Component title) {
        super(user, inventoryType, title);
    }

    @Override
    protected void offerAnimations(Collection<AnimationInformation> informations) {
        if (conditionsApply()) {
            super.asyncOfferAnimations(informations);
        }
    }
}
