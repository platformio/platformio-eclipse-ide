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

import java.util.Optional;

import org.eclipse.core.runtime.Platform;
import org.platformio.eclipse.ide.home.net.BaseRequest;
import org.platformio.eclipse.ide.home.net.HandlerRegistry;
import org.platformio.eclipse.ide.home.net.IDECommand;
import org.platformio.eclipse.ide.home.net.ResultHandler;

public final class ListenRequest extends BaseRequest {

	private final HandlerRegistry handlers;

	public ListenRequest(HandlerRegistry handlers) {
		this.handlers = handlers;
	}

	@Override
	public String method() {
		return "ide.listen_commands"; //$NON-NLS-1$
	}

	@Override
	public ResultHandler handler() {
		return element -> {
			String method = element.getAsJsonObject().get("method").getAsString(); //$NON-NLS-1$
			Optional<IDECommand> handler = handlers.get(method);
			if (handler.isPresent()) {
				handler.get().execute(element);
			} else {
				Platform.getLog(getClass()).error("Unsupported operation"); //$NON-NLS-1$
			}
		};
	}

}
