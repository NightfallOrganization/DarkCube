/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.world;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.util.data.PersistentDataType;

public interface Position extends Cloneable {
    double x();

    double y();

    double z();

    Position clone();

    record Simple(double x, double y, double z) implements Position {
        @Override
        public Position.Simple clone() {
            return new Position.Simple(x, y, z);
        }
    }

    interface Directed extends Position {
        float yaw();

        float pitch();

        @Override
        Directed clone();

        PersistentDataType<Directed> TYPE = new PersistentDataType<>() {
            @Override
            public Directed deserialize(Document doc, String key) {
                var d = doc.readDocument(key);
                var x = d.getDouble("x");
                var y = d.getDouble("y");
                var z = d.getDouble("z");
                var yaw = d.getFloat("yaw");
                var pitch = d.getFloat("pitch");
                return new Simple(x, y, z, yaw, pitch);
            }

            @Override
            public void serialize(Document.Mutable doc, String key, Directed data) {
                var d = Document.newJsonDocument();
                d.append("x", data.x());
                d.append("y", data.y());
                d.append("z", data.z());
                d.append("yaw", data.yaw());
                d.append("pitch", data.pitch());
                doc.append(key, d);
            }

            @Override
            public Directed clone(Directed object) {
                return object.clone();
            }
        };

        record Simple(double x, double y, double z, float yaw, float pitch) implements Directed {
            @Override
            public Position.Directed.Simple clone() {
                return new Position.Directed.Simple(x, y, z, yaw, pitch);
            }
        }
    }
}
