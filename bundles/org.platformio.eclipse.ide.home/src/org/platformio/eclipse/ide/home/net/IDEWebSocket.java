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
package org.platformio.eclipse.ide.home.net;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.platformio.eclipse.ide.home.net.json.RawResult;
import org.platformio.eclipse.ide.home.net.requests.VersionRequest;

import com.google.gson.Gson;

@WebSocket
public final class IDEWebSocket {

	private final CountDownLatch latch = new CountDownLatch(1);
	private final Map<Long, Handler> handlers = new HashMap<>();
	private Optional<Session> session = Optional.empty();
	private final Request listenRequest;

	public IDEWebSocket(Request listenRequest) {
		this.listenRequest = listenRequest;
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		this.session = Optional.of(session);
		sendRequest(new VersionRequest(result -> sendRequest(listenRequest)));
		latch.countDown();
	}

	@OnWebSocketMessage
	public void onMessage(Session session, String message) {
		RawResult result = new Gson().fromJson(message, RawResult.class);
		Platform.getLog(getClass()).info(message);
		handlers.get(result.id()).handle(result.result());
	}

	@OnWebSocketClose
	public void onClose(Session session, int status, String reason) {
		Platform.getLog(getClass()).info("Closed"); //$NON-NLS-1$
	}

	@OnWebSocketError
	public void onError(Throwable error) {
		Platform.getLog(getClass()).error("Error: " + error.toString()); //$NON-NLS-1$
	}

	public void sendMessage(String message) throws IOException {
		if (session.isPresent()) {
			Platform.getLog(getClass()).info("Sending to server: " + message); //$NON-NLS-1$
			session.get().getRemote().sendString(message);
		}
	}

	public void sendRequest(Request request) {
		long identifier = Math.round(Math.random() * 100000);
		try {
			sendMessage("{\"jsonrpc\": \"2.0\", \"id\": " + identifier //$NON-NLS-1$
					+ ", \"method\": \"" + request.method() + "\"}"); //$NON-NLS-1$ //$NON-NLS-2$
			handlers.put(identifier, request.handler());
		} catch (IOException e) {
			Platform.getLog(getClass()).info(e.getMessage());
		}
	}

	public CountDownLatch latch() {
		return latch;
	}

}
