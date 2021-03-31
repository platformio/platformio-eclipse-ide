/*******************************************************************************
 * Copyright (c) 2021 ArSysOp and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Nikifor Fedorov (ArSysOp) - initial API and implementation
 *******************************************************************************/
package org.platformio.eclipse.ide.home.net.requests;

import org.eclipse.core.runtime.Platform;
import org.platformio.eclipse.ide.home.net.Handler;
import org.platformio.eclipse.ide.home.net.Request;

public final class ListenCommandsRequest implements Request {

	@Override
	public String method() {
		return "ide.listen_commands"; //$NON-NLS-1$
	}

	@Override
	public Handler handler() {
		return element -> {
			switch (element.getAsJsonObject().get("method").getAsString()) { //$NON-NLS-1$
			case "open_project": //$NON-NLS-1$
				Platform.getLog(getClass()).info(element.getAsJsonObject().get("params").getAsString()); //$NON-NLS-1$
				break;
			default:
				Platform.getLog(getClass()).error("Unsupported operation"); //$NON-NLS-1$
				break;
			}
		};
	}

}
