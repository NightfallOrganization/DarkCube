/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.bossbar;

import eu.darkcube.system.libs.net.kyori.adventure.util.PlatformAPI;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;

/**
 * This class is a major hack, intended to allow certain platforms a way
 * to define a platform-native counterpart to an Adventure boss bar.
 *
 * @deprecated not an official API, and may disappear without warning
 */
@ApiStatus.Internal
@Deprecated
@PlatformAPI
abstract class HackyBossBarPlatformBridge {
}
