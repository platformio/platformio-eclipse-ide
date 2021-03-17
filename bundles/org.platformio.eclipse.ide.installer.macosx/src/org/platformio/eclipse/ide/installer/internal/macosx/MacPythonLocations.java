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
package org.platformio.eclipse.ide.installer.internal.macosx;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.platformio.eclipse.ide.installer.python.PythonLocations;

public final class MacPythonLocations implements PythonLocations {

	@Override
	public List<Path> customLocations() {
		return Collections.emptyList();
	}

	@Override
	public List<String> names() {
		return Arrays.asList("python", "python3", "python39"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

}
