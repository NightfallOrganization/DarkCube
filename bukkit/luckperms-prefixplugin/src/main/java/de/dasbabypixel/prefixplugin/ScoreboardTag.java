/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package de.dasbabypixel.prefixplugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class ScoreboardTag {

	private static final Map<UUID, ScoreboardTag> TAG_BY_PLAYER = new HashMap<>();

	private int position = 0;
	private UUID uuid;
	private int weight;

	private ScoreboardTag(UUID uuid, int weight) {
		this.uuid = uuid;
		this.weight = weight;
		loadPosition();
		TAG_BY_PLAYER.put(uuid, this);
	}

	public static ScoreboardTag getScoreboardTag(UUID uuid) {
		ScoreboardTag tag = TAG_BY_PLAYER.get(uuid);
		if (tag == null) {
			int weight = PrefixPluginBukkit.instance().getScoreboardManager().getUser(uuid)
					.getCachedData().getMetaData().getPrefixes().keySet().stream().mapToInt(i -> i)
					.max().orElse(Integer.MAX_VALUE);
			tag = new ScoreboardTag(uuid, weight);
		}
		return tag;
	}

	private void loadPosition() {
		for (ScoreboardTag tag : TAG_BY_PLAYER.values()) {
			if (position == tag.position) {
				position++;
				loadPosition();
				return;
			}
		}
	}

	public int weight() {
		return weight;
	}

	public int position() {
		return position;
	}

	public void unregister() {
		TAG_BY_PLAYER.remove(uuid);
	}

	@Override
	public String toString() {
		return Integer.MAX_VALUE - weight + "" + position;
	}
}
