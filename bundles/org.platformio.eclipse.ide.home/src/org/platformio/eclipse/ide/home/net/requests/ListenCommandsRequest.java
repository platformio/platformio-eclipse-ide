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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

import org.eclipse.core.runtime.Platform;
import org.platformio.eclipse.ide.home.net.BaseRequest;
import org.platformio.eclipse.ide.home.net.Handler;
import org.platformio.eclipse.ide.home.net.Request;

public final class ListenCommandsRequest extends BaseRequest {

	private final Consumer<Path> openProjectHandler;
	private final Consumer<Request> sendRequest;

	public ListenCommandsRequest(Consumer<Path> createProject, Consumer<Request> sendRequest) {
		this.openProjectHandler = createProject;
		this.sendRequest = sendRequest;
	}

	@Override
	public String method() {
		return "ide.listen_commands"; //$NON-NLS-1$
	}

	@Override
	public Handler handler() {
		return element -> {
			refresh();
			switch (element.getAsJsonObject().get("method").getAsString()) { //$NON-NLS-1$
			case "open_project": //$NON-NLS-1$
				openProjectHandler.accept(Paths.get(element.getAsJsonObject().get("params").getAsString())); //$NON-NLS-1$
				break;
			default:
				Platform.getLog(getClass()).error("Unsupported operation"); //$NON-NLS-1$
				break;
			}
		};
	}

	private void refresh() {
		sendRequest.accept(new ListenCommandsRequest(openProjectHandler, sendRequest));
	}

}
