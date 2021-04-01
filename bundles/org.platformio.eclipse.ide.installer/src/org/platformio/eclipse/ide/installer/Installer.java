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
package org.platformio.eclipse.ide.installer;

import java.io.IOException;
import java.util.Optional;
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

	public PlatformIO install(IProgressMonitor monitor) throws IOException, CoreException {
		Optional<String> executable = registry().findPython();
		if (!executable.isPresent()) {
			monitor.setTaskName(Messages.Python_installation_message);
			new PythonDistribution(environment).install(environment.home().resolve("python39")); //$NON-NLS-1$
			return install(monitor);
		}
		Python python = new LocalPython(environment, executable.get());

		monitor.setTaskName(Messages.Installing_Platformio_message);
		PioCoreDistribution pio = new LocalPioCoreDistribution(python);
		if (!pio.installed()) {
			pio.install();
		}
		return new LocalPlatformIO(python, registry().executableSuffix(), pio.paths());
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
