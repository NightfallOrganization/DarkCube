/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.module;

import com.google.common.base.Preconditions;
import de.dytanic.cloudnet.common.io.FileUtils;
import de.dytanic.cloudnet.driver.service.ServiceTemplate;
import de.dytanic.cloudnet.driver.template.FileInfo;
import de.dytanic.cloudnet.template.ClusterSynchronizedTemplateStorage;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

@SuppressWarnings({"UnstableApiUsage", "resource"})
public final class WoolBattleTemplateStorage extends ClusterSynchronizedTemplateStorage {

	public static final String WOOLBATTLE_TEMPLATE_STORAGE = "woolbattle";
	private final Path storageDirectory;

	public WoolBattleTemplateStorage(Path storageDirectory) {
		this.storageDirectory = storageDirectory;
		FileUtils.createDirectoryReported(storageDirectory);
	}

	@Override
	public boolean deployWithoutSynchronization(@NotNull Path directory,
			@NotNull ServiceTemplate target, @Nullable Predicate<Path> fileFilter) {
		Preconditions.checkNotNull(directory);
		Preconditions.checkNotNull(target);

		if (Files.isDirectory(directory)) {
			FileUtils.copyFilesToDirectory(directory,
					this.storageDirectory.resolve(target.getTemplatePath()),
					fileFilter == null ? null : fileFilter::test);
			return true;
		}

		return false;
	}

