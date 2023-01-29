/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.util;

import java.math.*;
import java.util.*;

public enum Calculator {

	SECOND(1l),

	MINUTE(60l),

	HOUR(60l * 60l),

	DAY(24l * 60l * 60l),

	MONTH(30l * 24l * 60l * 60l),

	YEAR(12l * 30l * 24l * 60l * 60l);

	private BigInteger modifier;

	Calculator(long modifier) {
		this.modifier = BigInteger.valueOf(modifier);
	}

	public BigInteger calculate(Calculator type, BigInteger typeInput) {
		return fromSeconds(type.toSeconds(typeInput));
	}

	public BigInteger fromSeconds(BigInteger seconds) {
		return seconds.divide(modifier);
	}

	public BigInteger toSeconds(BigInteger input) {
		return input.multiply(modifier);
	}

	public static Map<Calculator, BigInteger> deserialize(BigInteger second) {
		Map<Calculator, BigInteger> m = new HashMap<>();
		
		BigInteger years = YEAR.fromSeconds(second);
		BigInteger months = MONTH.fromSeconds(second.mod(YEAR.modifier));
		BigInteger days = DAY.fromSeconds(second.mod(MONTH.modifier));
		BigInteger hours = HOUR.fromSeconds(second.mod(DAY.modifier));
		BigInteger minutes = MINUTE.fromSeconds(second.mod(HOUR.modifier));
		BigInteger seconds = SECOND.fromSeconds(second.mod(MINUTE.modifier));

		m.put(YEAR, years);
		m.put(MONTH, months);
		m.put(DAY, days);
		m.put(HOUR, hours);
		m.put(MINUTE, minutes);
		m.put(SECOND, seconds);

		return m;
	}
}
