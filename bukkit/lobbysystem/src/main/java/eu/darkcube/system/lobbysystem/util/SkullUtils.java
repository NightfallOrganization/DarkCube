package eu.darkcube.system.lobbysystem.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import eu.darkcube.system.Reflection;

public class SkullUtils {

	private static final Class<?> GAME_PROFILE = Reflection.getClass("com.mojang.authlib.GameProfile");

	private static final Class<?> PROPERTY = Reflection.getClass("com.mojang.authlib.properties.Property");

	private static final Constructor<?> PROPERTY_NEW = Reflection.getConstructor(SkullUtils.PROPERTY, String.class,
			String.class);

	private static final Class<?> PROPERTY_MAP = Reflection.getClass("com.mojang.authlib.properties.PropertyMap");

	private static final Constructor<?> GAME_PROFILE_NEW = Reflection.getConstructor(SkullUtils.GAME_PROFILE,
			UUID.class, String.class);

	private static final Method GET_PROPERTIES = Reflection.getMethod(SkullUtils.GAME_PROFILE, "getProperties");

	private static final Method PUT = Reflection.getMethod(SkullUtils.PROPERTY_MAP, "put", Object.class, Object.class);
	
	public static final void setSkullTextureId(ItemStack skull, String textureValue) {
//		Object profile = new GameProfile(UUID.randomUUID(), null);
		Object profile = Reflection.newInstance(SkullUtils.GAME_PROFILE_NEW, UUID.randomUUID(), null);
		Object propertyMap = Reflection.invokeMethod(SkullUtils.GET_PROPERTIES, profile);
		Object property = Reflection.newInstance(SkullUtils.PROPERTY_NEW, "textures", textureValue);
		Reflection.invokeMethod(SkullUtils.PUT, propertyMap, "textures", property);
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