	@Override
	public boolean deployWithoutSynchronization(@NotNull InputStream inputStream,
			@NotNull ServiceTemplate target) {
		Preconditions.checkNotNull(inputStream);
		Preconditions.checkNotNull(target);

		try {
			FileUtils.extract0(new ZipInputStream(inputStream),
					this.storageDirectory.resolve(target.getTemplatePath()));
			return true;
		} catch (IOException exception) {
			exception.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean deleteWithoutSynchronization(@NotNull ServiceTemplate template) {
		Preconditions.checkNotNull(template);

		Path target = this.storageDirectory.resolve(template.getTemplatePath());
		if (Files.notExists(target)) {
			return false;
		} else {
			FileUtils.delete(target);
			return true;
		}
	}

	@Override
	public boolean createWithoutSynchronization(@NotNull ServiceTemplate template) {
		Path directory = this.storageDirectory.resolve(template.getTemplatePath());
		if (Files.notExists(directory)) {
			FileUtils.createDirectoryReported(directory);
			return true;
		}
		return false;
	}

	@Nullable
	@Override
	public OutputStream appendOutputStreamWithoutSynchronization(@NotNull ServiceTemplate template,
			@NotNull String path) throws IOException {
		Path file = this.storageDirectory.resolve(template.getTemplatePath()).resolve(path);
		if (Files.notExists(file)) {
			Files.createDirectories(file.getParent());
			Files.createFile(file);
		}
		return Files.newOutputStream(file, StandardOpenOption.APPEND);
	}

	@Nullable
	@Override
	public OutputStream newOutputStreamWithoutSynchronization(@NotNull ServiceTemplate template,
			@NotNull String path) throws IOException {
		Path file = this.storageDirectory.resolve(template.getTemplatePath()).resolve(path);
		if (Files.exists(file)) {
			Files.delete(file);
		} else {
			Files.createDirectories(file.getParent());
		}
		return Files.newOutputStream(file, StandardOpenOption.CREATE);
	}

	@Override
	public boolean createFileWithoutSynchronization(@NotNull ServiceTemplate template,
			@NotNull String path) throws IOException {
		Path file = this.storageDirectory.resolve(template.getTemplatePath()).resolve(path);
		if (Files.exists(file)) {
			return false;
		} else {
			Files.createDirectories(file.getParent());
			Files.createFile(file);
			return true;
		}
	}

	@Override
	public boolean createDirectoryWithoutSynchronization(@NotNull ServiceTemplate template,
			@NotNull String path) throws IOException {
		Path dir = this.storageDirectory.resolve(template.getTemplatePath()).resolve(path);
		if (Files.exists(dir)) {
			return false;
		} else {
			Files.createDirectories(dir);
			return true;
		}
	}

	@Override
	public boolean deleteFileWithoutSynchronization(@NotNull ServiceTemplate template,
			@NotNull String path) {
		Path file = this.storageDirectory.resolve(template.getTemplatePath()).resolve(path);
		if (Files.notExists(file)) {
			return false;
		} else {
			FileUtils.delete(file);
			return true;
		}
	}

	@Override
	public boolean copy(@NotNull ServiceTemplate template, @NotNull Path directory) {
		Preconditions.checkNotNull(template);
		Preconditions.checkNotNull(directory);

		FileUtils.copyFilesToDirectory(this.storageDirectory.resolve(template.getTemplatePath()),
				directory);
		return true;
	}

	@Override
	@Nullable
	public InputStream zipTemplate(@NotNull ServiceTemplate template) throws IOException {
		if (!this.has(template)) {
			return null;
		}

		Path directory = this.storageDirectory.resolve(template.getTemplatePath());
		Path tempFile = FileUtils.createTempFile();

		Path file = FileUtils.zipToFile(directory, tempFile);
		if (file == null) {
			return null;
		}

		return Files.newInputStream(file, StandardOpenOption.DELETE_ON_CLOSE,
				LinkOption.NOFOLLOW_LINKS);
	}

	@Override
	public boolean has(@NotNull ServiceTemplate template) {
		Preconditions.checkNotNull(template);
		return Files.exists(this.storageDirectory.resolve(template.getTemplatePath()));
	}

	@Override
	public boolean hasFile(@NotNull ServiceTemplate template, @NotNull String path) {
		Path file = this.storageDirectory.resolve(template.getTemplatePath()).resolve(path);
		return Files.exists(file);
	}

	@Override
	public @Nullable InputStream newInputStream(@NotNull ServiceTemplate template,
			@NotNull String path) throws IOException {
		Path file = this.storageDirectory.resolve(template.getTemplatePath()).resolve(path);
		return Files.exists(file) && !Files.isDirectory(file) ? Files.newInputStream(file) : null;
	}

	@Override
	public @Nullable FileInfo getFileInfo(@NotNull ServiceTemplate template, @NotNull String path)
	throws IOException {
		Path file = this.storageDirectory.resolve(template.getTemplatePath()).resolve(path);
		return Files.exists(file) ? FileInfo.of(file, Paths.get(path)) : null;
	}

	@Override
	public FileInfo[] listFiles(@NotNull ServiceTemplate template, @NotNull String dir,
			boolean deep) throws IOException {
		List<FileInfo> files = new ArrayList<>();
		Path directory = this.storageDirectory.resolve(template.getTemplatePath()).resolve(dir);
		if (Files.notExists(directory) || !Files.isDirectory(directory)) {
			return null;
		}

		if (deep) {
			Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
				throws IOException {
					files.add(FileInfo.of(dir, directory.relativize(dir), attrs));
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
				throws IOException {
					files.add(FileInfo.of(file, directory.relativize(file), attrs));
					return FileVisitResult.CONTINUE;
				}
			});
		} else {
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
				for (Path path : stream) {
					files.add(FileInfo.of(path, directory.relativize(path)));
				}
			}
		}
		return files.toArray(new FileInfo[0]);
	}

	@Override
	public @NotNull Collection<ServiceTemplate> getTemplates() {
		try {
			return Files.list(this.storageDirectory).filter(Files::isDirectory).flatMap(path -> {
				try {
					return Files.list(path);
				} catch (IOException exception) {
					return null;
				}
			}).filter(Objects::nonNull).map(path -> {
				Path relativize = this.storageDirectory.relativize(path);
				return new ServiceTemplate(relativize.getName(0).toString(),
						relativize.getName(1).toString(), WOOLBATTLE_TEMPLATE_STORAGE);
			}).collect(Collectors.toList());
		} catch (IOException exception) {
			exception.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	public void close() {
	}

	public Path getStorageDirectory() {
		return this.storageDirectory;
	}

	@Override
	public String getName() {
		return WOOLBATTLE_TEMPLATE_STORAGE;
	}
}
