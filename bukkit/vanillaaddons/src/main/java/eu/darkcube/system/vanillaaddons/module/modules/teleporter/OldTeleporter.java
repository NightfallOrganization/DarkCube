/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.module.modules.teleporter;

import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.GsonBuilder;
import eu.darkcube.system.vanillaaddons.module.modules.teleporter.Teleporter.TeleportAccess;
import eu.darkcube.system.vanillaaddons.util.BlockLocation;
import org.bukkit.Material;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.UUID;

public class OldTeleporter {
	private static final Gson gson = new GsonBuilder().create();
	public static final PersistentDataType<byte[], TeleportersOld> TELEPORTERS =
			new PersistentDataType<>() {

				@Override
				public @NotNull Class<byte[]> getPrimitiveType() {
					return byte[].class;
				}

				@Override
				public @NotNull Class<TeleportersOld> getComplexType() {
					return TeleportersOld.class;
				}

				@Override
				public byte @NotNull [] toPrimitive(@NotNull TeleportersOld complex,
						@NotNull PersistentDataAdapterContext context) {
					return gson.toJson(complex).getBytes(StandardCharsets.UTF_8);
				}

				@Override
				public @NotNull TeleportersOld fromPrimitive(byte @NotNull [] primitive,
						@NotNull PersistentDataAdapterContext context) {
					try {
						return gson.fromJson(new String(primitive, StandardCharsets.UTF_8),
								getComplexType());
					} catch (Exception e) {
						e.printStackTrace();
						return new TeleportersOld();
					}
				}
			};
	public static final PersistentDataType<byte[], OldTeleporter> TELEPORTER =
			new PersistentDataType<>() {
				@Override
				public @NotNull Class<byte[]> getPrimitiveType() {
					return byte[].class;
				}

				@Override
				public @NotNull Class<OldTeleporter> getComplexType() {
					return OldTeleporter.class;
				}

				@Override
				public byte @NotNull [] toPrimitive(@NotNull OldTeleporter complex,
						@NotNull PersistentDataAdapterContext context) {
					return gson.toJson(complex).getBytes(StandardCharsets.UTF_8);
				}

				@Override
				public @NotNull OldTeleporter fromPrimitive(byte @NotNull [] primitive,
						@NotNull PersistentDataAdapterContext context) {
					return gson.fromJson(new String(primitive, StandardCharsets.UTF_8),
							getComplexType());
				}
			};
	public Material icon;
	public String name;
	public BlockLocation block;
	public TeleportAccess access;
	public UUID owner;
	public LinkedHashSet<UUID> trustedList;
}
