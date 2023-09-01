/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.util.data;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class BukkitPersistentDataTypes extends PersistentDataTypes {
    public static final PersistentDataType<Location> LOCATION = new PersistentDataType<>() {

        @Override public Location deserialize(Document doc, String key) {
            Document d = doc.readDocument(key);
            double x = d.getDouble("x");
            double y = d.getDouble("y");
            double z = d.getDouble("z");
            float yaw = d.getFloat("yaw");
            float pitch = d.getFloat("pitch");
            World world = Bukkit.getWorld(java.util.UUID.fromString(d.getString("world"))); // TODO Upgrade
            return new Location(world, x, y, z, yaw, pitch);
        }

        @Override public void serialize(Document.Mutable doc, String key, Location data) {
            Document.Mutable d = Document.newJsonDocument();
            d.append("x", data.getX());
            d.append("y", data.getY());
            d.append("z", data.getZ());
            d.append("yaw", data.getYaw());
            d.append("pitch", data.getPitch());
            d.append("world", data.getWorld().getUID().toString());
            doc.append(key, d);
        }

        @Override public Location clone(Location object) {
            return object.clone();
        }
    };
}
