/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.com.mojang.brigadier.suggestion;

import eu.darkcube.system.libs.com.mojang.brigadier.Message;
import eu.darkcube.system.libs.com.mojang.brigadier.context.StringRange;

import java.util.Objects;

public class IntegerSuggestion extends Suggestion {
    private int value;

    public IntegerSuggestion(final StringRange range, final int value) {
        this(range, value, null);
    }

    public IntegerSuggestion(final StringRange range, final int value, final Message tooltip) {
        super(range, Integer.toString(value), tooltip);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IntegerSuggestion)) {
            return false;
        }
        final IntegerSuggestion that = (IntegerSuggestion) o;
        return value == that.value && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }

    @Override
    public String toString() {
        return "IntegerSuggestion{" +
                "value=" + value +
                ", range=" + getRange() +
                ", text='" + getText() + '\'' +
                ", tooltip='" + getTooltip() + '\'' +
                '}';
    }

    @Override
    public int compareTo(final Suggestion o) {
        if (o instanceof IntegerSuggestion) {
            return Integer.compare(value, ((IntegerSuggestion) o).value);
        }
        return super.compareTo(o);
    }

    @Override
    public int compareToIgnoreCase(final Suggestion b) {
        return compareTo(b);
    }
}
