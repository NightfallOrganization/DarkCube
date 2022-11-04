package eu.darkcube.minigame.woolbattle.listener.ingame;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.user.User;

public class ListenerBlockPlace extends Listener<BlockPlaceEvent> {
	@Override
	@EventHandler
	public void handle(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());
		if (!user.isTrollMode()) {
			if (user.getTeam().getType() == TeamType.SPECTATOR) {
				e.setCancelled(true);
				return;
			}
		} else {
			return;
		}
		if (user.getPassivePerk().calculateItem().equals(e.getItemInHand())) {
			e.setCancelled(true);
			return;
		}
		Block block = e.getBlock();
		Ingame ingame = Main.getInstance().getIngame();
		if (ingame.breakedWool.containsKey(block)) {
			e.setCancelled(true);
			return;
		}
		ingame.placedBlocks.add(block);
	}
}