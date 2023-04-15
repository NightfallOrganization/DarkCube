/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.inventoryapi.item.meta;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class SkullBuilderMeta implements BuilderMeta {
	private UserProfile owningPlayer;

	public SkullBuilderMeta() {
	}

	public SkullBuilderMeta(UserProfile owningPlayer) {
		this.owningPlayer = owningPlayer;
	}

	public UserProfile owningPlayer() {
		return owningPlayer;
	}

	public SkullBuilderMeta owningPlayer(UserProfile owningPlayer) {
		this.owningPlayer = owningPlayer;
		return this;
	}

	@Override
	public SkullBuilderMeta clone() {
		return new SkullBuilderMeta(owningPlayer);
	}

	public static final class UserProfile {
		private final String name;
		private final UUID uuid;
		private final Texture texture;

		public UserProfile(String name, UUID uuid) {
			this(name, uuid, null);
		}

		public UserProfile(String name, UUID uuid, Texture texture) {
			this.name = name;
			this.uuid = uuid;
			this.texture = texture;
		}

		public Texture texture() {
			return texture;
		}

		public String name() {
			return name;
		}

		public UUID uniqueId() {
			return uuid;
		}

		public static class Texture {
			private final String value;
			private final String signature;

			public Texture(String value) {
				this(value, null);
			}

			public Texture(String value, String signature) {
				this.value = value;
				this.signature = signature;
			}

			/**
			 * @return The property value, likely to be base64 encoded
			 */
			@NotNull
			public String value() {
				return value;
			}

			/**
			 * @return A signature from Mojang for signed properties
			 */
			@Nullable
			public String signature() {
				return signature;
			}

			/**
			 * @return If this property has a signature or not
			 */
			public boolean signed() {
				return this.signature != null;
			}

		}
	}
}
