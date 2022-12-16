/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util.scheduler;

public class Scheduler implements Runnable {

	private SchedulerTask task;
	private int weight;
	private Runnable run;

	public Scheduler() {
		run = this;
	}
	public Scheduler(Runnable run) {
		this.run = run;
	}
	
	public void cancel() {
		if (!isCancelled())
			task.cancel();
		task = null;
	}

	public void setWeight(int weight) {
		this.weight = weight;
		if (task != null)
			task.setWeight(weight);
	}

	public boolean isCancelled() {
		return task == null;
	}

	public void runTask() {
		runTaskLater(0);
	}

	public void runTaskLater(long delay) {
		task = new SchedulerTask(this, delay, Integer.valueOf(weight));
	}

	public void runTaskTimer(long repeat) {
		runTaskTimer(0, repeat);
	}

	public void runTaskTimer(long delay, long repeat) {
		task = new SchedulerTask(this, delay, repeat, weight);
	}

	@Override
	public void run() {
		run.run();
	}
}
