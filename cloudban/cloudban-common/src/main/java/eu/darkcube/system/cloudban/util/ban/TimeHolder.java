/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.util.ban;

import java.math.BigInteger;

public class TimeHolder implements Comparable<TimeHolder> {

	private BigInteger durationInSeconds;

	public TimeHolder(BigInteger seconds) {
		this.durationInSeconds = seconds;
	}

	public TimeHolder(BigInteger years, BigInteger months, BigInteger days, BigInteger hours, BigInteger minutes,
			BigInteger seconds) {
		this.durationInSeconds = BigInteger.ZERO;
		durationInSeconds = durationInSeconds.add(Calculator.YEAR.toSeconds(years));
		durationInSeconds = durationInSeconds.add(Calculator.MONTH.toSeconds(months));
		durationInSeconds = durationInSeconds.add(Calculator.DAY.toSeconds(days));
		durationInSeconds = durationInSeconds.add(Calculator.HOUR.toSeconds(hours));
		durationInSeconds = durationInSeconds.add(Calculator.MINUTE.toSeconds(minutes));
		durationInSeconds = durationInSeconds.add(Calculator.SECOND.toSeconds(seconds));
	}

	public BigInteger getDurationInSeconds() {
		return durationInSeconds;
	}

	@Override
	public String toString() {
		return BanUtil.GSON.toJson(this);
	}

	public BigInteger getYear() {
		return Calculator.deserialize(durationInSeconds).get(Calculator.YEAR);
	}

	public BigInteger getMonth() {
		return Calculator.deserialize(durationInSeconds).get(Calculator.MONTH);
	}

	public BigInteger getDay() {
		return Calculator.deserialize(durationInSeconds).get(Calculator.DAY);
	}

	public BigInteger getHour() {
		return Calculator.deserialize(durationInSeconds).get(Calculator.HOUR);
	}

	public BigInteger getMinute() {
		return Calculator.deserialize(durationInSeconds).get(Calculator.MINUTE);
	}

	public BigInteger getSecond() {
		return Calculator.deserialize(durationInSeconds).get(Calculator.SECOND);
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj)
				|| (obj instanceof TimeHolder && ((TimeHolder) obj).durationInSeconds.equals(durationInSeconds));
	}

	@Override
	public int compareTo(TimeHolder o) {
		return durationInSeconds.compareTo(o.durationInSeconds);
	}
}
