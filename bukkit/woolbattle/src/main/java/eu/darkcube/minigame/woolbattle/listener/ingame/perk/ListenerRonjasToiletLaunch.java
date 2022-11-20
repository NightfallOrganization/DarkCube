package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerRonjasToiletLaunch extends Listener<ProjectileLaunchEvent> {
	public static final Item RONJAS = PerkType.RONJAS_TOILET_SPLASH.getItem();
	public static final Item RONJAS_COOLDOWN = PerkType.RONJAS_TOILET_SPLASH.getCooldownItem();

	@Override
	@EventHandler
	public void handle(ProjectileLaunchEvent e) {
		if (e.getEntityType() == EntityType.EGG) {
			Egg egg = (Egg) e.getEntity();
			if (!(egg.getShooter() instanceof Player)) {
				return;
			}
			Player p = (Player) egg.getShooter();
			User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
			ItemStack item = p.getItemInHand();
			if (item == null)
				return;
			String itemid = ItemManager.getItemId(item);
			Perk perk = user.getPerkByItemId(itemid);
			if (perk == null) {
				return;
			}
			if (RONJAS_COOLDOWN.getItemId().equals(itemid)) {
				Ingame.playSoundNotEnoughWool(user);
				e.setCancelled(true);
				new Scheduler() {
					@Override
					public void run() {
						perk.setItem();
					}
				}.runTask();
				return;
			} else if (!RONJAS.getItemId().equals(itemid)) {
				return;
			}
			if (!p.getInventory().contains(Material.WOOL, PerkType.RONJAS_TOILET_SPLASH.getCost())) {
				Ingame.playSoundNotEnoughWool(user);
				e.setCancelled(true);
				new Scheduler() {
					@Override
					public void run() {
						perk.setItem();
					}
				}.runTask();
				return;
			}
			ItemManager.removeItems(user, p.getInventory(),
					new ItemStack(Material.WOOL, 1, user.getTeam().getType().getWoolColorByte()),
					PerkType.RONJAS_TOILET_SPLASH.getCost());

			egg.setMetadata("perk", new FixedMetadataValue(WoolBattle.getInstance(), perk.getPerkName().getName()));
			
			new Scheduler() {
				int cd = PerkType.RONJAS_TOILET_SPLASH.getCooldown() + 1;

				@Override
				public void run() {
					if (cd <= 1) {
						this.cancel();
						perk.setCooldown(0);
						return;
					}
					perk.setCooldown(--cd);
				}
			}.runTaskTimer(20);
		}
	}
}
