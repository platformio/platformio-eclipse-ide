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
package org.platformio.eclipse.ide.installer.net;

import java.nio.file.Path;
import java.util.Arrays;

import org.platformio.eclipse.ide.installer.api.Environment;

public final class MSIPackage {

	private final Path location;

	public MSIPackage(Path location) {
		this.location = location;
	}

	public void install(Environment environment, Path target) {
		Path logFile = environment.cache().resolve("python27msi.log"); //$NON-NLS-1$
		environment.execute("msiexec.exe", //$NON-NLS-1$
				Arrays.asList("/a", '"' + location.toString() + '"', "/qn", //$NON-NLS-1$ //$NON-NLS-2$
						"/li", '"' + logFile.toString() + '"', "TARGETDIR=\"" + target.toString() + '"')); //$NON-NLS-1$//$NON-NLS-2$
	}
}
