/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.util.ban;

import java.math.BigInteger;
import java.util.Map;

public class Duration extends TimeHolder {
	public static final Duration WARNING = new Duration(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO,
			BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO);
	public static final Duration PERMANENT = new Duration(BigInteger.valueOf(Long.MAX_VALUE), BigInteger.ZERO,
			BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO);

	public Duration(BigInteger durationInSeconds) {
		super(durationInSeconds);
	}

	public Duration(BigInteger years, BigInteger months, BigInteger days, BigInteger hours, BigInteger minutes,
			BigInteger seconds) {
		super(years, months, days, hours, minutes, seconds);
	}

	public String endingIn(DateTime bannedAt) {
		if (getYear().equals(PERMANENT.getYear()))
			return "PERMANENT";
		StringBuilder b = new StringBuilder();
		DateTime c = DateTime.current();
		Map<Calculator, BigInteger> ma = Calculator.deserialize(
				bannedAt.getDurationInSeconds().subtract(c.getDurationInSeconds()).add(getDurationInSeconds()));
		BigInteger Y = ma.get(Calculator.YEAR);
		BigInteger M = ma.get(Calculator.MONTH);
		BigInteger D = ma.get(Calculator.DAY);
		BigInteger h = ma.get(Calculator.HOUR);
		BigInteger m = ma.get(Calculator.MINUTE);
		BigInteger s = ma.get(Calculator.SECOND);
		if (s.compareTo(BigInteger.valueOf(55)) == 1) {
			m = m.add(BigInteger.ONE);
			s = BigInteger.ZERO;
			Duration duration = new Duration(Y, M, D, h, m, s);
			Y = duration.getYear();
			M = duration.getMonth();
			D = duration.getDay();
			h = duration.getHour();
			m = duration.getMinute();
			s = duration.getSecond();
		}
		if (Y.compareTo(BigInteger.ZERO) == 1 || M.compareTo(BigInteger.ZERO) == 1
				|| D.compareTo(BigInteger.ZERO) == 1) {
			if (Y.compareTo(BigInteger.ZERO) == 1) {
				b.append(Y);
				if (Y.compareTo(BigInteger.ONE) == 1)
					b.append(" Jahre ");
				else
					b.append(" Jahr ");
			}
			if (M.compareTo(BigInteger.ZERO) == 1) {
				b.append(M);
				if (M.compareTo(BigInteger.ONE) == 1)
					b.append(" Monate ");
				else
					b.append(" Monat ");
			}
			if (D.compareTo(BigInteger.ZERO) == 1) {
				b.append(D);
				if (D.compareTo(BigInteger.ONE) == 1)
					b.append(" Tage ");
				else
					b.append(" Tag ");
			}
			if (h.compareTo(BigInteger.ZERO) == 1) {
				b.append(h);
				if (h.compareTo(BigInteger.ONE) == 1)
					b.append(" Stunden ");
				else
					b.append(" Stunde ");
			}
			if (m.compareTo(BigInteger.ZERO) == 1) {
				b.append(m);
				if (m.compareTo(BigInteger.ONE) == 1)
					b.append(" Minuten ");
				else
					b.append(" Minute ");
			}
		} else {
			if (h.compareTo(BigInteger.ZERO) == 1) {
				b.append(h);
				if (h.compareTo(BigInteger.ONE) == 1)
					b.append(" Stunden ");
				else
					b.append(" Stunde ");
			}
			if (m.compareTo(BigInteger.ZERO) == 1) {
				b.append(m);
				if (m.compareTo(BigInteger.ONE) == 1)
					b.append(" Minuten ");
				else
					b.append(" Minute ");
			}
			if (s.compareTo(BigInteger.ZERO) == 1) {
				b.append(s);
				if (s.compareTo(BigInteger.ONE) == 1)
					b.append(" Sekunden ");
				else
					b.append(" Sekunde ");
			}
			if (b.length() == 2) {
				b.append("0 Sekunden");
			}
		}
		return b.toString();
	}

	public String toText() {
		if (getYear().equals(PERMANENT.getYear()))
			return "&cPERMANENT";
		StringBuilder b = new StringBuilder().append("&b");
		BigInteger Y = getYear();
		BigInteger M = getMonth();
		BigInteger D = getDay();
		BigInteger h = getHour();
		BigInteger m = getMinute();
		BigInteger s = getSecond();
		if (Y.compareTo(BigInteger.ZERO) == 1 || M.compareTo(BigInteger.ZERO) == 1
				|| D.compareTo(BigInteger.ZERO) == 1) {
			if (Y.compareTo(BigInteger.ZERO) == 1) {
				b.append(Y);
				if (Y.compareTo(BigInteger.ONE) == 1)
					b.append(" &6Jahre&b ");
				else
					b.append(" &6Jahr&b ");
			}
			if (M.compareTo(BigInteger.ZERO) == 1) {
				b.append(M);
				if (M.compareTo(BigInteger.ONE) == 1)
					b.append(" &6Monate&b ");
				else
					b.append(" &6Monat&b ");
			}
			if (D.compareTo(BigInteger.ZERO) == 1) {
				b.append(D);
				if (D.compareTo(BigInteger.ONE) == 1)
					b.append(" &6Tage&b ");
				else
					b.append(" &6Tag&b ");
			}
			if (h.compareTo(BigInteger.ZERO) == 1) {
				b.append(h);
				if (h.compareTo(BigInteger.ONE) == 1)
					b.append(" &6Stunden&b ");
				else
					b.append(" &6Stunde&b ");
			}
			if (m.compareTo(BigInteger.ZERO) == 1) {
				b.append(m);
				if (m.compareTo(BigInteger.ONE) == 1)
					b.append(" &6Minuten&b ");
				else
					b.append(" &6Minute&b ");
			}
		} else {
			if (h.compareTo(BigInteger.ZERO) == 1) {
				b.append(h);
				if (h.compareTo(BigInteger.ONE) == 1)
					b.append(" &6Stunden&b ");
				else
					b.append(" &6Stunde&b ");
			}
			if (m.compareTo(BigInteger.ZERO) == 1) {
				b.append(m);
				if (m.compareTo(BigInteger.ONE) == 1)
					b.append(" &6Minuten&b ");
				else
					b.append(" &6Minute&b ");
			}
			if (s.compareTo(BigInteger.ZERO) == 1) {
				b.append(s);
				if (s.compareTo(BigInteger.ONE) == 1)
					b.append(" &6Sekunden&b ");
				else
					b.append(" &6Sekunde&b ");
			}
			if (b.length() == 2) {
				b.append("0 &6Sekunden&b ");
			}
		}
		return b.toString();
	}
}
