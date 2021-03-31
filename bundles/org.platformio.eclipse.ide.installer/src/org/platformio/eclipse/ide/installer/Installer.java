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
package org.platformio.eclipse.ide.installer;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.platformio.eclipse.ide.home.api.Environment;
import org.platformio.eclipse.ide.home.api.PlatformIO;
import org.platformio.eclipse.ide.home.core.BaseEnvironment;
import org.platformio.eclipse.ide.home.core.LocalPlatformIO;
import org.platformio.eclipse.ide.home.python.LocalPython;
import org.platformio.eclipse.ide.home.python.Python;
import org.platformio.eclipse.ide.installer.api.PioCoreDistribution;
import org.platformio.eclipse.ide.installer.api.PythonsRegistry;
import org.platformio.eclipse.ide.installer.piocore.LocalPioCoreDistribution;
import org.platformio.eclipse.ide.installer.python.PythonDistribution;

public final class Installer {

	private final Environment environment = new BaseEnvironment();

	public PlatformIO install(IProgressMonitor monitor, Consumer<String> importProject)
			throws IOException, CoreException {
		Optional<String> executable = registry().findPython();
		if (!executable.isPresent()) {
			monitor.setTaskName(Messages.Python_installation_message);
			new PythonDistribution(environment).install(environment.home().resolve("python39")); //$NON-NLS-1$
			return install(monitor, importProject);
		}
		Python python = new LocalPython(environment, executable.get());

		monitor.setTaskName(Messages.Installing_Platformio_message);
		PioCoreDistribution pio = new LocalPioCoreDistribution(python);
		if (!pio.installed()) {
			pio.install();
		}
		return new LocalPlatformIO(python, registry().executableSuffix(), pio.paths(), importProject);
	}

	private PythonsRegistry registry() throws CoreException {
		Optional<IConfigurationElement> extensionItem = Stream
				.of(Platform.getExtensionRegistry()
						.getExtensionPoint("org.platformio.eclipse.ide.installer.prerequisites").getExtensions()) //$NON-NLS-1$
				.flatMap(extension -> Stream.of(extension.getConfigurationElements())) //
				.filter(element -> "registry".equals(element.getName())) // //$NON-NLS-1$
				.findAny();
		return (PythonsRegistry) extensionItem.get().createExecutableExtension("class"); //$NON-NLS-1$
	}

}
