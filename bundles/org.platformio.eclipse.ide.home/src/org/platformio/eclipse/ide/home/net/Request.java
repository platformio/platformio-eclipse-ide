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

public abstract class Request {

	private final long identifier;

	public Request() {
		this.identifier = Math.round(Math.random() * 100000);
	}

	public final long identifier() {
		return identifier;
	}

	public final String message() {
		return "{\"jsonrpc\": \"2.0\", \"id\": " + identifier() //$NON-NLS-1$
				+ ", \"method\": \"" + method() + "\"}"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public abstract String method();

	public abstract Handler handler();

}
