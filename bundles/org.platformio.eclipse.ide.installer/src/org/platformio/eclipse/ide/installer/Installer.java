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

import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;
import org.platformio.eclipse.ide.installer.api.Environment;
import org.platformio.eclipse.ide.installer.api.OS;
import org.platformio.eclipse.ide.installer.base.BaseEnvironment;
import org.platformio.eclipse.ide.installer.net.ExecutablesRegistry;
import org.platformio.eclipse.ide.installer.python.Conda;
import org.platformio.eclipse.ide.installer.python.Python;
import org.platformio.eclipse.ide.installer.python.PythonDistribution;
import org.platformio.eclipse.ide.installer.python.PythonVersion;

public final class Installer {

	private final Environment environment = new BaseEnvironment(OS.get());
	private final ExecutablesRegistry registry = new ExecutablesRegistry(environment);
	private final Conda conda = new Conda(environment);

	private Optional<Python> python;

	public void createVirtualEnvironment(IProgressMonitor monitor) {

		if (conda.installed()) {
			conda.createEnvironment();
			return;
		}

		python = registry.findPython();
		if (!python.isPresent()) {
			monitor.setTaskName(Messages.Python_installation_message);
			new PythonDistribution(environment, new PythonVersion(3, 9, 2), monitor) //
					.install(environment.home().resolve("python39")); //$NON-NLS-1$
			createVirtualEnvironment(monitor);
			return;
		}
		if (!python.get().moduleInstalled("virtualenv")) { //$NON-NLS-1$
			monitor.setTaskName(Messages.Virtualenv_installation_message);
			python.get().installModule("virtualenv"); //$NON-NLS-1$
		}

		monitor.setTaskName(Messages.Setting_workspace_message);
		python.get().execute("virtualenv"); //$NON-NLS-1$
		monitor.setTaskName(Messages.Installing_Platformio_message);
		python.get().installModule("platformio"); //$NON-NLS-1$
		monitor.setTaskName(Messages.Launching_Platformio_home_message);
		python.get().executeLasting("platformio", "home", "--no-open"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
	}

	public void killPio() {
		if (python.isPresent()) {
			python.get().killProcess("platformio"); //$NON-NLS-1$
		}
	}

}
