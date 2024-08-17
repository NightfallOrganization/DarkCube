/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.util;

import java.util.function.Function;
import java.util.function.Supplier;

import eu.darkcube.system.commandapi.util.Messages;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public abstract class MinMaxBounds<T extends Number> {

    public static final SimpleCommandExceptionType ERROR_EMPTY = Messages.ERROR_EMPTY.newSimpleCommandExceptionType();
    public static final SimpleCommandExceptionType ERROR_SWAPPED = Messages.ERROR_SWAPPED.newSimpleCommandExceptionType();

    protected final T min;
    protected final T max;

    protected MinMaxBounds(T min, T max) {
        this.min = min;
        this.max = max;
    }

    protected static <T extends Number, R extends MinMaxBounds<T>> R read(StringReader reader, Reader<T, R> minMaxReader, Function<String, T> parser, Function<T, T> formatter) throws CommandSyntaxException {
        if (!reader.canRead()) {
            throw ERROR_EMPTY.createWithContext(reader);
        }
        var cursor = reader.getCursor();

        try {
        }
    }

    private static <T extends Number> T readNumber(StringReader reader, Function<String, T> parser, Supplier<DynamicCommandExceptionType> exception) {

    }

    public static class IntBound extends MinMaxBounds<Integer> {
        public static final IntBound UNBOUNDED = new IntBound(null, null);

        public IntBound(Integer min, Integer max) {
            super(min, max);
        }
    }

    public static class FloatBound extends MinMaxBounds<Float> {
        public static final FloatBound UNBOUNDED = new FloatBound(null, null);

        private FloatBound(Float min, Float max) {
            super(min, max);
        }

        public static FloatBound min(float value) {
            return new FloatBound(value, null);
        }

        public static FloatBound max(float value) {
            return new FloatBound(null, value);
        }

        public static FloatBound minMax(float min, float max) {
            return new FloatBound(min, max);
        }

        private static FloatBound create(StringReader reader, Float min, Float max) throws CommandSyntaxException {
            if (min != null && max != null && min > max) {
                throw MinMaxBounds.ERROR_SWAPPED.createWithContext(reader);
            }
            return new FloatBound(min, max);
        }

        public static FloatBound read(StringReader reader) throws CommandSyntaxException {
            return MinMaxBounds.read(reader, FloatBound::create, Float::parseFloat, Function.identity());
        }
    }

    public interface Factory<T extends Number, R extends MinMaxBounds<T>> {
        R create(@Nullable T min, @Nullable T max);
    }

    public interface Reader<T extends Number, R extends MinMaxBounds<T>> {
        R read(StringReader reader, @Nullable T min, @Nullable T max) throws CommandSyntaxException;
    }
}
