/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.util;

import de.dytanic.cloudnet.common.document.gson.*;
import de.dytanic.cloudnet.driver.*;
import de.dytanic.cloudnet.ext.bridge.player.*;
import eu.darkcube.system.cloudban.util.communication.*;

public class Util {

	public static IPlayerManager getManager() {
		return CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
	}

	public static void log(String message) { 
		// TODO: Old code commented, they fixed this, right?
//		if (CloudNetDriver.getInstance().getDriverEnvironment() == DriverEnvironment.CLOUDNET) {
//
//			Module.INSTANCE.listenerReceiveMessage.handle(new ChannelMessageReceiveEvent(
//					ChannelMessage.builder().channel(Messenger.CHANNEL_LOG).message(message).build(), false));
//		} else {
			CloudNetDriver.getInstance().getMessenger().sendChannelMessage(Messenger.CHANNEL_LOG, message,
					new JsonDocument());
//		}
	}
}
