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
package org.platformio.eclipse.ide.home.net.requests;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.core.runtime.Platform;
import org.platformio.eclipse.ide.home.net.BaseRequest;
import org.platformio.eclipse.ide.home.net.Handler;
import org.platformio.eclipse.ide.home.net.Request;

public final class ListenRequest extends BaseRequest {

	private final Consumer<Request> sendRequest;
	private final Map<String, Handler> handlers;

	public ListenRequest(Consumer<Request> sendRequest) {
		this.sendRequest = sendRequest;
		this.handlers = new HashMap<>();
	}

	@Override
	public String method() {
		return "ide.listen_commands"; //$NON-NLS-1$
	}

	public void registerHandler(String method, Handler handler) {
		handlers.put(method, handler);
	}

	public void unregisterHandler(String method) {
		handlers.remove(method);
	}

	@Override
	public Handler handler() {
		return element -> {
			refresh();
			String method = element.getAsJsonObject().get("method").getAsString(); //$NON-NLS-1$
			if (handlers.containsKey(method)) {
				handlers.get(method).handle(element);
			} else {
				Platform.getLog(getClass()).error("Unsupported operation"); //$NON-NLS-1$
			}
		};
	}

	private void refresh() {
		ListenRequest request = new ListenRequest(sendRequest);
		handlers.forEach((key, value) -> request.registerHandler(key, value));
		sendRequest.accept(request);
	}

}
