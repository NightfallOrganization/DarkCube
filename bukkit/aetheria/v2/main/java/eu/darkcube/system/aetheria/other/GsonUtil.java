/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.other;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class GsonUtil {
    public static final Gson GSON = new GsonBuilder().create();

    public static <T> PersistentDataType<String, T> createDataType(Class<T> cls) {
        return new PersistentDataType<>() {
            @Override public @NotNull Class<String> getPrimitiveType() {
                return String.class;
            }

            @Override public @NotNull Class<T> getComplexType() {
                return cls;
            }

            @Override public @NotNull String toPrimitive(@NotNull T complex, @NotNull PersistentDataAdapterContext context) {
                return GSON.toJson(complex);
            }

            @Override public @NotNull T fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
                return GSON.fromJson(primitive, cls);
            }
        };
    }
}
