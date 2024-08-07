/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.listener;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.shop.ShopItem;
import de.pixel.bedwars.state.Ingame;
import de.pixel.bedwars.team.Team;
import de.pixel.bedwars.util.BedBreakAgent;
import de.pixel.bedwars.util.ItemManager;
import de.pixel.bedwars.util.Message;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class IngameBlock implements Listener {
	public static final Key key_shopitem = new Key(Main.getInstance(), "shopitem");
	public static Map<Block, BlockState> placed = new HashMap<>();

	public static void reset() {
		for (Block b : placed.keySet()) {
			BlockState state = placed.get(b);
			state.update(true, false);
		}
		placed.clear();
	}

	@EventHandler
	public void handle(BlockPhysicsEvent e) {
		new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.sendBlockChange(e.getBlock().getLocation(), e.getBlock().getType(),
							e.getBlock().getData());
				}
			}
		}.runTask(Main.getInstance());
		e.setCancelled(true);
	}

	@EventHandler
	public void handle(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if (p.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		Team team = Team.getTeam(p);
		if (team == Team.SPECTATOR) {
			e.setBuild(true);
			return;
		}
		if (!placed.containsKey(e.getBlock())) {
			placed.put(e.getBlock(), e.getBlockReplacedState());
			ItemStack item = e.getItemInHand();
			ItemBuilder b = ItemBuilder.item(item);
			if (b.persistentDataStorage().get(key_shopitem, PersistentDataTypes.BOOLEAN)) {
				e.getBlock().setMetadata("shopitem",
						new FixedMetadataValue(Main.getInstance(), ItemManager.getItemId(item)));
			}
		}
	}

	@SuppressWarnings({"deprecation", "unused"})
	private void appendBlocks(Block origin, Block block, Set<Block> blocks, Set<Block> checked,
			BlockFace[] faces) {
		if (checked.contains(block)) {
			return;
		}
		if (origin.getType() != block.getType() || origin.getData() != block.getData()
				|| blocks.contains(block)) {
			checked.add(block);
			return;
		}
		blocks.add(block);
		for (BlockFace f : faces) {
			appendBlocks(origin, block.getRelative(f), blocks, checked, faces);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void handle(BlockBreakEvent e) {
		if (placed.containsKey(e.getBlock())) {
			if (placed.get(e.getBlock()).getType() == Material.AIR) {
				placed.remove(e.getBlock());
			}
			if (e.getBlock().getMetadata("shopitem").size() != 0) {
				String itemid = e.getBlock().getMetadata("shopitem").get(0).asString();
				ShopItem sitem = ShopItem.getItem(itemid);
				e.setCancelled(true);
				e.getBlock().setType(Material.AIR);
				ItemStack item = sitem.getItem(e.getPlayer());
				item.setAmount(1);
				e.getBlock().getWorld()
						.dropItem(e.getBlock().getLocation().add(0.5, 0.75, 0.5), item);
				return;
			}
			return;
		}
		if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
			return;
		}
		//		BlockAgent agent = new BlockAgent(e.getBlock());
		//		agent.scheduledAction(Limit.NO_LIMIT, Limit.of(500), new Consumer<Block>() {
		//			@Override
		//			public void accept(Block t) {
		//				t.setType(Material.WOOL);
		//			}
		//		});
		Block b = e.getBlock();
		Material m = b.getType();

		Ingame ingame = Main.getInstance().getIngame();

		e.setCancelled(true);
		Player p = e.getPlayer();
		Team team = Team.getTeam(p);
		if (team == Team.SPECTATOR) {
			return;
		}

		Set<Block> blocks = BedBreakAgent.scan(b);
		for (Team other : Team.getTeams()) {
			Block bed = Main.getInstance().getIngame().getMap().getBedSafe(other).getBlock();

			if (blocks.contains(bed)) {
				if (other == team) {
					p.sendMessage(Message.CANT_BREAK_OWN_BED.getMessage(p));
					return;
				}

				BedBreakAgent.execute(blocks);

				other.setBed0(false);
				Bukkit.getOnlinePlayers().forEach(ingame::setScoreboardValues);

				List<UUID> disconnecteduuids = ingame.disconnectedPlayers.keySet().stream()
						.filter(s -> ingame.disconnectedPlayers.get(s) == other)
						.collect(Collectors.toList());

				disconnecteduuids.forEach(u -> ingame.checkPlayerOffline(other, u));

				List<OfflinePlayer> off = disconnecteduuids.stream().map(Bukkit::getOfflinePlayer)
						.collect(Collectors.toList());

				off.forEach(o -> {
					Main.sendMessage(Message.PLAYER_WAS_FINAL_KILLED,
							"§" + other.getNamecolor() + o.getName());
				});

				p.sendMessage(Message.YOU_HAVE_BROKEN_BED.getMessage(p, other.toString(p)));
				for (Player t : Bukkit.getOnlinePlayers()) {
					t.playSound(t.getLocation(), Sound.WITHER_DEATH, 10, 1);
					if (t != p) {
						if (other.getPlayers().contains(t)) {
							t.sendMessage(Message.OWN_BED_BROKEN.getMessage(t));
						} else {
							t.sendMessage(Message.BED_WAS_BROKEN.getMessage(t, other.toString(t)));
						}
					}
				}
				other.setBed(false);
			}
		}

		if (m == Material.LONG_GRASS || m == Material.YELLOW_FLOWER || m == Material.RED_ROSE
				|| m == Material.DOUBLE_PLANT || m == Material.DEAD_BUSH) {
			placed.put(b, b.getState());
			b.setType(Material.AIR);
		}
	}
}
