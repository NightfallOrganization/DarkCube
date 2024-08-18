/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.enums;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public enum Sounds {
    BUY(Sound.ENTITY_PLAYER_LEVELUP, 50f, 2f),
    SOUND_SET(Sound.BLOCK_END_PORTAL_FRAME_FILL, 50f, 2f),
    NO(Sound.ENTITY_VILLAGER_NO, 50f, 1f),
    TELEPORT(Sound.BLOCK_END_PORTAL_FRAME_FILL, 50f, 2f),

    THROW(Sound.ENTITY_EGG_THROW, 1f, 1f),

    FARMING_SOUND_STANDARD(Sound.ENTITY_ITEM_PICKUP, 50f, 2f, false),
    FARMING_SOUND_WOOLBATTLE(Sound.ENTITY_ITEM_PICKUP, 50f, 1f, true)

    ;

    private final Sound sound;
    private final float volume;
    private final float pitch;
    private final Boolean locked;

    Sounds(Sound sound, float volume, float pitch, boolean locked) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.locked = locked;
    }

    Sounds(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.locked = false;
    }

    public static List<Sounds> unlockedSounds() {
        List<Sounds> unlockedSounds = new ArrayList<>();
        for (Sounds sound : Sounds.values()) {
            if (!sound.isLocked()) {
                unlockedSounds.add(sound);
            }
        }
        return List.copyOf(unlockedSounds);
    }

    public Sound getSound() {
        return this.sound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public boolean isLocked() {
        return locked;
    }

    public void playSound(Player player) {
        player.playSound(player.getLocation(), this.sound, this.volume, this.pitch);
    }
}
