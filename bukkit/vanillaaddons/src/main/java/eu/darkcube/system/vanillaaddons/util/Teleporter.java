/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.util;

import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.GsonBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

public class Teleporter {

	public static final NamespacedKey teleporters =
			new NamespacedKey("vanillaaddons", "teleporters");
	private static final Gson gson = new GsonBuilder().create();
	public static final PersistentDataType<byte[], Teleporters> TELEPORTERS =
			new PersistentDataType<>() {

				@Override
				public @NotNull Class<byte[]> getPrimitiveType() {
					return byte[].class;
				}

				@Override
				public @NotNull Class<Teleporters> getComplexType() {
					return Teleporters.class;
				}

				@Override
				public byte @NotNull [] toPrimitive(@NotNull Teleporters complex,
						@NotNull PersistentDataAdapterContext context) {
					return gson.toJson(complex).getBytes(StandardCharsets.UTF_8);
				}

				@Override
				public @NotNull Teleporters fromPrimitive(byte @NotNull [] primitive,
						@NotNull PersistentDataAdapterContext context) {
					try {
						return gson.fromJson(new String(primitive, StandardCharsets.UTF_8),
								getComplexType());
					} catch (Exception e) {
						e.printStackTrace();
						return new Teleporters();
					}
				}
			};
	public static final PersistentDataType<byte[], Teleporter> TELEPORTER =
			new PersistentDataType<>() {
				@Override
				public @NotNull Class<byte[]> getPrimitiveType() {
					return byte[].class;
				}

				@Override
				public @NotNull Class<Teleporter> getComplexType() {
					return Teleporter.class;
				}

				@Override
				public byte @NotNull [] toPrimitive(@NotNull Teleporter complex,
						@NotNull PersistentDataAdapterContext context) {
					return gson.toJson(complex).getBytes(StandardCharsets.UTF_8);
				}

				@Override
				public @NotNull Teleporter fromPrimitive(byte @NotNull [] primitive,
						@NotNull PersistentDataAdapterContext context) {
					return gson.fromJson(new String(primitive, StandardCharsets.UTF_8),
							getComplexType());
				}
			};
	private Material icon;
	private String name;
	private BlockLocation block;
	private TeleportAccess access;
	private UUID owner;

	public Teleporter(Material icon, String name, BlockLocation block, TeleportAccess access,
			UUID owner) {
		this.icon = icon;
		this.name = name;
		this.block = block;
		this.access = access;
		this.owner = owner;
	}

	public void spawn() {
		Block b = this.block.block();
		b.setType(Material.RESPAWN_ANCHOR);
		RespawnAnchor anchor = (RespawnAnchor) b.getBlockData();
		anchor.setCharges(4);
		b.setBlockData(anchor);
	}

	public TeleportAccess access() {
		if (access == null)
			access = TeleportAccess.PUBLIC;
		return access;
	}

	public @Nullable UUID owner() {
		return owner;
	}

	public void owner(UUID owner) {
		this.owner = owner;
	}

	public void access(TeleportAccess access) {
		this.access = access;
	}

	public void icon(Material icon) {
		this.icon = icon;
	}

	public void block(BlockLocation block) {
		this.block = block;
	}

	public BlockLocation block() {
		return block;
	}

	public String name() {
		return name;
	}

	public void name(String name) {
		this.name = name;
	}

	public Material icon() {
		return icon;
	}

	@Override
	public int hashCode() {
		return Objects.hash(icon, name, block);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Teleporter that = (Teleporter) o;
		return icon == that.icon && Objects.equals(name, that.name) && Objects.equals(block,
				that.block);
	}

	public enum TeleportAccess {
		PUBLIC(Material.GREEN_DYE), PRIVATE(Material.RED_DYE);

		private final Material type;

		TeleportAccess(Material type) {
			this.type = type;// 189
		}// 190

		public Material getType() {
			return this.type;// 193
		}
	}
}
