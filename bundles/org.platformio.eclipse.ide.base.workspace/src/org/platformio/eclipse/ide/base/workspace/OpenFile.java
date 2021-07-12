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
package org.platformio.eclipse.ide.base.workspace;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.platformio.eclipse.ide.base.api.PlatformIO;

public final class OpenFile implements Supplier<Optional<IFile>> {

	private final String path;
	private final PlatformIO pio;

	public OpenFile(String path, PlatformIO pio) {
		this.path = path;
		this.pio = pio;
	}

	private Optional<IFile> fromWorkspace() {
		return Optional.ofNullable(ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(path)));
	}

	private Optional<IFile> fromImported() {
		Optional<String> found = new FindRoot().apply(path);
		if (found.isPresent()) {
			try {
				new ImportProject(pio).execute(Paths.get(found.get()));
				return fromWorkspace();
			} catch (IOException e) {
				Platform.getLog(getClass()).error(e.getMessage(), e);
			} catch (CoreException e) {
				Platform.getLog(getClass()).log(e.getStatus());
			}
		}
		return Optional.empty();
	}

	@Override
	public Optional<IFile> get() {
		return fromWorkspace().or(this::fromImported);
	}

}
