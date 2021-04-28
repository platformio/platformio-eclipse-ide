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
package org.platformio.eclipse.ide.home.internal.ui.actions;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.platformio.eclipse.ide.home.core.Messages;

public final class SelectProjectDialog implements Supplier<Optional<IProject>> {

	private final Shell shell;

	public SelectProjectDialog(Shell shell) {
		this.shell = shell;
	}

	@Override
	public Optional<IProject> get() {
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, new ProjectsLabelProvider());
		dialog.setTitle(Messages.Select_Project_Title);
		dialog.setMultipleSelection(false);
		dialog.setMessage(Messages.Select_Project_Message);
		dialog.setElements(projects().toArray());
		if (dialog.open() != Window.OK) {
			return Optional.empty();
		}
		IProject selection = (IProject) dialog.getFirstResult();
		return Optional.of(selection);
	}

	private List<IProject> projects() {
		List<IProject> projects = new LinkedList<>();
		IProject[] all = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject project : all) {
			if (project.isOpen() && project.exists(new Path("platformio.ini"))) { //$NON-NLS-1$
				projects.add(project);
			}
		}
		return projects;
	}

}
