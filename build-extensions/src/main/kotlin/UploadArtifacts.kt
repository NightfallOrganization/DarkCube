/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

import com.jcraft.jsch.AgentIdentityRepository
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.PageantConnector
import org.apache.tools.ant.filters.StringInputStream
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.FileType
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.kotlin.dsl.property
import org.gradle.work.ChangeType
import org.gradle.work.DisableCachingByDefault
import org.gradle.work.FileChange
import org.gradle.work.InputChanges
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject

@DisableCachingByDefault
abstract class UploadArtifacts : DefaultTask() {

    @get:Inject
    internal abstract val objects: ObjectFactory

    @get:Input
    @get:Option(option = "user", description = "The remote user to log in with")
    val user: Property<String> = objects.property()

    @get:Input
    @get:Option(option = "host", description = "The remote server")
    val host: Property<String> = objects.property()

    @get:SkipWhenEmpty
    @get:InputFiles
    val files: ConfigurableFileTree = objects.fileTree()

    @get:Input
    @get:Option(option = "remoteDir", description = "The remote file to upload to")
    val remoteDirectory: Property<String> = objects.property()

    @get:Input
    @get:Option(option = "fingerprint", description = "The ssh-ed25519 fingerprint of the remote machine")
    val fingerprint: Property<String> = objects.property()

    @TaskAction
    fun run(changes: InputChanges) {
        val start = System.currentTimeMillis()

        var remoteDir = this.remoteDirectory.get()
        if (!remoteDir.endsWith("/")) remoteDir += "/"

        val sftp = connect()

        changes.getFileChanges(files).forEach {
            handleChange(sftp, it)
        }
        sftp.exit()
        val time = System.currentTimeMillis() - start
        println("Upload took ${time}ms")
    }

    private fun handleChange(sftp: ChannelSftp, change: FileChange) {
        when (change.changeType) {
            ChangeType.ADDED, ChangeType.MODIFIED -> {
                uploadFile(sftp, change.file, change.fileType == FileType.DIRECTORY)
            }

            ChangeType.REMOVED -> {
                deleteFile(sftp, change.file, change.fileType == FileType.DIRECTORY)
            }

            else -> {}
        }
    }

    private fun uploadFile(sftp: ChannelSftp, file: File, directory: Boolean) {
        val converted = convert(file)
        if (directory) {
            println("Create remote $converted")
            try {
                sftp.mkdir(converted)
            } catch (_: Exception) {
            }
        } else {
            println("Upload file $file to $converted")
            val input = FileInputStream(file)
            sftp.put(input, converted)
            input.close()
        }
    }

    private fun deleteFile(sftp: ChannelSftp, file: File, directory: Boolean) {
        try {
            if (directory) {
                sftp.rmdir(convert(file))
            } else {
                sftp.rm(convert(file))
            }
        } catch (_: Exception) {
        }
    }

    private fun convert(file: File): String {
        return (remoteDirectory.get() + file.relativeTo(files.dir)).replace("\\", "/")
    }

    private fun connect(): ChannelSftp {
        val user = this.user.get()
        val host = this.host.get()
        val fingerprint = this.fingerprint.get()
        JSch.setConfig("PreferredAuthentications", "publickey")
        val jsch = JSch()
//        jsch.instanceLogger = object : Logger {
//            override fun isEnabled(level: Int): Boolean {
//                return true
//            }
//
//            override fun log(level: Int, message: String?) {
//                println(message)
//            }
//        }

        jsch.setKnownHosts(StringInputStream("$host ssh-ed25519 $fingerprint"))
        val session = jsch.getSession(user, host)
        val connector = PageantConnector()
        val repository = AgentIdentityRepository(connector)

        session.setIdentityRepository(repository)
        session.connect()
        val sftp = session.openChannel("sftp") as ChannelSftp
        sftp.connect()
        return sftp
    }

    init {
        outputs.upToDateWhen { true }
    }
}