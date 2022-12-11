/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.util.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class StringBuilderExclusion implements ExclusionStrategy {
	@Override
	public boolean shouldSkipField(FieldAttributes field) {
		return false;
	}

	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		if (clazz.equals(StringBuilder.class)) {
			return true;
		}
		return false;
	}
}
