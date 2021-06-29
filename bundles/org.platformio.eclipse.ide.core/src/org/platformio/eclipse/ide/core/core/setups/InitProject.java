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
package org.platformio.eclipse.ide.core.core.setups;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.platformio.eclipse.ide.core.core.BaseCommand;
import org.platformio.eclipse.ide.core.paths.DefaultWorkingDirectory;

public final class InitProject extends BaseCommand {

	private final String project;

	public InitProject(Supplier<String> executable, String project) {
		super(executable.get(), new DefaultWorkingDirectory().get().toFile());
		this.project = project;
	}

	@Override
	public List<String> arguments() {
		return Arrays.asList("project", "init", "-d", project, "--ide", "eclipse", //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				"--project-option", "nobuild"); //$NON-NLS-1$//$NON-NLS-2$
	}

}
