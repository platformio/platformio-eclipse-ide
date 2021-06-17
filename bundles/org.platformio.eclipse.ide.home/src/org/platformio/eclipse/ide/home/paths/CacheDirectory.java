/*******************************************************************************
 * Copyright (c) 2021 PlatformIO and ArSysOp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     Nikifor Fedorov (ArSysOp) - initial API and implementation
 *******************************************************************************/
package org.platformio.eclipse.ide.home.paths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

import org.eclipse.core.runtime.Platform;

public final class CacheDirectory implements Supplier<Path> {

	@Override
	public Path get() {
		Path dir = new HomeDirectory().get().resolve(".cache"); //$NON-NLS-1$
		if (!Files.isDirectory(dir)) {
			try {
				if (Files.isRegularFile(dir)) {
					Files.delete(dir);
				}
				Files.createDirectories(dir);
				Files.createDirectory(dir.resolve("downloads")); //$NON-NLS-1$
			} catch (IOException e) {
				Platform.getLog(getClass()).error(e.getMessage(), e);
			}
		}
		return dir;
	}

}
