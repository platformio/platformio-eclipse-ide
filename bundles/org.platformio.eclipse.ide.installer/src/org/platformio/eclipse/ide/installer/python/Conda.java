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

import java.util.Arrays;

import org.platformio.eclipse.ide.installer.api.Environment;

public final class Conda {

	private final Environment environment;

	public Conda(Environment environment) {
		this.environment = environment;
	}

	public boolean installed() {
		return environment.execute("conda", Arrays.asList("--version")).code() == 0; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void createEnvironment() {
		environment.execute("conda", Arrays.asList("create", //$NON-NLS-1$//$NON-NLS-2$
				"--yes", //$NON-NLS-1$
				"--quiet", //$NON-NLS-1$
				"python=2", //$NON-NLS-1$
				"pip", //$NON-NLS-1$
				"--prefix", "")); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
