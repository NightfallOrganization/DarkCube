/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.dasbabypixel.prefixplugin;

class ScoreboardManager {

	boolean failed = false;
	FailureCause cause;
	
	ScoreboardManager(Main main) {
		failed = !enableVersion(main);
	}

	@SuppressWarnings("unchecked")
	public synchronized boolean enableVersion(Main main) {
		Class<? extends IScoreboardManager> clazz = null;
		try {
			clazz = (Class<? extends IScoreboardManager>) Class
					.forName(IScoreboardManager.class.getPackage().getName() + ".ScoreboardManager_" + Main.version);
		} catch (ClassNotFoundException e) {
			cause = FailureCause.CLASS_NOT_FOUND_EXCEPTION;
		}
		if (clazz != null) {
			try {
				main.setScoreboardManager(clazz.newInstance());
				main.getServer().getPluginManager().registerEvents(main.getScoreboardManager(), main);
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}

			if (main.getScoreboardManager() == null) {
				cause = FailureCause.MANAGER_COULD_NOT_LOAD;
				return false;
			}
			return true;
		}
		return false;
	}
}
