/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api;

import eu.darkcube.minigame.woolbattle.provider.WoolBattleProvider;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
interface WoolBattleApiImpl {
    WoolBattleApi instance = WoolBattleProvider.PROVIDER.service(WoolBattleApi.class);
}
