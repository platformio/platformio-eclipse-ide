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
package org.platformio.eclipse.ide.workbench;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.platformio.eclipse.ide.home.api.PlatformIO;
import org.platformio.eclipse.ide.home.net.IDECommand;
import org.platformio.eclipse.ide.workspace.OpenFile;

import com.google.gson.JsonElement;

@Component
public final class OpenFileHandler implements IDECommand {

	private final List<PlatformIO> pio = new ArrayList<>(1);

	@Override
	public void execute(JsonElement element) {
		OpenEditorData data = new OpenEditorData(element);
		new OpenFile(data.path(), () -> pio.get(0)).get().ifPresent(file -> open(file, data.line()));
	}

	@Override
	public String method() {
		return "open_text_document"; //$NON-NLS-1$
	}

	@Reference(cardinality = ReferenceCardinality.MANDATORY)
	public void bindPlatformIO(PlatformIO reference) {
		pio.set(0, reference);
	}

	public void unbindPlatformIO(PlatformIO reference) {
		pio.remove(reference);
	}

	private void open(IFile file, int line) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().syncExec(() -> {
			try {
				IWorkbenchWindow window = window(workbench);
				IEditorPart editor = IDE.openEditor(window.getActivePage(), file);
				if (editor instanceof ITextEditor) {
					IRegion region = ((ITextEditor) editor).getDocumentProvider().getDocument(editor.getEditorInput())
							.getLineInformation(line - 1);
					((ITextEditor) editor).getSelectionProvider()
							.setSelection(new TextSelection(region.getOffset(), region.getLength()));
				}
			} catch (Exception e) {
				Platform.getLog(getClass()).info(e.toString());
			}
		});
	}

	private IWorkbenchWindow window(IWorkbench workbench) throws CoreException {
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		if (window == null) {
			if (workbench.getWorkbenchWindowCount() > 0) {
				window = workbench.getWorkbenchWindows()[0];
			} else {
				throw new CoreException(new Status(IStatus.ERROR, getClass(), "No workbench windows")); //$NON-NLS-1$
			}
		}
		return window;
	}
}
