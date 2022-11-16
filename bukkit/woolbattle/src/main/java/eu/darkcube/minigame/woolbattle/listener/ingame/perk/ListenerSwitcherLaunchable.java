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

public class ListenerSwitcherLaunchable extends Listener<LaunchableInteractEvent> {

	public static final Item SWITCHER = PerkType.SWITCHER.getItem();

	public static final Item SWITCHER_COOLDOWN = PerkType.SWITCHER.getCooldownItem();

//	private User lastUser;
//	private long lastTime = System.currentTimeMillis();

	@Override
	@EventHandler
	public void handle(LaunchableInteractEvent e) {
		if (e.getEntityType() == EntityType.SNOWBALL) {
			Player p = e.getPlayer();
			User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());

			ItemStack item = e.getItem();
			if (item == null) {
				return;
			}
			Perk perk = user.getPerkByItemId(ItemManager.getItemId(item));
			if (perk == null) {
				return;
			}
			if (ListenerSwitcherLaunchable.SWITCHER_COOLDOWN.getItemId().equals(ItemManager.getItemId(item))) {
				this.deny(user, perk);
				e.setCancelled(true);
				return;
			} else if (!ListenerSwitcherLaunchable.SWITCHER.getItemId().equals(ItemManager.getItemId(item))) {
				return;
			}
			if (perk.getCooldown() > 0 || !p.getInventory().contains(Material.WOOL, PerkType.SWITCHER.getCost())) {
				this.deny(user, perk);
				e.setCancelled(true);
				return;
			}

//			if (lastUser == user) {
//				if (System.currentTimeMillis() - lastTime < 20) {
//					lastUser = user;
//					lastTime = System.currentTimeMillis();
//					return;
//				}
//			}
//
//			lastUser = user;
//			lastTime = System.currentTimeMillis();

			Snowball ball = null;
			if (e.getEntity() != null) {
				ball = (Snowball) e.getEntity();
			} else if (item != null) {
				ball = p.launchProjectile(Snowball.class);
				e.setCancelled(true);
			}

			ItemManager.removeItems(user, p.getInventory(), user.getSingleWoolItem(), PerkType.SWITCHER.getCost());
			ball.setMetadata("perk", new FixedMetadataValue(Main.getInstance(), perk.getPerkName().getName()));

			new Scheduler() {

				int cd = perk.getMaxCooldown();

				@Override
				public void run() {
					perk.setCooldown(this.cd);
					if (this.cd <= 0) {
						this.cancel();
						return;
					}
					this.cd--;
				}

			}.runTaskTimer(20);

//			ItemManager.removeItems(p.getInventory(),
//					new ItemStack(Material.WOOL, 1, user.getTeam().getType().getWoolColor()),
//					PerkType.SWITCHER.getCost());
//			switcher.setMetadata("perk", new FixedMetadataValue(Main.getInstance(), perk.getPerkName().getName()));
//
//			new Scheduler() {
//				int cd = PerkType.SWITCHER.getCooldown() + 1;
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
//

		}
	}

//	@Override
//	@EventHandler
//	public void handle(ProjectileLaunchEvent e) {
//		if (e.getEntityType() == EntityType.SNOWBALL) {
//			Snowball switcher = (Snowball) e.getEntity();
//			if (!(switcher.getShooter() instanceof Player))
//				return;
//			Player p = (Player) switcher.getShooter();
//			User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());
//			ItemStack item = p.getItemInHand();
//			if (item == null)
//				return;
//			Perk perk = user.getPerkByItemId(ItemManager.getItemId(item));
//			if(perk == null) {
//				e.setCancelled(true);
//				return;
//			}
//			if (SWITCHER_COOLDOWN.getItemId().equals(ItemManager.getItemId(item))) {
//				Ingame.playSoundNotEnoughWool(user);
//				e.setCancelled(true);
//				new Scheduler() {
//					@Override
//					public void run() {
//						perk.setItem();
//					}
//				}.runTask();
//				return;
//			} else if (!SWITCHER.getItemId().equals(ItemManager.getItemId(item))) {
//				return;
//			}
//			if (!p.getInventory().contains(Material.WOOL, PerkType.SWITCHER.getCost()) || perk.getCooldown() > 0) {
//				e.setCancelled(true);
//				Ingame.playSoundNotEnoughWool(user);
//				new Scheduler() {
//					@Override
//					public void run() {
//						perk.setItem();
//					}
//				}.runTask();
//				return;
//			}
//			ItemManager.removeItems(p.getInventory(),
//					new ItemStack(Material.WOOL, 1, user.getTeam().getType().getWoolColor()),
//					PerkType.SWITCHER.getCost());
//			switcher.setMetadata("perk", new FixedMetadataValue(Main.getInstance(), perk.getPerkName().getName()));
//
//			new Scheduler() {
//				int cd = PerkType.SWITCHER.getCooldown() + 1;
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
//
//		}
//	}

	private void deny(User user, Perk perk) {
		Ingame.playSoundNotEnoughWool(user);
		this.setItem(perk);
	}

	private void setItem(Perk perk) {
		perk.setItem();
	}

}