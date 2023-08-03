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

import java.util.ArrayList;
import java.util.List;

public class UINewClassSelect extends InventoryUI {
	public UINewClassSelect(Player p) {
		super(3, "select class", p);
		for (int i = 0; i < SkylandClassTemplate.values().length; i++) {
			setInvSlot(new UIitemStack(true, SkylandClassTemplate.values()[i].getDisplay()), i / 9,
					i % 9);
		}
	}

	@Override
	public boolean invClickEvent(InventoryClickEvent e) {

		super.invClickEvent(e);
		if (e.getSlot() < SkylandClassTemplate.values().length) {
			e.getWhoClicked()
					.sendMessage("You chose " + SkylandClassTemplate.values()[e.getSlot()]);
			SkylandPlayer skp = SkylandPlayerModifier.getSkylandPlayer((Player) e.getWhoClicked());
			SkylandPlayerClass skpc =
					new SkylandPlayerClass(SkylandClassTemplate.values()[e.getSlot()], 1,
							new ArrayList<>());
			List<SkylandPlayerClass> classes = skp.getSkylandPlayerClasses();
			classes.add(skpc);
			skp.setSkylandPlayerClasses(classes);
			skp.setActiveClass(skp.getSkylandPlayerClasses().size()-1);
			inventory.close();
			return true;
		}

		return false;
	}


}
