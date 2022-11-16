package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;

public class ListenerCapsule extends BasicPerkListener {

	public ListenerCapsule() {
		super(PerkType.CAPSULE);
	}

	@Override
	protected boolean activate(User user) {
		Player p = user.getBukkitEntity();
		Location loc = p.getLocation();
		this.setBlock(loc.subtract(0, 1, 0));
		this.setBlock(loc.add(0, 3, 0));
		this.setBlock2(loc.subtract(1, 1, 0));
		this.setBlock2(loc.subtract(0, 1, 0));
		this.setBlock2(loc.add(2, 1, 0));
		this.setBlock2(loc.subtract(0, 1, 0));
		this.setBlock2(loc.subtract(1, 0, 1));
		this.setBlock2(loc.add(0, 1, 0));
		this.setBlock2(loc.add(0, 0, 2));
		this.setBlock2(loc.subtract(0, 1, 0));
		p.teleport(
				p.getLocation().getBlock().getLocation().add(.5, .25, .5).setDirection(p.getLocation().getDirection()));
		return true;
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
