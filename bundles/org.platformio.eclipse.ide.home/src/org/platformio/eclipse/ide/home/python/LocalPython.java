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
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.service.component.annotations.Component;
import org.platformio.eclipse.ide.home.api.ExecutionResult;
import org.platformio.eclipse.ide.home.api.Output;
import org.platformio.eclipse.ide.home.core.CommandExecution;
import org.platformio.eclipse.ide.home.core.CustomCommand;
import org.platformio.eclipse.ide.home.core.DefaultInput;

@Component
public final class LocalPython implements Python {

	private final String executable;
	private final String suffix;

	public LocalPython() throws CoreException {
		PythonsRegistry registry = registry();
		this.executable = registry.findPython().get();
		this.suffix = registry.executableSuffix();
	}

	public LocalPython(Path location) throws CoreException {
		this(location.toString());
	}

	public LocalPython(String location) throws CoreException {
		this.executable = location;
		this.suffix = registry().executableSuffix();
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
	public ExecutionResult executeModule(String module, String... moduleArgs) {
		List<String> executionArgs = new LinkedList<>();
		executionArgs.addAll(Arrays.asList("-m", module)); //$NON-NLS-1$
		executionArgs.addAll(Arrays.asList(moduleArgs));
		return new CommandExecution(new CustomCommand(executable, executionArgs)).start();
	}

	@Override
	public ExecutionResult executeScript(Path location, String... args) {
		List<String> executionArgs = new LinkedList<>();
		executionArgs.add(location.toString());
		executionArgs.addAll(Arrays.asList(args));
		return new CommandExecution(new CustomCommand(executable, executionArgs)).start();
	}

	@Override
	public ExecutionResult executeCode(String code) {
		List<String> executionArgs = new LinkedList<>();
		executionArgs.addAll(Arrays.asList("-c", "\"" + code + "\"")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return new CommandExecution(new CustomCommand(executable, executionArgs)).start();
	}

	private PythonsRegistry registry() throws CoreException {
		Optional<IConfigurationElement> extensionItem = Stream
				.of(Platform.getExtensionRegistry()
						.getExtensionPoint("org.platformio.eclipse.ide.installer.prerequisites").getExtensions()) //$NON-NLS-1$
				.flatMap(extension -> Stream.of(extension.getConfigurationElements())) //
				.filter(element -> "registry".equals(element.getName())) // //$NON-NLS-1$
				.findAny();
		return (PythonsRegistry) extensionItem.get().createExecutableExtension("class"); //$NON-NLS-1$
	}

	@Override
	public String suffix() {
		return suffix;
	}

	@Override
	public void execute(List<String> arguments, Output output) {
		new CommandExecution(new CustomCommand(executable, arguments), new DefaultInput(), output).start();
	}

}
