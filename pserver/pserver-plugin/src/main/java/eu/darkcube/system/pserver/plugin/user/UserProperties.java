package eu.darkcube.system.pserver.plugin.user;

import eu.darkcube.system.pserver.plugin.permission.UserPermissions;

public class UserProperties {

	public static final UserProperty<UserPermissions> PERMISSIONS = new UserProperty<>(
					"permissions", UserPermissions.class);

}
