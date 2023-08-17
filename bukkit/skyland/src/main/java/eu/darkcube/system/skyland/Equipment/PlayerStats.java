/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.Equipment;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerStats {

    public static final PersistentDataType<PlayerStats> TYPE = new PersistentDataType<PlayerStats>() {

        @Override public PlayerStats deserialize(Document doc, String key) {
            return doc.readObject(key, PlayerStats.class);
        }

        @Override public void serialize(Document.Mutable doc, String key, PlayerStats data) {
            doc.append(key, data);
        }

        @Override public PlayerStats clone(PlayerStats object) {
            return new PlayerStats(object.getType(), object.getMenge());
        }
    };
    public static final PersistentDataType<List<PlayerStats>> TYPEARRAY = PersistentDataTypes.list(PlayerStats.TYPE);
    PlayerStatsType type;
    int menge;

    public PlayerStats(PlayerStatsType type, int menge) {
        this.type = type;
        this.menge = menge;
    }

    public static PlayerStats parseString(String s) {

        return new PlayerStats(PlayerStatsType.valueOf(s.split("``")[0]), Integer.parseInt(s.split("``")[1]));
    }

    public static int getStatSum(PlayerStats[] ps, PlayerStatsType pst) {
        int out = 0;
        for (PlayerStats playerStats : ps) {
            if (playerStats.getType().equals(pst)) {
                out += playerStats.getMenge();
            }
        }
        return out;
    }

    public static PlayerStats[] mergePstats(List<PlayerStats> ps) {
        HashMap<PlayerStatsType, Integer> out = new HashMap<>();
        for (PlayerStats p : ps) {
            if (out.containsKey(p.getType())) {
                out.put(p.getType(), out.get(p.getType()) + p.getMenge());
            } else {
                out.put(p.getType(), p.getMenge());
            }
        }

        PlayerStats[] fout = new PlayerStats[out.keySet().size()];
        AtomicInteger i = new AtomicInteger();
        out.forEach((playerStatsType, integer) -> {
            fout[i.get()] = new PlayerStats(playerStatsType, integer);
            i.getAndIncrement();
        });
        return fout;
    }

    public static PlayerStats[] mergePstats(PlayerStats[] s, PlayerStats[] t) {

        ArrayList<PlayerStats> ps = new ArrayList<>();

        ps.addAll(List.of(s));
        ps.addAll(List.of(t));

        HashMap<PlayerStatsType, Integer> out = new HashMap<>();
        for (PlayerStats p : ps) {
            if (out.containsKey(p.getType())) {
                out.put(p.getType(), out.get(p.getType()) + p.getMenge());
            } else {
                out.put(p.getType(), p.getMenge());
            }
        }

        PlayerStats[] fout = new PlayerStats[out.keySet().size()];
        AtomicInteger i = new AtomicInteger();
        out.forEach((playerStatsType, integer) -> {
            fout[i.get()] = new PlayerStats(playerStatsType, integer);
            i.getAndIncrement();
        });
        return fout;
    }

    public PlayerStatsType getType() {
        return type;
    }

    public void setType(PlayerStatsType type) {
        this.type = type;
    }

    public int getMenge() {
        return menge;
    }

    public void setMenge(int menge) {
        this.menge = menge;
    }

    @Override public String toString() {

        return type + "``" + menge;
    }
}
