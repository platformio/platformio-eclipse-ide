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
package org.platformio.eclipse.ide.base.core;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.platformio.eclipse.ide.base.api.Command;
import org.platformio.eclipse.ide.base.api.Output;
import org.platformio.eclipse.ide.base.api.PlatformIO;
import org.platformio.eclipse.ide.base.core.setups.Build;
import org.platformio.eclipse.ide.base.core.setups.Clean;
import org.platformio.eclipse.ide.base.core.setups.InitProject;
import org.platformio.eclipse.ide.base.core.setups.Upload;
import org.platformio.eclipse.ide.base.json.Dump;

@Component
public final class LocalPlatformIO implements PlatformIO {

	private final Dump installation;

	public LocalPlatformIO() throws IOException {
		this.installation = new Dump();

	}

	@Override
	public void initProject(Path path) throws IOException {
		execute(new InitProject(new PIOExecutable.OfDump(installation), path.toString()), new DefaultOutput());
	}

	@Override
	public void build(Path path, Output output) {
		execute(new Build(new PIOExecutable.OfDump(installation), path.toFile()), output);
	}

	@Override
	public void clean(Path path, Output output) {
		execute(new Clean(new PIOExecutable.OfDump(installation), path.toFile()), output);
	}

	@Override
	public void upload(Path path, Output output) {
		execute(new Upload(new PIOExecutable.OfDump(installation), path.toFile()), output);
	}

	private void execute(Command command, Output output) {
		new CommandExecution(command, output).start();
	}

	@Override
	public void execute(List<String> command, Output output) {
		new CommandExecution(new CustomCommand(new PIOExecutable.OfDump(installation).get(), command), output).start();
	}

}
