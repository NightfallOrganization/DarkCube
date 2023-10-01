package eu.darkcube.system.minestom.server.util;

public final class BinaryOperations {
    private BinaryOperations() {
        throw new UnsupportedOperationException();
    }

    public static long combine(int x, int y) {
        return ((long) x << 32) | y & 0xFFFFFFFFL;
    }

    public static int combine(short x, short y) {
        return (x << 16) | (y & 0xFFFF);
    }

    public static int x(long combined) {
        return (int) (combined >> 32);
    }

    public static short x(int combined) {
        return (short) (combined >> 16);
    }

    public static int y(long combined) {
        return (int) combined;
    }

    public static short y(int combined) {
        return (short) combined;
    }
}
