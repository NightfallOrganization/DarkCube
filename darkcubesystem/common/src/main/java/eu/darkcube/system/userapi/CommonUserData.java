/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.Language;
import eu.darkcube.system.util.data.*;

import java.math.BigInteger;
import java.util.UUID;

public class CommonUserData implements UserData {
    public static final Key LANGUAGE_KEY = new Key("UserAPI", "language");
    public static final PersistentDataType<Language> LANGUAGE_TYPE = PersistentDataTypes.enumType(Language.class);
    public static final Key CUBES_KEY = new Key("UserAPI", "cubes");
    public static final PersistentDataType<BigInteger> CUBES_TYPE = PersistentDataTypes.BIGINTEGER;
    private final UUID uniqueId;
    private final String name;
    private final MetaDataStorage metadata;
    private final CommonPersistentDataStorage persistentData;

    public CommonUserData(UUID uniqueId, String name, CommonPersistentDataStorage persistentData) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.persistentData = persistentData;
        this.metadata = new BasicMetaDataStorage();
    }

    @Override public @NotNull UUID uniqueId() {
        return uniqueId;
    }

    @Override public @NotNull String name() {
        return name;
    }

    @Override public @NotNull BigInteger cubes() {
        return persistentData().get(CUBES_KEY, CUBES_TYPE, () -> BigInteger.valueOf(1000));
    }

    @Override public void cubes(@NotNull BigInteger cubes) {
        persistentData().set(CUBES_KEY, CUBES_TYPE, cubes);
    }

    @Override public @NotNull Language language() {
        return persistentData().get(LANGUAGE_KEY, LANGUAGE_TYPE, () -> Language.DEFAULT);
    }

    @Override public void language(@NotNull Language language) {
        persistentData().set(LANGUAGE_KEY, LANGUAGE_TYPE, language);
    }

    @Override public @NotNull MetaDataStorage metadata() {
        return metadata;
    }

    @Override public @NotNull CommonPersistentDataStorage persistentData() {
        return persistentData;
    }
}
