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

import java.util.function.Consumer;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;

public final class TerminalInputListener implements IDocumentListener {

	private final Consumer<DocumentEvent> handler;

	public TerminalInputListener(Consumer<DocumentEvent> handler) {
		this.handler = handler;
	}

	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
		// Do nothing
	}

	@Override
	public void documentChanged(DocumentEvent event) {
		if (System.lineSeparator().equals(event.getText())) {
			handler.accept(event);
		}
	}

}
