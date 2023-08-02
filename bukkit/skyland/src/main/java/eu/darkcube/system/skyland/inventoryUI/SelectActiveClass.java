/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.inventoryUI;

import eu.darkcube.system.skyland.SkylandClassSystem.SkylandClassTemplate;
import eu.darkcube.system.skyland.SkylandClassSystem.SkylandPlayer;
import eu.darkcube.system.skyland.SkylandClassSystem.SkylandPlayerClass;
import eu.darkcube.system.skyland.SkylandClassSystem.SkylandPlayerModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SelectActiveClass extends InventoryUI{

	public SelectActiveClass(Player p) {
		super(3, "select your active Class", p);
		//todo perhaps add another page (todo)
		SkylandPlayer skp = SkylandPlayerModifier.getSkylandPlayer(p);
		for (int i = 0; i < skp.getSkylandPlayerClasses().size(); i++) {
			setInvSlot(new UIitemStack(true, skp.getSkylandPlayerClasses().get(i).getsClass()
					.getDisplay()), i);
		}
	}//todo

	@Override
	public boolean invClickEvent(InventoryClickEvent e) {
		if (e.getSlot() < SkylandClassTemplate.values().length) {
			inventory.close();
			e.getWhoClicked()
					.sendMessage("You chose " + SkylandClassTemplate.values()[e.getSlot()]);
			SkylandPlayer skp = SkylandPlayerModifier.getSkylandPlayer((Player) e.getWhoClicked());
			//SkylandPlayerClass skpc = skp.getSkylandPlayerClasses().get(e.getSlot());
			skp.setActiveClass(e.getSlot());
			super.invClickEvent(e);
			return true;
		}
		super.invClickEvent(e);
		return false;
	}
}
