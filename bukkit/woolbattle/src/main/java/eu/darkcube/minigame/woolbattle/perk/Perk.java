package eu.darkcube.minigame.woolbattle.perk;

import java.util.Collection;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.observable.ObservableInteger;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutSetSlot;

public interface Perk {

	int getMaxCooldown();

	boolean hasCooldown();

	Item getCooldownItem();

	Item getItem();

	default void setItem() {
		if (getOwner().getPerk(getPerkNumber()) == this) {
			if (getSlot() == 100) {
				getOwner().getBukkitEntity().getOpenInventory().setCursor(calculateItem());
			} else {
				getOwner().getBukkitEntity().getHandle().defaultContainer.getBukkitView().setItem(getSlot(),
						calculateItem());
			}
			updateInventory();
		}
	}

	default void updateInventory() {
		CraftPlayer p = getOwner().getBukkitEntity();
		EntityPlayer ep = p.getHandle();
		ep.playerConnection.sendPacket(new PacketPlayOutSetSlot(ep.defaultContainer.windowId,
				getSlot(), CraftItemStack.asNMSCopy(calculateItem())));
	}

	ItemStack calculateItem();

	PerkName getPerkName();

	String getDisplayName();

	ObservableInteger getSlotLink();

	void setSlot(int slot);

	void setSlotSilent(int slot);

	int getSlot();

	ObservableInteger getCooldownLink();

	void setCooldown(int cooldown);

	int getCooldown();

	User getOwner();

	boolean isHardCooldown();

	void setHardCooldown(boolean hardCooldown);

	PerkNumber getPerkNumber();

	public static Collection<PerkType> getAllPerks() {
		return Arrays.asList(PerkType.values());
	}
}
