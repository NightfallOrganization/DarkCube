/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.util;

import java.math.*;
import java.util.*;
import java.util.regex.*;

public class DurationDeserializer {

	private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]");

	public static Duration deserialize(String userInput) {

		StringBuilder unit = new StringBuilder();

		BigInteger years = BigInteger.ZERO;
		BigInteger months = BigInteger.ZERO;
		BigInteger days = BigInteger.ZERO;
		BigInteger hours = BigInteger.ZERO;
		BigInteger minutes = BigInteger.ZERO;
		BigInteger seconds = BigInteger.ZERO;

		for (int i = 0; i < userInput.length(); i++) {

			char c = userInput.charAt(i);

			if (unit.length() == 0)
				unit.append('0');

			switch (c) {
			case 'Y':
			case 'y':
				years = years.add(new BigInteger(unit.toString()));
				unit = new StringBuilder();
				break;
			case 'M':
				months = months.add(new BigInteger(unit.toString()));
				unit = new StringBuilder();
				break;
			case 'D':
			case 'd':
				days = days.add(new BigInteger(unit.toString()));
				unit = new StringBuilder();
				break;
			case 'H':
			case 'h':
				hours = hours.add(new BigInteger(unit.toString()));
				unit = new StringBuilder();
				break;
			case 'm':
				minutes = minutes.add(new BigInteger(unit.toString()));
				unit = new StringBuilder();
				break;
			case 's':
			case 'S':
				seconds = seconds.add(new BigInteger(unit.toString()));
				unit = new StringBuilder();
				break;
			default:

				if (!NUMBER_PATTERN.matcher(Character.toString(c)).matches()) {
					return null;
				}
				unit.append(c);
				break;
			}

		}

		return new Duration(years, months, days, hours, minutes, seconds, UUID.randomUUID());
	}
}

/*
 * 1m14d12h6m3s
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
