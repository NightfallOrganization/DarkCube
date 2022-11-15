package eu.darkcube.minigame.woolbattle.listener.ingame.standard;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.observable.ObservableInteger;
import eu.darkcube.minigame.woolbattle.util.observable.ObservableObject;
import eu.darkcube.minigame.woolbattle.util.observable.SimpleObservableInteger;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerDoubleJump extends Listener<PlayerToggleFlightEvent> {
	public Map<Player, ObservableInteger> cooldown = new HashMap<>();

	public static final int COOLDOWN = 65;
	public static final int COST = 5;

	@Override
	@EventHandler
	public void handle(PlayerToggleFlightEvent e) {
		Player p = e.getPlayer();
		User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());
		if (p.getGameMode() != GameMode.SURVIVAL || user.isTrollMode()
				|| user.getTeam().getType() == TeamType.SPECTATOR) {
			return;
		}
		if (e.isFlying())
			e.setCancelled(true);
		if (refresh(p) && e.isFlying()) {
			cooldown.get(p).setObject(COOLDOWN);
			Vector velo = p.getLocation().getDirection();
			velo.setY(0).normalize().multiply(0.1);
//			velo.multiply(0.1);
			double heightMult = 1;
			if (user.getPassivePerk().getPerkName().equals(PerkType.ROCKETJUMP.getPerkName()))
				heightMult = 1.45;
			if(user.getPassivePerk().getPerkName().equals(PerkType.LONGJUMP.getPerkName()))
				velo.multiply(0.3);
			velo.setY(1.05 * heightMult);
			p.setAllowFlight(false);
			p.setVelocity(velo);
			ItemManager.removeItems(user, p.getInventory(),
					new ItemStack(Material.WOOL, 1, user.getTeam().getType().getWoolColor()), COST);
			new Scheduler() {
				@Override
				public void run() {
					if (cooldown.get(p).getObject() == 0) {
						p.setAllowFlight(true);
						this.cancel();
						return;
					}
					cooldown.get(p).setObject(cooldown.get(p).getObject() - 1);
				}
			}.runTaskTimer(0, 1);
		}
//		System.out.println(e.isFlying());
//		System.out.println(e.isCancelled());
	}

	public boolean refresh(Player p) {
		if (Main.getInstance().getIngame().listenerGhostInteract.ghosts
				.containsKey(Main.getInstance().getUserWrapper().getUser(p.getUniqueId()))) {
			if (p.getAllowFlight())
				p.setAllowFlight(false);
			return false;
		}

		if (!cooldown.containsKey(p)) {
			cooldown.put(p, new SimpleObservableInteger(0) {
				@Override
				public void onChange(ObservableObject<Integer> instance, Integer oldValue, Integer newValue) {
					p.setFoodLevel(
							(int) ((ListenerDoubleJump.COOLDOWN - newValue) * 20F / ListenerDoubleJump.COOLDOWN));
				}

				@Override
				public void onSilentChange(ObservableObject<Integer> instance, Integer oldValue, Integer newValue) {
				}
			});
		}
		if (p.getGameMode() == GameMode.SURVIVAL) {
			ObservableInteger cdi = cooldown.get(p);
			if (ItemManager.countItems(Material.WOOL, p.getInventory()) >= COST && cdi.getObject() == 0) {
				if (!p.getAllowFlight()) {
					p.setAllowFlight(true);
				}
			} else {
				if (p.getAllowFlight()) {
					p.setAllowFlight(false);
				}
			}
			return p.getAllowFlight();
		}
		p.setAllowFlight(true);
		return true;
	}
}