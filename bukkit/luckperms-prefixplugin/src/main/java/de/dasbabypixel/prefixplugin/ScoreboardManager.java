/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package de.dasbabypixel.prefixplugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

class ScoreboardManager {

	boolean failed;
	FailureCause cause;

	ScoreboardManager(Main main) {
		failed = !enableVersion(main);
	}

	@SuppressWarnings("unchecked")
	public synchronized boolean enableVersion(Main main) {
		Class<? extends IScoreboardManager> clazz = null;
		try {
			clazz = (Class<? extends IScoreboardManager>) Class.forName(
					IScoreboardManager.class.getPackage().getName() + ".ScoreboardManager_"
							+ Main.version);
		} catch (ClassNotFoundException e) {
			cause = FailureCause.CLASS_NOT_FOUND_EXCEPTION;
		}
		if (clazz != null) {
			try {
				Constructor<? extends IScoreboardManager> c = clazz.getDeclaredConstructor();
				c.setAccessible(true);
				main.setScoreboardManager(c.newInstance());
				main.getServer().getPluginManager()
						.registerEvents(main.getScoreboardManager(), main);
			} catch (InstantiationException | IllegalAccessException ignored) {
			} catch (InvocationTargetException | NoSuchMethodException e) {
				throw new RuntimeException(e);
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
