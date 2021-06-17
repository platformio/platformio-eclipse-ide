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
package org.platformio.eclipse.ide.home.internal.ui.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.ui.console.IOConsoleInputStream;
import org.platformio.eclipse.ide.home.api.Input;

public final class TerminalInput implements Input {

	private final IOConsoleInputStream stream;

	public TerminalInput(IOConsoleInputStream stream) {
		this.stream = stream;
	}

	@Override
	public void connect(OutputStream output) throws IOException {
		transfer(stream, output);
	}

	private void transfer(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[8192];
		int read;
		while ((read = in.read(buffer, 0, 8192)) >= 0) {
			out.write(buffer, 0, read);
		}
	}

}
