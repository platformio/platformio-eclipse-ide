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
import java.util.List;

import org.platformio.eclipse.ide.base.api.Output;
import org.platformio.eclipse.ide.base.api.PlatformIO;

public final class FakePlatformIO implements PlatformIO {

	@Override
	public void initProject(Path path) throws IOException {
		// Do nothing
	}

	@Override
	public void build(Path path, Output output) {
		// Do nothing
	}

	@Override
	public void clean(Path path, Output output) {
		// Do nothing
	}

	@Override
	public void upload(Path path, Output output) {
		// Do nothing
	}

	@Override
	public void execute(List<String> arguments, Output output) {
		// Do nothing
	}

}
