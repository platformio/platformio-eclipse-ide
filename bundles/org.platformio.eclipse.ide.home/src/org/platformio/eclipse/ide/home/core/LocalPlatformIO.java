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

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Component;
import org.platformio.eclipse.ide.home.api.PlatformIO;
import org.platformio.eclipse.ide.home.core.setups.Build;
import org.platformio.eclipse.ide.home.core.setups.Home;
import org.platformio.eclipse.ide.home.core.setups.InitProject;
import org.platformio.eclipse.ide.home.json.Dump;
import org.platformio.eclipse.ide.home.net.HandlerRegistry;
import org.platformio.eclipse.ide.home.net.IDEWebSocket;
import org.platformio.eclipse.ide.home.python.Python;

@Component
public final class LocalPlatformIO implements PlatformIO {

	private static final String PROCESS_ID = "pio"; //$NON-NLS-1$

	private final Python python;
	private final Dump installation;
	private final IDEWebSocket socket;

	public LocalPlatformIO() throws IOException {
		BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
		HandlerRegistry registry = context.getService(context.getServiceReference(HandlerRegistry.class));
		this.python = context.getService(context.getServiceReference(Python.class));
		this.socket = new IDEWebSocket(registry);
		this.installation = new Dump();

	}

	@Override
	public void home() throws IOException {
		new Home(new PIOExecutable.OfDump(installation, python.suffix()),
				python.environment().defaultWorkingDirectory().toFile(), PROCESS_ID) //
						.execute(python.environment());
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

	@Override
	public void initProject(Path path) throws IOException {
		new InitProject(new PIOExecutable.OfDump(installation, python.suffix()), path.toString(),
				python.environment().defaultWorkingDirectory().toFile()) //
						.execute(python.environment());
	}

	@Override
	public void build(Path path) {
		new Build(new PIOExecutable.OfDump(installation, python.suffix()), path.toFile()) //
				.execute(python.environment());
	}

	@Override
	public void clean(Path path) {
		// TODO Auto-generated method stub

	}

	@Override
	public void upload(Path path) {
		// TODO Auto-generated method stub

	}

}
