package eu.darkcube.system.userapi.data;

import eu.darkcube.system.userapi.User;

public interface UserModifier {

	default void onLoad(User user) {}

	default void onUnload(User user) {}

}
