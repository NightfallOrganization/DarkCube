package eu.darkcube.system.lobbysystem.user;

import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import eu.darkcube.system.inventory.api.v1.IInventory;
import eu.darkcube.system.language.core.Language;
import eu.darkcube.system.lobbysystem.gadget.Gadget;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.UUIDManager;

public class CachedUser extends User {

	public CachedUser(Language language, Gadget gadget, boolean sounds, boolean animations, UUID uuid,
			long lastDailyReward, IInventory openInventory, Set<Integer> rewardSlotsUsed) {
		super(language, gadget, sounds, animations, uuid, lastDailyReward, openInventory, rewardSlotsUsed);
	}

	@Override
	public User setGadget(Gadget gadget) {
		Player p = UUIDManager.getPlayerByUUID(this.uuid);
		if (p != null) {
			p.getInventory().setItem(4, Item.byGadget(gadget).getItem(this));
		}
		this.save();
		return super.setGadget(gadget);
	}
}
