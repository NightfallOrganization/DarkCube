package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import java.lang.reflect.Field;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftTNTPrimed;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;

public class ListenerWoolBombHit extends Listener<ProjectileHitEvent> {

	@Override
	@EventHandler
	public void handle(ProjectileHitEvent e) {
		if (e.getEntityType() == EntityType.SNOWBALL) {
			Snowball bomb = (Snowball) e.getEntity();
			if (!(bomb.getShooter() instanceof Player)) {
				return;
			}
			if (!bomb.hasMetadata("perk")) {
				return;
			}
			Player p = (Player) bomb.getShooter();
			User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());
			if (!bomb.getMetadata("perk").get(0).asString().equals(PerkType.WOOL_BOMB.getPerkName().getName())) {
				return;
			}
			CraftTNTPrimed tnt = (CraftTNTPrimed) bomb.getWorld().spawn(bomb.getLocation(), TNTPrimed.class);
			tnt.setMetadata("boost", new FixedMetadataValue(Main.getInstance(), 3));
			tnt.setFuseTicks(10);
			tnt.setYield(4f);
			try {
				Field f = tnt.getHandle().getClass().getDeclaredField("source");
				f.setAccessible(true);
				f.set(tnt.getHandle(), user.getBukkitEntity().getHandle());
			} catch (NoSuchFieldException ex) {
				ex.printStackTrace();
			} catch (SecurityException ex) {
				ex.printStackTrace();
			} catch (IllegalArgumentException ex) {
				ex.printStackTrace();
			} catch (IllegalAccessException ex) {
				ex.printStackTrace();
			}
		}
	}
}