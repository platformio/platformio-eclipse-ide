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
package org.platformio.eclipse.ide.core.internal.workbench.handlers;

import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ServiceCaller;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.platformio.eclipse.ide.core.api.Output;
import org.platformio.eclipse.ide.core.api.PlatformIO;
import org.platformio.eclipse.ide.core.core.DefaultOutput;
import org.platformio.eclipse.ide.core.workbench.SelectProject;

public abstract class PlatformIOHandler extends AbstractHandler {

	@Override
	public final Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = HandlerUtil.getCurrentStructuredSelection(event);
		Optional<IProject> project = new SelectProject(selection).get();
		if (project.isPresent()) {
			new ServiceCaller<>(getClass(), PlatformIO.class).call(pio -> {
				Job.create(title(), monitor -> {
					execute(pio, project.get(), new DefaultOutput());
				}).schedule();
			});
		}
		return null;
	}

	public abstract void execute(PlatformIO pio, IProject project, Output output) throws CoreException;

	public abstract String title();

}
