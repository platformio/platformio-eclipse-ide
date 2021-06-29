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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.platformio.eclipse.ide.core.api.Execution;
import org.platformio.eclipse.ide.core.core.CommandExecution;
import org.platformio.eclipse.ide.core.core.DefaultInput;
import org.platformio.eclipse.ide.core.core.DefaultOutput;
import org.platformio.eclipse.ide.core.core.PIOExecutable;
import org.platformio.eclipse.ide.core.json.Dump;
import org.platformio.eclipse.ide.core.paths.DefaultWorkingDirectory;
import org.platformio.eclipse.ide.home.api.HandlerRegistry;
import org.platformio.eclipse.ide.home.api.PlatformIOHome;
import org.platformio.eclipse.ide.home.internal.core.Home;

public final class LocalPlatformIOHome implements PlatformIOHome {

	private final List<Execution> running = new ArrayList<>(1);
	private final IDEWebSocket socket;
	private final Dump installation;

	public LocalPlatformIOHome(HandlerRegistry registry) throws IOException {
		this.socket = new IDEWebSocket(registry);
		this.installation = new Dump();
	}

	@Override
	public void launch(int port) {
		server(port);
		client(port);
	}

	private void client(int port) {
		WebSocketClient client = new WebSocketClient();
		try {
			client.start();
			URI address = new URI(String.format("ws://localhost:%d/wsrpc", port)); //$NON-NLS-1$
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			client.connect(socket, address, request);
			socket.latch().await();
		} catch (Exception e) {
			Platform.getLog(getClass()).error(e.getMessage(), e);
		}
	}

	@Override
	public void stop() {
		if (running.get(0) != null) {
			running.remove(0).interrupt();
		}

	}

	private void server(int port) {
		final CommandExecution execution = new CommandExecution(
				new Home(new PIOExecutable.OfDump(installation), new DefaultWorkingDirectory().get().toFile(), port),
				new DefaultInput(), new DefaultOutput());
		running.add(execution);
		execution.start();
	}

}
