/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.util;

import java.math.BigInteger;
import java.util.UUID;

import com.google.gson.*;

public class TimeHolder {

	public static final GsonBuilder builder = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {

		@Override
		public boolean shouldSkipField(FieldAttributes attr) {
			return false;
		}

		@Override
		public boolean shouldSkipClass(Class<?> clazz) {
			if (clazz.equals(StringBuilder.class))
				return true;
			return false;
		}
	});

	private BigInteger durationInSeconds;
	private UUID uuid;

	public TimeHolder(BigInteger years, BigInteger months, BigInteger days, BigInteger hours, BigInteger minutes,
			BigInteger seconds, UUID uuid) {
		this.uuid = uuid;
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

	public UUID getUUID() {
		return uuid;
	}

	@Override
	public String toString() {
		return builder.create().toJson(this);
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
}
