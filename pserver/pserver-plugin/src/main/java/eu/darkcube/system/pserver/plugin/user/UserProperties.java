/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.user;

import eu.darkcube.system.pserver.plugin.permission.UserPermissions;

public class UserProperties {

	public static final UserProperty<UserPermissions> PERMISSIONS = new UserProperty<>(
					"permissions", UserPermissions.class);

}
