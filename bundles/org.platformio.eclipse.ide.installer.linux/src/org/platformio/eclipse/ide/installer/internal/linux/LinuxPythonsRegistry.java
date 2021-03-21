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
package org.platformio.eclipse.ide.installer.internal.linux;

import java.io.IOException;
import java.util.Optional;

import org.eclipse.core.runtime.Platform;
import org.platformio.eclipse.ide.installer.api.PythonsRegistry;

public final class LinuxPythonsRegistry implements PythonsRegistry {

	@Override
	public Optional<String> findPython() {
		try {
			if (Runtime.getRuntime().exec("python3 -V").waitFor() == 0) //$NON-NLS-1$
				return Optional.of("python3"); //$NON-NLS-1$
			if (Runtime.getRuntime().exec("python -V").waitFor() == 0) //$NON-NLS-1$
				return Optional.of("python"); //$NON-NLS-1$
		} catch (InterruptedException | IOException e) {
			Platform.getLog(getClass()).error(e.getMessage());
		}
		return Optional.empty();
	}

	@Override
	public String executableSuffix() {
		return ""; //$NON-NLS-1$
	}

}
