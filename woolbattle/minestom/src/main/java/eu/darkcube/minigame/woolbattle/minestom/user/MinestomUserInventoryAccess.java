/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.user;

import eu.darkcube.minigame.woolbattle.api.user.WoolSubtractDirection;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.user.UserInventoryAccess;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.world.MinestomColoredWool;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.material.Material;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.inventory.TransactionOption;
import net.minestom.server.inventory.TransactionType;
import net.minestom.server.item.ItemStack;

public class MinestomUserInventoryAccess implements UserInventoryAccess {
    private final @NotNull MinestomWoolBattle woolbattle;
    private final @NotNull CommonWBUser user;

    public MinestomUserInventoryAccess(@NotNull MinestomWoolBattle woolbattle, @NotNull CommonWBUser user) {
        this.woolbattle = woolbattle;
        this.user = user;
    }

    @Override
    public void woolCount(int count) {
        final var player = woolbattle.player(user);
        var team = user.team();
        if (team == null) return;
        player.acquirable().sync(e -> {
            var p = (Player) e;
            var inventory = p.getInventory();
            var wool = (MinestomColoredWool) team.wool();

            var woolCount = 0;
            for (var itemStack : inventory.getItemStacks()) {
                var material = Material.of(itemStack.material());
                if (!material.equals(wool.material())) continue;
                woolCount += itemStack.amount();
            }

            var difference = count - woolCount;
            if (difference < 0) {
                // remove items
                removeItems(inventory, wool.createSingleItem().build(), -difference, user.woolSubtractDirection());
            } else if (difference > 0) {
                // add items
                inventory.addItemStack(wool.createSingleItem().amount(difference).build(), TransactionOption.ALL);
            }
        });
    }

    private static void removeItems(PlayerInventory inventory, ItemStack itemToRemove, int count, WoolSubtractDirection direction) {
        // var toDelete = count;
        // var deleted = 0;
        // for (var i = count - 1; i >= 0; i--) {
        //     do {
        //         var last = last(inventory, itemToRemove);
        //         if (last == -1) {
        //             break;
        //         }
        //         var item = inventory.getItemStack(last);
        //         var amount = item.amount();
        //         if (amount <= toDelete) {
        //             toDelete -= amount;
        //             inventory.takeItemStack()
        //         }
        //     } while (toDelete > 0);
        // }
        removeItems(inventory, itemToRemove.withAmount(count), inventory.getSize(), direction == WoolSubtractDirection.RIGHT_TO_LEFT);
    }

    private static void removeItems(@NotNull PlayerInventory inventory, @NotNull ItemStack itemToRemove, int size, boolean reverse) {
        // var slots = new ArrayList<Integer>(size);
        // if (reverse) {
        //     for (var i = size - 1; i >= 0; i--) {
        //         slots.add(i);
        //     }
        // } else {
        //     for (var i = 0; i < size; i++) {
        //         slots.add(i);
        //     }
        // }
        // var type = TransactionType.take(slots);
        // TransactionOption.ALL.fill(type, inventory, itemToRemove);
        var start = reverse ? inventory.getInnerSize() : 0;
        var end = reverse ? 0 : inventory.getInnerSize();
        var pair = reverse ? TAKE.process(inventory, itemToRemove, (_, _) -> true, start, end, -1) : TransactionType.TAKE.process(inventory, itemToRemove, (_, _) -> true, start, end, 1);
        TransactionOption.ALL.fill(inventory, pair.left(), pair.right());
    }

    // private static int last(PlayerInventory inventory, ItemStack item) {
    //     if (item == null) return -1;
    //     var contents = inventory.getItemStacks();
    //     for (var i = contents.length - 1; i >= 0; i--) {
    //         if (contents[i] != null && item.isSimilar(contents[i])) {
    //             return i;
    //         }
    //     }
    //     return -1;
    // }
    private static final TransactionType TAKE = (inventory, itemStack, slotPredicate, start, end, step) -> {
        Int2ObjectMap<ItemStack> itemChangesMap = new Int2ObjectOpenHashMap<>();
        for (var i = start; i >= end; i += step) {
            final var inventoryItem = inventory.getItemStack(i);
            if (inventoryItem.isAir()) continue;
            if (itemStack.isSimilar(inventoryItem)) {
                if (!slotPredicate.test(i, inventoryItem)) {
                    // Cancelled transaction
                    continue;
                }

                final var itemAmount = inventoryItem.amount();
                final var itemStackAmount = itemStack.amount();
                if (itemStackAmount < itemAmount) {
                    itemChangesMap.put(i, inventoryItem.withAmount(itemAmount - itemStackAmount));
                    itemStack = ItemStack.AIR;
                    break;
                }
                itemChangesMap.put(i, ItemStack.AIR);
                itemStack = itemStack.withAmount(itemStackAmount - itemAmount);
                if (itemStack.amount() == 0) {
                    itemStack = ItemStack.AIR;
                    break;
                }
            }
        }
        return Pair.of(itemStack, itemChangesMap);
    };
}
