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
package org.platformio.eclipse.ide.installer.piocore;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

import org.platformio.eclipse.ide.home.json.EnvironmentPaths;
import org.platformio.eclipse.ide.home.python.Python;
import org.platformio.eclipse.ide.installer.api.PioCoreDistribution;
import org.platformio.eclipse.ide.installer.json.PathsDeserializer;
import org.platformio.eclipse.ide.installer.net.RemoteResource;

import com.google.gson.GsonBuilder;

public final class LocalPioCoreDistribution implements PioCoreDistribution {

	private final Python python;
	private final Path location;

	public LocalPioCoreDistribution(Python python) {
		this.python = python;
		this.location = python.environment().cache().resolve("get-platformio.py"); //$NON-NLS-1$
	}

	@Override
	public boolean installed() {
		return python.executeScript(location, "check", "core").code() == 0; //$NON-NLS-1$//$NON-NLS-2$
	}

	@Override
	public EnvironmentPaths paths() throws IOException {
		final Path dump = python.environment().cache().resolve("tmpdir/pioinstaller-state.json"); //$NON-NLS-1$ ;
		python.executeScript(location, "check", "core", "--dump-state", dump.toString()); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		try (Reader reader = Files.newBufferedReader(dump)) {
			EnvironmentPaths installation = new GsonBuilder()
					.registerTypeAdapter(EnvironmentPaths.class, new PathsDeserializer()) // s
					.create().fromJson(reader, EnvironmentPaths.class);
			return installation;
		}
	}

	@Override
	public void install() throws IOException {
		if (!Files.exists(location)) {
			downloadPioInstaller(location);
		}
		python.executeScript(location);
	}

	private void downloadPioInstaller(Path resolve) throws IOException {
		new RemoteResource(
				"https://raw.githubusercontent.com/platformio/platformio-core-installer/master/get-platformio.py") //$NON-NLS-1$
						.download(resolve); // $NON-NLS-2$
	}

}
