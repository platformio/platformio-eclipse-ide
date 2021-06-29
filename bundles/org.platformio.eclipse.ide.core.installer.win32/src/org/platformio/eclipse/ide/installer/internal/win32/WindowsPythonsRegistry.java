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
package org.platformio.eclipse.ide.installer.internal.win32;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.platformio.eclipse.ide.core.python.PythonsRegistry;

public final class WindowsPythonsRegistry implements PythonsRegistry {

	private final WindowsPythonLocations python;

	public WindowsPythonsRegistry() {
		this.python = new WindowsPythonLocations();
	}

	@Override
	public Optional<String> findPython() {
		List<Path> locations = python.customLocations();
		addLocationsFromPath(locations);
		return locations.stream() //
				.flatMap(location -> python.names().stream().map(location::resolve)) //
				.filter(Files::exists) //
				.map(Path::toString) //
				.findAny();
	}

	private void addLocationsFromPath(List<Path> locations) {
		locations.addAll(Collections.list(new StringTokenizer(System.getenv("PATH"), File.pathSeparator)).stream() //$NON-NLS-1$
				.map(object -> Paths.get((String) object)).collect(Collectors.toList()));
	}

}
