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
package org.platformio.eclipse.ide.base.internal.ui.handlers;

import java.nio.file.Paths;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.platformio.eclipse.ide.base.api.Output;
import org.platformio.eclipse.ide.base.api.PlatformIO;
import org.platformio.eclipse.ide.base.internal.ui.Messages;

public final class CleanHandler extends PlatformIOHandler {

	@Override
	public void execute(PlatformIO pio, IProject project, Output output) throws CoreException {
		pio.clean(Paths.get(project.getDescription().getLocationURI()), output);
	}

	@Override
	public String title() {
		return Messages.Task_Clean_Title;
	}

}
