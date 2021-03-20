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

import java.util.Optional;

import org.platformio.eclipse.ide.installer.api.PythonsRegistry;

public class LinuxPythonsRegistry implements PythonsRegistry {

	@Override
	public Optional<String> findPython() {
		return Optional.of("python3"); //$NON-NLS-1$
	}

	@Override
	public String executableSuffix() {
		return ""; //$NON-NLS-1$
	}

}
