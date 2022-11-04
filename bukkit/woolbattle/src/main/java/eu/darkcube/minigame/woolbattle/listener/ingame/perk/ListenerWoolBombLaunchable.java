package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.event.LaunchableInteractEvent;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerWoolBombLaunchable extends Listener<LaunchableInteractEvent> {
	public static final Item BOMB = PerkType.WOOL_BOMB.getItem();
	public static final Item BOMB_COOLDOWN = PerkType.WOOL_BOMB.getCooldownItem();

//	@Override
//	@EventHandler
//	public void handle(ProjectileLaunchEvent e) {
//		if (e.getEntityType() == EntityType.SNOWBALL) {
//			Snowball bomb = (Snowball) e.getEntity();
//			if (!(bomb.getShooter() instanceof Player)) {
//				return;
//			}
//			Player p = (Player) bomb.getShooter();
//			User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());
//			ItemStack item = p.getItemInHand();
//			if (item == null)
//				return;
//			String itemid = ItemManager.getItemId(item);
//			Perk perk = user.getPerkByItemId(itemid);
//			if (perk == null) {
//				e.setCancelled(true);
//				return;
//			}

	@Override
	@EventHandler
	public void handle(LaunchableInteractEvent e) {
		if (e.getEntityType() == EntityType.SNOWBALL) {
			Player p = e.getPlayer();
			User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());

			ItemStack item = e.getItem() == null ? p.getItemInHand() : e.getItem();
			if (item == null) {
				return;
			}
			String itemid = ItemManager.getItemId(item);
			Perk perk = user.getPerkByItemId(itemid);
			if (perk == null) {
				return;
			}
			if (BOMB_COOLDOWN.getItemId().equals(itemid)) {
				deny(user, perk);
				e.setCancelled(true);
				return;
			} else if (!BOMB.getItemId().equals(itemid)) {
				return;
			}
			if (perk.getCooldown() > 0 || !p.getInventory().contains(Material.WOOL, PerkType.WOOL_BOMB.getCost())) {
				deny(user, perk);
				e.setCancelled(true);
				return;
			}

			Snowball bomb = null;
			if (e.getEntity() != null) {
				bomb = (Snowball) e.getEntity();
			} else {
				bomb = p.launchProjectile(Snowball.class);
				e.setCancelled(true);
			}
			ItemManager.removeItems(user, p.getInventory(), user.getSingleWoolItem(), PerkType.WOOL_BOMB.getCost());
			bomb.setMetadata("perk", new FixedMetadataValue(Main.getInstance(), perk.getPerkName().getName()));

			new Scheduler() {
				int cd = perk.getMaxCooldown();

				@Override
				public void run() {
					if (cd <= 0) {
						this.cancel();
						perk.setCooldown(0);
						return;
					}
					perk.setCooldown(cd--);
				}
			}.runTaskTimer(20);
		}
	}
//			if (BOMB_COOLDOWN.getItemId().equals(itemid)) {
//				Ingame.playSoundNotEnoughWool(user);
//				e.setCancelled(true);
//				new Scheduler() {
//					@Override
//					public void run() {
//						perk.setItem();
//					}
//				}.runTaskLater(2);
//				return;
//			} else if (!BOMB.getItemId().equals(itemid)) {
//				return;
//			}
//			if (!p.getInventory().contains(Material.WOOL, PerkType.SWITCHER.getCost()) || perk.getCooldown() > 0) {
//				Ingame.playSoundNotEnoughWool(user);
//				e.setCancelled(true);
//				new Scheduler() {
//					@Override
//					public void run() {
//						perk.setItem();
//					}
//				}.runTaskLater(2);
//				return;
//			}
//			ItemManager.removeItems(p.getInventory(),
//					new ItemStack(Material.WOOL, 1, user.getTeam().getType().getWoolColor()),
//					PerkType.WOOL_BOMB.getCost());
//
//			bomb.setMetadata("perk", new FixedMetadataValue(Main.getInstance(), perk.getPerkName().getName()));
//
//			new Scheduler() {
//				int cd = PerkType.WOOL_BOMB.getCooldown() + 1;
//
//				@Override
//				public void run() {
//					if (cd <= 1) {
//						this.cancel();
//						perk.setCooldown(0);
//						return;
//					}
//					perk.setCooldown(--cd);
//				}
//			}.runTaskTimer(20);
//		}
//	}

	private void deny(User user, Perk perk) {
		Ingame.playSoundNotEnoughWool(user);
		setItem(perk);
	}

	private void setItem(Perk perk) {
		perk.getOwner().getBukkitEntity().setItemInHand(perk.calculateItem());
	}
}
