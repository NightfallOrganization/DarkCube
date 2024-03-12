/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.world;

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

        record Simple(double x, double y, double z, float yaw, float pitch) implements Directed {
            @Override
            public Position.Directed.Simple clone() {
                return new Position.Directed.Simple(x, y, z, yaw, pitch);
            }
        }
    }
}
