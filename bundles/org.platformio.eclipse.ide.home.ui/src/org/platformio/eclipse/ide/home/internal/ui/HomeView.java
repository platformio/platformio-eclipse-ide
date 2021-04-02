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
package org.platformio.eclipse.ide.home.internal.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public final class HomeView extends ViewPart {

	static final String HOME_VIEW_ID = "org.platformio.eclipse.ide.home.ui.views.view.home"; //$NON-NLS-1$
	private Browser viewer;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new Browser(parent, SWT.CHROMIUM);
		initControls();
	}

	private void initControls() {
		// FIXME: get from Home service
		viewer.setUrl("http://127.0.0.1:8008/"); //$NON-NLS-1$
	}

	@Override
	public void setFocus() {
		viewer.setFocus();
	}

}
