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
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.core.runtime.IProgressMonitor;
import org.platformio.eclipse.ide.installer.api.Environment;
import org.platformio.eclipse.ide.installer.api.OS;
import org.platformio.eclipse.ide.installer.api.Python;
import org.platformio.eclipse.ide.installer.api.PythonVersion;
import org.platformio.eclipse.ide.installer.base.BaseEnvironment;
import org.platformio.eclipse.ide.installer.base.Conda;
import org.platformio.eclipse.ide.installer.net.ExecutablesRegistry;
import org.platformio.eclipse.ide.installer.net.RemoteResource;

public final class Installer {

	private final Environment environment = new BaseEnvironment(OS.get());
	private final ExecutablesRegistry registry = new ExecutablesRegistry(environment);
	private final Conda conda = new Conda(environment);

	public void installPlatformIOHome(Python python) {
		environment.executeLasting("pio", //$NON-NLS-1$
				Arrays.asList("home", "--no-open"), //$NON-NLS-1$ //$NON-NLS-2$
				python.location().getParent().toString());
	}

	public void createVirtualEnvironment(IProgressMonitor monitor) {
		if (conda.installed()) {
			conda.createEnvironment();
			return;
		}

		final Optional<Python> python = registry.findPython();
		if (!python.isPresent()) {
			monitor.setTaskName(Messages.Python_installation_message);
			installPython();
			createVirtualEnvironment(monitor);
			return;
		}
		if (!python.get().moduleInstalled("virtualenv")) { //$NON-NLS-1$
			monitor.setTaskName(Messages.Virtualenv_installation_message);
			python.get().installModule("virtualenv"); //$NON-NLS-1$
		}
		monitor.setTaskName(Messages.Setting_workspace_message);
		environment.execute("virtualenv", //$NON-NLS-1$
				Arrays.asList("-p", python.get().location().toString(), environment.env().toString())); //$NON-NLS-1$
		python.get().installModule("platformio"); //$NON-NLS-1$
		installPlatformIOHome(python.get());
	}

	private void installPython() {
		Path pythonDirectory = environment.home().resolve("python27"); //$NON-NLS-1$
		try {
			String coreModule = "core"; //$NON-NLS-1$
			new RemoteResource(source(coreModule)) //
					.download(target(coreModule)) //
					.install(environment, pythonDirectory);
			String exeModule = "exe"; //$NON-NLS-1$
			new RemoteResource(source(exeModule)) //
					.download(target(exeModule)) //
					.install(environment, pythonDirectory);
			String libModule = "lib"; //$NON-NLS-1$
			new RemoteResource(source(libModule)) //
					.download(target(libModule)) //
					.install(environment, pythonDirectory);
			String toolsModule = "tools"; //$NON-NLS-1$
			new RemoteResource(source(toolsModule)) //
					.download(target(toolsModule)) //
					.install(environment, pythonDirectory);
			String pipModule = "pip"; //$NON-NLS-1$
			new RemoteResource(source(pipModule)) //
					.download(target(pipModule)) //
					.install(environment, pythonDirectory);
			new RemoteResource("https://bootstrap.pypa.io/get-pip.py") //$NON-NLS-1$
					.download(pythonDirectory.resolve("get-pip.py")); //$NON-NLS-1$
			environment.execute(pythonDirectory.resolve("python.exe").toString(), //$NON-NLS-1$
					Arrays.asList(pythonDirectory.resolve("get-pip.py").toAbsolutePath().toString()), //$NON-NLS-1$
					pythonDirectory.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Path target(String module) {
		return environment.cache().resolve("downloads").resolve(module + ".msi"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private String source(String module) {
		Supplier<String> version = new PythonVersion(3, 9, 2);
		return "https://www.python.org/ftp/python/" + version.get() //$NON-NLS-1$
				+ environment.os().pythonArch() + module + ".msi"; //$NON-NLS-1$
	}

	public void killPio() {
		environment.killProcess("pio"); //$NON-NLS-1$
	}

}
