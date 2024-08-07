/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.util.scheduler;

import eu.darkcube.minigame.woolbattle.provider.WoolBattleProvider;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
interface TaskScheduleImpl {
    TaskScheduleProvider provider = WoolBattleProvider.PROVIDER.service(TaskScheduleProvider.class);
}
