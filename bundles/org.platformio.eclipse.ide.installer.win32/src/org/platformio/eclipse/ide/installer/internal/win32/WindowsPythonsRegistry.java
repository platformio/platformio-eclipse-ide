/*******************************************************************************
 * Copyright (c) 2021 ArSysOp and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/.
 *
 * SPDX-License-Identifier: EPL-2.0
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

import org.platformio.eclipse.ide.installer.api.PythonsRegistry;
import org.platformio.eclipse.ide.installer.python.PythonLocations;

public final class WindowsPythonsRegistry implements PythonsRegistry {

	private final PythonLocations python;

	public WindowsPythonsRegistry() {
		this.python = new WindowsPythonLocations();
	}

	@Override
	public Optional<String> findPython() {
		List<Path> locations = python.customLocations();
		addLocationsFromPath(locations);
		for (Path location : locations) {
			for (String exeName : python.names()) {
				Path executable = location.resolve(exeName).toAbsolutePath();
				if (Files.exists(executable)) {
					return Optional.of(executable.toString());
				}
			}
		}
		return Optional.empty();
	}

	private void addLocationsFromPath(List<Path> locations) {
		locations.addAll(Collections.list(new StringTokenizer(System.getenv("PATH"), File.pathSeparator)).stream() //$NON-NLS-1$
				.map(object -> Paths.get((String) object)).collect(Collectors.toList()));
	}

	@Override
	public String executableSuffix() {
		return ".exe"; //$NON-NLS-1$
	}

}
