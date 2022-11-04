package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerBoosterInteract extends Listener<PlayerInteractEvent> {

	public static final Item BOOSTER = PerkType.BOOSTER.getItem();
	public static final Item BOOSTER_COOLDOWN = PerkType.BOOSTER.getCooldownItem();

	@Override
	@EventHandler
	public void handle(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player p = e.getPlayer();
			ItemStack item = e.getItem();
			if (item == null)
				return;
			User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());
			Perk perk = user.getPerkByItemId(ItemManager.getItemId(item));
			if (BOOSTER_COOLDOWN.getItemId().equals(ItemManager.getItemId(item))) {
				Ingame.playSoundNotEnoughWool(user);
				new Scheduler() {
					@Override
					public void run() {
						perk.setItem();
					}
				}.runTask();
				e.setCancelled(true);
				return;
			} else if (!BOOSTER.getItemId().equals(ItemManager.getItemId(item))) {
				return;
			} else if (perk == null) {
				e.setCancelled(true);
				return;
			}
			e.setCancelled(true);
			if (!p.getInventory().contains(Material.WOOL, PerkType.BOOSTER.getCost()) || perk.getCooldown() > 0) {
				Ingame.playSoundNotEnoughWool(user);
				new Scheduler() {
					@Override
					public void run() {
						perk.setItem();
					}
				}.runTask();
				return;
			}
			ItemManager.removeItems(user, p.getInventory(),
					new ItemStack(Material.WOOL, 1, user.getTeam().getType().getWoolColor()),
					PerkType.BOOSTER.getCost());

			Vector velo =
					p.getLocation().getDirection().setY(p.getLocation().getDirection().getY() + 0.3).multiply(2.7);
			velo.setY(velo.getY() / 1.8);

			p.setVelocity(velo);
			new Scheduler() {
				int cd = PerkType.BOOSTER.getCooldown() + 1;

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
