package eu.darkcube.system.commandapi.v3;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public abstract class MinMaxBounds<T extends Number> {

	public static final SimpleCommandExceptionType ERROR_EMPTY = Message.ERROR_EMPTY.newSimpleCommandExceptionType();

	public static final SimpleCommandExceptionType ERROR_SWAPPED = Message.ERROR_SWAPPED
			.newSimpleCommandExceptionType();

	protected final T min;

	protected final T max;

	protected MinMaxBounds(T min, T max) {
		this.min = min;
		this.max = max;
	}

	public T getMin() {
		return this.min;
	}

	public T getMax() {
		return this.max;
	}

	public boolean isUnbounded() {
		return this.min == null && this.max == null;
	}

	public JsonElement serialize() {
		if (this.isUnbounded()) {
			return JsonNull.INSTANCE;
		} else if (this.min != null && this.min.equals(this.max)) {
			return new JsonPrimitive(this.min);
		} else {
			JsonObject jsonobject = new JsonObject();
			if (this.min != null) {
				jsonobject.addProperty("min", this.min);
			}

			if (this.max != null) {
				jsonobject.addProperty("max", this.max);
			}

			return jsonobject;
		}
	}

	protected static <T extends Number, R extends MinMaxBounds<T>> R fromJson(JsonElement element, R defaultIn,
			BiFunction<JsonElement, String, T> biFunction, MinMaxBounds.IBoundFactory<T, R> boundedFactory) {
		if (element != null && !element.isJsonNull()) {
			if (JSONUtils.isNumber(element)) {
				T t2 = biFunction.apply(element, "value");
				return boundedFactory.create(t2, t2);
			}
			JsonObject jsonobject = JSONUtils.getJsonObject(element, "value");
			T t = jsonobject.has("min") ? biFunction.apply(jsonobject.get("min"), "min") : null;
			T t1 = jsonobject.has("max") ? biFunction.apply(jsonobject.get("max"), "max") : null;
			return boundedFactory.create(t, t1);
		}
		return defaultIn;
	}

	protected static <T extends Number, R extends MinMaxBounds<T>> R fromReader(StringReader reader,
			MinMaxBounds.IBoundReader<T, R> minMaxReader, Function<String, T> valueFunction,
			Supplier<DynamicCommandExceptionType> commandExceptionSupplier, Function<T, T> function)
			throws CommandSyntaxException {
		if (!reader.canRead()) {
			throw MinMaxBounds.ERROR_EMPTY.createWithContext(reader);
		}
		int i = reader.getCursor();

		try {
			T t = MinMaxBounds.optionallyFormat(MinMaxBounds.readNumber(reader, valueFunction, commandExceptionSupplier), function);
			T t1;
			if (reader.canRead(2) && reader.peek() == '.' && reader.peek(1) == '.') {
				reader.skip();
				reader.skip();
				t1 = MinMaxBounds.optionallyFormat(MinMaxBounds.readNumber(reader, valueFunction, commandExceptionSupplier), function);
				if (t == null && t1 == null) {
					throw MinMaxBounds.ERROR_EMPTY.createWithContext(reader);
				}
			} else {
				t1 = t;
			}

			if (t == null && t1 == null) {
				throw MinMaxBounds.ERROR_EMPTY.createWithContext(reader);
			}
			return minMaxReader.create(reader, t, t1);
		} catch (CommandSyntaxException commandsyntaxexception) {
			reader.setCursor(i);
			throw new CommandSyntaxException(commandsyntaxexception.getType(), commandsyntaxexception.getRawMessage(),
					commandsyntaxexception.getInput(), i);
		}
	}

	private static <T extends Number> T readNumber(StringReader reader, Function<String, T> stringToValueFunction,
			Supplier<DynamicCommandExceptionType> commandExceptionSupplier) throws CommandSyntaxException {
		int i = reader.getCursor();

		while (reader.canRead() && MinMaxBounds.isAllowedInputChat(reader)) {
			reader.skip();
		}

		String s = reader.getString().substring(i, reader.getCursor());
		if (s.isEmpty()) {
			return null;
		}
		try {
			return stringToValueFunction.apply(s);
		} catch (NumberFormatException numberformatexception) {
			throw commandExceptionSupplier.get().createWithContext(reader, s);
		}
	}

	private static boolean isAllowedInputChat(StringReader reader) {
		char c0 = reader.peek();
		if ((c0 < '0' || c0 > '9') && c0 != '-') {
			if (c0 != '.') {
				return false;
			}
			return !reader.canRead(2) || reader.peek(1) != '.';
		}
		return true;
	}

	private static <T> T optionallyFormat(T value, Function<T, T> formatterFunction) {
		return value == null ? null : formatterFunction.apply(value);
	}

	public static class FloatBound extends MinMaxBounds<Float> {

		public static final MinMaxBounds.FloatBound UNBOUNDED = new MinMaxBounds.FloatBound((Float) null, (Float) null);

		private final Double minSquared;

		private final Double maxSquared;

		private static MinMaxBounds.FloatBound create(StringReader reader, Float min, Float max)
				throws CommandSyntaxException {
			if (min != null && max != null && min > max) {
				throw MinMaxBounds.ERROR_SWAPPED.createWithContext(reader);
			}
			return new MinMaxBounds.FloatBound(min, max);
		}

		private static Double square(Float value) {
			return value == null ? null : value.doubleValue() * value.doubleValue();
		}

		private FloatBound(Float min, Float max) {
			super(min, max);
			this.minSquared = FloatBound.square(min);
			this.maxSquared = FloatBound.square(max);
		}

		public static MinMaxBounds.FloatBound atLeast(float value) {
			return new MinMaxBounds.FloatBound(value, (Float) null);
		}

		public boolean test(float value) {
			if (this.min != null && this.min > value) {
				return false;
			}
			return this.max == null || !(this.max < value);
		}

		public boolean testSquared(double value) {
			if (this.minSquared != null && this.minSquared > value) {
				return false;
			}
			return this.maxSquared == null || !(this.maxSquared < value);
		}

		public static MinMaxBounds.FloatBound fromJson(JsonElement element) {
			return MinMaxBounds.fromJson(element, FloatBound.UNBOUNDED, JSONUtils::getFloat, MinMaxBounds.FloatBound::new);
		}

		public static MinMaxBounds.FloatBound fromReader(StringReader reader) throws CommandSyntaxException {
			return FloatBound.fromReader(reader, (p_211358_0_) -> {
				return p_211358_0_;
			});
		}

		public static MinMaxBounds.FloatBound fromReader(StringReader reader, Function<Float, Float> valueFunction)
				throws CommandSyntaxException {
			return MinMaxBounds.fromReader(reader, MinMaxBounds.FloatBound::create, Float::parseFloat,
					CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidFloat, valueFunction);
		}

	}

	@FunctionalInterface
	public interface IBoundFactory<T extends Number, R extends MinMaxBounds<T>> {

		R create(T p_create_1_, T p_create_2_);

	}

	@FunctionalInterface
	public interface IBoundReader<T extends Number, R extends MinMaxBounds<T>> {

		R create(StringReader p_create_1_, T p_create_2_, T p_create_3_) throws CommandSyntaxException;

	}

	public static class IntBound extends MinMaxBounds<Integer> {

		public static final MinMaxBounds.IntBound UNBOUNDED = new MinMaxBounds.IntBound((Integer) null, (Integer) null);

		private static MinMaxBounds.IntBound create(StringReader reader, Integer min, Integer max)
				throws CommandSyntaxException {
			if (min != null && max != null && min > max) {
				throw MinMaxBounds.ERROR_SWAPPED.createWithContext(reader);
			}
			return new MinMaxBounds.IntBound(min, max);
		}

		private static Long square(Integer value) {
			return value == null ? null : value.longValue() * value.longValue();
		}

		private IntBound(Integer min, Integer max) {
			super(min, max);
			IntBound.square(min);
			IntBound.square(max);
		}

		public static MinMaxBounds.IntBound exactly(int value) {
			return new MinMaxBounds.IntBound(value, value);
		}

		public static MinMaxBounds.IntBound atLeast(int value) {
			return new MinMaxBounds.IntBound(value, (Integer) null);
		}

		public boolean test(int value) {
			if (this.min != null && this.min > value) {
				return false;
			}
			return this.max == null || this.max >= value;
		}

		public static MinMaxBounds.IntBound fromJson(JsonElement element) {
			return MinMaxBounds.fromJson(element, IntBound.UNBOUNDED, JSONUtils::getInt, MinMaxBounds.IntBound::new);
		}

		public static MinMaxBounds.IntBound fromReader(StringReader reader) throws CommandSyntaxException {
			return IntBound.fromReader(reader, (p_211346_0_) -> {
				return p_211346_0_;
			});
		}

		public static MinMaxBounds.IntBound fromReader(StringReader reader, Function<Integer, Integer> valueFunction)
				throws CommandSyntaxException {
			return MinMaxBounds.fromReader(reader, MinMaxBounds.IntBound::create, Integer::parseInt,
					CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidInt, valueFunction);
		}

	}

}
