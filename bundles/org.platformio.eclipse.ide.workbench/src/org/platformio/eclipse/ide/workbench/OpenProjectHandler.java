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
package org.platformio.eclipse.ide.workbench;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.platformio.eclipse.ide.home.api.PlatformIO;
import org.platformio.eclipse.ide.home.net.IDECommand;
import org.platformio.eclipse.ide.workspace.ImportProject;

import com.google.gson.JsonElement;

@Component
public final class OpenProjectHandler implements IDECommand {

	private final List<PlatformIO> pio = new ArrayList<>(1);

	@Override
	public void execute(JsonElement element) {
		try {
			new ImportProject(pio.get(0)).execute(Paths.get(element.getAsJsonObject().get("params").getAsString())); //$NON-NLS-1$
		} catch (IOException | CoreException e) {
			Platform.getLog(getClass()).error(e.getMessage(), e);
		}
	}

	@Reference(cardinality = ReferenceCardinality.MANDATORY)
	public void bindPlatformIO(PlatformIO reference) {
		pio.set(0, reference);
	}

	public void unbindPlatformIO(PlatformIO reference) {
		pio.remove(reference);
	}

	@Override
	public String method() {
		return "open_project"; //$NON-NLS-1$
	}

}
