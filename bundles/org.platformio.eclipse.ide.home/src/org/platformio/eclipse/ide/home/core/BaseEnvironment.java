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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.platformio.eclipse.ide.home.api.CommandResult;
import org.platformio.eclipse.ide.home.api.Environment;

public final class BaseEnvironment implements Environment {

	private final Map<String, Process> running = new HashMap<>();

	private List<String> input(String command, List<String> args) {
		List<String> resolved = new LinkedList<>();
		resolved.add(command);
		resolved.addAll(args);
		return resolved;
	}

	@Override
	public CommandResult execute(String command, List<String> arguments) {
		try {
			Process process = start(command, arguments);
			int code = process.waitFor();
			return new CommandResult.Success(code);
		} catch (Exception e) {
			return new CommandResult.Failure(-1, e.getMessage());
		}
	}

	@Override
	public Path home() {
		Path directory = Optional.ofNullable(System.getenv("PLATFORMIO_HOME_DIR")).map(s -> Paths.get(s)) //$NON-NLS-1$
				.orElse(Paths.get(System.getProperty("user.home"), ".platformio")); //$NON-NLS-1$ //$NON-NLS-2$
		if (!isASCIIValid(directory)) {
			directory = directory.getRoot().resolve(".platformio"); //$NON-NLS-1$
		}
		return directory;
	}

	private boolean isASCIIValid(Path result) {
		return result.toAbsolutePath().toString().chars().anyMatch(ch -> ch <= 127);
	}

	@Override
	public Path cache() {
		Path dir = home().resolve(".cache"); //$NON-NLS-1$
		if (!Files.isDirectory(dir)) {
			try {
				Files.createDirectories(dir);
				Files.createDirectory(dir.resolve("downloads")); //$NON-NLS-1$
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return dir;
	}

	@Override
	public void executeLasting(String command, List<String> arguments, String id) {
		try {
			Process process = start(command, arguments);
			running.put(id, process);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	private Process start(String command, List<String> arguments) throws IOException {
		ProcessBuilder builder = new ProcessBuilder(input(command, arguments))//
				.directory(home().resolve("penv").resolve("Scripts").toFile()); //$NON-NLS-1$ //$NON-NLS-2$
		Process process = builder.start();
		new ReadStream(process.getErrorStream()).start();
		new ReadStream(process.getInputStream()).start();
		return process;
	}

	@Override
	public void killProcess(String id) {
		Process process = running.get(id);
		if (process != null) {
			process.destroy();
		}
	}

}
