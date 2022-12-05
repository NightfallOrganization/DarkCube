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
