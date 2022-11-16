package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.PerkListener;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;

public class ListenerBooster extends PerkListener {

	@EventHandler
	public void handle(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack item = e.getItem();
			if (item == null)
				return;
			User user = Main.getInstance().getUserWrapper().getUser(e.getPlayer().getUniqueId());
			if (!this.checkUsable(user, PerkType.BOOSTER, item, () -> e.setCancelled(true))) {
				return;
			}

			Player p = user.getBukkitEntity();
			Vector velo = p.getLocation()
					.getDirection()
					.setY(p.getLocation().getDirection().getY() + 0.3)
					.multiply(2.7);
			velo.setY(velo.getY() / 1.8);
			p.setVelocity(velo);

			this.payForThePerk(user, PerkType.BOOSTER);
			this.startCooldown(user, PerkType.BOOSTER);
		}
	}

}
