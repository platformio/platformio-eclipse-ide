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
package org.platformio.eclipse.ide.base.workspace;

import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.platformio.eclipse.ide.base.api.PlatformIO;

public final class ImportProject {

	private final PlatformIO installation;

	public ImportProject(PlatformIO installation) {
		this.installation = installation;
	}

	public void execute(Path location) throws IOException, CoreException {
		init(location);
		open(location);
	}

	private void init(Path path) throws IOException {
		installation.initProject(path);
	}

	private void open(Path path) throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProjectDescription description = workspace.loadProjectDescription(
				new org.eclipse.core.runtime.Path(path.toString() + java.io.File.separator + ".project")); //$NON-NLS-1$
		IProject project = workspace.getRoot().getProject(path.toFile().getName());
		IProgressMonitor monitor = new NullProgressMonitor();
		project.create(description, monitor);
		project.open(monitor);
	}

}
