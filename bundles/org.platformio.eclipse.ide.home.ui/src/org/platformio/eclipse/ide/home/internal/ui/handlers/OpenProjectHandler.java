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
import java.nio.file.Paths;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.osgi.service.component.annotations.Component;
import org.platformio.eclipse.ide.base.core.LocalPlatformIO;
import org.platformio.eclipse.ide.base.workspace.ImportProject;
import org.platformio.eclipse.ide.home.api.IDECommand;

import com.google.gson.JsonElement;

@Component
public final class OpenProjectHandler implements IDECommand {

	@Override
	public void execute(JsonElement element) {
		try {
			new ImportProject(new LocalPlatformIO())
					.execute(Paths.get(element.getAsJsonObject().get("params").getAsString())); //$NON-NLS-1$
		} catch (IOException | CoreException e) {
			Platform.getLog(getClass()).error(e.getMessage(), e);
		}
	}

	@Override
	public String method() {
		return "open_project"; //$NON-NLS-1$
	}

}
