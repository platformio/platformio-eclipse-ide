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
import java.util.concurrent.CountDownLatch;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.platformio.eclipse.ide.home.net.json.RawResult;
import org.platformio.eclipse.ide.home.net.requests.ListenRequest;
import org.platformio.eclipse.ide.home.net.requests.VersionRequest;

import com.google.gson.Gson;

@WebSocket
public final class IDEWebSocket {

	private final CountDownLatch latch = new CountDownLatch(1);
	private final Map<Long, ResultHandler> handlers = new HashMap<>();
	private final HandlerRegistry registry;

	public IDEWebSocket(HandlerRegistry registry) {
		this.registry = registry;
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		sendRequest(session, new VersionRequest(result -> listen(session))); // $NON-NLS-1$
		latch.countDown();
	}

	@OnWebSocketMessage
	public void onMessage(Session session, String message) {
		RawResult result = new Gson().fromJson(message, RawResult.class);
		Platform.getLog(getClass()).info(message);
		handlers.get(result.id()).handle(result.result());
		listen(session);
	}

	@OnWebSocketClose
	public void onClose(int status, String reason) {
		Platform.getLog(getClass()).info("Closed with status " + status + " for reason: " + reason); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@OnWebSocketError
	public void onError(Throwable error) {
		error.printStackTrace();
		Platform.getLog(getClass()).error("Error: " + error.toString()); //$NON-NLS-1$
	}

	private void sendRequest(Session session, Request request) {
		refreshHandlers(request);
		sendMessage(session, request.message());
	}

	private void listen(Session session) {
		sendRequest(session, new ListenRequest(registry));
	}

	private void sendMessage(Session session, String message) {
		try {
			Platform.getLog(getClass()).info("Sending to server: " + message); //$NON-NLS-1$
			session.getRemote().sendString(message);
		} catch (IOException e) {
			Platform.getLog(getClass()).info(e.toString());
		}
	}

	private void refreshHandlers(Request request) {
		handlers.clear();
		handlers.put(request.identifier(), request.handler());
	}

	public CountDownLatch latch() {
		return latch;
	}

}
