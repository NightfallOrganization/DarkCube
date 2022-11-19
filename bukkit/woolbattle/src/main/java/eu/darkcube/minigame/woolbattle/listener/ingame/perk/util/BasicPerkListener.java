package eu.darkcube.minigame.woolbattle.listener.ingame.perk.util;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.event.LaunchableInteractEvent;
import eu.darkcube.minigame.woolbattle.listener.RegisterNotifyListener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public abstract class BasicPerkListener extends PerkListener implements RegisterNotifyListener {

	private final Handle handle = new Handle();

	private final PerkType perkType;

	public BasicPerkListener(PerkType perkType) {
		this.perkType = perkType;
	}

	@Override
	public final void registered() {
		Main.registerListeners(this.handle);
	}

	@Override
	public final void unregistered() {
		Main.unregisterListeners(this.handle);
	}

	public PerkType getPerkType() {
		return this.perkType;
	}

	protected abstract boolean activate(User user, Perk perk);

	private class Handle implements Listener {

		@EventHandler
		private void handle(LaunchableInteractEvent event) {
			ItemStack item = event.getItem();
			if (item == null) {
				return;
			}
			User user = Main.getInstance().getUserWrapper().getUser(event.getPlayer().getUniqueId());
			if (!BasicPerkListener.this.checkUsable(user, BasicPerkListener.this.perkType, item, () -> {
				if (event.getEntity() != null) {
					Perk perk = user.getPerkByItemId(BasicPerkListener.this.perkType.getItem().getItemId());
					perk.setItem();
					new Scheduler(perk::setItem).runTask();
				}
				event.setCancelled(true);
			})) {
				return;
			}
			if (!BasicPerkListener.this.activate(user, user.getPerkByItemId(BasicPerkListener.this.perkType.getItem().getItemId()))) {
				return;
			}
			BasicPerkListener.this.payForThePerk(user, BasicPerkListener.this.perkType);
			BasicPerkListener.this.startCooldown(user, BasicPerkListener.this.perkType);
		}

	}

}
