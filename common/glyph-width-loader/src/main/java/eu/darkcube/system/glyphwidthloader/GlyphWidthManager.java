/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.glyphwidthloader;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused") public final class GlyphWidthManager {

    private final ThreadLocal<IntList> threadLocalList = ThreadLocal.withInitial(IntArrayList::new);
    private final IntList spaceCodepoints = new IntArrayList();
    private final FloatList spaceWidthsList = new FloatArrayList();
    private final Int2FloatMap bitmapWidths = new Int2FloatOpenHashMap();
    private final Int2FloatMap spaceWidths = new Int2FloatOpenHashMap();

    /**
     * Returns the nearest codepoint for the given width.
     */
    public int nearestCodepoint(float width) {
        return spaceCodepoints.getInt(nearestCodepointIndex(width));
    }

    /**
     * Same as {@link #spacesForWidth(float)} but returns codepoints.
     *
     * @return the codepoints that best correspond the width
     * @see #spacesForWidth(float)
     * @see #nearestCodepoint(float)
     */
    public int[] spaceCodepointsForWidth(final float width) {
        IntList codepoints = threadLocalList.get();
        float anySignRemaining = width;
        do {
            int index = nearestCodepointIndex(anySignRemaining);
            float anySignDist = anySignRemaining - spaceWidthsList.getFloat(index);
            if (Math.abs(anySignDist) < Math.abs(anySignRemaining)) {
                // Progress!!!
                anySignRemaining = anySignDist;
                codepoints.add(spaceCodepoints.getInt(index));
            } else {
                break;
            }
        } while (true);
        int[] array = codepoints.toIntArray();
        codepoints.clear();
        return array;
    }

    /**
     * Generates a string of spaces that correspond with the given width best. <br>
     * This means this method makes best efforts to generate a string representing the width, but the result is likely not perfect.
     * The accuracy of the text depends on the space-widths this {@link GlyphWidthManager} has to work with.
     *
     * @return a text of spaces that best correspond the width
     * @see #spaceCodepointsForWidth(float)
     */
    public String spacesForWidth(final float width) {
        int[] codepoints = spaceCodepointsForWidth(width);
        return new String(codepoints, 0, codepoints.length);
    }

    /**
     * Calculates the width of the text
     *
     * @return the width of the text
     * @see #width(int)
     */
    public float width(String text) {
        int[] codepoints = text.codePoints().toArray();
        float width = 0;
        for (int codepoint : codepoints) width += width(codepoint);
        return width;
    }

    /**
     * @return if this {@link GlyphWidthManager} has the given codepoint registered
     */
    public boolean has(int codepoint) {
        return bitmapWidths.containsKey(codepoint) || spaceWidths.containsKey(codepoint);
    }

    /**
     * @return the width of the codepoint, 0 if no codepoint was found (or if the width is 0)
     * @see #has(int)
     */
    public float width(int codepoint) {
        if (spaceWidths.containsKey(codepoint)) return spaceWidths.get(codepoint);
        if (bitmapWidths.containsKey(codepoint)) return bitmapWidths.get(codepoint);
        return 0;
    }

    /**
     * Loads data from the given {@link Path}
     */
    public void loadGlyphData(Path path) throws IOException {
        loadGlyphData(Files.newInputStream(path));
    }

    /**
     * Loads data from the given path on the current {@link ClassLoader}.<br>
     *
     * @see #loadGlyphDataFromClassLoader(ClassLoader, String)
     */
    public void loadGlyphDataFromClassLoader(String path) throws IOException {
        loadGlyphDataFromClassLoader(Thread.currentThread().getContextClassLoader(), path);
    }

    /**
     * Loads data from the given path on the current {@link ClassLoader}.<br>
     * Uses {@link ClassLoader#getResourceAsStream(String)}
     *
     * @see #loadGlyphData(InputStream)
     */
    public void loadGlyphDataFromClassLoader(ClassLoader classLoader, String path) throws IOException {
        loadGlyphData(classLoader.getResourceAsStream(path));
    }

    /**
     * Loads data from the given {@link InputStream}
     */
    public void loadGlyphData(InputStream in) throws IOException {
        spaceWidthsList.clear();
        spaceWidths.clear();
        bitmapWidths.clear();
        spaceCodepoints.clear();
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
            spaceWidths.put(entry.getIntKey(), entry.getFloatValue());
            spaceCodepoints.add(entry.getIntKey());
        }
        // Sort the spaceCodepoints by the width size corresponding to it
        Arrays.mergeSort(0, spaceCodepoints.size(), (k1, k2) -> Float.compare(spaceWidths.get(spaceCodepoints.getInt(k1)), spaceWidths.get(spaceCodepoints.getInt(k2))), (a, b) -> {
            int tmp = spaceCodepoints.getInt(a);
            spaceCodepoints.set(a, spaceCodepoints.getInt(b));
            spaceCodepoints.set(b, tmp);
        });
        for (int i = 0; i < spaceCodepoints.size(); i++) {
            spaceWidthsList.add(spaceWidths.get(spaceCodepoints.getInt(i)));
        }
    }

    private int nearestCodepointIndex(float width) {
        float midVal;
        int from = 0;
        int to = spaceWidthsList.size() - 1;
        if (to == -1) throw new IllegalStateException("No Space Glyph Data Loaded");
        while (from <= to) {
            final int mid = (from + to) >>> 1;
            midVal = spaceWidthsList.getFloat(mid);
            final int cmp = Float.compare(midVal, width);
            if (cmp < 0) from = mid + 1;
            else if (cmp > 0) to = mid - 1;
            else return mid;
        }
        if (from == 0) return from;
        if (from == spaceWidthsList.size()) return from - 1;
        float prev = spaceWidthsList.getFloat(from - 1);
        float d1 = Math.abs(prev - width);
        float d2 = Math.abs(spaceWidthsList.getFloat(from) - width);
        if (d1 < d2) return from - 1;
        return from;
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

}
