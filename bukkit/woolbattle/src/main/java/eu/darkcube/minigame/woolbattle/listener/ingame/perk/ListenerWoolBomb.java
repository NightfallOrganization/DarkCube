package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;

public class ListenerWoolBomb extends BasicPerkListener {

	public ListenerWoolBomb() {
		super(PerkType.WOOL_BOMB);
	}

	@Override
	protected boolean activate(User user, Perk perk) {
		Player p = user.getBukkitEntity();
		Snowball bomb = p.launchProjectile(Snowball.class);
		bomb.setMetadata("source", new FixedMetadataValue(Main.getInstance(), user));
		bomb.setMetadata("perk", new FixedMetadataValue(Main.getInstance(), perk.getPerkName().getName()));
		return true;
	}

	@EventHandler
	public void handle(ProjectileHitEvent e) {
		if (e.getEntityType() == EntityType.SNOWBALL) {
			Snowball bomb = (Snowball) e.getEntity();
			if (!bomb.hasMetadata("source")) {
				return;
			}
			if (!bomb.hasMetadata("perk")) {
				return;
			}
			if (!bomb.getMetadata("perk").get(0).asString().equals(PerkType.WOOL_BOMB.getPerkName().getName())) {
				return;
			}
			User user = (User) bomb.getMetadata("source").get(0).value();
			TNTPrimed tnt = bomb.getWorld().spawn(bomb.getLocation(), TNTPrimed.class);

			tnt.setMetadata("boost", new FixedMetadataValue(Main.getInstance(), 3));
			tnt.setMetadata("source", new FixedMetadataValue(Main.getInstance(), user));
			tnt.setFuseTicks(10);
			tnt.setYield(4f);
		}
	}

}
