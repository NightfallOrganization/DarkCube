/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.util;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.io.ByteStreams;

import eu.darkcube.system.loader.PluginClassLoader;
import eu.darkcube.system.lobbysystem.Lobby;

public class DependencyManager {

	private PluginClassLoader loader;

	public DependencyManager(PluginClassLoader loader) {
		this.loader = loader;
	}

	public void loadDependencies() {
		for(Dependency dependency : Dependency.values()) {
			Path file = dependency.getFile();
			if(!Files.exists(file)) {
				try {
					InputStream in = Lobby.getInstance().getResource(dependency.getURL());
					byte[] bytes = ByteStreams.toByteArray(in);
					Files.write(file, bytes);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			loader.loadJar(file);
		}
	}

	public static enum Dependency {
		
		;

		private String url;

		Dependency(String url) {
			this.url = url;
		}

		public String getURL() {
			return url;
		}

		public Path getFile() {
			Lobby plugin = Lobby.getInstance();
			File libs = new File(plugin.getDataFolder(), "libs");
			if (!libs.exists())
				libs.mkdirs();
			File file = new File(libs, getURL());
			return file.toPath();
		}
	}
}
