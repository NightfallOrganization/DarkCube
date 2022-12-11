/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.util;

import java.math.*;
import java.util.*;

public class Duration extends TimeHolder {

	public static final Duration WARNING = new Duration(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO,
			BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, UUID.fromString("00000000-0000-0000-0000-000000000000"));
	public static final Duration PERMANENT = new Duration(BigInteger.valueOf(Long.MAX_VALUE), BigInteger.ZERO, BigInteger.ZERO,
			BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, UUID.fromString("00000000-0000-0000-0000-999999999999"));

	public Duration(BigInteger years, BigInteger months, BigInteger days, BigInteger hours, BigInteger minutes,
			BigInteger seconds, UUID uuid) {
		super(years, months, days, hours, minutes, seconds, uuid);
	}

	public String toText() {
		if(getYear().equals(PERMANENT.getYear()))
			return "�cPERMANENT";
		StringBuilder b = new StringBuilder().append("�b");
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
				b.append("0 Sekunden ");
			}
		}
		return b.toString();
	}
}
