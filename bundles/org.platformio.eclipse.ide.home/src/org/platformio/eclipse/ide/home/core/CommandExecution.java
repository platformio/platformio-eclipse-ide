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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.platformio.eclipse.ide.home.api.Command;
import org.platformio.eclipse.ide.home.api.Execution;
import org.platformio.eclipse.ide.home.api.ExecutionResult;
import org.platformio.eclipse.ide.home.api.Input;
import org.platformio.eclipse.ide.home.api.Output;

public final class CommandExecution implements Execution {

	private final Command command;
	private final Input input;
	private final Output output;
	private final List<Process> executing;

	public CommandExecution(Command command, Input input, Output output) {
		this.command = command;
		this.input = input;
		this.output = output;
		this.executing = new ArrayList<>(1);
	}

	public CommandExecution(Command command) {
		this(command, new DefaultInput(), new DefaultOutput());
	}

	@Override
	public ExecutionResult start() {
		try {
			Process process = process();
			executing.add(process);
			int code = process.waitFor();
			executing.remove(process);
			return new ExecutionResult.Success(code);
		} catch (IOException | InterruptedException e) {
			return new ExecutionResult.Failure(-1, e.getMessage());
		}
	}

	@Override
	public void interrupt() {
		if (!executing.isEmpty()) {
			executing.get(0).destroy();
		}
	}

	@SuppressWarnings("resource")
	private Process process() throws IOException {
		ProcessBuilder builder = new ProcessBuilder(command.asList());
		if (command.workingDirectory().exists()) {
			builder.directory(command.workingDirectory());
		}
		Process process = builder.start();
		input.connect(process.getOutputStream());
		output.output(process.getInputStream());
		output.error(process.getErrorStream());
		return process;
	}

}
