package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.user.User;

public class ListenerRonjasToiletEntityDamageByEntity extends Listener<EntityDamageByEntityEvent> {

	@Override
	@EventHandler
	public void handle(EntityDamageByEntityEvent e) {
		if (e.getEntityType() != EntityType.PLAYER || e.getDamager().getType() != EntityType.EGG)
			return;
		Player t = (Player) e.getEntity();
		Egg egg = (Egg) e.getDamager();
		if (!(egg.getShooter() instanceof Player)) {
			return;
		}
		Player p = (Player) egg.getShooter();
		User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());
		User target = Main.getInstance().getUserWrapper().getUser(t.getUniqueId());
		if(user.getTeam() != target.getTeam() || user.isTrollMode()) {
			target.setLastHit(user);
			target.setTicksAfterLastHit(0);
		}
//		Main.getInstance().getUserWrapper().getUser(t.getUniqueId())
//				.setLastHit(Main.getInstance().getUserWrapper().getUser(p.getUniqueId()));
//		Main.getInstance().getUserWrapper().getUser(t.getUniqueId()).setTicksAfterLastHit(0);
	}
}