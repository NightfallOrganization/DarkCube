/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.util.gson.TypeAdapterLocation;
import eu.darkcube.minigame.woolbattle.util.gson.TypeAdapterMap;
import org.bukkit.Location;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class GsonSerializer {

    public static final Gson gson;

    static {
        GsonBuilder b = new GsonBuilder().registerTypeAdapter(Location.class, TypeAdapterLocation.INSTANCE).registerTypeAdapter(Map.class, TypeAdapterMap.INSTANCE).disableHtmlEscaping();
        b.addSerializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getAnnotation(DontSerialize.class) != null;
            }

            @Override
            public boolean shouldSkipClass(Class<?> var1) {
                return var1.isAnnotationPresent(DontSerialize.class);
            }
        });
        gson = b.create();
//		JsonDocument.GSON = gson;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface DontSerialize {

    }
}
