/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.bukkit.inventoryapi.item.meta;

import org.bukkit.FireworkEffect;

public final class FireworkBuilderMeta implements BuilderMeta {
	public FireworkEffect fireworkEffect;

	public FireworkBuilderMeta(FireworkEffect fireworkEffect) {
		this.fireworkEffect = fireworkEffect;
	}

	public FireworkBuilderMeta() {
	}

	public FireworkEffect fireworkEffect() {
		return fireworkEffect;
	}

	public FireworkBuilderMeta fireworkEffect(FireworkEffect fireworkEffect) {
		this.fireworkEffect = fireworkEffect;
		return this;
	}

	@Override
	public FireworkBuilderMeta clone() {
		return new FireworkBuilderMeta(fireworkEffect);
	}
}
