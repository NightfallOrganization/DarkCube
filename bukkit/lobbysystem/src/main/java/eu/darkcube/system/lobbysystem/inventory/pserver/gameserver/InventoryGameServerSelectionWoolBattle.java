/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.lobbysystem.inventory.pserver.gameserver;

import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.gameregistry.RegistryEntry;
import eu.darkcube.system.userapi.User;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class InventoryGameServerSelectionWoolBattle extends InventoryGameServerSelection {

    private static final InventoryType type_gameserver_selection_woolbattle = InventoryType.of("gameserver_selection_woolbattle");

    public InventoryGameServerSelectionWoolBattle(User user) {
        super(user, Item.GAMESERVER_SELECTION_WOOLBATTLE, InventoryGameServerSelectionWoolBattle.type_gameserver_selection_woolbattle, new Sup(), new Func());
    }

    public static class Func implements BiFunction<User, RegistryEntry, ItemBuilder> {

        @Override public ItemBuilder apply(User user, RegistryEntry t) {
            var display = t.taskName() + "-" + t.data();
            return ItemBuilder.item(Item.GAMESERVER_WOOLBATTLE.getItem(user, display));
        }

    }

    public static class Sup implements Supplier<Collection<RegistryEntry>> {

        @Override public Collection<RegistryEntry> get() {
            return Lobby
                    .getInstance()
                    .getDataManager()
                    .getWoolBattleTasks()
                    .stream()
                    .flatMap(s -> Lobby.getInstance().gameRegistry().entries(s).stream())
                    .collect(Collectors.toSet());
        }
    }
}
