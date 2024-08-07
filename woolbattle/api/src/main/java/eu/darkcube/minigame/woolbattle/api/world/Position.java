/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.world;

import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
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
        @NotNull
        Directed clone();

        @NotNull
        Directed aligned();

        default @NotNull Directed simple() {
            return simple(this);
        }

        static @NotNull Directed simple(@NotNull Directed position) {
            return new Simple(position.x(), position.y(), position.z(), position.yaw(), position.pitch());
        }

        PersistentDataType<Directed> TYPE = new PersistentDataType<>() {
            @Override
            public Directed deserialize(JsonElement json) {
                var d = json.getAsJsonObject();
                var x = d.get("x").getAsDouble();
                var y = d.get("y").getAsDouble();
                var z = d.get("z").getAsDouble();
                var yaw = d.get("yaw").getAsFloat();
                var pitch = d.get("pitch").getAsFloat();
                return new Simple(x, y, z, yaw, pitch);
            }

            @Override
            public JsonElement serialize(Directed data) {
                var d = new JsonObject();
                d.addProperty("x", data.x());
                d.addProperty("y", data.y());
                d.addProperty("z", data.z());
                d.addProperty("yaw", data.yaw());
                d.addProperty("pitch", data.pitch());
                return d;
            }

            @Override
            public Directed clone(Directed object) {
                return object.clone();
            }
        };

        record Simple(double x, double y, double z, float yaw, float pitch) implements Directed {
            private static final float F360 = 360f;
            private static final float F180 = 180f;

            @Override
            public @NotNull Directed.Simple clone() {
                return new Directed.Simple(x, y, z, yaw, pitch);
            }

            @Override
            public @NotNull Directed.Simple aligned() {
                return new Directed.Simple(nice(x), nice(y), nice(z), getNiceYaw(yaw), getNicePitch(pitch));
            }

            @Override
            public @NotNull Directed simple() {
                return this;
            }

            private static double nice(double c) {
                return ((int) (c * 2)) / 2D;
            }

            private static float getNicePitch(float y) {
                y += 90f;
                var interval = 22.5f;
                if (Math.round(y % interval) == y % interval) return y - 90f;
                var hInterval = interval / 2f;
                for (var i = F180; i >= 0f; i -= interval) {
                    var val1 = i - hInterval;
                    var val2 = i + hInterval;

                    if (y >= val1 && y <= val2) {
                        y = i;
                        y %= F180;
                        break;
                    }
                }
                return y - 90f;
            }

            private static float getNiceYaw(float y) {
                var interval = 22.5f;
                var hInterval = interval / 2f;
                y = antiNegYaw(y, hInterval);
                for (var i = F360; i >= 0f; i -= interval) {
                    var val1 = i - hInterval;
                    var val2 = i + hInterval;
                    if (y >= val1 && y < val2) {
                        y = i;
                        y %= F360;
                        break;
                    }
                }
                return y;
            }

            private static float antiNegYaw(float x, float hInterval) {
                if (x < (0f - hInterval)) {
                    x = F360 + x;
                }
                if (x >= F360) {
                    x %= F360;
                    x = -x;
                }
                return x;
            }
        }
    }
}
