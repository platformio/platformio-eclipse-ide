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
package org.platformio.eclipse.ide.core.internal.workbench.terminal;

import java.util.Optional;
import java.util.function.Consumer;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleView;
import org.platformio.eclipse.ide.core.internal.workbench.Messages;

public final class OpenTerminal implements Consumer<Terminal> {

	@Override
	public void accept(Terminal terminal) {
		Optional.ofNullable(PlatformUI.getWorkbench().getActiveWorkbenchWindow()) //
				.map(IWorkbenchWindow::getActivePage) //
				.ifPresent(page -> {
					try {
						IConsoleView consoleView = (IConsoleView) page.showView(IConsoleConstants.ID_CONSOLE_VIEW);
						IDocument document = terminal.getDocument();
						document.addDocumentListener(new TerminalInputListener(event -> {
							try {
								int offset = document.getLineOffset(document.getNumberOfLines() - 2);
								String command = document.get(offset, document.getLength() - offset - 2);
								new CustomInputHandler().handle(command, terminal);
							} catch (BadLocationException e) {
								Platform.getLog(getClass()).error(e.getMessage(), e);
							}
						}));
						ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { terminal });
						consoleView.display(terminal);
						document.set(Messages.Terminal_Top_Message + System.lineSeparator());
					} catch (PartInitException e) {
						Platform.getLog(getClass()).error(e.getMessage(), e);
					}
				});
	}
}
