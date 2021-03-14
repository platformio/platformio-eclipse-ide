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
package org.platformio.eclipse.ide.installer.api;

public abstract class OS {

	public abstract Architecture architecture();

	public static OS get() {
		if (System.getProperty("os.name").toLowerCase().contains("windows")) { //$NON-NLS-1$ //$NON-NLS-2$
			return new Windows();
		}
		return new Other();
	}

	public static final class Windows extends OS {

		@Override
		public Architecture architecture() {
			if (System.getenv("ProgramFiles(x86)") != null) { //$NON-NLS-1$
				return new Architecture.X64();
			}
			return new Architecture.X86();
		}

	}

	public static final class Other extends OS {

		@Override
		public Architecture architecture() {
			if (System.getProperty("os.arch").indexOf("64") != -1) { //$NON-NLS-1$ //$NON-NLS-2$
				return new Architecture.X64();
			}
			return new Architecture.X86();
		}

	}

}
