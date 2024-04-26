/*
 * Copyright (c) 2018-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.ComponentSerializer;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

import static eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit.MinecraftReflection.findClass;
import static eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit.MinecraftReflection.findMcClassName;
import static eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit.MinecraftReflection.findNmsClass;
import static eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit.MinecraftReflection.findNmsClassName;
import static eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit.MinecraftReflection.lookup;

/**
 * A component serializer for {@code net.minecraft.server.<version>.IChatBaseComponent}.
 *
 * <p>Due to Bukkit version namespaces, the return type does not reflect the actual type.</p>
 *
 * <p>Color downsampling will be performed as necessary for the running server version.</p>
 *
 * <p>If not {@link #isSupported()}, an {@link UnsupportedOperationException} will be thrown on any serialize or deserialize operations.</p>
 *
 * @see #get()
 * @since 4.0.0
 */
@ApiStatus.Experimental // due to direct server implementation references
public final class MinecraftComponentSerializer implements ComponentSerializer<Component, Component, Object> {
  private static final MinecraftComponentSerializer INSTANCE = new MinecraftComponentSerializer();

  /**
   * Gets whether this serializer is supported.
   *
   * @return if the serializer is supported.
   * @since 4.0.0
   */
  public static boolean isSupported() {
    return SUPPORTED;
  }

  /**
   * Gets the component serializer.
   *
   * @return a component serializer
   * @since 4.0.0
   */
  public static @NotNull MinecraftComponentSerializer get() {
    return INSTANCE;
  }

  private static final @Nullable Class<?> CLASS_JSON_DESERIALIZER = findClass("com.goo".concat("gle.gson.JsonDeserializer")); // Hide from relocation checkers
  private static final @Nullable Class<?> CLASS_CHAT_COMPONENT = findClass(
    findNmsClassName("IChatBaseComponent"),
    findMcClassName("network.chat.IChatBaseComponent"),
    findMcClassName("network.chat.Component")
  );
  private static final AtomicReference<RuntimeException> INITIALIZATION_ERROR = new AtomicReference<>(new UnsupportedOperationException());

  private static final Object MC_TEXT_GSON;
  private static final MethodHandle TEXT_SERIALIZER_DESERIALIZE;
  private static final MethodHandle TEXT_SERIALIZER_SERIALIZE;

  static {
    Object gson = null;
    MethodHandle textSerializerDeserialize = null;
    MethodHandle textSerializerSerialize = null;

    try {
      if (CLASS_CHAT_COMPONENT != null) {
        // Chat serializer //
        final Class<?> chatSerializerClass = Arrays.stream(CLASS_CHAT_COMPONENT.getClasses())
          .filter(c -> {
            if (CLASS_JSON_DESERIALIZER != null) {
              return CLASS_JSON_DESERIALIZER.isAssignableFrom(c);
            } else {
              for (final Class<?> itf : c.getInterfaces()) {
                if (itf.getSimpleName().equals("JsonDeserializer")) {
                  return true;
                }
              }
              return false;
            }
          })
          .findAny()
          .orElse(findNmsClass("ChatSerializer")); // 1.7.10 compat
        if (chatSerializerClass != null) {
          final Field gsonField = Arrays.stream(chatSerializerClass.getDeclaredFields())
            .filter(m -> Modifier.isStatic(m.getModifiers()))
            .filter(m -> m.getType().equals(Gson.class))
            .findFirst()
            .orElse(null);
          if (gsonField != null) {
            gsonField.setAccessible(true);
            gson = gsonField.get(null);
          } else {
            final Method[] declaredMethods = chatSerializerClass.getDeclaredMethods();
            final Method deserialize = Arrays.stream(declaredMethods)
              .filter(m -> Modifier.isStatic(m.getModifiers()))
              .filter(m -> CLASS_CHAT_COMPONENT.isAssignableFrom(m.getReturnType()))
              .filter(m -> m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(String.class))
              .min(Comparator.comparing(Method::getName)) // prefer the #a method
              .orElse(null);
            final Method serialize = Arrays.stream(declaredMethods)
              .filter(m -> Modifier.isStatic(m.getModifiers()))
              .filter(m -> m.getReturnType().equals(String.class))
              .filter(m -> m.getParameterCount() == 1 && CLASS_CHAT_COMPONENT.isAssignableFrom(m.getParameterTypes()[0]))
              .findFirst()
              .orElse(null);
            if (deserialize != null) {
              textSerializerDeserialize = lookup().unreflect(deserialize);
            }
            if (serialize != null) {
              textSerializerSerialize = lookup().unreflect(serialize);
            }
          }
        }
      }
    } catch (final Throwable error) {
      INITIALIZATION_ERROR.set(new UnsupportedOperationException("Error occurred during initialization", error));
    }

    MC_TEXT_GSON = gson;
    TEXT_SERIALIZER_DESERIALIZE = textSerializerDeserialize;
    TEXT_SERIALIZER_SERIALIZE = textSerializerSerialize;
  }

  private static final boolean SUPPORTED = MC_TEXT_GSON != null || (TEXT_SERIALIZER_DESERIALIZE != null && TEXT_SERIALIZER_SERIALIZE != null);

  @Override
  public @NotNull Component deserialize(final @NotNull Object input) {
    if (!SUPPORTED) throw INITIALIZATION_ERROR.get();

    try {
      if (MC_TEXT_GSON != null) {
        final JsonElement element = ((Gson) MC_TEXT_GSON).toJsonTree(input);
        return BukkitComponentSerializer.gson().serializer().fromJson(element, Component.class);
      }
      return GsonComponentSerializer.gson().deserialize((String) TEXT_SERIALIZER_SERIALIZE.invoke(input));
    } catch (final Throwable error) {
      throw new UnsupportedOperationException(error);
    }
  }

  @Override
  public @NotNull Object serialize(final @NotNull Component component) {
    if (!SUPPORTED) throw INITIALIZATION_ERROR.get();

    if (MC_TEXT_GSON != null) {
      final JsonElement json = BukkitComponentSerializer.gson().serializer().toJsonTree(component);
      try {
        return ((Gson) MC_TEXT_GSON).fromJson(json, CLASS_CHAT_COMPONENT);
      } catch (final Throwable error) {
        throw new UnsupportedOperationException(error);
      }
    } else {
      try {
        return TEXT_SERIALIZER_DESERIALIZE.invoke(BukkitComponentSerializer.gson().serialize(component));
      } catch (final Throwable error) {
        throw new UnsupportedOperationException(error);
      }
    }
  }
}
