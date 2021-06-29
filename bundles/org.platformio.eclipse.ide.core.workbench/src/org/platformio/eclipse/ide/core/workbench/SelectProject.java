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
package org.platformio.eclipse.ide.core.workbench;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.platformio.eclipse.ide.home.core.Messages;

public final class SelectProject implements Supplier<Optional<IProject>> {

	private final IStructuredSelection selection;

	public SelectProject(IStructuredSelection selection) {
		this.selection = selection;
	}

	@Override
	public Optional<IProject> get() {
		Optional<IProject> selected = selected();
		if (selected.isPresent()) {
			return selected;
		}
		return ask();
	}

	private Optional<IProject> selected() {
		return Arrays.asList(selection.toArray()).stream() //
				.filter(IResource.class::isInstance) //
				.map(resource -> ((IResource) resource).getProject()) //
				.filter(new IsPlatformIOProject()).findFirst();
	}

	private Optional<IProject> ask() {
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), new WorkbenchLabelProvider());
		dialog.setTitle(Messages.Select_Project_Title);
		dialog.setMultipleSelection(false);
		dialog.setMessage(Messages.Select_Project_Message);
		dialog.setElements(projects().toArray());
		if (dialog.open() != Window.OK) {
			return Optional.empty();
		}
		return Optional.of((IProject) dialog.getFirstResult());
	}

	private List<IProject> projects() {
		return Stream.of(ResourcesPlugin.getWorkspace().getRoot().getProjects()) //
				.filter(IProject::isOpen) //
				.filter(new IsPlatformIOProject()) //
				.collect(Collectors.toList());
	}

}
