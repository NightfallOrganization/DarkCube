/*
 * Copyright (c) 2018-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
/**
 * A library for examining objects and producing the output.
 *
 * <p>Objects which which to expose properties using examination should implement
 * {@link eu.darkcube.system.libs.net.kyori.examination.Examinable}, and provide any {@code toString} elements
 * as examinable properties. These objects should make sure to overide {@link java.lang.Object#toString()}
 * in order to call to the examination methods.</p>
 *
 *
 * <p>This API module does not itself contain any examiners that generate output. Those are provided by separate submodules.</p>
 */
package eu.darkcube.system.libs.net.kyori.examination;
