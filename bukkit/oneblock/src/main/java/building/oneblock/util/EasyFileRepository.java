/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package building.oneblock.util;

import com.google.gson.Gson;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class EasyFileRepository<T> {

    private final String path;
    private final Class<T> clazz;
    private final Gson gson = new Gson();

    public EasyFileRepository(String basePath, String folderName, Class<T> clazz) {
        this.clazz = clazz;
        this.path = basePath + File.separator + folderName;

        // Erstellt den Ordner, wenn er nicht existiert
        File directory = new File(this.path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public T loadFromFile(String fileName) {
        File file = new File(path, fileName);
        if (!file.exists()) {
            System.out.println("no File found with file name: " + fileName);
            return null;
        }
        try {
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            return gson.fromJson(content, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveToFile(String fileName, T content) {
        File file = new File(path, fileName);
        try {
            Files.writeString(file.toPath(), gson.toJson(content), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
