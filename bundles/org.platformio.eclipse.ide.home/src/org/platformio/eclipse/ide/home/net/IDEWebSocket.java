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
import org.platformio.eclipse.ide.home.net.requests.ListenCommandsRequest;
import org.platformio.eclipse.ide.home.net.requests.VersionRequest;

import com.google.gson.Gson;

@WebSocket
public final class IDEWebSocket {

	private final CountDownLatch latch = new CountDownLatch(1);
	private final Map<Long, Handler> handlers = new HashMap<>();
	private Optional<Session> session = Optional.empty();

	@OnWebSocketConnect
	public void onConnect(Session session) {
		this.session = Optional.of(session);
		sendRequest(new VersionRequest(result -> sendRequest(new ListenCommandsRequest())));
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
		Platform.getLog(getClass()).error("Error: " + error.getMessage()); //$NON-NLS-1$
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
