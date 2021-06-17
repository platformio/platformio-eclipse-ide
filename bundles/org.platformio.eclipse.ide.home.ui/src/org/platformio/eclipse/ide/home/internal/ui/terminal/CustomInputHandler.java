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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.ServiceCaller;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.platformio.eclipse.ide.home.api.PlatformIO;
import org.platformio.eclipse.ide.home.core.Messages;
import org.platformio.eclipse.ide.home.python.Python;

public final class CustomInputHandler {

	public void handle(String input, Terminal console) {
		List<String> words = new LinkedList<String>();
		words.addAll(Arrays.asList(input.trim().split(" "))); //$NON-NLS-1$
		String executable = words.remove(0);
		Job.create(executable, monitor -> {
			try (IOConsoleOutputStream stream = console.newOutputStream()) {
				TerminalOutput output = new TerminalOutput(stream);
				switch (executable) {
				case "pio": //$NON-NLS-1$
					new ServiceCaller<>(getClass(), PlatformIO.class).call(pio -> pio.execute(words, output));
					break;
				case "python": //$NON-NLS-1$
					new ServiceCaller<>(getClass(), Python.class).call(python -> python.execute(words, output));
					break;
				default:
					stream.write(Messages.Terminal_Unknown_Message + System.lineSeparator());
					break;
				}
			} catch (IOException e) {
				Platform.getLog(getClass()).error(e.getMessage(), e);
			}
		}).schedule();
	}

}
