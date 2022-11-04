package de.dasbabypixel.prefixplugin;

import java.util.UUID;

class ScoreboardManager_v1_13_R2 extends IScoreboardManager {

	@Override
	void setPrefix(UUID uuid, String prefix) {
		super.setPrefix(uuid, prefix);
		super.setPrefixAfter1_13(uuid, prefix);
	}
	
}
