/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.stats.api;

public enum Duration {

	HOUR(1),

	DAY(HOUR.hrs * 24),

	WEEK(DAY.hrs * 7),

	MONTH(DAY.hrs * 30),

	YEAR(DAY.hrs * 365),

	ALLTIME(-1),

	;

	private final long hrs;

	Duration(long hrs) {
		this.hrs = hrs;
	}

	public long getHours() {
		return hrs;
	}

	public String format() {
		switch (this) {
		case ALLTIME:
			return "Alltime";
		case DAY:
			return "Daily";
		case HOUR:
			return "Hourly";
		case MONTH:
			return "Monthly";
		case WEEK:
			return "Weekly";
		case YEAR:
			return "Yearly";
		}
		return "?";
	}
}
