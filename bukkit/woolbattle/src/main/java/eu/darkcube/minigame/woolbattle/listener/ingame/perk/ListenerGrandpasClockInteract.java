package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerGrandpasClockInteract extends Listener<PlayerInteractEvent> {

	public static final Item CLOCK = PerkType.GRANDPAS_CLOCK.getItem();
	public static final Item CLOCK_COOLDOWN = PerkType.GRANDPAS_CLOCK.getCooldownItem();

	public Map<User, Location> oldLoc = new HashMap<>();

	@Override
	@EventHandler
	public void handle(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player p = e.getPlayer();
			ItemStack item = e.getItem();
			if (item == null) {
				return;
			}
			User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());
			String itemid = ItemManager.getItemId(item);
			Perk perk = user.getPerkByItemId(itemid);
			if (perk == null) {
				return;
			}
			if (CLOCK_COOLDOWN.getItemId().equals(itemid)) {
				e.setCancelled(true);
				deny(user, perk);
				return;
			} else if (!CLOCK.getItemId().equals(itemid)) {
				return;
			}
			e.setCancelled(true);
			if (oldLoc.get(user) != null) {
				teleport(user, perk);
				return;
			}
			if (!p.getInventory().contains(Material.WOOL, PerkType.GRANDPAS_CLOCK.getCost())) {
				deny(user, perk);
				return;
			}
			ItemManager.removeItems(user, p.getInventory(), user.getSingleWoolItem(),
					PerkType.GRANDPAS_CLOCK.getCost());
			oldLoc.put(user, p.getLocation());
			new Scheduler() {
				int c = 0;

				@Override
				public void run() {
					if (oldLoc.get(user) != null) {
						teleport(user, perk);
					}
					c++;
					if (c % 15 == 0) {
						p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
					}
				}
			}.runTaskLater(60);

		}
	}

	private void teleport(User user, Perk perk) {
		user.getBukkitEntity().teleport(oldLoc.remove(user));
		user.getBukkitEntity().playSound(user.getBukkitEntity().getBedSpawnLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
		perk.setCooldown(perk.getMaxCooldown());
		new Scheduler() {
			int cd = perk.getMaxCooldown();

			@Override
			public void run() {
				if (cd <= 0) {
					perk.setCooldown(0);
					this.cancel();
					return;
				}
				perk.setCooldown(cd--);
			}
		}.runTaskTimer(20);
	}

	private void deny(User user, Perk perk) {
		Ingame.playSoundNotEnoughWool(user);
		setItem(perk);
	}

	private void setItem(Perk perk) {
		perk.getOwner().getBukkitEntity().setItemInHand(perk.calculateItem());
	}
}