/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Direction {
    DOWN(0, 1, -1, "down", AxisDirection.NEGATIVE, Axis.Y, new Vector3i(0, -1, 0)),
    UP(1, 0, -1, "up", AxisDirection.POSITIVE, Axis.Y, new Vector3i(0, 1, 0)),
    NORTH(2, 3, 2, "north", AxisDirection.NEGATIVE, Axis.Z, new Vector3i(0, 0, -1)),
    SOUTH(3, 2, 0, "south", AxisDirection.POSITIVE, Axis.Z, new Vector3i(0, 0, 1)),
    WEST(4, 5, 1, "west", AxisDirection.NEGATIVE, Axis.X, new Vector3i(-1, 0, 0)),
    EAST(5, 4, 3, "east", AxisDirection.POSITIVE, Axis.X, new Vector3i(1, 0, 0));

    private static final Direction[] VALUES = Direction.values();
    private static final Map<String, Direction> NAME_LOOKUP = Arrays
            .stream(Direction.VALUES)
            .collect(Collectors.toMap(Direction::getName2, (p_199787_0_) -> {
                return p_199787_0_;
            }));
    private static final Direction[] BY_INDEX = Arrays.stream(Direction.VALUES).sorted(Comparator.comparingInt((p_199790_0_) -> {
        return p_199790_0_.index;
    })).toArray((p_199788_0_) -> {
        return new Direction[p_199788_0_];
    });
    private static final Direction[] BY_HORIZONTAL_INDEX = Arrays.stream(Direction.VALUES).filter((p_199786_0_) -> {
        return p_199786_0_.getAxis().isHorizontal();
    }).sorted(Comparator.comparingInt((p_199789_0_) -> {
        return p_199789_0_.horizontalIndex;
    })).toArray((p_199791_0_) -> {
        return new Direction[p_199791_0_];
    });
    private final int index;
    private final int opposite;
    private final int horizontalIndex;
    private final String name;
    private final Axis axis;
    private final AxisDirection axisDirection;
    private final Vector3i directionVec;

    Direction(int indexIn, int oppositeIn, int horizontalIndexIn, String nameIn, AxisDirection axisDirectionIn, Axis axisIn, Vector3i directionVecIn) {
        this.index = indexIn;
        this.horizontalIndex = horizontalIndexIn;
        this.opposite = oppositeIn;
        this.name = nameIn;
        this.axis = axisIn;
        this.axisDirection = axisDirectionIn;
        this.directionVec = directionVecIn;
    }

    public static Direction rotateFace(Matrix4f matrixIn, Direction directionIn) {
        Vector3i vector3i = directionIn.getDirectionVec();
        Vector4f vector4f = new Vector4f(vector3i.getX(), vector3i.getY(), vector3i.getZ(), 0.0F);
        vector4f.transform(matrixIn);
        return Direction.getFacingFromVector(vector4f.getX(), vector4f.getY(), vector4f.getZ());
    }

    public static Direction byName(String name) {
        return name == null ? null : Direction.NAME_LOOKUP.get(name.toLowerCase(Locale.ROOT));
    }

    public static Direction byIndex(int index) {
        return Direction.BY_INDEX[MathHelper.abs(index % Direction.BY_INDEX.length)];
    }

    public static Direction byHorizontalIndex(int horizontalIndexIn) {
        return Direction.BY_HORIZONTAL_INDEX[MathHelper.abs(horizontalIndexIn % Direction.BY_HORIZONTAL_INDEX.length)];
    }

    public static Direction fromAngle(double angle) {
        return Direction.byHorizontalIndex(MathHelper.floor(angle / 90.0D + 0.5D) & 3);
    }

    public static Direction getFacingFromAxisDirection(Axis axisIn, AxisDirection axisDirectionIn) {
        switch (axisIn) {
            case X:
                return axisDirectionIn == AxisDirection.POSITIVE ? EAST : WEST;
            case Y:
                return axisDirectionIn == AxisDirection.POSITIVE ? UP : DOWN;
            case Z:
            default:
                return axisDirectionIn == AxisDirection.POSITIVE ? SOUTH : NORTH;
        }
    }

    public static Direction getFacingFromVector(double x, double y, double z) {
        return Direction.getFacingFromVector((float) x, (float) y, (float) z);
    }

    public static Direction getFacingFromVector(float x, float y, float z) {
        Direction direction = NORTH;
        float f = Float.MIN_VALUE;

        for (Direction direction1 : Direction.VALUES) {
            float f1 = x * direction1.directionVec.getX() + y * direction1.directionVec.getY() + z * direction1.directionVec.getZ();
            if (f1 > f) {
                f = f1;
                direction = direction1;
            }
        }

        return direction;
    }

    public static Direction getFacingFromAxis(AxisDirection axisDirectionIn, Axis axisIn) {
        for (Direction direction : Direction.VALUES) {
            if (direction.getAxisDirection() == axisDirectionIn && direction.getAxis() == axisIn) {
                return direction;
            }
        }

        throw new IllegalArgumentException("No such direction: " + axisDirectionIn + " " + axisIn);
    }

    public Quaternion getRotation() {
        Quaternion quaternion = Vector3f.XP.rotationDegrees(90.0F);
        switch (this) {
            case DOWN:
                return Vector3f.XP.rotationDegrees(180.0F);
            case UP:
                return Quaternion.ONE.copy();
            case NORTH:
                quaternion.multiply(Vector3f.ZP.rotationDegrees(180.0F));
                return quaternion;
            case SOUTH:
                return quaternion;
            case WEST:
                quaternion.multiply(Vector3f.ZP.rotationDegrees(90.0F));
                return quaternion;
            case EAST:
            default:
                quaternion.multiply(Vector3f.ZP.rotationDegrees(-90.0F));
                return quaternion;
        }
    }

    public int getIndex() {
        return this.index;
    }

    public int getHorizontalIndex() {
        return this.horizontalIndex;
    }

    public AxisDirection getAxisDirection() {
        return this.axisDirection;
    }

    public Direction getOpposite() {
        return Direction.byIndex(this.opposite);
    }

    public Direction rotateY() {
        switch (this) {
            case NORTH:
                return EAST;
            case SOUTH:
                return WEST;
            case WEST:
                return NORTH;
            case EAST:
                return SOUTH;
            default:
                throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
        }
    }

    public Direction rotateYCCW() {
        switch (this) {
            case NORTH:
                return WEST;
            case SOUTH:
                return EAST;
            case WEST:
                return SOUTH;
            case EAST:
                return NORTH;
            default:
                throw new IllegalStateException("Unable to get CCW facing of " + this);
        }
    }

    public int getXOffset() {
        return this.directionVec.getX();
    }

    public int getYOffset() {
        return this.directionVec.getY();
    }

    public int getZOffset() {
        return this.directionVec.getZ();
    }

    public Vector3f toVector3f() {
        return new Vector3f(this.getXOffset(), this.getYOffset(), this.getZOffset());
    }

    public String getName2() {
        return this.name;
    }

    public Axis getAxis() {
        return this.axis;
    }

    public float getHorizontalAngle() {
        return (this.horizontalIndex & 3) * 90;
    }

    @Override public String toString() {
        return this.name;
    }

    public String getString() {
        return this.name;
    }

    public Vector3i getDirectionVec() {
        return this.directionVec;
    }

    public boolean hasOrientation(float degrees) {
        float f = degrees * ((float) Math.PI / 180F);
        float f1 = -MathHelper.sin(f);
        float f2 = MathHelper.cos(f);
        return this.directionVec.getX() * f1 + this.directionVec.getZ() * f2 > 0.0F;
    }

    public enum Axis implements Predicate<Direction> {
        X("x") {
            @Override public int getCoordinate(int x, int y, int z) {
                return x;
            }

            @Override public double getCoordinate(double x, double y, double z) {
                return x;
            }
        }, Y("y") {
            @Override public int getCoordinate(int x, int y, int z) {
                return y;
            }

            @Override public double getCoordinate(double x, double y, double z) {
                return y;
            }
        }, Z("z") {
            @Override public int getCoordinate(int x, int y, int z) {
                return z;
            }

            @Override public double getCoordinate(double x, double y, double z) {
                return z;
            }
        };

        private static final Axis[] VALUES = Axis.values();
        private static final Map<String, Axis> NAME_LOOKUP = Arrays
                .stream(Axis.VALUES)
                .collect(Collectors.toMap(Axis::getName2, (p_199785_0_) -> p_199785_0_));
        private final String name;

        Axis(String nameIn) {
            this.name = nameIn;
        }

        public static Axis byName(String name) {
            return Axis.NAME_LOOKUP.get(name.toLowerCase(Locale.ROOT));
        }

        public String getName2() {
            return this.name;
        }

        public boolean isVertical() {
            return this == Y;
        }

        public boolean isHorizontal() {
            return this == X || this == Z;
        }

        @Override public String toString() {
            return this.name;
        }

        @Override public boolean test(Direction p_test_1_) {
            return p_test_1_ != null && p_test_1_.getAxis() == this;
        }

        public Plane getPlane() {
            return switch (this) {
                case X, Z -> Plane.HORIZONTAL;
                case Y -> Plane.VERTICAL;
            };
        }

        public String getString() {
            return this.name;
        }

        public abstract int getCoordinate(int x, int y, int z);

        public abstract double getCoordinate(double x, double y, double z);
    }

    public enum AxisDirection {
        POSITIVE(1, "Towards positive"), NEGATIVE(-1, "Towards negative");

        private final int offset;
        private final String description;

        AxisDirection(int offset, String description) {
            this.offset = offset;
            this.description = description;
        }

        public int getOffset() {
            return this.offset;
        }

        @Override public String toString() {
            return this.description;
        }

        public AxisDirection inverted() {
            return this == POSITIVE ? NEGATIVE : POSITIVE;
        }
    }

    public enum Plane implements Iterable<Direction>, Predicate<Direction> {
        HORIZONTAL(new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}, new Axis[]{Axis.X, Axis.Z}),
        VERTICAL(new Direction[]{Direction.UP, Direction.DOWN}, new Axis[]{Axis.Y});

        private final Direction[] facingValues;
        @SuppressWarnings("unused")
        private final Axis[] axisValues;

        Plane(Direction[] facingValuesIn, Axis[] axisValuesIn) {
            this.facingValues = facingValuesIn;
            this.axisValues = axisValuesIn;
        }

        @Override public boolean test(Direction p_test_1_) {
            return p_test_1_ != null && p_test_1_.getAxis().getPlane() == this;
        }

        @Override public @NotNull Iterator<Direction> iterator() {
            return Arrays.asList(this.facingValues).iterator();
        }

        public Stream<Direction> getDirectionValues() {
            return Arrays.stream(this.facingValues);
        }
    }
}
