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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.osgi.service.component.annotations.Component;
import org.platformio.eclipse.ide.home.net.IDECommandHandler;

import com.google.gson.JsonElement;

@Component
public final class OpenFileHandler implements IDECommandHandler {

	@Override
	public void handle(JsonElement element) {
		PlatformUI.getWorkbench().getDisplay().syncExec(() -> {
			try {
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				String path = element.getAsJsonObject().get("params").getAsJsonObject().get("path").getAsString(); //$NON-NLS-1$ //$NON-NLS-2$
				IFile file = workspace.getRoot().getFileForLocation(new Path(path));
				IDE.openEditor(PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage(), file);
			} catch (Exception e) {
				e.printStackTrace();
				Platform.getLog(getClass()).info(e.toString());
			}
		});
	}

	@Override
	public String method() {
		return "open_text_document"; //$NON-NLS-1$
	}

}
