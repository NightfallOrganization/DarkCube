package eu.darkcube.system;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;

public class Reflection {

	public static final String MINECRAFT_PREFIX = "net.minecraft.server";
	public static final String CRAFTBUKKIT_PREFIX = "org.bukkit.craftbukkit";

	private static final String version = Bukkit.getServer().getClass().getName().split("\\.")[3];

	public static Class<?> getVersionClass(String prefix, String suffix) {
		try {
			return Class.forName(prefix + "." + version + "." + suffix);
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static Class<?> getClass(String name) {
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	public static Object newInstance(Constructor<?> constructor, Object... arguments) {
		try {
			return constructor.newInstance(arguments);
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		} catch (InvocationTargetException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... arguments) {
		try {
			return clazz.getConstructor(arguments);
		} catch (NoSuchMethodException ex) {
			ex.printStackTrace();
		} catch (SecurityException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static Object getFieldValue(Field field, Object object) {
		try {
			return field.get(object);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static Field getField(Class<?> clazz, String field) {
		try {
			return clazz.getField(field);
		} catch (NoSuchFieldException ex) {
			ex.printStackTrace();
		} catch (SecurityException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static Method getMethod(Class<?> clazz, String name, Class<?>... arguments) {
		try {
			return clazz.getMethod(name, arguments);
		} catch (NoSuchMethodException ex) {
			ex.printStackTrace();
		} catch (SecurityException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static Object invokeMethod(Method method, Object object, Object... arguments) {
		try {
			return method.invoke(object, arguments);
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		} catch (InvocationTargetException ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
