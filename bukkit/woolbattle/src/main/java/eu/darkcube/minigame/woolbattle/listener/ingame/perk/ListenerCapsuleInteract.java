package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import org.bukkit.Location;
import org.bukkit.Material;
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

public class ListenerCapsuleInteract extends Listener<PlayerInteractEvent> {
	public static final Item CAPSULE = PerkType.CAPSULE.getItem();
	public static final Item CAPSULE_COOLDOWN = PerkType.CAPSULE.getCooldownItem();

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
			if (CAPSULE_COOLDOWN.getItemId().equals(ItemManager.getItemId(item))) {
				Ingame.playSoundNotEnoughWool(user);
				new Scheduler() {
					@Override
					public void run() {
						perk.setItem();
					}
				}.runTask();
				e.setCancelled(true);
				return;
			} else if (!CAPSULE.getItemId().equals(ItemManager.getItemId(item))) {
				return;
			} else if (perk == null) {
				e.setCancelled(true);
				return;
			}
			e.setCancelled(true);
			if (!p.getInventory().contains(Material.WOOL, PerkType.CAPSULE.getCost()) || perk.getCooldown() > 0) {
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
					PerkType.CAPSULE.getCost());

			Location loc = p.getLocation();
			setBlock(loc.subtract(0, 1, 0));
			setBlock(loc.add(0, 3, 0));
			setBlock2(loc.subtract(1, 1, 0));
			setBlock2(loc.subtract(0, 1, 0));
			setBlock2(loc.add(2, 1, 0));
			setBlock2(loc.subtract(0, 1, 0));
			setBlock2(loc.subtract(1, 0, 1));
			setBlock2(loc.add(0, 1, 0));
			setBlock2(loc.add(0, 0, 2));
			setBlock2(loc.subtract(0, 1, 0));
			p.teleport(p.getLocation().getBlock().getLocation().add(.5, .25, .5)
					.setDirection(p.getLocation().getDirection()));

			new Scheduler() {
				int cd = PerkType.CAPSULE.getCooldown() + 1;

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

	private void setBlock(Location block) {
		if (block.getBlock().getType() == Material.AIR) {
			block.getBlock().setType(Material.WOOL);
			Ingame.setBlockDamage(block.getBlock(), 2);
			Ingame.setMetaData(block.getBlock(), "capsule", true);
			Main.getInstance().getIngame().placedBlocks.add(block.getBlock());
		}
	}

	private void setBlock2(Location block) {
		if (block.getBlock().getType() == Material.AIR) {
			block.getBlock().setType(Material.WOOL);
			Ingame.setMetaData(block.getBlock(), "capsule", true);
			Main.getInstance().getIngame().placedBlocks.add(block.getBlock());
		}
	}
}
