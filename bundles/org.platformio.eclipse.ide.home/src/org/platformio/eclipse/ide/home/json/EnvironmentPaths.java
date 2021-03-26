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
package org.platformio.eclipse.ide.home.json;

import java.nio.file.Path;

public final class EnvironmentPaths {

	private final Path envBinDir;
	private final Path envDir;

	public EnvironmentPaths(Path executable, Path envBinDir) {
		this.envBinDir = envBinDir;
		this.envDir = executable;
	}

	public Path envBinDir() {
		return envBinDir;
	}

	public Path envDir() {
		return envDir;
	}

}
