/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.version.v1_8_8;

public class Version implements eu.darkcube.system.version.Version {
	private eu.darkcube.system.version.v1_8_8.CommandAPI commandAPI;
	private ItemProvider itemProvider;

	@Override
	public void init() {
		this.commandAPI = new eu.darkcube.system.version.v1_8_8.CommandAPI();
		this.itemProvider = new ItemProvider();
	}

	@Override
	public String getClassifier() {
		return "1_8_8";
	}

	@Override
	public eu.darkcube.system.version.v1_8_8.CommandAPI commandApi() {
		return commandAPI;
	}

	@Override
	public ItemProvider itemProvider() {
		return itemProvider;
	}

}
