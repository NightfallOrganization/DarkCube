import com.jcraft.jsch.AgentIdentityRepository
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.PageantConnector
import org.apache.tools.ant.filters.StringInputStream
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import java.io.File
import java.io.FileInputStream

/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
@DisableCachingByDefault
abstract class UploadArtifact : DefaultTask() {
    @TaskAction
    fun run() {
        val username = System.getProperty("uploadUser")
        val hostname = System.getProperty("uploadHost")
        val localFileName = System.getProperty("uploadFile")
        val remoteFileName: String? = System.getProperty("uploadRemoteFile")
        val fingerprint = System.getProperty("uploadRemoteFingerprint")
        val localFile = File(localFileName)
        if (!localFile.exists()) throw RuntimeException("File ${localFile.canonicalPath} does not exist")
        println("Uploading ${localFile.path} to $username@$hostname:$remoteFileName");
        JSch.setConfig("PreferredAuthentications", "publickey");
        val jsch = JSch()

        if (fingerprint != null)
            jsch.setKnownHosts(StringInputStream("$hostname ssh-ed25519 $fingerprint"))
        val session = jsch.getSession(username, hostname)
        val connector = PageantConnector()
        val repository = AgentIdentityRepository(connector)

        session.setIdentityRepository(repository)
        session.connect()
        val sftp = session.openChannel("sftp") as ChannelSftp
        sftp.connect()
        val inputStream = FileInputStream(localFile)
        sftp.put(inputStream, remoteFileName)
        inputStream.close()
        sftp.exit()
    }
}