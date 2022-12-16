/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.permission;

import eu.darkcube.system.pserver.common.*;
import eu.darkcube.system.pserver.plugin.user.*;

public class UserPermissions {

	private boolean admin = false;
	private boolean owner = false;

	public UserPermissions() {
	}

	public UserPermissions(User user) {
		System.out.println(user);
		System.out.println(PServerProvider.getInstance().getCurrentPServer());
		System.out.println(PServerProvider.getInstance().getCurrentPServer().getOwners());
		if (PServerProvider.getInstance().getCurrentPServer().getOwners().contains(user.getUUID())) {
			this.owner = true;
		}
	}

	public UserPermissions(boolean admin, boolean owner) {
		this.admin = admin;
		this.owner = owner;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public void setOwner(boolean owner) {
		this.owner = owner;
	}

	public boolean isAdmin() {
		return admin;
	}

	public boolean isOwner() {
		return owner;
	}

}
