/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.lobbysystem.inventory.pserver.gameserver;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.provider.ServiceTaskProvider;
import eu.cloudnetservice.driver.service.ServiceTask;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.userapi.User;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class InventoryGameServerSelectionWoolBattle extends InventoryGameServerSelection {

    private static final InventoryType type_gameserver_selection_woolbattle = InventoryType.of("gameserver_selection_woolbattle");

    public InventoryGameServerSelectionWoolBattle(User user) {
        super(user, Item.GAMESERVER_SELECTION_WOOLBATTLE, InventoryGameServerSelectionWoolBattle.type_gameserver_selection_woolbattle, new Sup(), new Func());
    }

    public static class Func implements BiFunction<User, ServiceTask, ItemBuilder> {

        private static final Pattern pattern = Pattern.compile("\\d");

        @Override public ItemBuilder apply(User user, ServiceTask t) {
            Matcher matcher = Func.pattern.matcher(t.name());
            String text;
            if (matcher.find()) {
                text = t.name().substring(matcher.start());
            } else {
                text = "Invalid Task!";
            }
            return ItemBuilder.item(Item.GAMESERVER_WOOLBATTLE.getItem(user, text));
        }

    }

    public static class Sup implements Supplier<Collection<ServiceTask>> {

        @Override public Collection<ServiceTask> get() {
            ServiceTaskProvider prov = InjectionLayer.boot().instance(ServiceTaskProvider.class);
            return Lobby
                    .getInstance()
                    .getDataManager()
                    .getWoolBattleTasks()
                    .stream()
                    .filter(s -> prov.serviceTask(s) != null)
                    .map(prov::serviceTask)
                    .collect(Collectors.toList());
        }

    }

}
