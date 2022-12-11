/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.util.ban;

import java.math.BigInteger;
import java.util.Calendar;

public class DateTime extends TimeHolder {
	private static final BigInteger TWO = BigInteger.valueOf(2);
	private static final BigInteger FOUR = BigInteger.valueOf(4);
	
	public DateTime(BigInteger year, BigInteger month, BigInteger day, BigInteger hour, BigInteger minute,
			BigInteger second) {
		super(year, month, day, hour, minute, second);
	}
	
	public DateTime(BigInteger seconds) {
		super(seconds);
	}

	public static DateTime current() {
		Calendar c = Calendar.getInstance();
		return new DateTime(BigInteger.valueOf(c.get(Calendar.YEAR)), BigInteger.valueOf(c.get(Calendar.MONTH) + 1),
				BigInteger.valueOf(c.get(Calendar.DAY_OF_MONTH)), BigInteger.valueOf(c.get(Calendar.HOUR_OF_DAY)),
				BigInteger.valueOf(c.get(Calendar.MINUTE)), BigInteger.valueOf(c.get(Calendar.SECOND)));
	}

	private StringBuilder b;

	public synchronized String toDate() {
		b = new StringBuilder();
		append(getDay(), TWO).append('.').append(getMonth(), TWO).append('.').append(getYear(), FOUR).append(' ')
				.append(getHour(), TWO).append(':').append(getMinute(), TWO);
		return b.toString();
	}

	private DateTime append(char c) {
		b.append(c);
		return this;
	}

	private DateTime append(BigInteger unit, BigInteger charcount) {
		String u = unit.toString();
		while (u.length() < charcount.longValueExact()) {
			u = '0' + u;
		}
		b.append(u);
		return this;
	}
}
