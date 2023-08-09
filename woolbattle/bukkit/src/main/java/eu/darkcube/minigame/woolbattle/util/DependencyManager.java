/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.util;

import com.google.common.io.ByteStreams;
import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class DependencyManager {

    private WoolBattleBukkit plugin;

    public DependencyManager(WoolBattleBukkit plugin) {
        this.plugin = plugin;
    }

    public void loadDependencies(Dependency... dependencies) {
        for (Dependency depend : dependencies) {
            Path file = depend.getFile();
            if (!Files.exists(file)) {
                try {
                    InputStream in =
                            new URL("https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.20/mysql-connector-java-8.0.20.jar").openConnection()
                                    .getInputStream();
                    byte[] bytes = ByteStreams.toByteArray(in);
                    Files.write(file, bytes);
                    in.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            plugin.pluginClassLoader().loadJar(file);
        }
    }

    public enum Dependency {

        MYSQL_DRIVER("mysql_driver.jar");

        private String url;

        Dependency(String url) {
            this.url = url;
        }

        public String getURL() {
            return url;
        }

        public Path getFile() {
            WoolBattleBukkit plugin = WoolBattleBukkit.instance();
            File libs = new File(plugin.getDataFolder(), "libs");
            if (!libs.exists())
                libs.mkdirs();
            File file = new File(libs, getURL());
            return file.toPath();
        }
    }
}
