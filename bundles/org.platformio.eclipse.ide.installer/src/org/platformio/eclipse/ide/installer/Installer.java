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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;
import org.platformio.eclipse.ide.installer.api.Architecture;
import org.platformio.eclipse.ide.installer.api.Environment;
import org.platformio.eclipse.ide.installer.api.OS;
import org.platformio.eclipse.ide.installer.api.Python;
import org.platformio.eclipse.ide.installer.base.BaseEnvironment;
import org.platformio.eclipse.ide.installer.base.Conda;
import org.platformio.eclipse.ide.installer.net.ExecutablesRegistry;
import org.platformio.eclipse.ide.installer.net.RemoteResource;

public final class Installer {

	private final Environment environment = new BaseEnvironment(OS.get());
	private final ExecutablesRegistry registry = new ExecutablesRegistry(environment);
	private final Conda conda = new Conda(environment);

	public void installPlatformIOHome() {
		environment.execute("pio", Arrays.asList("home", "--host", "__do_not_start__")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	public void createVirtualEnvironment(IProgressMonitor monitor) {
		if (conda.installed()) {
			conda.createEnvironment();
			return;
		}
		System.out.println(environment.home().toString());

		final Optional<Python> python = registry.findPython();
		if (!python.isPresent()) {
			monitor.setTaskName(Messages.Python_installation_message);
			installPython();
		}
		if (!python.get().moduleInstalled("virtualenv")) { //$NON-NLS-1$
			monitor.setTaskName(Messages.Virtualenv_installation_message);
			python.get().installModule("virtualenv"); //$NON-NLS-1$
		}
		monitor.setTaskName(Messages.Setting_workspace_message);
		environment.execute("virtualenv", //$NON-NLS-1$
				Arrays.asList("-p", python.get().location().toString(), environment.env().toString())); //$NON-NLS-1$
		python.get().installModule("platformio"); //$NON-NLS-1$
	}

	private void installPython() {
		String resourceUrl = source(environment.os().architecture());
		Path packageDirectory = target(resourceUrl);
		Path pythonDirectory = environment.home().resolve("python27"); //$NON-NLS-1$
		try {
			new RemoteResource(resourceUrl) //
					.download(packageDirectory) //
					.install(environment, pythonDirectory);
			appendToPath(pythonDirectory);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void appendToPath(Path directory) {
		environment.execute("cmd", Arrays.asList("set", "PATH=%PATH%;" + directory.toString())); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
	}

	private Path target(String source) {
		try {
			return environment.cache().resolve(new URL(source).getFile());
		} catch (MalformedURLException e) {
			return environment.cache().resolve("python27"); //$NON-NLS-1$
		}
	}

	private String source(Architecture architecture) {
		String source = "https://www.python.org/ftp/python/" + Python.VERSION + "/python-" + Python.VERSION //$NON-NLS-1$ //$NON-NLS-2$
				+ architecture.pythonArch() + ".msi"; //$NON-NLS-1$
		return source;
	}

}
