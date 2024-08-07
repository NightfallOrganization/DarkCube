/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.gadget;

import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.util.Item;

public enum Gadget {

    HOOK_ARROW {
        @Override public void fillExtraItems(LobbyUser user) {
            user.asPlayer().getInventory().setItem(35, Item.GADGET_HOOK_ARROW_ARROW.getItem(user.user()));
        }
    }, GRAPPLING_HOOK;

    public static Gadget fromString(String gadget) {
        for (Gadget g : Gadget.values()) {
            if (g.name().equalsIgnoreCase(gadget)) {
                return g;
            }
        }
        return HOOK_ARROW;
    }

    public void fillExtraItems(LobbyUser user) {
    }
}
