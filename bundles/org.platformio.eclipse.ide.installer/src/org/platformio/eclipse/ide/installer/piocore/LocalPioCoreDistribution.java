/*******************************************************************************
 * Copyright (c) 2021 ArSysOp and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Nikifor Fedorov (ArSysOp) - initial API and implementation
 *******************************************************************************/
package org.platformio.eclipse.ide.installer.piocore;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.platformio.eclipse.ide.installer.json.EnvironmentPaths;
import org.platformio.eclipse.ide.installer.json.PathsDeserializer;
import org.platformio.eclipse.ide.installer.net.RemoteResource;
import org.platformio.eclipse.ide.installer.python.Python;

import com.google.gson.GsonBuilder;

public final class LocalPioCoreDistribution implements PioCoreDistribution {

	private final Python python;
	private final Path location;
	private final Path dump;
	private final String suffix;

	public LocalPioCoreDistribution(Python python, String suffix) {
		this.python = python;
		this.suffix = suffix;
		this.location = python.environment().cache().resolve("get-platformio.py"); //$NON-NLS-1$
		this.dump = python.environment().cache().resolve("tmpdir/pioinstaller-state.json"); //$NON-NLS-1$
	}

	@Override
	public boolean installed() {
		return python.executeScript(location, "check", "core").code() == 0; //$NON-NLS-1$//$NON-NLS-2$
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

	private void dump() {
		python.executeScript(location, "check", "core", "--dump-state", dump.toString()); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
	}

	@Override
	public void home() throws IOException {
		dump();
		try (Reader reader = Files.newBufferedReader(dump)) {
			EnvironmentPaths installation = new GsonBuilder()
					.registerTypeAdapter(EnvironmentPaths.class, new PathsDeserializer()) // s
					.create().fromJson(reader, EnvironmentPaths.class);
			python.environment().executeLasting(installation.envBinDir().resolve("platformio" + suffix).toString(), //$NON-NLS-1$
					Arrays.asList("home", "--no-open"), "pio"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		}
	}

}
