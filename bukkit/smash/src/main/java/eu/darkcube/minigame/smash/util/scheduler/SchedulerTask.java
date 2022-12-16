/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.smash.util.scheduler;

import eu.darkcube.minigame.smash.Main;
import net.minecraft.server.v1_8_R3.MinecraftServer;

public class SchedulerTask implements Comparable<SchedulerTask> {

	private long delay;
	private long repeat;
	private long lastExecution;
	private Integer weight;
	private Scheduler scheduler;

	SchedulerTask(Scheduler scheduler, long delay) {
		this(scheduler, delay, 0);
	}

	SchedulerTask(Scheduler scheduler, long delay, long repeat) {
		this.scheduler = scheduler;
		this.repeat = repeat;
		this.delay = delay;
		this.lastExecution = MinecraftServer.currentTick + delay - repeat;
		Main.getInstance().getSchedulers().add(this);
	}

	SchedulerTask(Scheduler scheduler, long delay, Integer weight) {
		this(scheduler, delay);
		setWeight(weight);
	}

	SchedulerTask(Scheduler scheduler, long delay, long repeat, Integer weight) {
		this(scheduler, delay, repeat);
		setWeight(weight);
	}

	public boolean isRepeating() {
		return repeat != 0;
	}

	public long getRepeat() {
		return repeat;
	}

	public long getDelay() {
		return delay;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight == null ? 0 : weight;
	}

	public final void cancel() {
		Main.getInstance().getSchedulers().remove(this);
		scheduler = null;
		delay = 0;
		repeat = 0;
	}

	public void run() {
		lastExecution = MinecraftServer.currentTick;
		scheduler.run();
		if (!isRepeating()) {
			this.cancel();
		}
	}

	public final boolean canExecute() {
		return scheduler != null && MinecraftServer.currentTick >= getNextExecution();
	}

	public long getLastExecution() {
		return lastExecution;
	}

	public long getNextExecution() {
		if (repeat != 0)
			return getLastExecution() + repeat;
		return getLastExecution();
	}

	@Override
	public int compareTo(SchedulerTask o) {
		return weight.compareTo(o.weight);
	}
}
