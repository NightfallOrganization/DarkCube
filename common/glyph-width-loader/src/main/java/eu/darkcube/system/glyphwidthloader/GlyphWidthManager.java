/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.glyphwidthloader;

import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GlyphWidthManager {

    private final Int2FloatMap bitmapWidths = new Int2FloatOpenHashMap();
    private final Int2FloatMap spaceWidthsPositive = new Int2FloatOpenHashMap();
    private final Int2FloatMap spaceWidthsNegative = new Int2FloatOpenHashMap();

    public static void main(String[] args) throws IOException {
        GlyphWidthManager manager = new GlyphWidthManager();
        manager.loadGlyphData(Paths.get("C:\\Users\\dasba\\Documents\\Development\\DarkCube\\bukkit\\citybuild\\build\\resources\\generated\\glyph-widths.bin".replace('\\', '/')));
        System.out.println(manager.width('i'));
        System.out.println(manager.width('s'));
        System.out.println(manager.width('w'));
        System.out.println(manager.width('.'));
        System.out.println(manager.width('I'));
        System.out.println(manager.width('Q'));
        System.out.println(manager.width("\uDAFF\uDE77".codePointAt(0)));
        System.out.println(manager.width("\uD8FC\uDECB".codePointAt(0)));
        System.out.println(manager.width("\uD8FC\uDECC".codePointAt(0)));
    }

    public void loadGlyphData(Path path) throws IOException {
        loadGlyphData(Files.newInputStream(path));
    }

    public void loadGlyphDataFromClassLoader(String path) throws IOException {
        loadGlyphData(GlyphWidthManager.class.getClassLoader().getResourceAsStream(path));
    }

    public void loadGlyphData(InputStream in) throws IOException {
        BufferedInputStream bin = new BufferedInputStream(in);
        ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        readBuffer(buffer, bin);
        int spaceWidthCount = buffer.getInt(0);
        int bitmapWidthCount = buffer.getInt(4);
        Int2FloatMap spaceWidths = readWidths(spaceWidthCount, buffer, bin);
        Int2FloatMap bitmapWidths = readWidths(bitmapWidthCount, buffer, bin);
        bin.close();
        putAllSpaces(spaceWidths);
        putAllBitmaps(bitmapWidths);

    }

    private void putAllBitmaps(Int2FloatMap widths) {
        this.bitmapWidths.putAll(widths);
    }

    private void putAllSpaces(Int2FloatMap widths) {
        for (Int2FloatMap.Entry entry : widths.int2FloatEntrySet()) {
            if (entry.getFloatValue() >= 0) {
                spaceWidthsPositive.put(entry.getIntKey(), entry.getFloatValue());
            } else {
                spaceWidthsNegative.put(entry.getIntKey(), entry.getFloatValue());
            }
        }
    }

    private Int2FloatMap readWidths(int count, ByteBuffer buffer, BufferedInputStream in) throws IOException {
        Int2FloatMap widths = new Int2FloatOpenHashMap();
        for (int i = 0; i < count; i++) {
            readBuffer(buffer, in);
            int codepoint = buffer.getInt(0);
            float width = buffer.getFloat(4);
            widths.put(codepoint, width);
        }
        return widths;
    }

    private void readBuffer(ByteBuffer buffer, BufferedInputStream in) throws IOException {
        int read = in.read(buffer.array(), 0, 8);
        if (read != 8) throw new IllegalArgumentException("Corrupt glyph data");
    }

    /**
     * @return if this {@link GlyphWidthManager} has the given codepoint registered
     */
    public boolean has(int codepoint) {
        return bitmapWidths.containsKey(codepoint) || spaceWidthsNegative.containsKey(codepoint) || spaceWidthsPositive.containsKey(codepoint);
    }

    /**
     * @return the width of the codepoint, 0 if no codepoint was found (or if the width is 0)
     * @see #has(int)
     */
    public float width(int codepoint) {
        if (bitmapWidths.containsKey(codepoint)) return bitmapWidths.get(codepoint);
        if (spaceWidthsNegative.containsKey(codepoint)) return spaceWidthsNegative.get(codepoint);
        if (spaceWidthsPositive.containsKey(codepoint)) return spaceWidthsPositive.get(codepoint);
        return 0;
    }
}
