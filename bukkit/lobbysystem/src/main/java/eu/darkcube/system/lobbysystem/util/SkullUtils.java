/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import eu.darkcube.system.ReflectionUtils;

public class SkullUtils {

	private static final Class<?> GAME_PROFILE = ReflectionUtils.getClass("com.mojang.authlib.GameProfile");

	private static final Class<?> PROPERTY = ReflectionUtils.getClass("com.mojang.authlib.properties.Property");

	private static final Constructor<?> PROPERTY_NEW = ReflectionUtils.getConstructor(SkullUtils.PROPERTY, String.class,
			String.class);

	private static final Class<?> PROPERTY_MAP = ReflectionUtils.getClass("com.mojang.authlib.properties.PropertyMap");

	private static final Constructor<?> GAME_PROFILE_NEW = ReflectionUtils.getConstructor(SkullUtils.GAME_PROFILE,
			UUID.class, String.class);

	private static final Method GET_PROPERTIES = ReflectionUtils.getMethod(SkullUtils.GAME_PROFILE, "getProperties");

	private static final Method PUT = ReflectionUtils.getMethod(SkullUtils.PROPERTY_MAP, "put", Object.class,
			Object.class);

	public static final void setSkullTextureId(ItemStack skull, String textureValue) {
//		Object profile = new GameProfile(UUID.randomUUID(), null);
		Object profile = ReflectionUtils.instantiateObject(SkullUtils.GAME_PROFILE_NEW, UUID.randomUUID(), null);
		Object propertyMap = ReflectionUtils.invokeMethod(profile, SkullUtils.GET_PROPERTIES);
		Object property = ReflectionUtils.instantiateObject(SkullUtils.PROPERTY_NEW, "textures", textureValue);
		ReflectionUtils.invokeMethod(propertyMap, SkullUtils.PUT, "textures", property);
//		profile.getProperties().put("textures", new Property("textures", textureValue));
		ItemMeta meta = skull.getItemMeta();
		try {
			Field f = meta.getClass().getDeclaredField("profile");
			f.setAccessible(true);
			f.set(meta, profile);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		skull.setItemMeta(meta);
	}

}
