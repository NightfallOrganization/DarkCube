/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi.argument;

import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.commandapi.util.MathHelper;
import eu.darkcube.system.commandapi.util.Vector2f;
import eu.darkcube.system.commandapi.util.Vector3d;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Objects;

public class LocalLocationArgument implements ILocationArgument {
    private final double left;
    private final double up;
    private final double forwards;

    public LocalLocationArgument(double leftIn, double upIn, double forwardsIn) {
        this.left = leftIn;
        this.up = upIn;
        this.forwards = forwardsIn;
    }

    public static LocalLocationArgument parse(StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        double d0 = LocalLocationArgument.parseCoord(reader, i);
        if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            double d1 = LocalLocationArgument.parseCoord(reader, i);
            if (reader.canRead() && reader.peek() == ' ') {
                reader.skip();
                double d2 = LocalLocationArgument.parseCoord(reader, i);
                return new LocalLocationArgument(d0, d1, d2);
            }
            reader.setCursor(i);
            throw Vec3Argument.POS_INCOMPLETE.createWithContext(reader);
        }
        reader.setCursor(i);
        throw Vec3Argument.POS_INCOMPLETE.createWithContext(reader);
    }

    private static double parseCoord(StringReader reader, int start) throws CommandSyntaxException {
        if (!reader.canRead()) {
            throw LocationPart.EXPECTED_DOUBLE.createWithContext(reader);
        } else if (reader.peek() != '^') {
            reader.setCursor(start);
            throw Vec3Argument.POS_MIXED_TYPES.createWithContext(reader);
        } else {
            reader.skip();
            return reader.canRead() && reader.peek() != ' ' ? reader.readDouble() : 0.0D;
        }
    }

    @Override public Vector3d getPosition(CommandSource source) {
        Vector2f vector2f = source.getRotation();
        Vector3d vector3d = source.getEntityAnchorType().apply(source);
        float f = MathHelper.cos((vector2f.x + 90.0F) * ((float) Math.PI / 180F));
        float f1 = MathHelper.sin((vector2f.x + 90.0F) * ((float) Math.PI / 180F));
        float f2 = MathHelper.cos(-vector2f.y * ((float) Math.PI / 180F));
        float f3 = MathHelper.sin(-vector2f.y * ((float) Math.PI / 180F));
        float f4 = MathHelper.cos((-vector2f.y + 90.0F) * ((float) Math.PI / 180F));
        float f5 = MathHelper.sin((-vector2f.y + 90.0F) * ((float) Math.PI / 180F));
        Vector3d vector3d1 = new Vector3d(f * f2, f3, f1 * f2);
        Vector3d vector3d2 = new Vector3d(f * f4, f5, f1 * f4);
        Vector3d vector3d3 = vector3d1.crossProduct(vector3d2).scale(-1.0D);
        double d0 = vector3d1.x * this.forwards + vector3d2.x * this.up + vector3d3.x * this.left;
        double d1 = vector3d1.y * this.forwards + vector3d2.y * this.up + vector3d3.y * this.left;
        double d2 = vector3d1.z * this.forwards + vector3d2.z * this.up + vector3d3.z * this.left;
        return new Vector3d(vector3d.x + d0, vector3d.y + d1, vector3d.z + d2);
    }

    @Override public Vector2f getRotation(CommandSource source) {
        return Vector2f.ZERO;
    }

    @Override public boolean isXRelative() {
        return true;
    }

    @Override public boolean isYRelative() {
        return true;
    }

    @Override public boolean isZRelative() {
        return true;
    }

    @Override public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (!(p_equals_1_ instanceof LocalLocationArgument)) {
            return false;
        } else {
            LocalLocationArgument locallocationargument = (LocalLocationArgument) p_equals_1_;
            return this.left == locallocationargument.left && this.up == locallocationargument.up && this.forwards == locallocationargument.forwards;
        }
    }

    @Override public int hashCode() {
        return Objects.hash(this.left, this.up, this.forwards);
    }
}
