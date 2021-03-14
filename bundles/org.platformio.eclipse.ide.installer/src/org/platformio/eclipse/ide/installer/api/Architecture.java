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

public abstract class Architecture {

	public abstract String pythonArch();

	public static final class X64 extends Architecture {

		@Override
		public String pythonArch() {
			return ".amd64"; //$NON-NLS-1$
		}

	}

	public static final class X86 extends Architecture {

		@Override
		public String pythonArch() {
			return ""; //$NON-NLS-1$
		}

	}

}
