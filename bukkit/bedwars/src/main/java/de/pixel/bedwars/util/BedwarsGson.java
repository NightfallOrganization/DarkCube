/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.util;

import org.bukkit.Location;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.pixel.bedwars.util.gson.TypeAdapterLocation;

public class BedwarsGson {
	public static Gson GSON = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
		@Override
		public boolean shouldSkipField(FieldAttributes var1) {
			return var1.getAnnotation(DontSerialize.class) != null;
		}

		@Override
		public boolean shouldSkipClass(Class<?> var1) {
			return false;
		}
	}).registerTypeAdapter(Location.class, TypeAdapterLocation.INSTANCE).create();
}
