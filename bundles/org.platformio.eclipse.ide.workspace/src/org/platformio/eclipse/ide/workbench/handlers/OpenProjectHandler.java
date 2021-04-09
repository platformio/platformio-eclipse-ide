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
package org.platformio.eclipse.ide.workbench.handlers;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.osgi.service.component.annotations.Component;
import org.platformio.eclipse.ide.home.net.IDECommandHandler;

import com.google.gson.JsonElement;

@Component
public final class OpenProjectHandler implements IDECommandHandler {

//	private final Python python;
//	private final String suffix;
//	private final EnvironmentPaths installation;
//
//	public OpenProjectHandler(Python python, String suffix, EnvironmentPaths installation) {
//		this.python = python;
//		this.suffix = suffix;
//		this.installation = installation;
//	}

	@Override
	public void handle(JsonElement element) {
		Path location = Paths.get(element.getAsJsonObject().get("params").getAsString()); //$NON-NLS-1$
//		init(location);
		open(location);
	}

//	private void init(Path path) {
//		python.environment().execute(installation.envBinDir().resolve("pio" + suffix).toString(), //$NON-NLS-1$
//				Arrays.asList("project", "init", "-d", path.toString(), "--ide", "eclipse", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
//						"--project-option", "nobuild")); //$NON-NLS-1$//$NON-NLS-2$
//	}

	private void open(Path path) {
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IProjectDescription description = workspace.loadProjectDescription(
					new org.eclipse.core.runtime.Path(path.toString() + java.io.File.separator + ".project")); //$NON-NLS-1$
			IProject project = workspace.getRoot().getProject(path.toFile().getName());
			NullProgressMonitor monitor = new NullProgressMonitor();
			project.create(description, monitor);
			project.open(monitor);
		} catch (CoreException e) {
			Platform.getLog(getClass()).log(e.getStatus());
		}
	}

	@Override
	public String method() {
		return "open_project"; //$NON-NLS-1$
	}

}
