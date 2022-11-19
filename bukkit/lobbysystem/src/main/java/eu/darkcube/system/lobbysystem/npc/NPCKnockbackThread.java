package eu.darkcube.system.lobbysystem.npc;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.modifier.AnimationModifier.EntityAnimation;
import com.github.juliarn.npc.modifier.LabyModModifier.LabyModAction;

import eu.darkcube.system.labymod.emotes.Emotes;

public class NPCKnockbackThread extends Thread {

	private final NPC npc;

	public NPCKnockbackThread(NPC npc) {
		this.npc = npc;
	}

	@Override
	public void run() {
		Consumer cons = new Consumer();
		while (true) {
			this.npc.getLocation()
					.getWorld()
					.getNearbyEntities(this.npc.getLocation().clone().add(0, 0.6, 0), 0.3, 0.7, 0.3)
					.stream()
					.filter(e -> e instanceof Player)
					.filter(e -> !e.hasPermission("system.npc.knockback.ignore"))
					.map(e -> (Player) e)
					.forEach(cons);
			try {
				Thread.sleep(250);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	private class Consumer implements java.util.function.Consumer<Player> {

		@Override
		public void accept(Player p) {
			if (p.isFlying())
				return;
			Location loc1 = p.getLocation();
			Location loc2 = NPCKnockbackThread.this.npc.getLocation();
			double x = loc1.getX() - loc2.getX();
			double y = loc1.getY() - loc2.getY();
			double z = loc1.getZ() - loc2.getZ();
			p.setVelocity(new Vector(x, Math.abs(y) > 1.7 ? 2 : Math.abs(y) + 0.3, z).normalize().multiply(.7));
			NPCKnockbackThread.this.npc.animation().queue(EntityAnimation.SWING_MAIN_ARM).send(p);
			NPCKnockbackThread.this.npc.labymod().queue(LabyModAction.EMOTE, Emotes.KARATE.getId()).send(p);
		}

	}

}
