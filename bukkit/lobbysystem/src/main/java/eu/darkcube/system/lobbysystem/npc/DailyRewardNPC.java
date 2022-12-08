/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.npc;

import java.util.Collections;
import java.util.Random;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.SpawnCustomizer;
import com.github.juliarn.npc.modifier.MetadataModifier;
import com.github.juliarn.npc.profile.Profile;

import eu.darkcube.system.lobbysystem.Lobby;

public class DailyRewardNPC {

	public static NPC create() {
		Profile profile = new Profile(new UUID(new Random().nextLong(), 0), "§aDaily Reward",
				Collections.singleton(new DailyRewardSkin()));
		NPC npc = NPC.builder()
				.profile(profile)
				.location(Lobby.getInstance().getDataManager().getDailyRewardNPCLocation())
				.imitatePlayer(false)
				.lookAtPlayer(true)
				.spawnCustomizer(new SpawnCustomizer() {

					@Override
					public void handleSpawn(@NotNull NPC npc, @NotNull Player player) {
						npc.metadata().queue(MetadataModifier.EntityMetadata.SKIN_LAYERS, true).send();
					}

				})
				.usePlayerProfiles(false)
				.build(Lobby.getInstance().getNpcPool());
		new NPCKnockbackThread(npc).start();
		return npc;
	}

	private static class DailyRewardSkin extends Profile.Property {

		public DailyRewardSkin() {
			super("textures",
					"ewogICJ0aW1lc3RhbXAiIDogMTU5MjIzMDU2MTM4NywKICAicHJvZmlsZUlkIiA6ICI5ZDFhNGMwNGQ3M2Y0Njc0ODg0NjBj"
							+ "YjUyYjU5YmEzNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJEaWVzZXNNTGVubkciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgO"
							+ "iB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy"
							+ "5taW5lY3JhZnQubmV0L3RleHR1cmUvNmU0NGNjNjQ3MjI0MmMyOTcxZTkyZjg2MmVmZjI1ZDg0YWFiYzY1N2E4ZGYyYzB"
							+ "mNmNmZjhlNmQ5ZjFmNmE1IgogICAgfQogIH0KfQ==",
					"VIIeMriekC/89hPWzsO3mg3spiehxXe9md7fMys1NlNX9PI354gzHMx+Pu1rq8AwBQMxAL6KZumKF3ZlBMTSxiYF3yoj/ddAOeu"
							+ "hUZ0d8n8TaHLk93XFZwu/D5fbvIVXYdCvqHP/STdAgg/gqBGpmINxRH7T+LynzjIc2zM7c7knC0S+3kn7wqo1Ql7s3wOimGH"
							+ "tW4Peqg6EiUTsZZ12Orr9VlVziUyWU1wfrblTROT4GWOXkJ/koQk804P1R0GDpumuY4KSY2hOuUX0ZWYC+6LLPvbiD9+rO7q"
							+ "KoRY8wE0wjbp61yGJ/0/cMeMdyRzi2u7FI7COys7yB9FTqkDPD1XKYECnmqGNUhTSP3u7RHA4TsCZxh418oiZX62k/NUGNBq"
							+ "9a++rWYLgSoabL0G7nDhxwfyf5NyhgPuSUTImKVnvE+M3LGwvO4DH6o/9hjoAFxEZWmx0bfhjZ2VbYQj7etwJeJ+8yBlFbBS"
							+ "mCLqxI5KFgKbnNi4QpR8rioF/HVGr6Nwisb8KigC5Y1ZJpOw3mmGnkqaaS/y6f2fkPThJRNZPjLyW4XwDkXU1ofhIsSAoj7B"
							+ "vmJM2xWh9Z/Qo+JnmkbvKLvx3jSjggPX6vdi3w+1yMzPlZ2V6g7TwSWTtcbL3hQF3je6mvZOdnfnPRvuZGn4m34bQFkpUBsz"
							+ "8y6lseHk=");
		}

	}

}
