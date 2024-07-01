/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.dasbabypixel.prefixplugin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

class ScoreboardManagerLoader {

    private static final Logger logger = Logger.getLogger("PrefixPlugin");

    boolean failed;
    FailureCause cause;
    IScoreboardManager scoreboardManager;

    ScoreboardManagerLoader() {
        failed = !enableVersion();
    }

    public synchronized boolean enableVersion() {
        Class<? extends IScoreboardManager> clazz = null;
        try {
            clazz = (Class<? extends IScoreboardManager>) Class.forName(IScoreboardManager.class.getPackage().getName() + ".ScoreboardManager_" + PrefixPluginBukkit.version);
        } catch (ClassNotFoundException e) {
            // class not found, try latest version instead
            try {
                var in = getClass().getClassLoader().getResourceAsStream("prefix.versions");
                if (in != null) {
                    var versions = new String(in.readAllBytes(), StandardCharsets.UTF_8).split("\n");
                    var version = versions[versions.length - 1];
                    clazz = (Class<? extends IScoreboardManager>) Class.forName(IScoreboardManager.class.getPackage().getName() + ".ScoreboardManager_" + version);
                }
            } catch (ClassNotFoundException | IOException ignored) {
            }
            if (clazz == null) cause = FailureCause.CLASS_NOT_FOUND_EXCEPTION;
        }
        if (clazz != null) {
            try {
                logger.info("Using: " + clazz.getSimpleName());
                var c = clazz.getDeclaredConstructor();
                c.setAccessible(true);
                scoreboardManager = c.newInstance();
                //				main.getServer().getPluginManager()
                //						.registerEvents(main.getScoreboardManager(), main);
            } catch (InstantiationException | IllegalAccessException ignored) {
            } catch (InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            if (scoreboardManager == null) {
                cause = FailureCause.MANAGER_COULD_NOT_LOAD;
                return false;
            }
            return true;
        }
        return false;
    }
}
