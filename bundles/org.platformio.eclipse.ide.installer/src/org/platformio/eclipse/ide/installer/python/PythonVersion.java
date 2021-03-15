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

import java.util.function.Supplier;

public final class PythonVersion implements Supplier<String> {

	private final int[] version;

	public PythonVersion(int major, int minor, int patch) {
		version = new int[] { major, minor, patch };
	}

	@Override
	public String get() {
		return version[0] + "." + version[1] + "." + version[2]; //$NON-NLS-1$//$NON-NLS-2$
	}

}
