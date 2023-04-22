/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.inventoryUI;

import eu.darkcube.system.skyland.Skyland;
import eu.darkcube.system.skyland.SkylandClassSystem.SkylandPlayer;
import org.bukkit.entity.Player;

public class SelectActiveClass extends InventoryUI{

	public SelectActiveClass(int row, String name, Player p) {
		super(3, "select your active Class", p);

		SkylandPlayer skp = Skyland.getInstance().getSkylandPlayers(p);
		for (int i = 0; i < skp.getSkylandPlayerClasses().size(); i++) {
			setInvSlot(new UIitemStack(true, skp.getSkylandPlayerClasses().get(i).getsClass()
					.getDisplay()), i);
		}
	}//todo
}
