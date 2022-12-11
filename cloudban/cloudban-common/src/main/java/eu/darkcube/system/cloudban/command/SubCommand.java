/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.command;

public abstract class SubCommand extends Command {

	public SubCommand(String[] names, String usage, String permission, String prefix, String description) {
		super(names, usage, permission, prefix, description, false);
	}

	private String spaced;

	public void setSpaced(String spaced) {
		this.spaced = spaced;
	}

	public String getSpaced() {
		return spaced;
	}

}
