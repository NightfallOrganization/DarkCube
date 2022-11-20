package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerRopeInteract extends Listener<PlayerInteractEvent> {
	public static final Item ROPE = PerkType.ROPE.getItem();
	public static final Item ROPE_COOLDOWN = PerkType.ROPE.getCooldownItem();

	@Override
	@EventHandler
	public void handle(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_AIR
						|| e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player p = e.getPlayer();
			ItemStack item = e.getItem();
			if (item == null)
				return;
			User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
			Perk perk = user.getPerkByItemId(ItemManager.getItemId(item));
			if (ROPE_COOLDOWN.getItemId().equals(ItemManager.getItemId(item))) {
				Ingame.playSoundNotEnoughWool(user);
				new Scheduler() {
					@Override
					public void run() {
						perk.setItem();
					}
				}.runTask();
				e.setCancelled(true);
				return;
			} else if (!ROPE.getItemId().equals(ItemManager.getItemId(item))) {
				return;
			} else if (perk == null) {
				e.setCancelled(true);
				return;
			}
			e.setCancelled(true);
			if (!p.getInventory().contains(Material.WOOL, PerkType.ROPE.getCost())
							|| perk.getCooldown() > 0) {
				Ingame.playSoundNotEnoughWool(user);
				new Scheduler() {
					@Override
					public void run() {
						perk.setItem();
					}
				}.runTask();
				return;
			}
			ItemManager.removeItems(user, p.getInventory(), new ItemStack(
							Material.WOOL, 1,
							user.getTeam().getType().getWoolColorByte()), PerkType.ROPE.getCost());

			Vector vec = p.getLocation().getDirection().setY(0).normalize();
			double ax = Math.abs(vec.getX());
			double az = Math.abs(vec.getZ());
			if (ax > az) {
				vec.setZ(0);
				vec.normalize();
			} else {
				vec.setX(0);
				vec.normalize();
			}
			Location loc = p.getLocation().add(vec).add(0, 1, 0);

			for (int i = 0; i < 10; i++) {
				loc = loc.subtract(0, 1, 0);
				setBlock(loc, user);
			}

			p.teleport(p.getLocation().getBlock().getLocation().add(.5, .25, .5).setDirection(p.getLocation().getDirection()));

			new Scheduler() {
				int cd = PerkType.ROPE.getCooldown() + 1;

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

	@SuppressWarnings("deprecation")
	private void setBlock(Location block, User user) {
		if (block.getBlock().getType() == Material.AIR) {
			block.getBlock().setType(Material.WOOL);
			block.getBlock().setData(user.getTeam().getType().getWoolColorByte());
			WoolBattle.getInstance().getIngame().placedBlocks.add(block.getBlock());
		}
	}
}
