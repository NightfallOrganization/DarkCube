/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.perk;

import java.util.function.Supplier;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.perk.Perk.Cooldown.Unit;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutSetSlot;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class PerkItem {

    public static final Key KEY_PERK_ID = Key.key(WoolBattleBukkit.instance(), "perk_id");
    public static final PersistentDataType<Integer> TYPE_PERK_ID = PersistentDataTypes.INTEGER;

    private final Supplier<Item> itemSupplier;
    private final UserPerk perk;

    public PerkItem(Supplier<Item> itemSupplier, UserPerk perk) {
        this.itemSupplier = itemSupplier;
        this.perk = perk;
    }

    /**
     * Sets the item in the perk's owner's inventory
     */
    public void setItem() {
        ItemStack item = calculateItem();
        if (item == null) return;
        int slot = perk.slot();
        if (slot == 100) {
            perk.owner().getBukkitEntity().getOpenInventory().setCursor(item);
        } else {
            perk.owner().getBukkitEntity().getHandle().defaultContainer.getBukkitView().setItem(slot, item);
            updateInventory(slot, item);
        }
    }

    public ItemStack calculateItem() {
        Item item = itemSupplier.get();
        if (item == null) return null;
        ItemBuilder b = ItemBuilder.item(item.getItem(perk.owner()));
        int amt = itemAmount();
        if (amt > 0) {
            b.amount(Math.min(amt, 65));
        } else if (amt == 0) {
            b.glow(true);
        }
        b.persistentDataStorage().set(KEY_PERK_ID, TYPE_PERK_ID, perk.id());
        modify(b);
        return b.build();
    }

    protected void modify(ItemBuilder item) {
    }

    protected int itemAmount() {
        return perk.perk().cooldown().unit() == Unit.TICKS ? (perk.cooldown() + 19) / 20 : perk.cooldown();
    }

    private void updateInventory(int slot, ItemStack item) {
        EntityPlayer ep = perk.owner().getBukkitEntity().getHandle();
        ep.playerConnection.sendPacket(new PacketPlayOutSetSlot(ep.defaultContainer.windowId, slot, CraftItemStack.asNMSCopy(item)));
    }

    public UserPerk perk() {
        return perk;
    }
}
