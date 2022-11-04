package eu.darkcube.system.inventory.api.v1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import eu.darkcube.system.commons.AsyncExecutor;
import eu.darkcube.system.inventory.api.InventoryAPI;

public abstract class AnimatedInventory extends AbstractInventory {

	protected final AtomicBoolean animationRunning = new AtomicBoolean(false);
	protected final AtomicLong animationStarted = new AtomicLong();
	protected final AtomicReference<AnimationRunnable> animation = new AtomicReference<>();
	protected final List<AnimationInformation> informations = Collections.synchronizedList(new ArrayList<>());
//					(i1, i2) -> Long.valueOf(i1.showAfter).compareTo(Long.valueOf(i2.showAfter)));

	public AnimatedInventory(InventoryType inventoryType, String title,
					int size) {
		super(inventoryType, title, size);
	}

	protected class AnimationRunnable extends BukkitRunnable {
		@Override
		public void run() {
			boolean updated = false;
			final long started = animationStarted.get();
			final long time = System.currentTimeMillis();
			tick();
			List<AnimationInformation> toRemove = new ArrayList<>();
			for (AnimationInformation information : informations) {
				if (information.showAfter + started > time) {
					continue;
				}
				toRemove.add(information);
				handle.setItem(information.slot, information.item);
				updated = true;
			}
			informations.removeAll(toRemove);
			if (updated) {
				opened.stream().filter(p -> p instanceof Player).map(p -> (Player) p).forEach(p -> p.updateInventory());
			}
		}
	}

	protected void tick() {
	}

	protected void offerAnimations(
					final Collection<AnimationInformation> informations) {
		AsyncExecutor.service().submit(() -> asyncOfferAnimations(informations));
	}

	protected void asyncOfferAnimations(
					final Collection<AnimationInformation> informations) {
	}

	public class AnimationInformation {

		public final long showAfter;
		public final int slot;
		public final ItemStack item;

		public AnimationInformation(long showAfter, int slot, ItemStack item) {
			this.showAfter = showAfter;
			this.slot = slot;
			this.item = item;
		}

		public AnimationInformation(int slot, ItemStack item) {
			this(-1, slot, item);
		}
	}

	public void startAnimation() {
		if (animation.get() == null
						&& animationRunning.compareAndSet(false, true)) {
			offerAnimations(informations);
			AnimationRunnable runnable = new AnimationRunnable();
			animation.set(runnable);
			runnable.runTaskTimer(InventoryAPI.getInstance(), 0, 1);
			animationStarted.set(System.currentTimeMillis());
		}
	}

	private void stopAnimation() {
		if (animationRunning.compareAndSet(true, false)) {
			animation.get().cancel();
		}
	}

	@Override
	protected void destroy() {
		super.destroy();
		this.stopAnimation();
	}
}
