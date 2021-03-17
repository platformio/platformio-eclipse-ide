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
package org.platformio.eclipse.ide.installer.base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.platformio.eclipse.ide.installer.api.CommandResult;
import org.platformio.eclipse.ide.installer.api.Environment;

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
		ProcessBuilder builder = new ProcessBuilder(input(command, arguments));
		try {
			Process process = builder.start();
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
				e.printStackTrace();
			}
		}
		return dir;
	}

	@Override
	public void executeLasting(String command, List<String> arguments, String id) {
		ProcessBuilder builder = new ProcessBuilder(input(command, arguments));
		try {
			Process process = builder.start();
			running.put(id, process);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void killProcess(String id) {
		Process process = running.get(id);
		if (process != null) {
			process.destroy();
		}
	}

}
