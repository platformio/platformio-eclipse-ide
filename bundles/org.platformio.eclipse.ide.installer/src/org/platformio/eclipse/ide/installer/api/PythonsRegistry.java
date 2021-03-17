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
package org.platformio.eclipse.ide.installer.api;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.platformio.eclipse.ide.installer.python.LocalPython;
import org.platformio.eclipse.ide.installer.python.Python;
import org.platformio.eclipse.ide.installer.python.PythonLocations;

public final class PythonsRegistry {

	private final Environment environment;

	public PythonsRegistry(Environment environment) {
		this.environment = environment;
	}

	public Optional<Python> findPython() {
		for (PythonLocations locations : pythons()) {
			Optional<Python> found = findPython(locations);
			if (found.isPresent()) {
				return found;
			}
		}
		return Optional.empty();
	}

	private Optional<Python> findPython(PythonLocations python) {
		List<Path> locations = python.customLocations();
		addLocationsFromPath(locations);
		for (Path location : locations) {
			for (String exeName : python.names()) {
				Path executable = location.resolve(exeName).toAbsolutePath();
				if (Files.exists(executable)) {
					return Optional.of(new LocalPython(environment, executable));
				}
			}
		}
		return Optional.empty();
	}

	private void addLocationsFromPath(List<Path> locations) {
		Collections.list(new StringTokenizer(System.getenv("PATH"), File.pathSeparator)).forEach(item -> { //$NON-NLS-1$
			String itemString = ((String) item).replaceAll("\"", ""); //$NON-NLS-1$//$NON-NLS-2$
			Path itemPath = Paths.get(itemString);
			if (!locations.contains(item)) {
				locations.add(itemPath);
			}
		});
	}

	private List<PythonLocations> pythons() {
		List<PythonLocations> locations = new LinkedList<>();
		IExtension[] extensions = Platform.getExtensionRegistry()
				.getExtensionPoint("org.platformio.eclipse.ide.installer.prerequisites").getExtensions(); //$NON-NLS-1$
		for (IExtension extension : extensions) {
			IConfigurationElement[] prereqs = extension.getConfigurationElements();
			for (IConfigurationElement ce : prereqs) {
				String type = ce.getAttribute("type"); //$NON-NLS-1$
				if ("python".equals(type)) { //$NON-NLS-1$
					Object executable;
					try {
						executable = ce.createExecutableExtension("class"); //$NON-NLS-1$
						if (executable instanceof PythonLocations) {
							locations.add((PythonLocations) executable);
						}
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return locations;
	}

}
