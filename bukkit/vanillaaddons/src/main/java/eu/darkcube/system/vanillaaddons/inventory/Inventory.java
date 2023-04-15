/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.inventory;

import eu.darkcube.system.vanillaaddons.AUser;

public interface Inventory<Data> {

	void init(Data data);

	void open(AUser user);

	boolean isOpened(AUser user);

	void close(AUser user);

}
