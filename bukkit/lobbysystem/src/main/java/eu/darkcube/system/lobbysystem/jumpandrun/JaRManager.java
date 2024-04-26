/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.jumpandrun;

import eu.cloudnetservice.driver.database.Database;
import eu.cloudnetservice.driver.database.DatabaseProvider;
import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.lobbysystem.user.LobbyUser;

import java.util.ArrayList;
import java.util.List;

public class JaRManager {

    final List<JaRRegion> regions = new ArrayList<>();
    private final Database database;

    public JaRManager() {
        database = InjectionLayer.boot().instance(DatabaseProvider.class).database("lobbysystem_jumpandrun");
        queryRegions();
    }

    private void queryRegions() {
        regions.clear();
        if (database.contains("regions")) {
            Document dregions = database.get("regions");
            for (String key : dregions.keys()) {
                JaRRegion region = JaRRegion.deserialize(dregions.getString(key));
                regions.add(region);
            }
        }
    }

    public void saveRegions() {
        Document.Mutable dregions = Document.newJsonDocument();
        int i = 0;
        for (JaRRegion r : regions) {
            dregions.append(Integer.toString(i++), r.serialize());
        }
        database.insert("regions", dregions);
    }

    public List<JaRRegion> getRegions() {
        return this.regions;
    }

    public JaR startJaR(LobbyUser user) {
        JaR jar = new JaR(this, user);
        if (jar.getCurrentBlock() != null)
            user.asPlayer().teleport(jar.getCurrentBlock().getLocation().add(0.5, 1, 0.5).setDirection(jar.getCurrentDirection()));
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
