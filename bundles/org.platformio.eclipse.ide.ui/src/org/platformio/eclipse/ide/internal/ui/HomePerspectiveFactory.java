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

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.internal.browser.WebBrowserView;
import org.eclipse.ui.navigator.resources.ProjectExplorer;

public class HomePerspectiveFactory implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.addView(ProjectExplorer.VIEW_ID, IPageLayout.LEFT, (float) 0.3,
				layout.getEditorArea());
		layout.addView(WebBrowserView.WEB_BROWSER_VIEW_ID, IPageLayout.RIGHT, (float) 0.7,
				layout.getEditorArea());
	}

}
