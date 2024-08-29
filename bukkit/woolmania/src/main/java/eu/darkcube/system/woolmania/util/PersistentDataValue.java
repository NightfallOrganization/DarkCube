/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.util;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PersistentDataValue<T> {
    NamespacedKey namespacedKey;
    Type type;
    PersistentDataContainer persistentDataContainer;
    static Gson gson = new Gson();
    T defaultValue;

    public PersistentDataValue(NamespacedKey namespacedKey, Type type, PersistentDataContainer persistentDataContainer, T defautValue) {
        this.namespacedKey = namespacedKey;
        this.type = type;
        this.persistentDataContainer = persistentDataContainer;
        this.defaultValue = defautValue;
    }

    public T getOrDefault() {
        var value = persistentDataContainer.get(namespacedKey, PersistentDataType.STRING);
        if (value == null) {
            return defaultValue;
        }
        return gson.fromJson(value, type);
    }

    public void reset() {
        set(defaultValue);
    }

    /**
     * @param value this value needs to be serializable by Gson
     */
    public void set(T value) {
        persistentDataContainer.set(namespacedKey, PersistentDataType.STRING, gson.toJson(value));
    }

    public boolean isValuePresent() {
        return persistentDataContainer.has(namespacedKey);
    }

}