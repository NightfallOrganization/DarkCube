package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import org.bukkit.Location;
import org.bukkit.Material;
import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;

public class ListenerSafetyPlatform extends BasicPerkListener {

	public ListenerSafetyPlatform() {
		super(PerkType.SAFETY_PLATFORM);
	}

	@Override
	protected boolean activateRight(User user, Perk perk) {
		boolean suc = setBlocks(user);
		if (!suc)
			Ingame.playSoundNotEnoughWool(user);
		return suc;
	}

	private boolean setBlocks(User p) {

		final Location pLoc = p.getBukkitEntity().getLocation();
		Location center =
				pLoc.getBlock().getLocation().add(0.5, 0.25, 0.5).setDirection(pLoc.getDirection());
		center.subtract(-0.5, 0, -0.5);
		final double radius = 2.5;
		for (double x = -radius; x <= radius; x++) {
			for (double z = -radius; z <= radius; z++) {
				if (isCorner(x, z, radius)) {
					continue;
				}
				block(center.clone().add(x, -1, z), p);
			}
		}

		p.getBukkitEntity().teleport(center);
		return true;
	}

	private boolean isCorner(double x, double z, double r) {
		return (x == -r && z == -r) || (x == r && z == -r) || (x == -r && z == r)
				|| (x == r && z == r);
	}

	@SuppressWarnings("deprecation")
	private void block(Location loc, User u) {
		if (loc.getBlock().getType() == Material.AIR) {
			loc.getBlock().setType(Material.WOOL);
			loc.getBlock().setData(u.getTeam().getType().getWoolColorByte());
			WoolBattle.getInstance().getIngame().placedBlocks.add(loc.getBlock());
		}
	}
}
