/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.plugin.permission;

import eu.darkcube.system.pserver.plugin.user.User;

public class UserPermissions {

	private boolean admin = false;
	private boolean owner = false;

	public UserPermissions() {
	}

	public UserPermissions(User user) {
	}

	public UserPermissions(boolean admin, boolean owner) {
		this.admin = admin;
		this.owner = owner;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public boolean isOwner() {
		return owner;
	}

	public void setOwner(boolean owner) {
		this.owner = owner;
	}

}
