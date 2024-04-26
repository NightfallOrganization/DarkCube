/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.glyphwidthloader;

import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Generator {
    private static Path tempDirectory;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        if (args.length < 2) {
            System.err.println("Arguments: <version> <outputFile> [<resourcePacks>...]");
            return;
        }
        tempDirectory = Files.createTempDirectory("glyph-width-loader");
        String wantedVersion = args[0];
        VersionManifestLoader loader = new VersionManifestLoader();
        loader.load().get();
        VersionManifest unresolved = loader.manifests().get(wantedVersion);
        if (unresolved == null) {
            System.err.println("Unknown version: " + wantedVersion);
            return;
        }
        VersionManifest.Resolved manifest = unresolved.resolve().get();
        if (manifest == null) {
            System.err.println("Unable to resolve manifest");
            return;
        }
        URL url = new URL(manifest.clientUrl());
        System.out.println("Downloading client from " + url);
        URLConnection con = url.openConnection();
        InputStream in = con.getInputStream();

        Path unzippedDirectory = unzip(in);
        System.out.println("Unzipped into " + new URL("file", "", "///" + unzippedDirectory
                .toAbsolutePath()
                .toString()
                .replace('\\', '/')));

        Path assets = unzippedDirectory.resolve("assets");
        FontData fontData = FontData.load(assets);
        String[] resourcePacksString = Arrays.copyOfRange(args, 2, args.length);
        System.out.println("Custom ResourcePacks: " + Arrays.toString(resourcePacksString));

        for (String resourcePackString : resourcePacksString) {
            URL resourcePackUrl;
            try {
                resourcePackUrl = new URL(resourcePackString);
            } catch (Throwable ignored) {
                resourcePackUrl = new URL("file", "", "///" + resourcePackString);
            }
            System.out.println("Unzipping ResourcePack: " + resourcePackUrl);
            InputStream resourcePackIn = resourcePackUrl.openConnection().getInputStream();
            Path unzipped = unzip(resourcePackIn);
            System.out.println("Unzipped ResourcePack: " + unzipped.toAbsolutePath());
            FontData data = FontData.load(unzipped.resolve("assets"));
            fontData.providers().addAll(data.providers());
            copyFolder(unzipped, unzippedDirectory, StandardCopyOption.REPLACE_EXISTING);
        }

        String outputFile = args[1];
        Path outputPath = Paths.get(outputFile);
        Files.createDirectories(outputPath.getParent());
        BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING));

        Int2FloatMap spaceWidths = new Int2FloatOpenHashMap();
        Int2FloatMap bitmapWidths = new Int2FloatOpenHashMap();

        for (FontData.Provider provider : fontData.providers()) {
            if (provider.type().equals("space")) {
                FontData.Provider.space(provider, bitmapWidths, spaceWidths);
            } else if (provider.type().equals("bitmap")) {
                FontData.Provider.bitmap(assets, provider, spaceWidths, bitmapWidths);
            } else {
                System.err.println("Unsupported provider: " + provider.type());
            }
        }

        System.out.println("Saving glyphs to " + outputPath.toAbsolutePath());

        ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(0, spaceWidths.size());
        buffer.putInt(4, bitmapWidths.size());
        out.write(buffer.array());
        write(buffer, out, spaceWidths);
        write(buffer, out, bitmapWidths);
        out.close();

//        try (Stream<Path> walk = Files.walk(tempDirectory)) {
//            walk.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
//        }
    }

    private static void write(ByteBuffer buffer, OutputStream out, Int2FloatMap widths) throws IOException {
        IntIterator keyIterator = widths.keySet().iterator();
        while (keyIterator.hasNext()) {
            int codepoint = keyIterator.nextInt();
            float width = widths.get(codepoint);
            buffer.putInt(0, codepoint).putFloat(4, width);
            out.write(buffer.array());
        }
    }

    private static Path unzip(InputStream in) throws IOException {
        BufferedInputStream bin = new BufferedInputStream(in);
        Path unzippedDirectory = Files.createTempDirectory(tempDirectory, "data");

        ZipInputStream zipIn = new ZipInputStream(bin, StandardCharsets.UTF_8);
        ZipEntry zipEntry;
        while ((zipEntry = zipIn.getNextEntry()) != null) {
            if (!zipEntry.getName().startsWith("assets")) continue;
            Path path = unzippedDirectory.resolve(zipEntry.getName());
            Path parent = path.getParent();
            if (!zipEntry.isDirectory()) {
                if (!Files.exists(parent)) Files.createDirectories(parent);
                Files.copy(zipIn, path);
            } else {
                Files.createDirectories(path);
            }
        }
        return unzippedDirectory;
    }

    private static void copyFolder(Path source, Path target, CopyOption... options) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<>() {

            @Override public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Files.createDirectories(target.resolve(source.relativize(dir).toString()));
                return FileVisitResult.CONTINUE;
            }

            @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, target.resolve(source.relativize(file).toString()), options);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
