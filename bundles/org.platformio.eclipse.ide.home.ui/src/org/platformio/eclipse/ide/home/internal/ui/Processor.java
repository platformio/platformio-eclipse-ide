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

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MArea;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

public final class Processor {

	@Inject
	public void execute(MApplication application, EModelService service) {
		MPlaceholder editor = (MPlaceholder) service.find("org.eclipse.ui.editorss", application); //$NON-NLS-1$
		MArea area = (MArea) editor.getRef();
		MPartStack stack = (MPartStack) area.getChildren().get(0);
		List<MStackElement> children = stack.getChildren();
		if (!homeLaunched(children)) {
			MPart home = service.createPart(service.getPartDescriptor("org.platformio.eclipse.ide.home.ui.views.home")); //$NON-NLS-1$
			children.add(home);
		}
	}

	private boolean homeLaunched(List<MStackElement> children) {
		return children.stream().map(MStackElement::getElementId)
				.anyMatch(id -> id.equals("org.platformio.eclipse.ide.home.ui.views.home")); //$NON-NLS-1$
	}

}
