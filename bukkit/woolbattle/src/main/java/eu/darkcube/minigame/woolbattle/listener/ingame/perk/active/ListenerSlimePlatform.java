/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.active;

import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.perks.active.SlimePlatformPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class ListenerSlimePlatform extends BasicPerkListener {
	public ListenerSlimePlatform() {
		super(SlimePlatformPerk.SLIME_PLATFORM);
	}

	//	// todo add lore for the items and perk

	@Override
	protected boolean activateRight(UserPerk perk) {

		Player p = perk.owner().getBukkitEntity();

		ArrayList<Block> blocks = new ArrayList<>();
		this.setBlock(p.getLocation().subtract(0, 5, 0), blocks);
		this.setBlock(p.getLocation().subtract(1, 5, 0), blocks);
		this.setBlock(p.getLocation().subtract(-1, 5, 0), blocks);
		this.setBlock(p.getLocation().subtract(0, 5, 1), blocks);
		this.setBlock(p.getLocation().subtract(0, 5, -1), blocks);

		return true;
	}

	@SuppressWarnings("deprecation")
	private void setBlock(Location block, ArrayList<Block> l) {
		if (block.getBlock().getType() == Material.AIR) {
			block.getBlock().setType(Material.STAINED_CLAY);
			block.getBlock().setData((byte) 13);
			l.add(block.getBlock());
			Ingame.setMetaData(block.getBlock(), "slime", l);
		}
	}

	private boolean setBlock2(Location block) {
		if (block.getBlock().getType() == Material.STAINED_CLAY) {
			block.getBlock().setType(Material.AIR);
			Ingame.setMetaData(block.getBlock(), "slime", null);
			return true;
		}
		return false;
	}

	@EventHandler
	@SuppressWarnings("unchecked")
	public void moveEvent(PlayerMoveEvent e) {

		MetadataValue mv =
				Ingame.getMetaData(e.getTo().clone().subtract(0, 1, 0).getBlock(), "slime");
		MetadataValue mv2 =
				Ingame.getMetaData(e.getTo().clone().subtract(0, 2, 0).getBlock(), "slime");

		if (mv != null || mv2 != null) {

			ArrayList<Block> blocks = (ArrayList<Block>) (mv == null ? mv2.value() : mv.value());
			for (Block b : blocks) {
				setBlock2(b.getLocation());
			}

			e.getPlayer().setVelocity(new Vector(0, 3, 0).add(
					e.getPlayer().getVelocity().multiply(new Vector(0.1, -0.4, 0.1))));
		}

	}
}