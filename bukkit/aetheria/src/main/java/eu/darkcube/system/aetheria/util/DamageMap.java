/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.util;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;

public class DamageMap {

    public static final PersistentDataType<byte[], DamageMap> TYPE = new PersistentDataType<>() {
        @Override public @NotNull Class<byte[]> getPrimitiveType() {
            return byte[].class;
        }

        @Override public @NotNull Class<DamageMap> getComplexType() {
            return DamageMap.class;
        }

        @Override public byte @NotNull [] toPrimitive(@NotNull DamageMap complex, @NotNull PersistentDataAdapterContext context) {
            ByteBuffer buffer = ByteBuffer.allocate(complex.map.size() * 24);
            for (Object2DoubleMap.Entry<UUID> entry : complex.map.object2DoubleEntrySet()) {
                buffer.putLong(entry.getKey().getLeastSignificantBits());
                buffer.putLong(entry.getKey().getMostSignificantBits());
                buffer.putDouble(entry.getDoubleValue());
            }
            return buffer.array();
        }

        @Override public @NotNull DamageMap fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
            ByteBuffer buffer = ByteBuffer.wrap(primitive);
            DamageMap damageMap = new DamageMap();
            while (buffer.position() < buffer.capacity()) {
                long least = buffer.getLong();
                long most = buffer.getLong();
                double damage = buffer.getDouble();
                damageMap.set(new UUID(most, least), damage);
            }
            return damageMap;
        }
    };

    private final Object2DoubleMap<UUID> map;

    public DamageMap() {
        this.map = new Object2DoubleOpenHashMap<>();
    }

    public void set(UUID uuid, double damage) {
        map.put(uuid, damage);
    }

    public void add(UUID uuid, double damage) {
        set(uuid, get(uuid) + damage);
    }

    public double get(UUID uuid) {
        return map.getDouble(uuid);
    }

    public @Nullable UUID topDamager() {
        return map.object2DoubleEntrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
    }
}
