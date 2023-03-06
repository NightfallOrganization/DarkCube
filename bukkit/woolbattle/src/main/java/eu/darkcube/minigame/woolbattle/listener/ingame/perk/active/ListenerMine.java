/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.active;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.perks.active.MinePerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicReference;

public class ListenerMine extends BasicPerkListener {

	public ListenerMine(Perk perk) {
		super(perk);
	}

	@EventHandler
	public void handle(BlockPlaceEvent event) {
		if (event.getItemInHand() == null)
			return;
		ItemStack item = event.getItemInHand();
		WBUser user = WBUser.getUser(event.getPlayer());
		AtomicReference<UserPerk> perkRef = new AtomicReference<>(null);
		if (!checkUsable(user, item, perk(), perkRef::set)) {
			if (perkRef.get() != null)
				event.setCancelled(true);
			return;
		}
		UserPerk userPerk = perkRef.get();
		userPerk.currentPerkItem().setItem();
		new Scheduler(userPerk.currentPerkItem()::setItem).runTask();
		event.setCancelled(true);
		activated(userPerk);
		WoolBattle.getInstance().getIngame()
				.place(event.getBlock(), t -> t == Material.STONE_PLATE, b -> {
					new Scheduler(() -> b.setType(Material.STONE_PLATE)).runTask();
					Ingame.setMetaData(b, "perk", userPerk);
				});
	}

	@EventHandler
	public void handle(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		WBUser user = WBUser.getUser(p);
		if (!user.getTeam().canPlay())
			return;
		if (event.getAction() != Action.PHYSICAL)
			return;
		Block block = event.getClickedBlock();
		if (block == null)
			return;
		UserPerk perk = Ingame.getMetaData(block, "perk", null);
		if (perk == null)
			return;
		if (!perk.perk().perkName().equals(MinePerk.MINE))
			return;
		event.setCancelled(true);
		WoolBattle.getInstance().getIngame().destroy(block);
		block.getWorld().createExplosion(block.getLocation().add(0.5, 0.5, 0.5), 3);

		new Scheduler(() -> {
			Vector playerLoc = p.getLocation().toVector();
			Vector blockLoc = block.getLocation().add(0.5, 0, 0.5).toVector();

			Vector velocity = playerLoc.subtract(blockLoc).normalize();
			velocity.multiply(Math.pow(playerLoc.distance(blockLoc), 0.4) / 6);
			if (WoolBattle.getInstance().getIngame().attack(perk.owner(), user)) {
				p.damage(0);
			}
			velocity.setY(1.3);
			p.setVelocity(velocity);
		}).runTaskLater(2);
	}

	@Override
	protected boolean mayActivate() {
		return false;
	}
}
