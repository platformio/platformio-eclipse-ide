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

public abstract class CommandResult {

	private final int code;

	public final int code() {
		return code;
	}

	public CommandResult(int code) {
		this.code = code;
	}

	public static final class Success extends CommandResult {

		public Success(int code) {
			super(code);
		}

	}

	public static final class Failure extends CommandResult {

		private final String message;

		public Failure(int code, String message) {
			super(code);
			this.message = message;
		}

		public String message() {
			return message;
		}

	}
}
