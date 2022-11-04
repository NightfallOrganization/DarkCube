package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;

public class ListenerSwitcherSwitch extends Listener<EntityDamageByEntityEvent> {
	@Override
	@EventHandler
	public void handle(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		if (e.getDamager().getType() != EntityType.SNOWBALL) {
			return;
		}
		Snowball snowball = (Snowball) e.getDamager();
		if (!(snowball.getShooter() instanceof Player)) {
			return;
		}
		Player p = (Player) snowball.getShooter();
		Player hit = (Player) e.getEntity();
		if (snowball.getMetadata("perk").size() != 0
				&& snowball.getMetadata("perk").get(0).asString().equals(PerkType.SWITCHER.getPerkName().getName())) {
			e.setCancelled(true);
			User user = Main.getInstance().getUserWrapper().getUser(hit.getUniqueId());
			if (user.getTeam().getType() != Main.getInstance().getUserWrapper().getUser(p.getUniqueId()).getTeam().getType()) {
				if (user.getTicksAfterLastHit() < 600) {
					user.setTicksAfterLastHit(0);
					user.setLastHit(Main.getInstance().getUserWrapper().getUser(p.getUniqueId()));
				}
				Location loc = p.getLocation();
				p.teleport(hit);
				hit.teleport(loc);
				
				p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 1.8F);
				hit.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 1.8F);
			}
		}
	}
}
