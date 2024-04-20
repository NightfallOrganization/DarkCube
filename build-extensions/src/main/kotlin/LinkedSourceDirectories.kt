/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
import org.gradle.api.file.DirectoryTree
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.work.Incremental
import java.util.function.Consumer

class LinkedSourceDirectories {

    @Optional
    @Nested
    private var head: Node? = null

    fun add(files: FileCollection, tree: DirectoryTree) {
        val node = Node(files, tree)
        if (head == null) head = node
        else last()!!.next = node
    }

    fun forEach(consumer: Consumer<Node>) {
        var f = first()
        while (f != null) {
            consumer.accept(f)
            f = f.next
        }
    }

    fun getHead(): Node? {
        return head
    }

    fun first(): Node? {
        return head
    }

    fun last(): Node? {
        var cur: Node? = head
        var last: Node? = cur
        while (cur != null) {
            last = cur
            cur = cur.next
        }

        return last
    }

    class Node(
        @Incremental @InputFiles @PathSensitive(PathSensitivity.RELATIVE) val files: FileCollection, @Internal val tree: DirectoryTree, @Nested @Optional var next: Node? = null
    )

}