/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.util;

import eu.darkcube.system.BaseMessage;

import java.util.Objects;

public record Message(String key) implements BaseMessage {

	public static final String PREFIX_MODIFIER = "VANILLAADDONS_";

	@Override
	public String getPrefixModifier() {
		return PREFIX_MODIFIER;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Message message = (Message) o;
		return Objects.equals(key, message.key);
	}

	@Override
	public int hashCode() {
		return Objects.hash(key);
	}

	@Override
	public String toString() {
		return key;
	}
}
