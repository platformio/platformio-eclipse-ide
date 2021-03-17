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
import org.platformio.eclipse.ide.installer.api.Environment;
import org.platformio.eclipse.ide.installer.api.PythonsRegistry;
import org.platformio.eclipse.ide.installer.base.BaseEnvironment;
import org.platformio.eclipse.ide.installer.piocore.LocalPioCoreDistribution;
import org.platformio.eclipse.ide.installer.piocore.PioCoreDistribution;
import org.platformio.eclipse.ide.installer.python.Python;
import org.platformio.eclipse.ide.installer.python.PythonDistribution;

public final class Installer {

	private final Environment environment = new BaseEnvironment();
	private final PythonsRegistry registry;

	private Optional<Python> python;

	public Installer() {
		this.registry = new PythonsRegistry(environment);
	}

	public void install(IProgressMonitor monitor) throws IOException {
		python = registry.findPython();
		if (!python.isPresent()) {
			monitor.setTaskName(Messages.Python_installation_message);
			new PythonDistribution(environment).install(environment.home().resolve("python39")); //$NON-NLS-1$
			install(monitor);
			return;
		}

		monitor.setTaskName(Messages.Installing_Platformio_message);
		PioCoreDistribution pio = new LocalPioCoreDistribution(python.get());
		if (!pio.installed()) {
			pio.install();
		}
		monitor.setTaskName(Messages.Launching_Platformio_home_message);
		pio.home();

	}

	public void killPio() {
		if (python.isPresent()) {
			python.get().killProcess("pio"); //$NON-NLS-1$
		}
	}

}
