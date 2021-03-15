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
package org.platformio.eclipse.ide.installer.net;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

import org.platformio.eclipse.ide.installer.api.Environment;
import org.platformio.eclipse.ide.installer.api.OS;
import org.platformio.eclipse.ide.installer.python.LocalPython;
import org.platformio.eclipse.ide.installer.python.Python;

public final class ExecutablesRegistry {

	private final Environment environment;

	public ExecutablesRegistry(Environment environment) {
		this.environment = environment;
	}

	public Optional<Python> findPython(Path... customDirs) {
		List<String> exeNames = executableNames();
		List<Path> locations = new ArrayList<Path>(Arrays.asList(customDirs));
		if (isWindows()) {
			locations.add(environment.home().resolve("python39")); //$NON-NLS-1$
		}
		Collections.list(new StringTokenizer(System.getenv("PATH"), File.pathSeparator)).forEach(item -> { //$NON-NLS-1$
			String itemString = ((String) item).replaceAll("\"", ""); //$NON-NLS-1$//$NON-NLS-2$
			Path itemPath = Paths.get(itemString);
			if (!locations.contains(item)) {
				locations.add(itemPath);
			}
		});
		for (Path location : locations) {
			for (String exeName : exeNames) {
				Path executable = location.resolve(exeName).toAbsolutePath();
				if (Files.exists(executable)) {
					return Optional.of(new LocalPython(environment, executable));
				}
			}
		}
		return Optional.empty();
	}

	private List<String> executableNames() {
		if (isWindows()) {
			return Arrays.asList("python.exe"); //$NON-NLS-1$
		} else {
			return Arrays.asList("python3.9", "python3", "python"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	private boolean isWindows() {
		return OS.Windows32.class.isInstance(environment.os()) || OS.Windows64.class.isInstance(environment.os());
	}

}
