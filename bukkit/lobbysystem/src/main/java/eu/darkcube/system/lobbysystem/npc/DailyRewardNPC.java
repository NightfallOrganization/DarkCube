/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.npc;

import eu.darkcube.system.libs.com.github.juliarn.npc.NPC;
import eu.darkcube.system.libs.com.github.juliarn.npc.modifier.MetadataModifier;
import eu.darkcube.system.libs.com.github.juliarn.npc.profile.Profile;
import eu.darkcube.system.lobbysystem.Lobby;

import java.util.Collections;
import java.util.Random;
import java.util.UUID;

public class DailyRewardNPC {

	public static NPC create() {
		Profile profile = new Profile(new UUID(new Random().nextLong(), 0), "§aDaily Reward",
				Collections.singleton(new DailyRewardSkin()));
		NPC npc = NPC.builder().profile(profile)
				.location(Lobby.getInstance().getDataManager().getDailyRewardNPCLocation())
				.imitatePlayer(false).lookAtPlayer(true).spawnCustomizer(
						(npc1, player) -> npc1.metadata()
								.queue(MetadataModifier.EntityMetadata.SKIN_LAYERS, true).send())
				.usePlayerProfiles(false).build(Lobby.getInstance().getNpcPool());
		new NPCKnockbackThread(npc).runTaskTimer(Lobby.getInstance(), 5, 5);
		return npc;
	}

	static class DailyRewardSkin extends Profile.Property {

		public DailyRewardSkin() {
			super("textures",
					"ewogICJ0aW1lc3RhbXAiIDogMTY3MDcxNTY1NDI5OCwKICAicHJvZmlsZUlkIiA6ICJmNTgyNGRmNGIwMTU0MDA4OGRhMzUyYTQxODU1MDQ0NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJGb3hHYW1lcjUzOTIiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTI3NjBlMWJhNjYxMDMxNWJmYWM5NzAyZjQzYjZjZDI5N2Y4ZjUyOWYyOGZkNGFiNGU0YWJmYzM4ZDVjNzBkNyIKICAgIH0KICB9Cn0=",
					"IGrBvjujIajLSeWBqg9kXboUFp7dPkzZqeC1L9NXKWRBhHeBGWw5Sj1hFFwBgaUaDPGXJXXjok+l62VkxDb9ygA9E0FMzplTZlNu71tx4XyO2xi3Kdn4mvdPY9r1ap/+HNW7ICiy/ReL+RQ9eJ+79kzK73ygD19r2oHGaw79k1eERgSuCRU3hynkxxj9VMbdlMaXssKXVEYfYea2s1STmyp3PzRRxgGGN19kB5PAe4K9nh1s/bVw3RweO7mOxpqnTFC9rfzguuNOgbi+s49ljZa9g+LzDIO56AovusY553y5mE8QG/jAoVymK8SZcklgbDBYy/zZPb+kZanDzNxy0zU/2ADxNZezW03iRiJ6LZDV3YRDiUZOa68mzMJqyGAspVg0r+gGQNLkiO1+Xvrjzf5hPne5TeC+PM28qo8NU9p3RFHXbkHN8pfzD73Hnk7QPslm17GtXYwRXjzV9ExCVY6vIKaH6na8azBEKQj0e40+RDZoZzPEBU5v8P16U2iA9yujnhQDw8aGEQSUIR3VJbaboNG29K/TaegqRLrh823toDDPg/d4my2bsr2drsfxnjT7d5waXOhNS0S1ZcE5wWGdOBZjJUYAOQDkVZq/5r5e9nKS0yy8fqbUZLI5NZyUzRQeS+K6flgIwGzvSY7U3zGIe+M6AHM1Epu29sfcOl4=");
		}

	}

}
