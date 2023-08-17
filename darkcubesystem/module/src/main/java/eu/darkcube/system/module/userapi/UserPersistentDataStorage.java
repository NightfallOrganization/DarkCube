/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module.userapi;

import eu.cloudnetservice.driver.document.Document;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserPersistentDataStorage {
    final Document.Mutable data = Document.newJsonDocument();
    final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(false);
}
