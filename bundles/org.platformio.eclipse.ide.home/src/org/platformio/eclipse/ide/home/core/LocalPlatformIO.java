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
package org.platformio.eclipse.ide.home.core;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Consumer;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.platformio.eclipse.ide.home.api.PlatformIO;
import org.platformio.eclipse.ide.home.json.EnvironmentPaths;
import org.platformio.eclipse.ide.home.net.IDEWebSocket;
import org.platformio.eclipse.ide.home.net.requests.ListenCommandsRequest;
import org.platformio.eclipse.ide.home.python.Python;

public final class LocalPlatformIO implements PlatformIO {

	private static final String PROCESS_ID = "pio"; //$NON-NLS-1$
	private final Python python;
	private final String suffix;
	private final EnvironmentPaths installation;
	private final Consumer<String> importProject;

	public LocalPlatformIO(Python python, String suffix, EnvironmentPaths installation,
			Consumer<String> importProject) {
		this.python = python;
		this.suffix = suffix;
		this.installation = installation;
		this.importProject = importProject;
	}

	@Override
	public void home() throws IOException {
		python.environment().executeLasting(installation.envBinDir().resolve("pio" + suffix).toString(), //$NON-NLS-1$
				Arrays.asList("home", "--no-open"), //$NON-NLS-1$//$NON-NLS-2$
				PROCESS_ID);
		WebSocketClient client = new WebSocketClient();
		try {
			IDEWebSocket socket = new IDEWebSocket(new ListenCommandsRequest(this::initEclipseProject));
			client.start();
			URI address = new URI("ws://localhost:8008/wsrpc"); //$NON-NLS-1$
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			client.connect(socket, address, request);
			socket.latch().await();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() throws IOException {
		python.environment().killProcess(PROCESS_ID);
	}

	private void initEclipseProject(Path path) {
		python.environment().execute(installation.envBinDir().resolve("pio" + suffix).toString(), //$NON-NLS-1$
				Arrays.asList("project", "init", "-d", path.toString(), "--ide", "eclipse")); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$
		importProject.accept(path.toString());
	}

}
