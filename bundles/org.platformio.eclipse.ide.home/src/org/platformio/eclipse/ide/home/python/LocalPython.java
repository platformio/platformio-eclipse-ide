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
package org.platformio.eclipse.ide.home.python;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.platformio.eclipse.ide.home.api.CommandResult;
import org.platformio.eclipse.ide.home.api.Environment;

public final class LocalPython implements Python {

	private final Environment environment;
	private final String executable;

	public LocalPython(Environment environment, Path location) {
		this.environment = environment;
		this.executable = location.toString();
	}

	public LocalPython(Environment environment, String location) {
		this.environment = environment;
		this.executable = location;
	}

	@Override
	public void installModule(String name) {
		executeModule("pip", "install", "-U", name); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public boolean moduleInstalled(String module) {
		return executeModule(module, "-V").code() >= 0; //$NON-NLS-1$
	}

	@Override
	public Path executable() {
		return Paths.get(executable);
	}

	@Override
	public CommandResult executeModule(String module, String... moduleArgs) {
		List<String> executionArgs = new LinkedList<>();
		executionArgs.addAll(Arrays.asList("-m", module)); //$NON-NLS-1$
		executionArgs.addAll(Arrays.asList(moduleArgs));
		return environment.execute(executable, executionArgs);
	}

	@Override
	public void executeLasting(String module, String... moduleArgs) {
		List<String> executionArgs = new LinkedList<>();
		executionArgs.addAll(Arrays.asList("-m", module)); //$NON-NLS-1$
		executionArgs.addAll(Arrays.asList(moduleArgs));
		environment.executeLasting(executable, executionArgs, module);
	}

	@Override
	public void killProcess(String module) {
		environment.killProcess(module);
	}

	@Override
	public CommandResult executeScript(Path location, String... args) {
		List<String> executionArgs = new LinkedList<>();
		executionArgs.add(location.toString());
		executionArgs.addAll(Arrays.asList(args));
		return environment.execute(executable, executionArgs);
	}

	@Override
	public CommandResult executeCode(String code) {
		List<String> executionArgs = new LinkedList<>();
		executionArgs.addAll(Arrays.asList("-c", "\"" + code + "\"")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return environment.execute(executable, executionArgs);
	}

	@Override
	public Environment environment() {
		return environment;
	}

}
