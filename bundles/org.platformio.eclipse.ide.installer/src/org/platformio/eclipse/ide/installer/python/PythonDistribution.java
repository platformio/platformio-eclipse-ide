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
package org.platformio.eclipse.ide.installer.python;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.eclipse.core.runtime.IProgressMonitor;
import org.platformio.eclipse.ide.installer.Messages;
import org.platformio.eclipse.ide.installer.api.Environment;
import org.platformio.eclipse.ide.installer.net.RemoteResource;

public class PythonDistribution {

	private final List<String> requiredParts = Arrays.asList("core", "tools", "exe", "lib"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
	private final Supplier<String> version;
	private final Environment environment;
	private final IProgressMonitor monitor;

	public PythonDistribution(Environment environment, Supplier<String> version, IProgressMonitor monitor) {
		this.version = version;
		this.environment = environment;
		this.monitor = monitor;
	}

	public void install(Path target) {
		requiredParts.forEach(part -> installPart(part, target));
		installPip(target); // The way we install pip is kinda different
	}

	private void installPart(String part, Path target) {
		try {
			monitor.setTaskName(String.format(Messages.Installing_module_message, part));
			new RemoteResource(source(part)) //
					.download(environment.cache().resolve("downloads").resolve(part + ".msi")) // //$NON-NLS-1$ //$NON-NLS-2$
					.install(environment, target);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void installPip(Path target) {
		try {
			monitor.setTaskName(String.format(Messages.Installing_module_message, "pip")); //$NON-NLS-1$
			new RemoteResource("https://bootstrap.pypa.io/get-pip.py") //$NON-NLS-1$
					.download(target.resolve("get-pip.py")); //$NON-NLS-1$
			environment.execute(target.resolve("python.exe").toString(), //$NON-NLS-1$
					Arrays.asList(target.resolve("get-pip.py").toString())); //$NON-NLS-1$
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String source(String module) {
		return "https://www.python.org/ftp/python/" + version.get() //$NON-NLS-1$
				+ environment.os().pythonArch() + module + ".msi"; //$NON-NLS-1$
	}

}
