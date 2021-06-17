/*******************************************************************************
 * Copyright (c) 2021 PlatformIO and ArSysOp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     Nikifor Fedorov (ArSysOp) - initial API and implementation
 *******************************************************************************/
package org.platformio.eclipse.ide.home.api;

public abstract class ExecutionResult {

	private final int code;

	public final int code() {
		return code;
	}

	public ExecutionResult(int code) {
		this.code = code;
	}

	public static final class Success extends ExecutionResult {

		public Success(int code) {
			super(code);
		}

	}

	public static final class Failure extends ExecutionResult {

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
