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

	public abstract String pythonArch();

	public static OS get() {
		if (System.getProperty("os.name").toLowerCase().contains("windows")) { //$NON-NLS-1$ //$NON-NLS-2$
			if (System.getenv("ProgramFiles(x86)") != null) { //$NON-NLS-1$
				return new Windows64();
			}
			return new Windows32();
		}
		return new Other();
	}

	public static final class Windows32 extends OS {

		@Override
		public String pythonArch() {
			return "/win32/"; //$NON-NLS-1$
		}

	}

	public static final class Windows64 extends OS {

		@Override
		public String pythonArch() {
			return "/amd64/"; //$NON-NLS-1$
		}

	}

	public static final class Other extends OS {

		@Override
		public String pythonArch() {
			if (System.getProperty("os.arch").indexOf("64") != -1) { //$NON-NLS-1$ //$NON-NLS-2$
				return "/amd64/"; //$NON-NLS-1$
			}
			return ""; //$NON-NLS-1$
		}

	}

}
