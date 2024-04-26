/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.module;

import java.util.HashMap;
import java.util.Map;

public class ModuleManager {
	private final Map<Class<? extends Module>, Module> modules = new HashMap<>();

	public void addModule(Module module) {
		modules.put(module.getClass(), module);
	}

	public <T extends Module> T module(Class<T> clz) {
		return clz.cast(modules.get(clz));
	}

	public void enableAll() {
		for (Module module : modules.values()) {
			try {
				module.onEnable();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	public void disableAll() {
		for (Module module : modules.values()) {
			try {
				module.onDisable();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
}
