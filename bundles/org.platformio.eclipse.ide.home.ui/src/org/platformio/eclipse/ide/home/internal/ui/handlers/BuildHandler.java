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

import java.nio.file.Paths;
import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.platformio.eclipse.ide.home.api.PlatformIO;

public final class BuildHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
		PlatformIO pio = context.getService(context.getServiceReference(PlatformIO.class));
		IStructuredSelection selection = HandlerUtil.getCurrentStructuredSelection(event);
		Optional<IProject> project = new SelectProject(selection).get();
		if (project.isPresent()) {
			try {
				pio.build(Paths.get(project.get().getDescription().getLocationURI()));
			} catch (CoreException e) {
				Platform.getLog(getClass()).error(e.toString());
			}
		}
		return null;
	}

}
