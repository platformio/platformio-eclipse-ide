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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.platformio.eclipse.ide.installer.api.Environment;
import org.platformio.eclipse.ide.installer.api.PythonsRegistry;
import org.platformio.eclipse.ide.installer.base.BaseEnvironment;
import org.platformio.eclipse.ide.installer.piocore.LocalPioCoreDistribution;
import org.platformio.eclipse.ide.installer.piocore.PioCoreDistribution;
import org.platformio.eclipse.ide.installer.python.Python;
import org.platformio.eclipse.ide.installer.python.PythonDistribution;
import org.platformio.eclipse.ide.installer.python.PythonVersion;

public final class Installer {

	private final Environment environment = new BaseEnvironment();
	private final PythonsRegistry registry;

	private Optional<Python> python;

	public Installer() {
		this.registry = new PythonsRegistry(environment);
	}

	public Status install(IProgressMonitor monitor) {
		python = registry.findPython();
		if (!python.isPresent()) {
			monitor.setTaskName(Messages.Python_installation_message);
			new PythonDistribution(environment, new PythonVersion(3, 9, 2))
					.install(environment.home().resolve("python39")); //$NON-NLS-1$
			return install(monitor);
		}

		try {
			monitor.setTaskName(Messages.Installing_Platformio_message);
			PioCoreDistribution pio = new LocalPioCoreDistribution(python.get());
			if (!pio.installed()) {
				pio.install();
			}
			monitor.setTaskName(Messages.Launching_Platformio_home_message);
			pio.home();
			return new Status(IStatus.OK, getClass(), Messages.Installation_successful_message);
		} catch (IOException e) {
			e.printStackTrace();
			return new Status(IStatus.ERROR, getClass(),
					String.format(Messages.Installation_failed_message, e.getMessage()));
		}

	}

	public void killPio() {
		if (python.isPresent()) {
			python.get().killProcess("pio"); //$NON-NLS-1$
		}
	}

}
