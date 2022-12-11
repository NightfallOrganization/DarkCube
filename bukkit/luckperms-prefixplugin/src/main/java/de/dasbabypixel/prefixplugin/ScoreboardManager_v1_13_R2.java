/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.dasbabypixel.prefixplugin;

import java.util.UUID;

class ScoreboardManager_v1_13_R2 extends IScoreboardManager {

	@Override
	void setPrefix(UUID uuid, String prefix) {
		super.setPrefix(uuid, prefix);
		super.setPrefixAfter1_13(uuid, prefix);
	}
	
}
