/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.inventoryapi.v1;

import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.util.AsyncExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;

public abstract class AnimatedInventory extends AbstractInventory {

	protected final AtomicBoolean animationRunning = new AtomicBoolean(false);

	protected final AtomicLong animationStarted = new AtomicLong();

	protected final AtomicReference<AnimationRunnable> animation = new AtomicReference<>();

	protected final List<AnimationInformation> informations =
			Collections.synchronizedList(new ArrayList<>());

	private final BooleanSupplier instant;
	//					(i1, i2) -> Long.valueOf(i1.showAfter).compareTo(Long.valueOf(i2.showAfter)));

	public AnimatedInventory(InventoryType inventoryType, Component title, int size,
			BooleanSupplier instant) {
		super(inventoryType, title, size);
		this.instant = instant;
	}

	public boolean isInstant() {
		return this.instant.getAsBoolean();
	}

	protected void tick() {
	}

	protected void offerAnimations(final Collection<AnimationInformation> informations) {
		AsyncExecutor.service().submit(() -> this.asyncOfferAnimations(informations));
	}

	protected void asyncOfferAnimations(final Collection<AnimationInformation> informations) {
	}

	protected void startAnimation() {
		if (this.animation.get() == null && this.animationRunning.compareAndSet(false, true)) {
			this.offerAnimations(this.informations);
			AnimationRunnable runnable = new AnimationRunnable();
			this.animation.set(runnable);
			runnable.runTaskTimer(DarkCubePlugin.systemPlugin(), 0, 1);
			this.animationStarted.set(System.currentTimeMillis());
		}
	}

	public void recalculate() {
		if (this.animationRunning.get()) {
			this.offerAnimations(this.informations);
		}
	}

	private void stopAnimation() {
		if (this.animationRunning.compareAndSet(true, false)) {
			this.animation.get().cancel();
		}
	}

	@Override
	protected void destroy() {
		super.destroy();
		this.stopAnimation();
	}

	protected class AnimationRunnable extends BukkitRunnable {

		@Override
		public void run() {
			boolean updated = false;
			final long started = AnimatedInventory.this.animationStarted.get();
			final long time = System.currentTimeMillis();
			AnimatedInventory.this.tick();
			List<AnimationInformation> toRemove = new ArrayList<>();
			for (AnimationInformation information : AnimatedInventory.this.informations) {
				if (information.showAfter + started > time) {
					continue;
				}
				toRemove.add(information);
				AnimatedInventory.this.handle.setItem(information.slot, information.item);
				updated = true;
			}
			AnimatedInventory.this.informations.removeAll(toRemove);
			if (updated) {
				AnimatedInventory.this.opened.stream().filter(p -> p instanceof Player)
						.map(p -> (Player) p).forEach(p -> p.updateInventory());
			}
		}

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

}
