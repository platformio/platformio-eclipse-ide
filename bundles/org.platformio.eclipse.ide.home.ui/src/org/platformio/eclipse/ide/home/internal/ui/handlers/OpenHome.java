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
package org.platformio.eclipse.ide.home.internal.ui.handlers;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.platformio.eclipse.ide.home.core.Messages;
import org.platformio.eclipse.ide.home.internal.ui.HomeInput;
import org.platformio.eclipse.ide.home.internal.ui.HomeViewId;

public final class OpenHome implements Consumer<Supplier<IWorkbenchWindow>> {

	@Override
	public void accept(Supplier<IWorkbenchWindow> window) {
		if (window.get() != null) {
			IWorkbenchPage activePage = window.get().getActivePage();
			if (activePage != null) {
				try {
					IDE.openEditor(activePage, new HomeInput(), new HomeViewId().get());
					return;
				} catch (PartInitException e) {
					Platform.getLog(getClass()).error(e.toString(), e);
				}
			}
		}
		Platform.getLog(getClass()).error(Messages.View_Not_Opened_Error_text);
	}

}
