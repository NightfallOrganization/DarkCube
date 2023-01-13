/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import java.util.function.Function;

public class MinMaxBoundsWrapped {

	public static final MinMaxBoundsWrapped UNBOUNDED =
			new MinMaxBoundsWrapped((Float) null, (Float) null);

	public static final SimpleCommandExceptionType ERROR_INTS_ONLY =
			Messages.ERROR_INTS_ONLY.newSimpleCommandExceptionType();

	private final Float min;

	private final Float max;

	public MinMaxBoundsWrapped(Float min, Float max) {
		this.min = min;
		this.max = max;
	}

	public static MinMaxBoundsWrapped fromReader(StringReader reader, boolean isFloatingPoint,
			Function<Float, Float> valueFunction) throws CommandSyntaxException {
		if (!reader.canRead()) {
			throw MinMaxBounds.ERROR_EMPTY.createWithContext(reader);
		}
		int i = reader.getCursor();
		Float f = MinMaxBoundsWrapped.map(MinMaxBoundsWrapped.fromReader(reader, isFloatingPoint),
				valueFunction);
		Float f1;
		if (reader.canRead(2) && reader.peek() == '.' && reader.peek(1) == '.') {
			reader.skip();
			reader.skip();
			f1 = MinMaxBoundsWrapped.map(MinMaxBoundsWrapped.fromReader(reader, isFloatingPoint),
					valueFunction);
			if (f == null && f1 == null) {
				reader.setCursor(i);
				throw MinMaxBounds.ERROR_EMPTY.createWithContext(reader);
			}
		} else {
			if (!isFloatingPoint && reader.canRead() && reader.peek() == '.') {
				reader.setCursor(i);
				throw MinMaxBoundsWrapped.ERROR_INTS_ONLY.createWithContext(reader);
			}

			f1 = f;
		}

		if (f == null && f1 == null) {
			reader.setCursor(i);
			throw MinMaxBounds.ERROR_EMPTY.createWithContext(reader);
		}
		return new MinMaxBoundsWrapped(f, f1);
	}

	private static Float fromReader(StringReader reader, boolean isFloatingPoint)
			throws CommandSyntaxException {
		int i = reader.getCursor();

		while (reader.canRead() && MinMaxBoundsWrapped.isValidNumber(reader, isFloatingPoint)) {
			reader.skip();
		}

		String s = reader.getString().substring(i, reader.getCursor());
		if (s.isEmpty()) {
			return null;
		}
		try {
			return Float.parseFloat(s);
		} catch (NumberFormatException numberformatexception) {
			if (isFloatingPoint) {
				throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble()
						.createWithContext(reader, s);
			}
			throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt()
					.createWithContext(reader, s);
		}
	}

	private static boolean isValidNumber(StringReader reader, boolean isFloatingPoint) {
		char c0 = reader.peek();
		if ((c0 < '0' || c0 > '9') && c0 != '-') {
			if (isFloatingPoint && c0 == '.') {
				return !reader.canRead(2) || reader.peek(1) != '.';
			}
			return false;
		}
		return true;
	}

	private static Float map(Float value, Function<Float, Float> valueFunction) {
		return value == null ? null : valueFunction.apply(value);
	}

	public Float getMin() {
		return this.min;
	}

	public Float getMax() {
		return this.max;
	}

}
