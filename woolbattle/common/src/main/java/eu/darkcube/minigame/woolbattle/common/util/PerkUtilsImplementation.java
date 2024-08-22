/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.util.PerkUtils;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.sound.Sound;

public class PerkUtilsImplementation implements PerkUtils.Implementation {
    @Override
    public void playSoundNotEnoughWool(WBUser user) {
        user.playSound(Sound.sound(Key.key("minecraft:entity.villager.no"), Sound.Source.VOICE, 100, 1));
    }
}
