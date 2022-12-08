/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi.events;

import de.dytanic.cloudnet.driver.event.Event;
import eu.darkcube.system.userapi.User;

public class UserUnloadEvent extends Event {

	private User user;

	public UserUnloadEvent(User user) {
		this.user = user;
	}

	public User getUser() {
		return this.user;
	}
}
