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
package org.platformio.eclipse.ide.home.core;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.platformio.eclipse.ide.home.api.PlatformIO;
import org.platformio.eclipse.ide.home.json.EnvironmentPaths;
import org.platformio.eclipse.ide.home.net.IDEWebSocket;
import org.platformio.eclipse.ide.home.net.requests.ListenCommandsRequest;
import org.platformio.eclipse.ide.home.net.requests.VersionRequest;
import org.platformio.eclipse.ide.home.python.Python;

public final class LocalPlatformIO implements PlatformIO {

	private static final String PROCESS_ID = "pio"; //$NON-NLS-1$
	private final Python python;
	private final String suffix;
	private final EnvironmentPaths installation;
	private final IDEWebSocket socket;

	public LocalPlatformIO(Python python, String suffix, EnvironmentPaths installation) {
		this.python = python;
		this.suffix = suffix;
		this.installation = installation;
		this.socket = new IDEWebSocket(this::listen);
	}

	@Override
	public void home() throws IOException {
		python.environment().executeLasting(installation.envBinDir().resolve("pio" + suffix).toString(), //$NON-NLS-1$
				Arrays.asList("home", "--no-open"), //$NON-NLS-1$//$NON-NLS-2$
				PROCESS_ID);
		WebSocketClient client = new WebSocketClient();
		try {
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

	private void listen(Session session) {
		ListenCommandsRequest listenRequest = new ListenCommandsRequest(this::initEclipseProject,
				request -> socket.sendRequest(session, request));
		socket.sendRequest(session, new VersionRequest(result -> socket.sendRequest(session, listenRequest))); // $NON-NLS-1$
	}

	private void initEclipseProject(Path path) {
		python.environment().execute(installation.envBinDir().resolve("pio" + suffix).toString(), //$NON-NLS-1$
				Arrays.asList("project", "init", "-d", path.toString(), "--ide", "eclipse", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
						"--project-option", "nobuild")); //$NON-NLS-1$//$NON-NLS-2$
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IProjectDescription description = workspace.loadProjectDescription(
					new org.eclipse.core.runtime.Path(path.toString() + java.io.File.separator + ".project")); //$NON-NLS-1$
			IProject project = workspace.getRoot().getProject(path.toFile().getName());
			NullProgressMonitor monitor = new NullProgressMonitor();
			project.create(description, monitor);
			project.open(monitor);
		} catch (CoreException e) {
			Platform.getLog(getClass()).log(e.getStatus());
		}
	}

}
