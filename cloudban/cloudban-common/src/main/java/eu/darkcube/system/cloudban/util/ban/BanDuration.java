/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.util.ban;

import java.math.BigInteger;

public class BanDuration extends Duration {

	private BanType type;

	public BanDuration(BigInteger years, BigInteger months, BigInteger days, BigInteger hours, BigInteger minutes,
			BigInteger seconds, BanType type) {
		super(years, months, days, hours, minutes, seconds);
		this.type = type;
	}

	public BanDuration(BigInteger durationInSeconds, BanType type) {
		super(durationInSeconds);
		this.type = type;
	}

	public BanType getType() {
		return type;
	}

	public static final BanDuration WARNING_BAN = new BanDuration(BigInteger.valueOf(0), BanType.BAN);
	public static final BanDuration WARNING_MUTE = new BanDuration(BigInteger.valueOf(0), BanType.MUTE);
}
