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
package org.platformio.eclipse.ide.home.json;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.platformio.eclipse.ide.home.python.Python;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public final class Dump {

	private final Path envBinDir;
	private final Path envDir;

	public Dump() throws IOException {
		BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
		Python python = context.getService(context.getServiceReference(Python.class));
		Path script = python.environment().cache().resolve("get-platformio.py"); //$NON-NLS-1$
		Path dump = python.environment().cache().resolve("tmpdir/state.json"); //$NON-NLS-1$
		python.executeScript(script, "check", "core", "--dump-state", dump.toString()); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		try (Reader reader = Files.newBufferedReader(dump)) {
			JsonElement root = JsonParser.parseReader(reader);
			this.envDir = Paths.get(root.getAsJsonObject().get("penv_dir").getAsString()); //$NON-NLS-1$
			this.envBinDir = Paths.get(root.getAsJsonObject().get("penv_bin_dir").getAsString()); //$NON-NLS-1$
		}
	}

	public Path envBinDir() {
		return envBinDir;
	}

	public Path envDir() {
		return envDir;
	}

}
