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
package org.platformio.eclipse.ide.installer.base;

import java.nio.file.Path;
import java.util.Arrays;

import org.platformio.eclipse.ide.installer.api.Environment;
import org.platformio.eclipse.ide.installer.api.Python;

public final class LocalPython implements Python {

	private final Environment environment;
	private final Path location;

	public LocalPython(Environment environment, Path location) {
		this.environment = environment;
		this.location = location;
	}

	@Override
	public void installModule(String name) {
		environment.execute("pip", Arrays.asList("install", "-U", name)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public boolean moduleInstalled(String module) {
		return environment.execute(module, Arrays.asList("-V")).code() >= 0; //$NON-NLS-1$
	}

	@Override
	public Path location() {
		return location;
	}

}
