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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.console.TextConsoleViewer;

public final class TerminalViewer extends TextConsoleViewer {

	private final TextConsole console;

	public TerminalViewer(Composite parent, TextConsole console) {
		super(parent, console);
		this.console = console;
		getTextWidget().addCaretListener(event -> {
			int caretOffset = event.caretOffset;
			int lineOffset = lastLine();
			setEditable(caretOffset >= lineOffset);
		});
		getTextWidget().addVerifyKeyListener(event -> {
			int caretOffset = getTextWidget().getCaretOffset();
			int lineOffset = lastLine();
			if (event.keyCode == SWT.BS) {
				if (caretOffset == lineOffset) {
					event.doit = false;
				}
			}
		});
	}

	private int lastLine() {
		try {
			return console.getDocument().getLineOffset(console.getDocument().getNumberOfLines() - 1);
		} catch (BadLocationException e) {
			return 0;
		}
	}

}
