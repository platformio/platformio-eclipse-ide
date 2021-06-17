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
package org.platformio.eclipse.ide.home.internal.ui.handlers;

import java.io.IOException;
import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.ServiceCaller;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.handlers.HandlerUtil;
import org.platformio.eclipse.ide.home.api.Input;
import org.platformio.eclipse.ide.home.api.Output;
import org.platformio.eclipse.ide.home.api.PlatformIO;
import org.platformio.eclipse.ide.home.core.DefaultInput;
import org.platformio.eclipse.ide.home.internal.ui.terminal.ExistingTerminal;
import org.platformio.eclipse.ide.home.internal.ui.terminal.Terminal;
import org.platformio.eclipse.ide.home.internal.ui.terminal.TerminalOutput;

public abstract class PlatformIOHandler extends AbstractHandler {

	@Override
	public final Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = HandlerUtil.getCurrentStructuredSelection(event);
		Optional<IProject> project = new SelectProject(selection).get();
		if (project.isPresent()) {
			Terminal terminal = new ExistingTerminal().get();
			new ServiceCaller<>(getClass(), PlatformIO.class).call(pio -> {
				Job.create(getClass().getName(), monitor -> {
					try (IOConsoleOutputStream output = terminal.newOutputStream()) {
						execute(pio, project.get(), new DefaultInput(), new TerminalOutput(output));
					} catch (IOException e) {
						Platform.getLog(getClass()).error(e.getMessage(), e);
					}
				}).schedule();
			});
		}
		return null;
	}

	public abstract void execute(PlatformIO pio, IProject project, Input input, Output output) throws CoreException;

}
