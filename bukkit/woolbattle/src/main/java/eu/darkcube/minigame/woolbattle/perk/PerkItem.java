/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutSetSlot;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public class PerkItem {

	public static final Key KEY_PERK_ID = new Key(WoolBattle.instance(), "perk_id");
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
		if (item == null)
			return;
		int slot = perk.slot();
		if (slot == 100) {
			perk.owner().user().asPlayer().getOpenInventory().setCursor(item);
		} else {
			perk.owner().getBukkitEntity().getHandle().defaultContainer.getBukkitView()
					.setItem(slot, item);
			updateInventory(slot, item);
		}
	}

	public ItemStack calculateItem() {
		Item item = itemSupplier.get();
		if (item == null)
			return null;
		ItemBuilder b = ItemBuilder.item(item.getItem(perk.owner()));
		int cd = (perk.cooldown() + 19) / 20;
		if (cd > 0) {
			b.amount(cd);
		} else if (perk.cooldown() == 0) {
			b.glow(true);
		}
		b.persistentDataStorage().set(KEY_PERK_ID, TYPE_PERK_ID, perk.id());
		return b.build();
	}

	private void updateInventory(int slot, ItemStack item) {
		EntityPlayer ep = perk.owner().getBukkitEntity().getHandle();
		ep.playerConnection.sendPacket(new PacketPlayOutSetSlot(ep.defaultContainer.windowId, slot,
				CraftItemStack.asNMSCopy(item)));
	}

	public UserPerk perk() {
		return perk;
	}
}
