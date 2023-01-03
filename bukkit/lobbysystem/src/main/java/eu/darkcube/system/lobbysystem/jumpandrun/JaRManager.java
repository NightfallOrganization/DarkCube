/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.jumpandrun;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.database.Database;
import eu.darkcube.system.lobbysystem.user.LobbyUser;

import java.util.ArrayList;
import java.util.List;

public class JaRManager {

	final List<JaRRegion> regions = new ArrayList<>();
	private final Database database;

	public JaRManager() {
		database = CloudNetDriver.getInstance().getDatabaseProvider()
				.getDatabase("lobbysystem_jumpandrun");
		queryRegions();
	}

	private void queryRegions() {
		regions.clear();
		if (database.contains("regions")) {
			JsonDocument dregions = database.get("regions");
			for (String key : dregions.keys()) {
				JaRRegion region = JaRRegion.deserialize(dregions.getString(key));
				regions.add(region);
			}
		}
	}

	public void saveRegions() {
		JsonDocument dregions = new JsonDocument();
		int i = 0;
		for (JaRRegion r : regions) {
			dregions.append(Integer.toString(i++), r.serialize());
		}
		if (!database.contains("regions")) {
			database.insert("regions", dregions);
		} else {
			database.update("regions", dregions);
		}
	}

	public List<JaRRegion> getRegions() {
		return this.regions;
	}

	public JaR startJaR(LobbyUser user) {
		JaR jar = new JaR(this, user);
		if (jar.getCurrentBlock() != null)
			user.getUser().asPlayer().teleport(jar.getCurrentBlock().getLocation().add(0.5, 1, 0.5)
					.setDirection(jar.getCurrentDirection()));
		return jar;
	}

	//	public void startJaR(Player player) {
	//		JaR jar = createJaR(player);
	//		Block cur = jar.getCurrentBlock();
	//		if (cur == null) {
	//			return;
	//		}
	//		player.teleport(cur.getLocation().add(0.5, 1, 0.5).setDirection(jar.getCurrentDirection()));
	//	}

}
