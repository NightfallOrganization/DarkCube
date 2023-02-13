/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.active;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.perks.active.GrapplingHookPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import org.bukkit.Location;
import org.bukkit.entity.FishHook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class ListenerGrapplingHook extends BasicPerkListener {

	public ListenerGrapplingHook() {
		super(GrapplingHookPerk.GRAPPLING_HOOK);
	}

	@Override
	protected boolean activateRight(UserPerk perk) {
		FishHook hook = perk.owner().getBukkitEntity().launchProjectile(FishHook.class);
		hook.setVelocity(hook.getVelocity().multiply(1.5));
		hook.setMetadata("perk", new FixedMetadataValue(WoolBattle.getInstance(), perk));
		return false;
	}

	@EventHandler
	public void handle(PlayerFishEvent event) {
		FishHook hook = event.getHook();
		PlayerFishEvent.State state = event.getState();
		if (!hook.hasMetadata("perk")) {
			return;
		}
		Object objectPerk = hook.getMetadata("perk").get(0).value();
		if (!(objectPerk instanceof UserPerk))
			return;
		UserPerk perk = (UserPerk) objectPerk;
		if (!perk.perk().equals(perk()))
			return;
		if (state == PlayerFishEvent.State.IN_GROUND || state == PlayerFishEvent.State.CAUGHT_ENTITY
				|| hook.getLocation().subtract(0, 1, 0).getBlock().getType().isSolid()) {
			if (!checkUsable(perk)) {
				hook.remove();
				event.setCancelled(true);
				return;
			}
			Location from = perk.owner().getBukkitEntity().getLocation();
			Location to = hook.getLocation();
			Vector v = to.toVector().subtract(from.toVector()).add(new Vector(0, 3, 0));
			double multiplier = Math.pow(v.length(), 0.35);
			v = v.normalize().multiply(new Vector(multiplier, multiplier * 1.1, multiplier));
			perk.owner().getBukkitEntity().setVelocity(v);
			startCooldown(perk);
			hook.remove();
			event.setCancelled(true);
		}
	}

	//			ItemManager.removeItems(user, p.getInventory(),
	//					new ItemStack(Material.WOOL, 1, user.getTeam().getType().getWoolColorByte()),
	//					PerkType.GRAPPLING_HOOK.getCost());
	//
	//			Location from = p.getLocation();
	//			Location to = hook.getLocation();
	//
	//			double x = to.getX() - from.getX();
	//			double y = to.getY() - from.getY() + 3;
	//			double z = to.getZ() - from.getZ();
	//			Vector v = new Vector(x, y, z);
	//			double mult = Math.pow(v.length(), 0.35);
	//			v = v.normalize().multiply(new Vector(mult, mult * 1.1, mult));
	//
	//			p.setVelocity(v);
	//
	//			startCooldown(perk);
	//		}
	//	}
}
