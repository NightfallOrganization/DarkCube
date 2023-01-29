/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module.statsreset;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import eu.darkcube.system.module.statsreset.mysql.MySQL;
import eu.darkcube.system.stats.api.Duration;

public class Resetter implements Runnable {

	private Timer timer = new Timer();
	private Duration duration;

	public Resetter(Duration duration) {
		this.duration = duration;
	}

	public void start() {
		if (duration == Duration.ALLTIME)
			return;
		long repeat = TimeUnit.HOURS.toMillis(duration.getHours());
		if (repeat <= 0)
			repeat = Integer.MAX_VALUE;

		int millis = 0;
		switch (duration) {
		case HOUR:
			millis = getMillisUntilNextHour();
			break;
		case DAY:
			millis = getMillisUntilNextDay();
			break;
		case WEEK:
			millis = getMillisUntilNextWeek();
			break;
		case MONTH:
			millis = getMillisUntilNextMonth();
			break;
		case YEAR:
			millis = getMillisUntilNextYear();
			break;
		case ALLTIME:
			break;
		}
		if (millis < 0) {
			millis = Integer.MAX_VALUE;
		}

		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Main.getCloudNet().getLogger().warning("Resetting a stats table");
				MySQL.clearTables(duration);
			}
		}, millis, repeat);

	}

	public static int getMillisUntilNextDay() {
		return until(Calendar.DAY_OF_MONTH, Calendar.SECOND) * 1000;
	}

	public static int getMillisUntilNextHour() {
		return until(Calendar.HOUR_OF_DAY, Calendar.MILLISECOND);
	}

	public static int getMillisUntilNextWeek() {
		return until(Calendar.WEEK_OF_YEAR, Calendar.MINUTE) * 60 * 1000;
	}

	public static int getMillisUntilNextMonth() {
		return until(Calendar.MONTH, Calendar.MINUTE) * 60 * 1000;
	}

	public static int getMillisUntilNextYear() {
		return until(Calendar.YEAR, Calendar.MINUTE) * 60 * 1000;
	}

	public static int getMillisUntilNextMinute() {
		return until(Calendar.MINUTE, Calendar.MILLISECOND);
	}

	private static int until(int id1, int id2) {
		Calendar c = Calendar.getInstance();
		int cur = c.get(id1);
		int i = 0;
		while (c.get(id1) == cur) {
			c.set(id2, c.get(id2) + 1);
			i++;
		}
		return i;
	}

	public void stop() {
		timer.cancel();
		timer = new Timer();
	}

	@Override
	public void run() {
		MySQL.clearTables(duration);
	}
}
