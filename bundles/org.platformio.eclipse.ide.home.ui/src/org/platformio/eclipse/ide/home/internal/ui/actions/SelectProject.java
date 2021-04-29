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

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.platformio.eclipse.ide.home.core.Messages;

public final class SelectProject implements Supplier<Optional<IProject>> {

	@Override
	public Optional<IProject> get() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		ISelectionService selectionService = window.getActivePage();
		ISelection selection = selectionService.getSelection("org.eclipse.ui.navigator.ProjectExplorer"); //$NON-NLS-1$
		if (selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof IResource) {
				IProject project = ((IResource) element).getProject();
				if (project.exists(new Path("platformio.ini"))) {//$NON-NLS-1$
					return Optional.of(project);
				}
			}
		}
		return ask(window);
	}

	private Optional<IProject> ask(IWorkbenchWindow window) {
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(window.getShell(),
				new ProjectsLabelProvider());
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
		return Stream.of(ResourcesPlugin.getWorkspace().getRoot().getProjects()) //
				.filter(project -> project.isOpen() && project.exists(new Path("platformio.ini"))) //$NON-NLS-1$
				.collect(Collectors.toList());
	}

}
