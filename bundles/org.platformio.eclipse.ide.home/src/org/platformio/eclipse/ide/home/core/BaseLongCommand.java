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
package org.platformio.eclipse.ide.home.core;

import java.io.File;

import org.platformio.eclipse.ide.home.api.Command;

public abstract class BaseLongCommand implements Command {

	private final String command;
	private final File workingDirectory;

	public BaseLongCommand(String command, File workingDirectory) {
		this.command = command;
		this.workingDirectory = workingDirectory;
	}

	@Override
	public final String command() {
		return command;
	}

	@Override
	public final File workingDirectory() {
		return workingDirectory;
	}

}
