/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.util;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.minigame.woolbattle.api.world.Position;
import eu.darkcube.system.util.data.PersistentDataType;

public interface WoolBattlePersistentDataTypes {
    PersistentDataType<Position.Directed> POSITION_DIRECTED = new PersistentDataType<>() {
        @Override
        public Position.Directed deserialize(Document doc, String key) {
            var d = doc.readDocument(key);
            var x = d.getDouble("x");
            var y = d.getDouble("y");
            var z = d.getDouble("z");
            var yaw = d.getFloat("yaw");
            var pitch = d.getFloat("pitch");
            return new Position.Directed.Simple(x, y, z, yaw, pitch);
        }

        @Override
        public void serialize(Document.Mutable doc, String key, Position.Directed data) {
            var d = Document.newJsonDocument();
            d.append("x", data.x());
            d.append("y", data.y());
            d.append("z", data.z());
            d.append("yaw", data.yaw());
            d.append("pitch", data.pitch());
            doc.append(key, d);
        }

        @Override
        public Position.Directed clone(Position.Directed object) {
            return object.clone();
        }
    };
}
