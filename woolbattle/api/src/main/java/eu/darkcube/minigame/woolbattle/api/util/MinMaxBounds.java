/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.util;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import eu.darkcube.system.commandapi.util.Messages;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public interface MinMaxBounds<T extends Number> {

    SimpleCommandExceptionType ERROR_EMPTY = Messages.ERROR_EMPTY.newSimpleCommandExceptionType();
    SimpleCommandExceptionType ERROR_SWAPPED = Messages.ERROR_SWAPPED.newSimpleCommandExceptionType();

    Optional<T> min();

    Optional<T> max();

    default boolean isAny() {
        return this.min().isEmpty() && this.max().isEmpty();
    }

    default Optional<T> unwrapPoint() {
        var min = this.min();
        var max = this.max();
        return min.equals(max) ? min : Optional.empty();
    }

    static <T extends Number, R extends MinMaxBounds<T>> R fromReader(StringReader commandReader, MinMaxBounds.BoundsFromReaderFactory<T, R> commandFactory, Function<String, T> converter, Supplier<DynamicCommandExceptionType> exceptionTypeSupplier, Function<T, T> mapper) throws CommandSyntaxException {
        if (!commandReader.canRead()) {
            throw ERROR_EMPTY.createWithContext(commandReader);
        } else {
            var cursor = commandReader.getCursor();

            try {
                var optional = readNumber(commandReader, converter, exceptionTypeSupplier).map(mapper);
                Optional<T> optional2;
                if (commandReader.canRead(2) && commandReader.peek() == '.' && commandReader.peek(1) == '.') {
                    commandReader.skip();
                    commandReader.skip();
                    optional2 = readNumber(commandReader, converter, exceptionTypeSupplier).map(mapper);
                    if (optional.isEmpty() && optional2.isEmpty()) {
                        throw ERROR_EMPTY.createWithContext(commandReader);
                    }
                } else {
                    optional2 = optional;
                }

                if (optional.isEmpty() && optional2.isEmpty()) {
                    throw ERROR_EMPTY.createWithContext(commandReader);
                } else {
                    return commandFactory.create(commandReader, optional, optional2);
                }
            } catch (CommandSyntaxException var8) {
                commandReader.setCursor(cursor);
                throw new CommandSyntaxException(var8.getType(), var8.getRawMessage(), var8.getInput(), cursor);
            }
        }
    }

    private static <T extends Number> Optional<T> readNumber(StringReader reader, Function<String, T> converter, Supplier<DynamicCommandExceptionType> exceptionTypeSupplier) throws CommandSyntaxException {
        var i = reader.getCursor();

        while (reader.canRead() && isAllowedInputChat(reader)) {
            reader.skip();
        }

        var string = reader.getString().substring(i, reader.getCursor());
        if (string.isEmpty()) {
            return Optional.empty();
        } else {
            try {
                return Optional.of(converter.apply(string));
            } catch (NumberFormatException var6) {
                throw exceptionTypeSupplier.get().createWithContext(reader, string);
            }
        }
    }

    private static boolean isAllowedInputChat(StringReader reader) {
        var c = reader.peek();
        return c >= '0' && c <= '9' || c == '-' || c == '.' && (!reader.canRead(2) || reader.peek(1) != '.');
    }

    @FunctionalInterface
    interface BoundsFromReaderFactory<T extends Number, R extends MinMaxBounds<T>> {
        R create(StringReader reader, Optional<T> min, Optional<T> max) throws CommandSyntaxException;
    }

    record Doubles(@Override Optional<Double> min, @Override Optional<Double> max, Optional<Double> minSq, Optional<Double> maxSq) implements MinMaxBounds<Double> {
        public static final MinMaxBounds.Doubles ANY = new MinMaxBounds.Doubles(Optional.empty(), Optional.empty());

        private Doubles(Optional<Double> min, Optional<Double> max) {
            this(min, max, squareOpt(min), squareOpt(max));
        }

        private static MinMaxBounds.Doubles create(StringReader reader, Optional<Double> min, Optional<Double> max) throws CommandSyntaxException {
            if (min.isPresent() && max.isPresent() && min.get() > max.get()) {
                throw ERROR_SWAPPED.createWithContext(reader);
            } else {
                return new MinMaxBounds.Doubles(min, max);
            }
        }

        private static Optional<Double> squareOpt(Optional<Double> value) {
            return value.map(d -> d * d);
        }

        public static MinMaxBounds.Doubles exactly(double value) {
            return new MinMaxBounds.Doubles(Optional.of(value), Optional.of(value));
        }

        public static MinMaxBounds.Doubles between(double min, double max) {
            return new MinMaxBounds.Doubles(Optional.of(min), Optional.of(max));
        }

        public static MinMaxBounds.Doubles atLeast(double value) {
            return new MinMaxBounds.Doubles(Optional.of(value), Optional.empty());
        }

        public static MinMaxBounds.Doubles atMost(double value) {
            return new MinMaxBounds.Doubles(Optional.empty(), Optional.of(value));
        }

        public boolean matches(double value) {
            return (this.min.isEmpty() || !(this.min.get() > value)) && (this.max.isEmpty() || !(this.max.get() < value));
        }

        public boolean matchesSqr(double value) {
            return (this.minSq.isEmpty() || !(this.minSq.get() > value)) && (this.maxSq.isEmpty() || !(this.maxSq.get() < value));
        }

        public static MinMaxBounds.Doubles fromReader(StringReader reader) throws CommandSyntaxException {
            return fromReader(reader, value -> value);
        }

        public static MinMaxBounds.Doubles fromReader(StringReader reader, Function<Double, Double> mapper) throws CommandSyntaxException {
            return MinMaxBounds.fromReader(reader, MinMaxBounds.Doubles::create, Double::parseDouble, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidDouble, mapper);
        }
    }

    record Ints(@Override Optional<Integer> min, @Override Optional<Integer> max, Optional<Long> minSq, Optional<Long> maxSq) implements MinMaxBounds<Integer> {
        public static final MinMaxBounds.Ints ANY = new MinMaxBounds.Ints(Optional.empty(), Optional.empty());

        private Ints(Optional<Integer> min, Optional<Integer> max) {
            this(min, max, min.map(i -> i.longValue() * i.longValue()), squareOpt(max));
        }

        private static MinMaxBounds.Ints create(StringReader reader, Optional<Integer> min, Optional<Integer> max) throws CommandSyntaxException {
            if (min.isPresent() && max.isPresent() && min.get() > max.get()) {
                throw ERROR_SWAPPED.createWithContext(reader);
            } else {
                return new MinMaxBounds.Ints(min, max);
            }
        }

        private static Optional<Long> squareOpt(Optional<Integer> value) {
            return value.map(i -> i.longValue() * i.longValue());
        }

        public static MinMaxBounds.Ints exactly(int value) {
            return new MinMaxBounds.Ints(Optional.of(value), Optional.of(value));
        }

        public static MinMaxBounds.Ints between(int min, int max) {
            return new MinMaxBounds.Ints(Optional.of(min), Optional.of(max));
        }

        public static MinMaxBounds.Ints atLeast(int value) {
            return new MinMaxBounds.Ints(Optional.of(value), Optional.empty());
        }

        public static MinMaxBounds.Ints atMost(int value) {
            return new MinMaxBounds.Ints(Optional.empty(), Optional.of(value));
        }

        public boolean matches(int value) {
            return (this.min.isEmpty() || this.min.get() <= value) && (this.max.isEmpty() || this.max.get() >= value);
        }

        public boolean matchesSqr(long value) {
            return (this.minSq.isEmpty() || this.minSq.get() <= value) && (this.maxSq.isEmpty() || this.maxSq.get() >= value);
        }

        public static MinMaxBounds.Ints fromReader(StringReader reader) throws CommandSyntaxException {
            return fromReader(reader, value -> value);
        }

        public static MinMaxBounds.Ints fromReader(StringReader reader, Function<Integer, Integer> converter) throws CommandSyntaxException {
            return MinMaxBounds.fromReader(reader, MinMaxBounds.Ints::create, Integer::parseInt, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidInt, converter);
        }
    }
}
