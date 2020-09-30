/*******************************************************************************
 *	Copyright (c) 2020 PlatformIO and ArSysOp
 *
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 *
 *	SPDX-License-Identifier: Apache-2.0
 *
 *	Contributors:
 *		ArSysOp - initial API and implementation
 *******************************************************************************/
package org.platformio.eclipse.ide.internal.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.internal.browser.BrowserViewer;
import org.eclipse.ui.internal.browser.WebBrowserView;

@SuppressWarnings("restriction")
public final class HomeView extends WebBrowserView {

	public static final String HOME_VIEW_ID = "org.platformio.eclipse.ide.ui.views.view.home"; //$NON-NLS-1$

	@Override
	public void createPartControl(Composite parent) {
		viewer = new BrowserViewer(parent, SWT.NONE);
		viewer.setContainer(this);
		initDragAndDrop();
		initUrl();
	}

	private void initUrl() {
		viewer.setURL("http://127.0.0.1:8008/"); //$NON-NLS-1$
	}

}
