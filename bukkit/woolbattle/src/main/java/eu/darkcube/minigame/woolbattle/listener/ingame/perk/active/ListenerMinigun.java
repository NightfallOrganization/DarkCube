/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.active;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.perks.active.MinigunPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.system.util.data.Key;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ListenerMinigun extends BasicPerkListener {

	private static final Key DATA_SCHEDULER = new Key(WoolBattle.getInstance(), "minigunScheduler");

	public ListenerMinigun() {
		super(MinigunPerk.MINIGUN);
	}

	@Override
	protected boolean activateRight(UserPerk perk) {
		WBUser user = perk.owner();
		if (user.user().getMetaDataStorage().has(DATA_SCHEDULER)) {
			return false;
		}
		user.user().getMetaDataStorage().set(DATA_SCHEDULER, new Scheduler() {
			private int count = 0;

			{
				runTaskTimer(3);
			}

			@Override
			public void cancel() {
				startCooldown(perk);
				super.cancel();
			}

			@Override
			public void run() {
				Player p = user.getBukkitEntity();
				ItemStack item = p.getItemInHand();

				if (count >= 20 || item == null || !item.equals(
						perk.currentPerkItem().calculateItem())) {
					stop(user);
					return;
				}
				count++;
				p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 3, false, false),
						true);
				Snowball s = p.getWorld().spawn(p.getEyeLocation(), Snowball.class);
				s.setShooter(p);
				s.setVelocity(p.getLocation().getDirection().multiply(2.5));
				s.setMetadata("type", new FixedMetadataValue(WoolBattle.getInstance(), "minigun"));
				payForThePerk(perk);
			}
		});

		return false;
	}

	@EventHandler
	public void handle(PlayerItemHeldEvent event) {
		WBUser user = WBUser.getUser(event.getPlayer());
		stop(user);
	}

	private void stop(WBUser user) {
		if (user.user().getMetaDataStorage().has(DATA_SCHEDULER)) {
			user.user().getMetaDataStorage().<Scheduler>remove(DATA_SCHEDULER).cancel();
		}
	}
}
